package com.bsuir.ftpclient.ui.alert;

import javafx.scene.control.Alert;

public class DisconnectAlert {

    public void show() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successful");
        alert.setHeaderText("Successful");
        alert.setContentText("Connection is closed!");

        alert.show();
    }
}
