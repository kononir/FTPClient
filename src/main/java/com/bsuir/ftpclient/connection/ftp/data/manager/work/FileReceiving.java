package com.bsuir.ftpclient.connection.ftp.data.manager.work;

import com.bsuir.ftpclient.connection.ftp.Connection;
import com.bsuir.ftpclient.connection.ftp.control.exception.FtpConnectionException;
import com.bsuir.ftpclient.util.IOActions;
import org.apache.log4j.Logger;

import java.io.*;

public class FileReceiving implements Runnable {
    private static final Logger LOGGER = Logger.getLogger("dataWorkLogger");

    private Connection dataConnection;
    private String toPath;

    public FileReceiving(Connection dataConnection, String toPath) {
        this.dataConnection = dataConnection;
        this.toPath = toPath;
    }

    @Override
    public void run() {
        try {
            BufferedInputStream inputStream = new BufferedInputStream(dataConnection.getInputStream());

            File file = new File(toPath);
            file.createNewFile();
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));

            IOActions actions = new IOActions();
            actions.writeToStream(inputStream, outputStream);
        } catch (FtpConnectionException | IOException e) {
            LOGGER.error("File receiving error.", e);
        }
    }
}
