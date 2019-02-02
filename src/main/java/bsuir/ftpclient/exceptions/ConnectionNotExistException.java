package main.java.bsuir.ftpclient.exceptions;

public class ConnectionNotExistException extends Exception {
    public ConnectionNotExistException() {
    }

    public ConnectionNotExistException(String message) {
        super(message);
    }

    public ConnectionNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionNotExistException(Throwable cause) {
        super(cause);
    }
}
