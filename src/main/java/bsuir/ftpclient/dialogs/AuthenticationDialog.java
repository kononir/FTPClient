package main.java.bsuir.ftpclient.dialogs;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class AuthenticationDialog extends Dialog<Pair<String, String>> {
    public AuthenticationDialog() {
        this.setTitle("Text input");
        this.setHeaderText("Authentication");

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

        DialogPane dialogPane = this.getDialogPane();
        ObservableList<ButtonType> buttonTypes = dialogPane.getButtonTypes();
        buttonTypes.addAll(loginButtonType, ButtonType.CANCEL);

        this.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(loginTextField.getText(), passwordTextField.getText());
            }
            return null;
        });

        dialogPane.setContent(pane);
    }
}
