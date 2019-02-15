package main.java.bsuir.ftpclient.dialogs.controllers;

import javafx.util.Pair;
import main.java.bsuir.ftpclient.connection.Connection;
import main.java.bsuir.ftpclient.connection.ConnectionActions;
import main.java.bsuir.ftpclient.exceptions.ConnectionNotExistException;

import java.io.IOException;

public class AuthenticationDialogController {
    private Pair<String, String> dialogInformation;

    public AuthenticationDialogController(Pair<String, String> dialogInformation) {
        this.dialogInformation = dialogInformation;
    }

    public void controlAuthentication(Connection connection)
            throws IOException, ConnectionNotExistException {
        ConnectionActions actions = new ConnectionActions();

        String loginCommand = "USER " + dialogInformation.getKey();
        String passwordCommand = "PASS " + dialogInformation.getValue();

        actions.sendCommand(connection, loginCommand);
        actions.sendCommand(connection, passwordCommand);
    }
}
