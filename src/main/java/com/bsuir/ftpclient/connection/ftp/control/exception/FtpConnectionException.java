package com.bsuir.ftpclient.connection.ftp.control.exception;

public class FtpConnectionException extends Exception {

    public FtpConnectionException(String message) {
        super(message);
    }

    public FtpConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
