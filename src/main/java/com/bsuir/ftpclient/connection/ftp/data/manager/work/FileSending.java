package com.bsuir.ftpclient.connection.ftp.data.manager.work;

import com.bsuir.ftpclient.connection.ftp.data.DataConnectionActions;
import com.bsuir.ftpclient.connection.ftp.data.exception.DataConnectionException;
import org.apache.log4j.Logger;

public class FileSending implements Runnable {
    private static final Logger LOGGER = Logger.getLogger("dataWorkLogger");

    private DataConnectionActions actions;
    private String fromFilePath;

    public FileSending(DataConnectionActions actions, String fromFilePath) {
        this.actions = actions;
        this.fromFilePath = fromFilePath;
    }

    @Override
    public void run() {
        try {
            actions.saveFile(fromFilePath);
        } catch (DataConnectionException e) {
            LOGGER.error("File sending error.", e);
        }
    }
}
