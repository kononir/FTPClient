package com.bsuir.ftpclient.connection.ftp;

import com.bsuir.ftpclient.connection.ftp.control.exception.ControlConnectionException;
import com.bsuir.ftpclient.connection.ftp.exception.ConnectionExistException;
import com.bsuir.ftpclient.connection.ftp.exception.ConnectionNotExistException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;

public class Connection {
    private Socket socket;
    private String hostname;

    public Socket getSocket() {
        return socket;
    }

    public void connect(String connectInformation, int port)
            throws ConnectionExistException, ControlConnectionException {
        try {
            if (!isClosed()) {
                throw new ConnectionExistException();
            }

            socket = new Socket(connectInformation, port);

            hostname = connectInformation;
        } catch (IOException e) {
            throw new ControlConnectionException("Socket open error!", e);
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

        connection.socket = new Socket("localhost", 21);
        connection.disconnect();

        Assert.assertTrue("Positive test of 'connect' failed!", connection.socket.isClosed());
    }

    @Test(expected = ConnectionNotExistException.class)
    public void disconnectConnectionNotExistException() throws ConnectionNotExistException, ControlConnectionException {
        Connection connection = new Connection();

        connection.disconnect();

        Assert.fail("Negative test (Expected ConnectionNotExistException) of 'disconnect' failed!");
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

        boolean isClosed = connection.isClosed();

        Assert.assertTrue("True test of 'isClosed' (only build connection) failed", isClosed);
    }

    @Test
    public void isClosedSocketIsClosedTest() throws IOException, ConnectionNotExistException, ControlConnectionException {
        Connection connection = new Connection();
        connection.socket = new Socket("localhost", 21);
        connection.disconnect();

        boolean isClosed = connection.isClosed();

        Assert.assertTrue("True test of 'isClosed' (build connection, connect and disconnect) failed", isClosed);
    }

    @Test
    public void isClosedSocketIsNotClosedTest() throws IOException {
        Connection connection = new Connection();

        connection.socket = new Socket("localhost", 21);

        Assert.assertTrue("False test of 'isClosed' (build connection, connect) failed", !connection.isClosed());
    }

    @Test
    public void testConnectShouldCreateSocketAndSetHostnameWhenItIsNewConnecting()
            throws ConnectionExistException, ControlConnectionException {
        Connection connection = new Connection();
        String expectedHostname = "localhost";

        connection.connect("localhost", 21);

        Assert.assertFalse("Create new connection test is failed: socket isn't created", connection.socket.isClosed());
        Assert.assertEquals("Create new connection test is failed: hostname isn't set", expectedHostname, connection.hostname);
    }

    @Test (expected = ConnectionExistException.class)
    public void testConnectShouldThrowConnectionExistExceptionWhenConnectionAlreadyExist()
            throws ConnectionExistException, ControlConnectionException, IOException {
        Connection connection = new Connection();

        connection.socket = new Socket("localhost", 21);

        connection.connect("localhost", 21);

        Assert.fail("Test should throw ConnectionExistException");
    }
}
