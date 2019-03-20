package com.bsuir.ftpclient.connection.ftp.data.manager.work;

import com.bsuir.ftpclient.connection.ftp.Connection;

public class CatalogueSending implements Runnable {
    private Connection dataConnection;

    public CatalogueSending(Connection dataConnection) {
        this.dataConnection = dataConnection;
    }

    @Override
    public void run() {

    }
}
