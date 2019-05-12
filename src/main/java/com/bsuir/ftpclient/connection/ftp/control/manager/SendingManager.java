package com.bsuir.ftpclient.connection.ftp.control.manager;

import com.bsuir.ftpclient.connection.database.DatabaseConnectionActions;
import com.bsuir.ftpclient.connection.database.exception.DatabaseConnectionException;
import com.bsuir.ftpclient.connection.ftp.Connection;
import com.bsuir.ftpclient.connection.ftp.control.ControlConnectionActions;
import com.bsuir.ftpclient.connection.ftp.control.ControlStructure;
import com.bsuir.ftpclient.connection.ftp.control.exception.FtpConnectionException;
import org.apache.log4j.Logger;

import java.util.concurrent.*;

public class SendingManager {
    private static final Logger LOGGER = Logger.getLogger("senderLogger");

    private Connection controlConnection;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static final int TIMEOUT = 5000;
    private Exchanger<String> responseExchanger;

    public SendingManager(Connection controlConnection, Exchanger<String> responseExchanger) {
        this.controlConnection = controlConnection;
        this.responseExchanger = responseExchanger;
    }

    private class Sender implements Runnable {
        private String request;

        private Sender(String request) {
            this.request = request;
        }

        @Override
        public void run() {
            try {
                ControlConnectionActions connectionActions = new ControlConnectionActions(controlConnection);
                connectionActions.sendRequest(request);
                String response = connectionActions.receiveResponse();

                handleAnswerCodeOfResponse(response);

                DatabaseConnectionActions databaseConnectionActions = new DatabaseConnectionActions();
                ControlStructure controlStructure = new ControlStructure(request, response);
                databaseConnectionActions.insertControlStructure(controlStructure);
            } catch (FtpConnectionException | TimeoutException
                    | InterruptedException | DatabaseConnectionException e) {
                LOGGER.error("Sending message error.", e);
            }
        }

        private void handleAnswerCodeOfResponse(String response)
                throws TimeoutException, InterruptedException, FtpConnectionException {
            String answerCode = response.substring(0, 3);

            switch (answerCode) {
                // answer to PASV with hostname and port of server
                case "227":
                    responseExchanger.exchange(response, TIMEOUT, TimeUnit.MILLISECONDS);
                    break;
                // answer to QUIT or when server died
                case "421":
                case "221":
                    controlConnection.disconnect();
                    break;
            }
        }
    }

    public void send(String request) {
        Sender sender = new Sender(request);
        executorService.execute(sender);
    }

    public void killAllSenders() {
        executorService.shutdown();
    }

    public void restart() {
        executorService.shutdown();
        executorService = Executors.newSingleThreadExecutor();
    }
}
