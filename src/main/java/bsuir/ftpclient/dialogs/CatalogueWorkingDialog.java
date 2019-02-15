package main.java.bsuir.ftpclient.dialogs;

import javafx.scene.control.TextInputDialog;
import main.java.bsuir.ftpclient.alerts.ConnectionErrorAlert;
import main.java.bsuir.ftpclient.connection.Connection;
import main.java.bsuir.ftpclient.dialogs.controllers.CatalogueWorkingDialogController;
import main.java.bsuir.ftpclient.exceptions.ConnectionNotExistException;

import java.io.IOException;
import java.util.Optional;

public class CatalogueWorkingDialog {
    private TextInputDialog dialog;

    public CatalogueWorkingDialog() {
        dialog = new TextInputDialog("r");
        dialog.setTitle("Text input");
        dialog.setHeaderText("Text input");
        dialog.setContentText("Please enter catalogue name");
    }

    public void createCatalogue(Connection connection) {
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(catalogueName -> {
            try {
                new CatalogueWorkingDialogController(catalogueName).controlCreating(connection);
            } catch (IOException | ConnectionNotExistException e) {
                new ConnectionErrorAlert(e);
            }
        });
    }

    public void deleteCatalogue(Connection connection) {
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(catalogueName -> {
            try {
                new CatalogueWorkingDialogController(catalogueName).controlDeleting(connection);
            } catch (IOException | ConnectionNotExistException e) {
                new ConnectionErrorAlert(e);
            }
        });
    }
}
