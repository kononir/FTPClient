package com.bsuir.ftpclient.connection.ftp.data.manager.work;

import com.bsuir.ftpclient.connection.ftp.Connection;
import com.bsuir.ftpclient.connection.ftp.control.exception.FtpConnectionException;
import com.bsuir.ftpclient.util.IOActions;
import org.apache.log4j.Logger;

import java.io.*;

public class FileSending implements Runnable {
    private static final Logger LOGGER = Logger.getLogger("dataWorkLogger");

    private Connection dataConnection;
    private String fromFilePath;

    public FileSending(Connection dataConnection, String fromFilePath) {
        this.dataConnection = dataConnection;
        this.fromFilePath = fromFilePath;
    }

    @Override
    public void run() {
        try {
            File file = new File(fromFilePath);
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));

            BufferedOutputStream outputStream = new BufferedOutputStream(dataConnection.getOutputStream());

            IOActions actions = new IOActions();
            actions.writeToStream(inputStream, outputStream);
        } catch (FtpConnectionException | IOException e) {
            LOGGER.error("File sending error.", e);
        }
    }
}
