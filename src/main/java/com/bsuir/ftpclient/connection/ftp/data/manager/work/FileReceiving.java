package com.bsuir.ftpclient.connection.ftp.data.manager.work;

import com.bsuir.ftpclient.connection.ftp.data.DataConnectionActions;
import com.bsuir.ftpclient.connection.ftp.data.exception.DataConnectionException;
import org.apache.log4j.Logger;

public class FileReceiving implements Runnable {
    private static final Logger LOGGER = Logger.getLogger("dataWorkLogger");

    private DataConnectionActions actions;
    private String toPath;

    public FileReceiving(DataConnectionActions actions, String toPath) {
        this.actions = actions;
        this.toPath = toPath;
    }

    @Override
    public void run() {
        try {
            actions.loadFile(toPath);
        } catch (DataConnectionException e) {
            LOGGER.error("File receiving error.", e);
        }
    }
}
