package com.bsuir.ftpclient.windows.controllers;

import com.bsuir.ftpclient.connection.Connection;
import com.bsuir.ftpclient.connection.ControlConnectionActions;
import com.bsuir.ftpclient.exceptions.ConnectionExistException;
import com.bsuir.ftpclient.exceptions.ConnectionNotExistException;
import com.bsuir.ftpclient.managers.FileNamesManager;
import com.bsuir.ftpclient.managers.SendingManager;
import com.bsuir.ftpclient.managers.ViewManager;
import javafx.scene.control.TreeView;
import javafx.util.Pair;

import java.io.IOException;

public class MainWindowController {
    private Connection controlConnection = new Connection();
    private Connection dataConnection = new Connection();
    private SendingManager sendingManager = new SendingManager(controlConnection);

    public void controlConnecting(String connectInformation)
            throws IOException, ConnectionExistException {
        int controlPort = 21;
        controlConnection.connect(connectInformation, controlPort);
    }

    public void controlDisconnecting()
            throws IOException, ConnectionNotExistException {
        controlConnection.disconnect();
    }

    public void controlAuthenticating(Pair<String, String> authenticationPair)
            throws IOException, ConnectionNotExistException {
        ControlConnectionActions actions = new ControlConnectionActions();

        String loginCommand = "USER " + authenticationPair.getKey();
        String passwordCommand = "PASS " + authenticationPair.getValue();

        actions.sendCommand(controlConnection, loginCommand);
        actions.sendCommand(controlConnection, passwordCommand);
    }

    public void controlStartingCheckForFileNames(TreeView<String> fileTree) throws IOException, ConnectionExistException {
        FileNamesManager fileNamesManager = new FileNamesManager(fileTree);

        String hostname = controlConnection.getHostname();
        int dataPort = 20;

        dataConnection.connect(hostname, dataPort);

        fileNamesManager.startCheckingForFileNames(dataConnection);
    }

    public void controlStartingCheckForAnswers(ViewManager viewManager) {
        viewManager.startCheckingForAnswers(controlConnection);
    }

    public void controlStoppingCheckForAnswers(ViewManager viewManager) {
        viewManager.stopCheckingForAnswers();
    }

    public void controlCreatingCatalogue(String catalogueName) throws IOException, ConnectionNotExistException {
        String catalogueDeleteCommand = "MKD " + catalogueName;
        new ControlConnectionActions().sendCommand(controlConnection, catalogueDeleteCommand);
    }

    public void controlDeletingCatalogue(String catalogueName) throws IOException, ConnectionNotExistException {
        String catalogueDeleteCommand = "RMD " + catalogueName;
        new ControlConnectionActions().sendCommand(controlConnection, catalogueDeleteCommand);
    }
}
