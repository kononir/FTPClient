package com.bsuir.ftpclient.connection.ftp;

import com.bsuir.ftpclient.connection.ftp.control.exception.ControlConnectionException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;

public class Connection {
    private static final int TIMEOUT = 10000;

    private Socket socket;

    public Socket getSocket() {
        return socket;
    }

    public void connect(String connectInformation, int port)
            throws ControlConnectionException {
        if (socket != null && !socket.isClosed()) {
            throw new ControlConnectionException("Connection is already established!");
        }

        try {
            socket = new Socket(connectInformation, port);
            socket.setSoTimeout(TIMEOUT);
        } catch (IOException e) {
            throw new ControlConnectionException("Socket open error!", e);
        }
    }

    public void disconnect() throws ControlConnectionException {
        if (socket == null || socket.isClosed()) {
            throw new ControlConnectionException("Connection is not established!");
        }

        try {
            socket.close();
        } catch (IOException e) {
            throw new ControlConnectionException("Socket close error!", e);
        }
    }

    @Test
    public void disconnectPositiveTest() throws ControlConnectionException, IOException {
        Connection connection = new Connection();

        connection.socket = new Socket("localhost", 21);
        connection.disconnect();

        Assert.assertTrue("Positive test of 'connect' failed!", connection.socket.isClosed());
    }

    @Test(expected = ControlConnectionException.class)
    public void disconnectConnectionNotExistException() throws ControlConnectionException {
        Connection connection = new Connection();

        connection.disconnect();

        Assert.fail("Negative test (Expected ControlConnectionException) of 'disconnect' failed!");
    }

    public boolean isClosed() {
        return socket == null || socket.isClosed();
    }

    @Test
    public void isClosedSocketIsClosedTest() throws IOException {
        Connection connection = new Connection();
        connection.socket = new Socket("localhost", 21);
        connection.socket.close();

        boolean isClosed = connection.isClosed();

        Assert.assertTrue("True test of 'isClosed' (build connection, connect and disconnect) failed", isClosed);
    }

    @Test
    public void isClosedSocketIsNotClosedTest() throws IOException {
        Connection connection = new Connection();
        connection.socket = new Socket("localhost", 21);

        boolean isClosed = connection.isClosed();

        Assert.assertFalse("False test of 'isClosed' (build connection, connect) failed", isClosed);
    }

    @Test
    public void testConnectShouldCreateSocketAndSetHostnameWhenItIsNewConnecting()
            throws ControlConnectionException {
        Connection connection = new Connection();
        String expectedHostname = "localhost";

        connection.connect("localhost", 21);

        Assert.assertFalse("Create new connection test is failed: socket isn't created", connection.socket.isClosed());
    }

    @Test (expected = ControlConnectionException.class)
    public void testConnectShouldThrowConnectionExistExceptionWhenConnectionAlreadyExist()
            throws ControlConnectionException, IOException {
        Connection connection = new Connection();

        connection.socket = new Socket("localhost", 21);

        connection.connect("localhost", 21);

        Assert.fail("Test should throw ControlConnectionException");
    }
}
