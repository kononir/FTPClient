package com.bsuir.ftpclient.connection;

import com.bsuir.ftpclient.exceptions.ConnectionNotExistException;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class ConnectionActions {
    public void sendCommand(Connection connection, String line) throws IOException, ConnectionNotExistException {
        if (connection.getSocket() == null) {
            throw new ConnectionNotExistException();
        }

        Socket socket = connection.getSocket();

        PrintStream out = new PrintStream(socket.getOutputStream());
        out.println(line);
    }
}
