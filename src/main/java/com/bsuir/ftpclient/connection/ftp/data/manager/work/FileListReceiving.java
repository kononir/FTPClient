package com.bsuir.ftpclient.connection.ftp.data.manager.work;

import com.bsuir.ftpclient.connection.ftp.Connection;
import com.bsuir.ftpclient.connection.ftp.data.DataConnectionActions;
import com.bsuir.ftpclient.connection.ftp.data.exception.DataConnectionException;
import com.bsuir.ftpclient.connection.ftp.data.file.ServerFile;
import com.bsuir.ftpclient.connection.ftp.data.file.parser.FileNamesParser;
import com.bsuir.ftpclient.connection.ftp.exception.ConnectionNotExistException;

import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FileListReceiving implements Runnable {
    private Connection dataConnection;

    private FileNamesParser parser;

    private Exchanger<List<ServerFile>> exchanger;
    private static final int TIMEOUT = 100;

    public FileListReceiving(Connection dataConnection, FileNamesParser parser, Exchanger<List<ServerFile>> exchanger) {
        this.dataConnection = dataConnection;
        this.parser = parser;
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        try {
            DataConnectionActions actions = new DataConnectionActions(dataConnection);
            List<String> filesInfo = actions.loadFileList();

            List<ServerFile> fileComponents = parser.parse(filesInfo);
            exchanger.exchange(fileComponents, TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (ConnectionNotExistException | DataConnectionException
                | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
