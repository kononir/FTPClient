package com.bsuir.ftpclient.manager;

import com.bsuir.ftpclient.connection.database.DatabaseConnection;
import com.bsuir.ftpclient.connection.database.exception.ControlStructureException;
import com.bsuir.ftpclient.connection.ftp.exception.ConnectionNotExistException;
import com.bsuir.ftpclient.connection.ftp.Connection;
import com.bsuir.ftpclient.connection.ftp.control.ControlConnectionActions;
import com.bsuir.ftpclient.connection.ftp.control.ControlStructure;
import com.bsuir.ftpclient.connection.ftp.control.exception.ControlConnectionException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SendingManager {
    private static final int ONE_THREAD = 1;
    private ExecutorService executorService = Executors.newFixedThreadPool(ONE_THREAD);

    private Connection controlConnection;

    public SendingManager(Connection controlConnection) {
        this.controlConnection = controlConnection;
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

                ControlStructure controlStructure = new ControlStructure(request, response);
                DatabaseConnection databaseConnection = new DatabaseConnection();

                databaseConnection.insertControlStructure(controlStructure);
            } catch (ConnectionNotExistException | ControlConnectionException | ControlStructureException e) {
                e.printStackTrace();
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
