package com.bsuir.ftpclient.ui.dialog.choose;

import javafx.scene.control.TextInputDialog;

public class ServerFileDialog extends TextInputDialog {
    public ServerFileDialog() {
        super("loading.txt");

        this.setTitle("Text input");
        this.setHeaderText("Text input");
        this.setContentText("Please enter file name");
    }
}
