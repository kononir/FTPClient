package com.bsuir.ftpclient.ui.dialog.choose;

import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class ServerFileDialog extends TextInputDialog {

    public ServerFileDialog() {
        super("loading.txt");

        this.setTitle("Text input");
        this.setHeaderText("Text input");
        this.setContentText("Please enter file name");
    }

    public Optional<String> showDialog() {
        return this.showAndWait();
    }
}
