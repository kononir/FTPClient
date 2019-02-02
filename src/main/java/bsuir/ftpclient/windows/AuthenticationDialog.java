package main.java.bsuir.ftpclient.windows;

import main.java.bsuir.ftpclient.connection.Connection;
import main.java.bsuir.ftpclient.connection.ConnectionActions;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class AuthenticationDialog {
    public AuthenticationDialog(Connection connection) {
        Stage stage = new Stage();
        stage.setTitle("Text input");

        Label authenticationLabel = new Label("Authentication");
        StackPane.setAlignment(authenticationLabel, Pos.TOP_CENTER);
        StackPane.setMargin(
                authenticationLabel,
                new Insets(10, 60, 0, 60)
        );

        Label loginLabel = new Label("Login");
        Label passwordLabel = new Label("Password");

        VBox labelVBox = new VBox(loginLabel, passwordLabel);
        labelVBox.setSpacing(10);
        labelVBox.setPrefWidth(100);

        TextField loginTextField = new TextField("anonymous");
        TextField passwordTextField = new TextField("root@example.com");

        VBox textFieldVBox = new VBox(loginTextField, passwordTextField);

        HBox inputHBox = new HBox(labelVBox, textFieldVBox);
        inputHBox.setSpacing(10);

        StackPane.setAlignment(inputHBox, Pos.CENTER);
        StackPane.setMargin(
                inputHBox,
                new Insets(60, 60, 60, 60)
        );

        Button okButton = new Button("Ok");
        okButton.setOnAction(event -> {
            ConnectionActions actions = new ConnectionActions();

            try {
                String answer = actions.authenticate(
                        connection,
                        loginTextField.getText(),
                        passwordTextField.getText()
                );

                MainWindow.addTextToMemo(answer);
            } catch (IOException e) {
                e.printStackTrace();

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText("Connection problems!");

                alert.showAndWait();
            } finally {
                stage.close();
            }
        });

        StackPane.setAlignment(okButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(okButton, new Insets(10));

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> stage.close());

        StackPane.setAlignment(cancelButton, Pos.BOTTOM_LEFT);
        StackPane.setMargin(cancelButton, new Insets(10));

        Pane pane = new StackPane(authenticationLabel, inputHBox, okButton, cancelButton);
        pane.setPrefSize(300, 200);

        Scene scene = new Scene(pane);

        stage.setScene(scene);
        stage.show();
    }
}
