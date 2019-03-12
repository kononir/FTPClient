package com.bsuir.ftpclient.connection.ftp;

import com.bsuir.ftpclient.connection.ftp.control.exception.ControlConnectionException;
import com.bsuir.ftpclient.connection.ftp.exception.ConnectionExistException;
import com.bsuir.ftpclient.connection.ftp.exception.ConnectionNotExistException;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class Connection {
    private Socket socket;
    private String hostname;

    public Socket getSocket() {
        return socket;
    }

    public String getHostname() {
        return hostname;
    }

    public void connect(String connectInformation, int port)
            throws ConnectionExistException, ControlConnectionException {
        if (isClosed()) {
            try {
                socket = new Socket(connectInformation, port);
            } catch (IOException e) {
                throw new ControlConnectionException("Socket open error!", e);
            }

            hostname = connectInformation;
        } else {
            throw new ConnectionExistException();
        }
    }

    public void disconnect() throws ConnectionNotExistException, ControlConnectionException {
        if (!isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                throw new ControlConnectionException("Socket close error!", e);
            }
        } else {
            throw new ConnectionNotExistException();
        }
    }

    @Test
    public void disconnectPositiveTest() throws ConnectionNotExistException, ControlConnectionException, IOException {
        Connection connection = new Connection();

        connection.socket = new Socket("91.122.30.115", 21);
        connection.disconnect();

        assertTrue("Positive test of 'connect' failed!", connection.socket.isClosed());
    }

    @Test(expected = ConnectionNotExistException.class)
    public void disconnectConnectionNotExistException() throws ConnectionNotExistException, ControlConnectionException {
        Connection connection = new Connection();

        connection.disconnect();

        fail("Negative test (Expected ConnectionNotExistException) of 'disconnect' failed!");
    }

    private boolean isClosed() {
        if (socket == null) {
            return true;
        } else {
            return socket.isClosed();
        }
    }

    @Test
    public void isClosedSocketNullTest() {
        Connection connection = new Connection();

        assertTrue("True test of 'isClosed' (only create connection) failed", connection.isClosed());
    }

    @Test
    public void isClosedSocketIsClosedTest() throws IOException, ConnectionNotExistException, ControlConnectionException {
        Connection connection = new Connection();

        connection.socket = new Socket("91.122.30.115", 21);
        connection.disconnect();

        assertTrue("True test of 'isClosed' (create connection, connect and disconnect) failed", connection.isClosed());
    }

    @Test
    public void isClosedSocketIsNotClosedTest() throws IOException {
        Connection connection = new Connection();

        connection.socket = new Socket("91.122.30.115", 21);

        assertTrue("False test of 'isClosed' (create connection, connect) failed", !connection.isClosed());
    }

}
