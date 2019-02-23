package com.bsuir.ftpclient.dialogs;

import javafx.scene.control.TextInputDialog;

public class HostnameDialog extends TextInputDialog {
    public HostnameDialog() {
        super("localhost");

        this.setTitle("Text input");
        this.setHeaderText("Text input");
        this.setContentText("Please enter domain name or ip");
    }
}
