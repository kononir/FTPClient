package main.java.bsuir.ftpclient.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ConnectionActions {
    public String authenticate(Connection connection, String login, String password) throws IOException {
        Socket socket = connection.getSocket();

        return sendCommand(socket, "USER " + login) + "\n"
                + sendCommand(socket, "PASS " + password);
    }

    private String sendCommand(Socket socket, String line) throws IOException {
        PrintStream out = new PrintStream(socket.getOutputStream());
        out.println(line);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return in.readLine();
    }
}
