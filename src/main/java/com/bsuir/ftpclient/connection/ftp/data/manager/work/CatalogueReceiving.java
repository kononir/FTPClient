package com.bsuir.ftpclient.connection.ftp.data.manager.work;

import com.bsuir.ftpclient.connection.ftp.Connection;
import com.bsuir.ftpclient.connection.ftp.data.DataConnectionActions;
import com.bsuir.ftpclient.connection.ftp.data.exception.DataConnectionException;
import com.bsuir.ftpclient.connection.ftp.exception.ConnectionNotExistException;

public class CatalogueReceiving implements Runnable {
    private Connection dataConnection;
    private String toPath;

    public CatalogueReceiving(Connection dataConnection, String toPath) {
        this.dataConnection = dataConnection;
        this.toPath = toPath;
    }

    @Override
    public void run() {
        DataConnectionActions actions = new DataConnectionActions(dataConnection);
        actions.loadCatalogue(toPath);
    }
}
