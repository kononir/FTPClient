package main.java.bsuir.ftpclient.dialogs;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Pair;
import main.java.bsuir.ftpclient.alerts.ConnectionErrorAlert;
import main.java.bsuir.ftpclient.connection.Connection;
import main.java.bsuir.ftpclient.dialogs.controllers.AuthenticationDialogController;
import main.java.bsuir.ftpclient.exceptions.ConnectionNotExistException;

import java.io.IOException;
import java.util.Optional;

public class AuthenticationDialog {
    private Dialog<Pair<String, String>> dialogWindow;

    public AuthenticationDialog() {
        dialogWindow = new Dialog<>();
        dialogWindow.setTitle("Text input");
        dialogWindow.setHeaderText("Authentication");

        TextField loginTextField = new TextField("anonymous");
        loginTextField.setPromptText("Login");
        TextField passwordTextField = new TextField("root@example.com");
        passwordTextField.setPromptText("Password");

        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(20, 150, 10, 10));

        pane.add(new Label("Username:"), 0, 0);
        pane.add(loginTextField, 1, 0);
        pane.add(new Label("Password:"), 0, 1);
        pane.add(passwordTextField, 1, 1);

        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);

        DialogPane dialogPane = dialogWindow.getDialogPane();
        ObservableList<ButtonType> buttonTypes = dialogPane.getButtonTypes();
        buttonTypes.addAll(loginButtonType, ButtonType.CANCEL);

        dialogWindow.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(loginTextField.getText(), passwordTextField.getText());
            }
            return null;
        });

        dialogPane.setContent(pane);
    }

    public void authenticate(Connection connection) {
        Optional<Pair<String, String>> authenticationOptional = dialogWindow.showAndWait();

        authenticationOptional.ifPresent(authenticationInform -> {
            try {
                new AuthenticationDialogController(authenticationInform).controlAuthentication(connection);
            } catch (IOException | ConnectionNotExistException e) {
                new ConnectionErrorAlert(e);
            }
        });
    }
}
