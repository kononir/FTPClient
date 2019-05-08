package com.bsuir.ftpclient.ui.dialog;

import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class HostnameDialog extends TextInputDialog {

    public HostnameDialog() {
        super("localhost");

        this.setTitle("Text input");
        this.setHeaderText("Text input");
        this.setContentText("Please enter domain name or ip");
    }

    public Optional<String> showDialog() {
        return this.showAndWait();
    }
}
