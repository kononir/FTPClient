package bsuir.ftpclient;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Optional;

public class MainWindow {
    private TextArea memo;
    private Connection connection = new Connection();

    public MainWindow() {
        MenuBar menuBar = new MenuBar();
        StackPane.setAlignment(menuBar, Pos.TOP_CENTER);

        Menu connectionMenu = new Menu("Connection");

        MenuItem connect = new MenuItem("Connect");
        connect.setOnAction(event -> {
            if (connection.isClosed()) {
                TextInputDialog dialog = new TextInputDialog("91.122.30.115");
                dialog.setTitle("Text input");
                dialog.setHeaderText("Text input");
                dialog.setContentText("Please enter domain name or ip");

                Optional<String> result = dialog.showAndWait();

                result.ifPresent(connectInform -> {
                    addTextToMemo(connection.connect(connectInform));
                });
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText("Connection is already exist!");

                alert.showAndWait();
            }
        });

        MenuItem disconnect = new MenuItem("Disconnect");
        disconnect.setOnAction(event -> {
            if (!connection.isClosed()) {
                addTextToMemo(connection.disconnect());
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText("Connection isn't exist!");

                alert.showAndWait();
            }
        });

        connectionMenu.getItems().addAll(connect, disconnect);

        menuBar.getMenus().addAll(connectionMenu);

        memo = new TextArea();
        memo.setEditable(false);

        StackPane.setAlignment(memo, Pos.CENTER);
        StackPane.setMargin(memo, new Insets(35, 5, 35, 5));

        Button sendButton = new Button("Exit");
        sendButton.setOnAction(event -> Platform.exit());

        StackPane.setAlignment(sendButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(sendButton, new Insets(5));

        StackPane pane = new StackPane(menuBar, memo, sendButton);
        pane.setPrefSize(500, 400);

        Scene scene = new Scene(pane);

        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void addTextToMemo(String text) {
        memo.setText(memo.getText() + text);
    }
}
