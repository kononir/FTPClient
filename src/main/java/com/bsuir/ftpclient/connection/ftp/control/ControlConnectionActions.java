package com.bsuir.ftpclient.connection.ftp.control;

import com.bsuir.ftpclient.connection.ftp.Connection;
import com.bsuir.ftpclient.connection.ftp.control.exception.ControlConnectionException;
import com.bsuir.ftpclient.connection.ftp.exception.ConnectionNotExistException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ControlConnectionActions {
    private Connection controlConnection;

    public ControlConnectionActions(Connection controlConnection) {
        this.controlConnection = controlConnection;
    }

    public void sendRequest(String request)
            throws ControlConnectionException, ConnectionNotExistException {
        Socket socket = controlConnection.getSocket();

        if (socket == null) {
            throw new ConnectionNotExistException();
        }

        try {
            PrintStream output = new PrintStream(socket.getOutputStream());
            output.println(request);
        } catch (IOException e) {
            throw new ControlConnectionException("Send request error", e);
        }
    }

    public String receiveResponse() throws ConnectionNotExistException, ControlConnectionException {
        Socket socket = controlConnection.getSocket();

        if (socket == null) {
            throw new ConnectionNotExistException();
        }

        StringBuilder response;

        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            response = new StringBuilder(input.readLine());

            boolean isMultipleLine = "-".equals(response.substring(3, 4));

            if (isMultipleLine) {
                String endLine = response.substring(0, 3) + ' ';
                String currentLine;

                do {
                    currentLine = input.readLine();

                    response.append('\n').append(currentLine);
                } while (!endLine.equals(currentLine.substring(0, 4)));
            }
        } catch (IOException e) {
            throw new ControlConnectionException("Receive response error", e);
        }

        return response.toString();
    }
}
