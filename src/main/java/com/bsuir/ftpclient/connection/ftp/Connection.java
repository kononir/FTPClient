package com.bsuir.ftpclient.connection.ftp;

import com.bsuir.ftpclient.connection.ftp.control.exception.FtpConnectionException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Connection {
    private Socket socket;

    //----- For tests -----
    public String getHostAddress() {
        return socket.getInetAddress().getHostAddress();
    }

    public int getPort() {
        return socket.getPort();
    }
    //---------------------

    public InputStream getInputStream() throws FtpConnectionException {
        if (isClosed()) {
            throw new FtpConnectionException("Connection is not established!");
        }

        try {
            return socket.getInputStream();
        } catch (IOException e) {
            throw new FtpConnectionException("Problems with connection input!", e);
        }
    }

    public OutputStream getOutputStream() throws FtpConnectionException {
        if (isClosed()) {
            throw new FtpConnectionException("Connection is not established!");
        }

        try {
            return socket.getOutputStream();
        } catch (IOException e) {
            throw new FtpConnectionException("Problems with connection output!", e);
        }
    }

    public Void connect(String connectInformation, int port)
            throws FtpConnectionException {
        if (!isClosed()) {
            throw new FtpConnectionException("Connection is already established!");
        }

        try {
            socket = new Socket(connectInformation, port);
        } catch (IOException e) {
            throw new FtpConnectionException("Connection open error!", e);
        }

        return null;
    }

    public void disconnect() throws FtpConnectionException {
        if (isClosed()) {
            throw new FtpConnectionException("Connection is not established!");
        }

        try {
            socket.close();
        } catch (IOException e) {
            throw new FtpConnectionException("Connection close error!", e);
        }
    }

    @Test
    public void disconnectPositiveTest() throws FtpConnectionException, IOException {
        Connection connection = new Connection();

        connection.socket = new Socket("localhost", 21);
        connection.disconnect();

        Assert.assertTrue("Positive test of 'connect' failed!", connection.socket.isClosed());
    }

    @Test(expected = FtpConnectionException.class)
    public void disconnectConnectionNotExistException() throws FtpConnectionException {
        Connection connection = new Connection();

        connection.disconnect();

        Assert.fail("Negative test (Expected FtpConnectionException) of 'disconnect' failed!");
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
    public void testConnectShouldCreateSocketWhenItIsNewConnecting()
            throws FtpConnectionException {
        Connection connection = new Connection();

        connection.connect("localhost", 21);

        Assert.assertFalse("Create new connection test is failed: socket isn't created", connection.socket.isClosed());
    }

    @Test (expected = FtpConnectionException.class)
    public void testConnectShouldThrowConnectionExistExceptionWhenConnectionAlreadyExist()
            throws FtpConnectionException, IOException {
        Connection connection = new Connection();

        connection.socket = new Socket("localhost", 21);

        connection.connect("localhost", 21);

        Assert.fail("Test should throw FtpConnectionException");
    }
}
