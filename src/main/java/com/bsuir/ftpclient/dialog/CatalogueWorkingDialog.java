package com.bsuir.ftpclient.dialog;

import javafx.scene.control.TextInputDialog;

public class CatalogueWorkingDialog extends TextInputDialog {
    public CatalogueWorkingDialog() {
        super("r");

        this.setTitle("Text input");
        this.setHeaderText("Text input");
        this.setContentText("Please enter catalogue name");
    }
}
