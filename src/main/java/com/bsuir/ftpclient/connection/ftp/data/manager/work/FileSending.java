package com.bsuir.ftpclient.connection.ftp.data.manager.work;

import com.bsuir.ftpclient.connection.ftp.Connection;

public class FileSending implements Runnable {
    private Connection dataConnection;

    public FileSending(Connection dataConnection) {
        this.dataConnection = dataConnection;
    }

    @Override
    public void run() {

    }
}
