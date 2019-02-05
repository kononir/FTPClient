package main.java.bsuir.ftpclient.connection;

import main.java.bsuir.ftpclient.exceptions.ConnectionExistException;
import main.java.bsuir.ftpclient.exceptions.ConnectionNotExistException;
import org.junit.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.Assert.*;

public class Connection {
    private Socket socket;

    public Socket getSocket() {
        return socket;
    }

    public String connect(String connectInform) throws IOException, ConnectionExistException {
        String answer;

        if (isClosed()) {
            socket = new Socket(connectInform, 21);

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            answer = br.readLine() + '\n';
        } else {
            throw new ConnectionExistException();
        }

        return answer;
    }

    @Test
    public void connectPositiveTest() throws IOException, ConnectionExistException {
        Connection connection = new Connection();

        String expected = "220";
        String actual = connection.connect("91.122.30.115").substring(0, 3);

        assertEquals("Positive test of 'connect' failed! Please, check your network.", expected, actual);
    }

    @Test(expected = IOException.class)
    public void connectIOException() throws IOException, ConnectionExistException {
        Connection connection = new Connection();

        connection.connect("Bad connectInform");

        fail("Negative test (Expected IOException) of 'connect' failed!");
    }

    @Test(expected = ConnectionExistException.class)
    public void connectConnectionExistException() throws IOException, ConnectionExistException {
        Connection connection = new Connection();

        connection.socket = new Socket("91.122.30.115", 21);
        connection.connect("Try to connect again");

        fail("Negative test (Expected ConnectionExistException) of 'connect' failed!");
    }

    public void disconnect() throws IOException, ConnectionNotExistException {
        if (!isClosed()) {
            socket.close();
        } else {
            throw new ConnectionNotExistException();
        }
    }

    @Test
    public void disconnectPositiveTest() throws IOException, ConnectionNotExistException {
        Connection connection = new Connection();

        connection.socket = new Socket("91.122.30.115", 21);
        connection.disconnect();

        assertTrue("Positive test of 'connect' failed!", connection.socket.isClosed());
    }

    @Test(expected = ConnectionNotExistException.class)
    public void disconnectConnectionNotExistException() throws ConnectionNotExistException, IOException {
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
    public void isClosedSocketIsClosedTest() throws IOException, ConnectionNotExistException {
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
