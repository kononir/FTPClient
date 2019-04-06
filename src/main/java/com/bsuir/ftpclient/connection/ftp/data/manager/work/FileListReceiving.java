package com.bsuir.ftpclient.connection.ftp.data.manager.work;

import com.bsuir.ftpclient.connection.ftp.Connection;
import com.bsuir.ftpclient.connection.ftp.data.DataConnectionActions;
import com.bsuir.ftpclient.connection.ftp.data.exception.DataConnectionException;
import com.bsuir.ftpclient.connection.ftp.data.file.FileComponent;
import com.bsuir.ftpclient.connection.ftp.data.file.parser.FileNameParser;
import com.bsuir.ftpclient.connection.ftp.exception.ConnectionNotExistException;

import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FileListReceiving implements Runnable {
    private Connection dataConnection;

    private Exchanger<List<FileComponent>> exchanger;
    private static final int TIMEOUT = 10;

    public FileListReceiving(Connection dataConnection, Exchanger<List<FileComponent>> exchanger) {
        this.dataConnection = dataConnection;
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        try {
            DataConnectionActions actions = new DataConnectionActions(dataConnection);
            List<String> filesInfo = actions.loadFileList();

            FileNameParser parser = new FileNameParser();
            List<FileComponent> fileComponents = parser.parse(filesInfo);

            exchanger.exchange(fileComponents, TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (ConnectionNotExistException | DataConnectionException
                | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
