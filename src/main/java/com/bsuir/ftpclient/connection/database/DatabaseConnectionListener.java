package com.bsuir.ftpclient.connection.database;

import com.bsuir.ftpclient.connection.database.exception.DatabaseConnectionException;
import com.bsuir.ftpclient.connection.ftp.control.ControlStructure;

import java.util.List;
import java.util.concurrent.Exchanger;

public class DatabaseConnectionListener implements Runnable {

    private Exchanger<List<ControlStructure>> exchanger;
    private DatabaseConnection connection = new DatabaseConnection();

    public DatabaseConnectionListener(Exchanger<List<ControlStructure>> exchanger) {
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        try {
            List<ControlStructure> controlStructures = connection.selectControlStructures();

            if (!controlStructures.isEmpty()) {
                exchanger.exchange(controlStructures);
            }
        } catch (DatabaseConnectionException e) {
            e.printStackTrace();
        } catch (InterruptedException ignored) {
        }
    }
}
