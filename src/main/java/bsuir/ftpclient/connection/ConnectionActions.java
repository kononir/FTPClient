package main.java.bsuir.ftpclient.connection;

import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConnectionActions {

    public void sendCommand(Connection connection, String line) throws IOException {
        Socket socket = connection.getSocket();

        PrintStream out = new PrintStream(socket.getOutputStream());
        out.println(line);
    }
/*
    @Test
    public void sendCommandPositiveTest() throws IOException {
        ConnectionActions actions = new ConnectionActions();

        Connection connectionMock = mock(Connection.class);
        when(connectionMock.getSocket()).thenReturn(new Socket("91.122.30.115", 21));

        String expected = "220";
        String actual = actions.sendCommand(connectionMock, "USER anonymous").substring(0, 3);

        assertEquals("Positive test of 'sendCommand' failed!", expected, actual);
    }

    @Test(expected = IOException.class)
    public void sendCommandIOException() throws IOException {
        ConnectionActions actions = new ConnectionActions();

        Answer<Socket> answer = invocationOnMock -> {
            Socket socket = new Socket("91.122.30.115", 21);
            socket.close();

            return socket;
        };

        Connection connectionMock = mock(Connection.class);
        when(connectionMock.getSocket()).thenAnswer(answer);

        actions.sendCommand(connectionMock, "Socket is closed");

        fail("Negative test (Expected IOException) of 'sendCommand' failed!");
    }
*/
}
