package com.bsuir.ftpclient.connection.ftp.control;

import com.bsuir.ftpclient.connection.ftp.Connection;
import com.bsuir.ftpclient.connection.ftp.control.exception.FtpConnectionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class ControlConnectionActions {
    private Connection controlConnection;

    public ControlConnectionActions(Connection controlConnection) {
        this.controlConnection = controlConnection;
    }

    public void sendRequest(String request) throws FtpConnectionException {
        if (controlConnection.isClosed()) {
            throw new FtpConnectionException("Connection doesn't exist");
        }

        PrintStream output = new PrintStream(controlConnection.getOutputStream());
        output.println(request);
    }

    public String receiveResponse() throws FtpConnectionException {
        if (controlConnection.isClosed()) {
            throw new FtpConnectionException("Connection doesn't exist");
        }

        StringBuilder response;

        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(controlConnection.getInputStream()));
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
            throw new FtpConnectionException("Receive response error", e);
        }

        return response.toString();
    }
}
