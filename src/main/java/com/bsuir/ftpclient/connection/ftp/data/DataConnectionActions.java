package com.bsuir.ftpclient.connection.ftp.data;

import com.bsuir.ftpclient.connection.ftp.Connection;
import com.bsuir.ftpclient.connection.ftp.data.exception.DataConnectionException;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class DataConnectionActions {
    private Connection dataConnection;

    public DataConnectionActions(Connection dataConnection) {
        this.dataConnection = dataConnection;
    }

    public void saveFile(String fromFilePath) throws DataConnectionException {
        try {
            if (dataConnection.isClosed()) {
                throw new DataConnectionException("Connection doesn't exist");
            }

            File file = new File(fromFilePath);
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));

            Socket socket = dataConnection.getSocket();
            BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());

            writeToStream(inputStream, outputStream);
        } catch (IOException e) {
            throw new DataConnectionException("Save file error", e);
        }
    }

    public void loadFile(String toPath) throws DataConnectionException {
        try {
            if (dataConnection.isClosed()) {
                throw new DataConnectionException("Connection doesn't exist");
            }

            Socket socket = dataConnection.getSocket();
            BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());

            File file = new File(toPath);
            file.createNewFile();
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));

            writeToStream(inputStream, outputStream);
        } catch (IOException e) {
            throw new DataConnectionException("Load catalogue error", e);
        }
    }

    private void writeToStream(BufferedInputStream inputStream, BufferedOutputStream outputStream)
            throws IOException {
        int currDigit;
        while ((currDigit = inputStream.read()) != -1) {
            outputStream.write(currDigit);
        }

        outputStream.flush();
    }

    public List<String> loadFileList() throws DataConnectionException {
        List<String> fileList = new ArrayList<>();

        try {
            if (dataConnection.isClosed()) {
                throw new DataConnectionException("Connection doesn't exist");
            }

            Socket socket = dataConnection.getSocket();
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String fileListLine;
            while ((fileListLine = inputStream.readLine()) != null) {
                fileList.add(fileListLine);
            }

            System.out.println(fileList.toString());
        } catch (IOException e) {
            throw new DataConnectionException("Load file list error", e);
        }

        return fileList;
    }
}
