package com.bsuir.ftpclient.connection.ftp.data.exception;

public class DataConnectionException extends Exception {

    public DataConnectionException(String message) {
        super(message);
    }

    public DataConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
