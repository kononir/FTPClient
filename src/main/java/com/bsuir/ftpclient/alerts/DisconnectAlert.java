package com.bsuir.ftpclient.alerts;

import javafx.scene.control.Alert;

public class DisconnectAlert {
    public DisconnectAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successful");
        alert.setHeaderText("Successful");
        alert.setContentText("Connection is closed!");

        alert.showAndWait();
    }
}
