package com.bsuir.ftpclient.window.controller;

import com.bsuir.ftpclient.connection.ftp.Connection;
import com.bsuir.ftpclient.connection.ftp.control.exception.ControlConnectionException;
import com.bsuir.ftpclient.connection.ftp.exception.ConnectionExistException;
import com.bsuir.ftpclient.connection.ftp.exception.ConnectionNotExistException;
import com.bsuir.ftpclient.manager.FileNamesManager;
import com.bsuir.ftpclient.manager.SendingManager;
import com.bsuir.ftpclient.manager.GeneralViewManager;
import javafx.scene.control.TreeView;
import javafx.util.Pair;

public class MainWindowController {
    private Connection controlConnection = new Connection();
    private Connection dataConnection = new Connection();
    private SendingManager sendingManager = new SendingManager(controlConnection);

    public void controlConnecting(String connectInformation)
            throws ConnectionExistException, ControlConnectionException {
        int controlPort = 21;
        controlConnection.connect(connectInformation, controlPort);

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

    public void controlStartingCheckForFileNames(TreeView<String> fileTree)
            throws ConnectionExistException, ControlConnectionException {
        FileNamesManager fileNamesManager = new FileNamesManager(fileTree);

        String hostname = controlConnection.getHostname();
        int dataPort = 20;

        dataConnection.connect(hostname, dataPort);

        fileNamesManager.startCheckingForFileNames(dataConnection);
    }

    public void controlStoppingCheckForAnswers(GeneralViewManager generalViewManager) {
        generalViewManager.stopCheckingForAnswers();
        sendingManager.killAllSenders();
    }

    public void controlCreatingCatalogue(String catalogueName) {
        String catalogueCreatingCommand = "MKD " + catalogueName;
        sendingManager.send(catalogueCreatingCommand);
    }

    public void controlDeletingCatalogue(String catalogueName) {
        String catalogueDeletingCommand = "RMD " + catalogueName;
        sendingManager.send(catalogueDeletingCommand);
    }
}
