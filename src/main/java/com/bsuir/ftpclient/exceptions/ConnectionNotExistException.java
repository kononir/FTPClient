package com.bsuir.ftpclient.exceptions;

public class ConnectionNotExistException extends Exception {
    public ConnectionNotExistException() {
        super("Connection isn't exist!");
    }

    public ConnectionNotExistException(String message) {
        super(message);
    }

    public ConnectionNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionNotExistException(Throwable cause) {
        super("Connection isn't exist!", cause);
    }
}
