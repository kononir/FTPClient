package com.bsuir.ftpclient.ui.window.controller;

import com.bsuir.ftpclient.connection.ftp.Connection;
import com.bsuir.ftpclient.connection.ftp.control.exception.ControlConnectionException;
import com.bsuir.ftpclient.connection.ftp.control.manager.SendingManager;
import com.bsuir.ftpclient.connection.ftp.data.manager.DataManager;
import com.bsuir.ftpclient.connection.ftp.data.manager.work.CatalogueReceiving;
import com.bsuir.ftpclient.connection.ftp.data.manager.work.FileReceiving;
import com.bsuir.ftpclient.connection.ftp.exception.ConnectionExistException;
import com.bsuir.ftpclient.connection.ftp.exception.ConnectionNotExistException;
import javafx.util.Pair;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainWindowController {
    private static final int CONTROL_PORT = 21;

    private Connection controlConnection = new Connection();

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

    /*public void controlStartingCheckForFileNames(TreeView<String> fileTree)
            throws ConnectionExistException, ControlConnectionException, TimeoutException, InterruptedException {
        Connection dataConnection = establishDataConnection();

        FileNamesManager fileNamesManager = new FileNamesManager(fileTree);
        fileNamesManager.startCheckingForFileNames(dataConnection);
    }*/

    public void controlCreatingCatalogue(String catalogueName) {
        String catalogueCreatingCommand = "MKD " + catalogueName;
        sendingManager.send(catalogueCreatingCommand);
    }

    public void controlDeletingCatalogue(String catalogueName) {
        String catalogueDeletingCommand = "RMD " + catalogueName;
        sendingManager.send(catalogueDeletingCommand);
    }

    public void controlLoadingCatalogue(String fromPath, String toPath)
            throws ConnectionExistException, ControlConnectionException, TimeoutException, InterruptedException {
        Connection dataConnection = establishDataConnection();

        String catalogueLoadingCommand = "RETR " + fromPath;
        sendingManager.send(catalogueLoadingCommand);

        CatalogueReceiving catalogueReceiving = new CatalogueReceiving(dataConnection, toPath);
        DataManager dataManager = new DataManager();
        dataManager.manageWork(catalogueReceiving);
    }

    public void controlLoadingFile(String fileName, String toDirectoryPath)
            throws InterruptedException, TimeoutException, ConnectionExistException, ControlConnectionException {
        Connection dataConnection = establishDataConnection();

        String catalogueLoadingCommand = "RETR " + fileName;
        sendingManager.send(catalogueLoadingCommand);

        FileReceiving fileReceiving = new FileReceiving(dataConnection, toDirectoryPath + '/' + fileName);
        DataManager dataManager = new DataManager();
        dataManager.manageWork(fileReceiving);
    }

    private Connection establishDataConnection()
            throws TimeoutException, InterruptedException, ConnectionExistException, ControlConnectionException {
        String passiveModeCommand = "PASV";
        sendingManager.send(passiveModeCommand);

        String response = responseExchanger.exchange(null, 100, TimeUnit.MILLISECONDS);

        Pattern pattern = Pattern.compile("(\\d)+(,(\\d)+){5}");
        Matcher matcher = pattern.matcher(response);

        matcher.find();
        String addressDigitsStr = matcher.group();

        String[] addressDigits = addressDigitsStr.split(",");

        String ipAddress = addressDigits[0] + '.' + addressDigits[1] + '.' + addressDigits[2] + '.' + addressDigits[3];
        int port = Integer.parseInt(addressDigits[4]) * 256 + Integer.parseInt(addressDigits[5]);

        Connection dataConnection = new Connection();
        dataConnection.connect(ipAddress, port);

        return dataConnection;
    }

    public void controlClose() {
        sendingManager.killAllSenders();
    }
}
