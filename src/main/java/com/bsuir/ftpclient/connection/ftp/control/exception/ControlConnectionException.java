package com.bsuir.ftpclient.connection.ftp.control.exception;

public class ControlConnectionException extends Exception {

    public ControlConnectionException(String message) {
        super(message);
    }

    public ControlConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
