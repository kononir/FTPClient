package com.bsuir.ftpclient.connection.ftp.data.manager.work;

import com.bsuir.ftpclient.connection.ftp.Connection;
import com.bsuir.ftpclient.connection.ftp.control.exception.FtpConnectionException;
import com.bsuir.ftpclient.connection.ftp.data.file.ServerFile;
import com.bsuir.ftpclient.connection.ftp.data.file.parser.FileNameParser;
import com.bsuir.ftpclient.util.IOActions;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FileListReceiving implements Runnable {
    private static final Logger LOGGER = Logger.getLogger("dataWorkLogger");

    private Connection dataConnection;

    private FileNameParser parser;

    private Exchanger<List<ServerFile>> exchanger;
    private static final int TIMEOUT = 100;

    public FileListReceiving(Connection dataConnection, FileNameParser parser, Exchanger<List<ServerFile>> exchanger) {
        this.dataConnection = dataConnection;
        this.parser = parser;
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        try {
            IOActions actions = new IOActions();
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(dataConnection.getInputStream()));
            List<String> filesInfo = actions.readListByReader(inputStream);

            List<ServerFile> serverFiles = new ArrayList<>();
            for (String fileInfo : filesInfo) {
                 ServerFile serverFile = parser.parse(fileInfo);
                 serverFiles.add(serverFile);
            }

            exchanger.exchange(serverFiles, TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (FtpConnectionException | InterruptedException | TimeoutException | IOException e) {
            LOGGER.error("File list receiving error.", e);
        }
    }
}
