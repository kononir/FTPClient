package com.bsuir.ftpclient.ui.dialog.choose;

import javafx.scene.control.TextInputDialog;

public class ServerCatalogueDialog extends TextInputDialog {
    public ServerCatalogueDialog() {
        super("r");

        this.setTitle("Text input");
        this.setHeaderText("Text input");
        this.setContentText("Please enter catalogue name");
    }
}
