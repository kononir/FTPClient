package main.java.bsuir.ftpclient.windows.controllers;

import javafx.scene.control.TreeView;
import javafx.util.Pair;
import main.java.bsuir.ftpclient.connection.Connection;
import main.java.bsuir.ftpclient.connection.ConnectionActions;
import main.java.bsuir.ftpclient.exceptions.ConnectionExistException;
import main.java.bsuir.ftpclient.exceptions.ConnectionNotExistException;
import main.java.bsuir.ftpclient.managers.FileNamesManager;
import main.java.bsuir.ftpclient.managers.SendingManager;
import main.java.bsuir.ftpclient.managers.ViewManager;

import java.io.IOException;

public class MainWindowController {
    private Connection controlConnection = new Connection();
    private Connection dataConnection = new Connection();
    private SendingManager sendingManager = new SendingManager();

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
        ConnectionActions actions = new ConnectionActions();

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
        new ConnectionActions().sendCommand(controlConnection, catalogueDeleteCommand);
    }

    public void controlDeletingCatalogue(String catalogueName) throws IOException, ConnectionNotExistException {
        String catalogueDeleteCommand = "RMD " + catalogueName;
        new ConnectionActions().sendCommand(controlConnection, catalogueDeleteCommand);
    }
}
