package com.bsuir.ftpclient.ui.window.controller;

import com.bsuir.ftpclient.connection.ftp.Connection;
import com.bsuir.ftpclient.connection.ftp.control.exception.ControlConnectionException;
import com.bsuir.ftpclient.connection.ftp.control.manager.SendingManager;
import com.bsuir.ftpclient.connection.ftp.data.file.FileComponent;
import com.bsuir.ftpclient.connection.ftp.data.file.impl.AbstractFileComponent;
import com.bsuir.ftpclient.connection.ftp.data.manager.DataManager;
import com.bsuir.ftpclient.connection.ftp.data.manager.work.CatalogueReceiving;
import com.bsuir.ftpclient.connection.ftp.data.manager.work.FileListReceiving;
import com.bsuir.ftpclient.connection.ftp.data.manager.work.FileReceiving;
import com.bsuir.ftpclient.connection.ftp.exception.ConnectionExistException;
import com.bsuir.ftpclient.connection.ftp.exception.ConnectionNotExistException;
import com.bsuir.ftpclient.ui.window.controller.exception.MainControllerException;
import javafx.util.Pair;

import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainWindowController {
    private static final int CONTROL_PORT = 21;

    private Connection controlConnection = new Connection();

    private static final int TIMEOUT = 5;
    private Exchanger<String> responseExchanger = new Exchanger<>();
    private SendingManager sendingManager = new SendingManager(controlConnection, responseExchanger);

    public void controlConnecting(String connectInformation)
            throws ConnectionExistException, ControlConnectionException {
        controlConnection.connect(connectInformation, CONTROL_PORT);

        String connectCommand = "";
        sendingManager.send(connectCommand);
    }

    public void controlDisconnecting()
            throws ConnectionNotExistException, ControlConnectionException {
        controlConnection.disconnect();
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

    public void controlLoadingCatalogue(String fromPath, String toPath)
            throws ConnectionExistException, ControlConnectionException, MainControllerException {
        Connection dataConnection = establishDataConnection();

        String catalogueLoadingCommand = "RETR " + fromPath;
        sendingManager.send(catalogueLoadingCommand);

        CatalogueReceiving catalogueReceiving = new CatalogueReceiving(dataConnection, toPath);
        DataManager dataManager = new DataManager();
        dataManager.manageWork(catalogueReceiving);
    }

    public void controlLoadingFile(String fileName, String toDirectoryPath)
            throws ConnectionExistException, ControlConnectionException, MainControllerException {
        Connection dataConnection = establishDataConnection();

        String catalogueLoadingCommand = "RETR " + fileName;
        sendingManager.send(catalogueLoadingCommand);

        FileReceiving fileReceiving = new FileReceiving(dataConnection, toDirectoryPath + '/' + fileName);
        DataManager dataManager = new DataManager();
        dataManager.manageWork(fileReceiving);
    }

    private Connection establishDataConnection()
            throws ConnectionExistException, ControlConnectionException, MainControllerException {
        String passiveModeCommand = "PASV";
        sendingManager.send(passiveModeCommand);

        Connection dataConnection = new Connection();
        try {
            String response = responseExchanger.exchange(null, TIMEOUT, TimeUnit.SECONDS);

            Pattern pattern = Pattern.compile("(\\d)+(,(\\d)+){5}");
            Matcher matcher = pattern.matcher(response);

            matcher.find();
            String addressDigitsStr = matcher.group();

            String[] addressDigits = addressDigitsStr.split(",");

            String ipAddress = addressDigits[0] + '.' + addressDigits[1] + '.' + addressDigits[2] + '.' + addressDigits[3];
            int port = Integer.parseInt(addressDigits[4]) * 256 + Integer.parseInt(addressDigits[5]);

            dataConnection.connect(ipAddress, port);
        } catch (InterruptedException | TimeoutException e) {
            throw new MainControllerException("Error when establish data connection", e);
        }

        return dataConnection;
    }

    public void controlClose() {
        sendingManager.killAllSenders();
    }

    public List<FileComponent> controlLoadingFileList()
            throws ConnectionExistException, ControlConnectionException, MainControllerException {
        Connection dataConnection = establishDataConnection();
        return recursiveLoadFileList(dataConnection);
    }

    private List<FileComponent> recursiveLoadFileList(Connection dataConnection) throws MainControllerException {
        List<FileComponent> fileComponents;
        try {
            String fileListLoadingCommand = "LIST";
            sendingManager.send(fileListLoadingCommand);

            Exchanger<List<FileComponent>> listExchanger = new Exchanger<>();
            FileListReceiving fileListReceiving = new FileListReceiving(dataConnection, listExchanger);

            DataManager dataManager = new DataManager();
            dataManager.manageWork(fileListReceiving);

            fileComponents = listExchanger.exchange(null, TIMEOUT, TimeUnit.SECONDS);

            addChildrenToDirectories(fileComponents, dataConnection);
        } catch (InterruptedException | TimeoutException e) {
            throw new MainControllerException("Error when getting file list.", e);
        }
        return fileComponents;
    }

    private void addChildrenToDirectories(List<FileComponent> fileComponents, Connection dataConnection)
            throws MainControllerException {
        for (FileComponent fileComponent : fileComponents) {
            String fileName = ((AbstractFileComponent) fileComponent).getName();

            if (fileComponent.getChildren() != null) {
                changeWorkingDirectory(fileName);

                List<FileComponent> childrenFileComponents = recursiveLoadFileList(dataConnection);
                childrenFileComponents.forEach(fileComponent::add);

                changeWorkingDirectoryToParent();
            }
        }
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
