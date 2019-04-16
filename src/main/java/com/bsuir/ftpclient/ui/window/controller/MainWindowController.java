package com.bsuir.ftpclient.ui.window.controller;

import com.bsuir.ftpclient.connection.ftp.Connection;
import com.bsuir.ftpclient.connection.ftp.control.exception.ControlConnectionException;
import com.bsuir.ftpclient.connection.ftp.control.manager.SendingManager;
import com.bsuir.ftpclient.connection.ftp.data.file.ServerFile;
import com.bsuir.ftpclient.connection.ftp.data.file.parser.FileNamesParser;
import com.bsuir.ftpclient.connection.ftp.data.file.parser.NLSTParser;
import com.bsuir.ftpclient.connection.ftp.data.manager.DataManager;
import com.bsuir.ftpclient.connection.ftp.data.manager.work.FileListReceiving;
import com.bsuir.ftpclient.connection.ftp.data.manager.work.FileReceiving;
import com.bsuir.ftpclient.ui.window.controller.exception.MainControllerException;
import javafx.util.Pair;

import java.io.File;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainWindowController {
    private static final int CONTROL_PORT = 21;

    private Connection controlConnection = new Connection();

    private static final int TIMEOUT = 5000;
    private Exchanger<String> responseExchanger = new Exchanger<>();

    private SendingManager sendingManager = new SendingManager(controlConnection, responseExchanger);
    private DataManager dataManager = new DataManager();

    public void controlConnecting(String connectInformation) throws MainControllerException {
        try {
            controlConnection.connect(connectInformation, CONTROL_PORT);
            String connectCommand = "";
            sendingManager.send(connectCommand);
        } catch (ControlConnectionException e) {
            throw new MainControllerException("Error when connect", e);
        }

    }

    public void controlDisconnecting() {
        String exitCommand = "QUIT";
        sendingManager.send(exitCommand);
    }

    public void controlAuthenticating(Pair<String, String> authenticationPair) {
        String loginCommand = "USER " + authenticationPair.getKey();
        sendingManager.send(loginCommand);

        String passwordCommand = "PASS " + authenticationPair.getValue();
        sendingManager.send(passwordCommand);
    }

    public void controlCreatingCatalogue(String catalogueName) {
        String catalogueCreatingCommand = "MKD " + catalogueName;
        sendingManager.send(catalogueCreatingCommand);
    }

    public void controlDeletingCatalogue(String catalogueName) {
        String catalogueDeletingCommand = "RMD " + catalogueName;
        sendingManager.send(catalogueDeletingCommand);
    }

    public void controlLoadingCatalogue(String fromDirectoryPath, String toDirectoryPath)
            throws MainControllerException {
        File file = new File(toDirectoryPath);
        if (!file.exists()) {
            file.mkdir();
        }

        List<ServerFile> files = controlLoadingFileList(fromDirectoryPath);
        for (ServerFile serverFile : files) {
            if (serverFile.isDirectory()) {
                String dirName = serverFile.getName();

                String newFromPath;
                if ("/".equals(fromDirectoryPath)) {
                    newFromPath = fromDirectoryPath + dirName;
                } else {
                    newFromPath = fromDirectoryPath + "/" + dirName;
                }

                String newToPath = toDirectoryPath + "/" + dirName;
                controlLoadingCatalogue(newFromPath, newToPath);
            } else {
                String catalogueLoadingCommand = "RETR " + fromDirectoryPath;
                sendingManager.send(catalogueLoadingCommand);

                controlLoadingFile(serverFile.getName(), toDirectoryPath);
            }
        }
    }

    public void controlLoadingFile(String fileName, String toDirectoryPath) throws MainControllerException {
        Connection dataConnection = establishDataConnection();

        String catalogueLoadingCommand = "RETR " + fileName;
        sendingManager.send(catalogueLoadingCommand);

        FileReceiving fileReceiving = new FileReceiving(dataConnection, toDirectoryPath + '/' + fileName);
        dataManager.manageWork(fileReceiving);
    }

    private Connection establishDataConnection() throws MainControllerException {
        String passiveModeCommand = "PASV";
        sendingManager.send(passiveModeCommand);

        Connection dataConnection = new Connection();
        try {
            String response = responseExchanger.exchange(null, TIMEOUT, TimeUnit.MILLISECONDS);

            Pattern pattern = Pattern.compile("(\\d)+(,(\\d)+){5}");
            Matcher matcher = pattern.matcher(response);

            matcher.find();
            String addressDigitsStr = matcher.group();

            String[] addressDigits = addressDigitsStr.split(",");

            String ipAddress = addressDigits[0] + '.' + addressDigits[1] + '.' + addressDigits[2] + '.' + addressDigits[3];
            int port = Integer.parseInt(addressDigits[4]) * 256 + Integer.parseInt(addressDigits[5]);

            dataConnection.connect(ipAddress, port);
        } catch (InterruptedException | TimeoutException | ControlConnectionException e) {
            throw new MainControllerException("Error when establish data connection", e);
        }

        return dataConnection;
    }

    public void controlClose() {
        sendingManager.killAllSenders();
        dataManager.shutdown();
    }

    public List<ServerFile> controlLoadingFileList(String directoryName)
            throws MainControllerException {
        List<ServerFile> fileComponents;
        try {
            Connection dataConnection = establishDataConnection();
            String fileListLoadingCommand = "NLST " + directoryName; // or MLSD/LIST/NLST (another format of request - another parser)
            sendingManager.send(fileListLoadingCommand);

            Exchanger<List<ServerFile>> listExchanger = new Exchanger<>();
            FileNamesParser parser = new NLSTParser();
            FileListReceiving fileListReceiving = new FileListReceiving(dataConnection, parser, listExchanger);
            dataManager.manageWork(fileListReceiving);

            fileComponents = listExchanger.exchange(null, TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | TimeoutException e) {
            throw new MainControllerException("Error when getting file list.", e);
        }
        return fileComponents;
    }

    public void changeWorkingDirectory(String directoryName) throws MainControllerException {
        if (directoryName == null) {
            throw new MainControllerException("Null directory name.");
        }

        String changingDirectoryCommand = "CWD " + directoryName;
        sendingManager.send(changingDirectoryCommand);
    }

    public void changeWorkingDirectoryToParent() {
        String changingDirectoryCommand = "CDUP";
        sendingManager.send(changingDirectoryCommand);
    }
}
