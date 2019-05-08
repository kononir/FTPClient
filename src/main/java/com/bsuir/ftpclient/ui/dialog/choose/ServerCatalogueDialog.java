package com.bsuir.ftpclient.ui.dialog.choose;

import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class ServerCatalogueDialog extends TextInputDialog {

    public ServerCatalogueDialog() {
        super("r");

        this.setTitle("Text input");
        this.setHeaderText("Text input");
        this.setContentText("Please enter catalogue name");
    }

    public Optional<String> showDialog() {
        return this.showAndWait();
    }
}
