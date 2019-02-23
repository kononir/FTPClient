package com.bsuir.ftpclient.exceptions;

public class ConnectionExistException extends Exception {
    public ConnectionExistException() {
        super("Connection is already exist!");
    }

    public ConnectionExistException(String message) {
        super(message);
    }

    public ConnectionExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionExistException(Throwable cause) {
        super("Connection is already exist!", cause);
    }
}
