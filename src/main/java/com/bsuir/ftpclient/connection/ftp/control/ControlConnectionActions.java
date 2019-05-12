package com.bsuir.ftpclient.connection.ftp.control;

import com.bsuir.ftpclient.connection.ftp.Connection;
import com.bsuir.ftpclient.connection.ftp.control.exception.FtpConnectionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class ControlConnectionActions {
    private static final String SIGN_OF_MULTILINE_RESPONSE = "-";
    private static final String SIGN_OF_ENDING_MULTILINE_RESPONSE = " ";
    private static final String SIGN_OF_EXPECTING_ONE_MORE_COMMAND = "1";

    private Connection controlConnection;

    public ControlConnectionActions(Connection controlConnection) {
        this.controlConnection = controlConnection;
    }

    public void sendRequest(String request) throws FtpConnectionException {
        PrintStream output = new PrintStream(controlConnection.getOutputStream());
        output.println(request);
    }

    public String receiveResponse() throws FtpConnectionException {
        StringBuilder fullResponse = new StringBuilder();

        try {
            String firstLineOfCurrentResponsePart;
            BufferedReader input = new BufferedReader(new InputStreamReader(controlConnection.getInputStream()));
            do {
                firstLineOfCurrentResponsePart = input.readLine();
                fullResponse.append(firstLineOfCurrentResponsePart);

                if (SIGN_OF_MULTILINE_RESPONSE.equals(fullResponse.substring(3, 4))) {
                    addOtherLinesOfMultipleLineResponse(input, fullResponse);
                }

                fullResponse.append("\n");
            } while (SIGN_OF_EXPECTING_ONE_MORE_COMMAND.equals(firstLineOfCurrentResponsePart.substring(0, 1)));
        } catch (IOException e) {
            throw new FtpConnectionException("Receive response error", e);
        }

        return fullResponse.toString();
    }

    private void addOtherLinesOfMultipleLineResponse(BufferedReader input, StringBuilder fullResponse)
            throws IOException {
        String currentLine;
        do {
            currentLine = input.readLine();
            fullResponse.append('\n').append(currentLine);
        } while (!SIGN_OF_ENDING_MULTILINE_RESPONSE.equals(currentLine.substring(3, 4)));
    }
}
