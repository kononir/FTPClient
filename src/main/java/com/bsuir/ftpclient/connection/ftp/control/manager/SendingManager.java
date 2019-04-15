package com.bsuir.ftpclient.connection.ftp.control.manager;

import com.bsuir.ftpclient.connection.database.DatabaseConnection;
import com.bsuir.ftpclient.connection.database.exception.DatabaseConnectionException;
import com.bsuir.ftpclient.connection.ftp.Connection;
import com.bsuir.ftpclient.connection.ftp.control.ControlConnectionActions;
import com.bsuir.ftpclient.connection.ftp.control.ControlStructure;
import com.bsuir.ftpclient.connection.ftp.control.exception.ControlConnectionException;

import java.util.concurrent.*;

public class SendingManager {
    private Connection controlConnection;

    private static final int ONE_THREAD = 1;
    private ExecutorService executorService = Executors.newFixedThreadPool(ONE_THREAD);

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

                DatabaseConnection databaseConnection = new DatabaseConnection();

                String firstCode;
                do {
                    String response = connectionActions.receiveResponse();
                    firstCode = response.substring(0, 1);

                    handleAnswerCode(response);

                    ControlStructure controlStructure = new ControlStructure(request, response);
                    databaseConnection.insertControlStructure(controlStructure);

                    request = "";
                } while ("1".equals(firstCode));
            } catch (ControlConnectionException | TimeoutException
                    | InterruptedException | DatabaseConnectionException e) {
                e.printStackTrace();
            }
        }

        private void handleAnswerCode(String response)
                throws TimeoutException, InterruptedException, ControlConnectionException {
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
}
