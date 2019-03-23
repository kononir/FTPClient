package com.bsuir.ftpclient.connection.ftp.data;

import com.bsuir.ftpclient.connection.ftp.Connection;
import com.bsuir.ftpclient.connection.ftp.data.exception.DataConnectionException;
import com.bsuir.ftpclient.connection.ftp.exception.ConnectionNotExistException;

import java.io.*;
import java.net.Socket;

public class DataConnectionActions {
    private Connection dataConnection;

    public DataConnectionActions(Connection dataConnection) {
        this.dataConnection = dataConnection;
    }

    public void loadCatalogue(String toPath) throws DataConnectionException, ConnectionNotExistException {
        try {
            Socket socket = dataConnection.getSocket();

            if (socket == null) {
                throw new ConnectionNotExistException();
            }

            BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());

            File directory = new File(toPath);

            //directory.mkdir();

            //FileOutputStream outputStream = new FileOutputStream(directory);

            int currDigit;
            while ((currDigit = inputStream.read()) != -1) {
                //outputStream.write(currDigit);
                System.out.println(currDigit);
            }
        } catch (IOException e) {
            throw new DataConnectionException("Load catalogue error", e);
        }
    }

    public void loadFile(String toPath) throws ConnectionNotExistException, DataConnectionException {
        try {
            Socket socket = dataConnection.getSocket();

            if (socket == null) {
                throw new ConnectionNotExistException();
            }

            BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());

            File file = new File(toPath);

            file.createNewFile();

            FileOutputStream outputStream = new FileOutputStream(file);

            int currDigit;
            while ((currDigit = inputStream.read()) != -1) {
                outputStream.write(currDigit);
            }
        } catch (IOException e) {
            throw new DataConnectionException("Load catalogue error", e);
        }
    }
}
