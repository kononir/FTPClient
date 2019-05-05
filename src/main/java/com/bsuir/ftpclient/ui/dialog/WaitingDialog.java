package com.bsuir.ftpclient.ui.dialog;

import com.bsuir.ftpclient.ui.window.MainWindowController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class WaitingDialog {
    private MainWindowController controller;
    private Stage stage = new Stage();

    public WaitingDialog(MainWindowController controller) {
        this.controller = controller;
    }

    public void show() {
        BorderPane pane = new BorderPane();
        pane.setPrefSize(200, 120);

        ProgressIndicator progressIndicator = new ProgressIndicator();

        pane.setCenter(progressIndicator);
        BorderPane.setMargin(progressIndicator, new Insets(10));

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> cancel());

        pane.setBottom(cancelButton);
        BorderPane.setAlignment(cancelButton, Pos.BOTTOM_CENTER);

        Scene scene = new Scene(pane);

        stage.setOnCloseRequest(event -> cancel());

        stage.setAlwaysOnTop(true);
        stage.setTitle("Waiting");
        stage.setScene(scene);
        stage.show();
    }

    private void cancel() {
        controller.controlRestartSendingMessages();
        stage.close();
    }

    public void close() {
        stage.close();
    }

    public boolean isShowing() {
        return stage.isShowing();
    }
}
