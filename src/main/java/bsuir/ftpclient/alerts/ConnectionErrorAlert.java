package main.java.bsuir.ftpclient.alerts;

import javafx.scene.control.Alert;

public class ConnectionErrorAlert {
    public ConnectionErrorAlert(Exception e) {
        e.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.setContentText(e.getMessage());

        alert.showAndWait();
    }
}
