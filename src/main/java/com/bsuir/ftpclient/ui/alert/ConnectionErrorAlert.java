package com.bsuir.ftpclient.ui.alert;

import javafx.scene.control.Alert;

public class ConnectionErrorAlert {

    public void show(Exception e) {
        e.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.setContentText(e.getMessage());

        alert.showAndWait();
    }
}
