package com.bsuir.ftpclient.connection.ftp;

import com.bsuir.ftpclient.connection.ftp.control.exception.ControlConnectionException;
import com.bsuir.ftpclient.connection.ftp.exception.ConnectionExistException;
import com.bsuir.ftpclient.connection.ftp.exception.ConnectionNotExistException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;

public class Connection {
    private Socket socket = new Socket();
    private String hostname;

    public Socket getSocket() {
        return socket;
    }

    public void connect(String connectInformation, int port)
            throws ControlConnectionException {
        try {
            socket = new Socket(connectInformation, port);
            hostname = connectInformation;
        } catch (IOException e) {
            throw new ControlConnectionException("Socket open error!", e);
        }
    }

    public void disconnect() throws ControlConnectionException {
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

    @Test(expected = ConnectionNotExistException.class)
    public void disconnectConnectionNotExistException() throws ControlConnectionException {
        Connection connection = new Connection();

        connection.disconnect();

        Assert.fail("Negative test (Expected ConnectionNotExistException) of 'disconnect' failed!");
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    @Test
    public void isConnectedSocketNullTest() {
        Connection connection = new Connection();

        boolean isClosed = connection.isConnected();

        Assert.assertTrue("True test of 'isConnected' (only build connection) failed", isClosed);
    }

    @Test
    public void isConnectedSocketIsClosedTest() throws IOException, ControlConnectionException {
        Connection connection = new Connection();
        connection.socket = new Socket("localhost", 21);
        connection.disconnect();

        boolean isClosed = connection.isConnected();

        Assert.assertTrue("True test of 'isConnected' (build connection, connect and disconnect) failed", isClosed);
    }

    @Test
    public void isConnectedSocketIsNotClosedTest() throws IOException {
        Connection connection = new Connection();

        connection.socket = new Socket("localhost", 21);

        Assert.assertTrue("False test of 'isConnected' (build connection, connect) failed", !connection.isConnected());
    }

    @Test
    public void testConnectShouldCreateSocketAndSetHostnameWhenItIsNewConnecting()
            throws ControlConnectionException {
        Connection connection = new Connection();
        String expectedHostname = "localhost";

        connection.connect("localhost", 21);

        Assert.assertFalse("Create new connection test is failed: socket isn't created", connection.socket.isClosed());
        Assert.assertEquals("Create new connection test is failed: hostname isn't set", expectedHostname, connection.hostname);
    }

    @Test (expected = ConnectionExistException.class)
    public void testConnectShouldThrowConnectionExistExceptionWhenConnectionAlreadyExist()
            throws ControlConnectionException, IOException {
        Connection connection = new Connection();

        connection.socket = new Socket("localhost", 21);

        connection.connect("localhost", 21);

        Assert.fail("Test should throw ConnectionExistException");
    }
}
