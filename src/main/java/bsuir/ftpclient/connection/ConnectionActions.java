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
import static org.mockito.Mockito.*;

public class ConnectionActions {
    public String authenticate(Connection connection, String login, String password) throws IOException {
        Socket socket = connection.getSocket();

        return sendCommand(socket, "USER " + login) + "\n"
                + sendCommand(socket, "PASS " + password);
    }

    @Test
    public void authenticateTest() throws IOException {
        Connection connection = mock(Connection.class);

        Answer<Socket> answer = (invocationOnMock -> {
            Socket socket = new Socket("91.122.30.115", 21);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            in.readLine();

            return socket;
        });
        when(connection.getSocket()).thenAnswer(answer);

        ConnectionActions actions = new ConnectionActions();

        String expected = "331";
        String actual = actions
                .authenticate(connection, "USER anonymous", "PASS root@example.com")
                .substring(0, 3);

        assertEquals("Positive test of 'authenticate' failed! Please, check your network.", expected, actual);
    }

    private String sendCommand(Socket socket, String line) throws IOException {
        PrintStream out = new PrintStream(socket.getOutputStream());
        out.println(line);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return in.readLine();
    }

    @Test
    public void sendCommandPositiveTest() throws IOException {
        ConnectionActions actions = new ConnectionActions();
        Socket socket = new Socket("91.122.30.115", 21);

        String expected = "220";
        String actual = actions.sendCommand(socket, "USER anonymous").substring(0, 3);

        assertEquals("Positive test of 'sendCommand' failed! Please, check your network.", expected, actual);
    }

    @Test(expected = IOException.class)
    public void sendCommandIOException() throws IOException {
        ConnectionActions actions = new ConnectionActions();

        Socket socket = new Socket("91.122.30.115", 21);
        socket.close();

        actions.sendCommand(socket, "Socket is closed");

        fail("Negative test (Expected IOException) of 'sendCommand' failed!");
    }
}
