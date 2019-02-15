package main.java.bsuir.ftpclient.dialogs.controllers;

import main.java.bsuir.ftpclient.connection.Connection;
import main.java.bsuir.ftpclient.connection.ConnectionActions;
import main.java.bsuir.ftpclient.exceptions.ConnectionNotExistException;

import java.io.IOException;

public class CatalogueWorkingDialogController {
    private String catalogueName;

    public CatalogueWorkingDialogController(String dialogInformation) {
        this.catalogueName = dialogInformation;
    }

    public void controlCreating(Connection connection) throws IOException, ConnectionNotExistException {
        String catalogueDeleteCommand = "MKD " + catalogueName;
        new ConnectionActions().sendCommand(connection, catalogueDeleteCommand);
    }

    public void controlDeleting(Connection connection) throws IOException, ConnectionNotExistException {
        String catalogueDeleteCommand = "RMD " + catalogueName;
        new ConnectionActions().sendCommand(connection, catalogueDeleteCommand);
    }
}
