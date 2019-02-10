package main.java.bsuir.ftpclient.windows;

import javafx.util.Pair;
import main.java.bsuir.ftpclient.connection.Connection;
import main.java.bsuir.ftpclient.connection.ConnectionActions;
import main.java.bsuir.ftpclient.exceptions.ConnectionExistException;
import main.java.bsuir.ftpclient.exceptions.ConnectionNotExistException;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import main.java.bsuir.ftpclient.updaters.MemoUpdater;

import java.io.IOException;
import java.util.Optional;

public class MainWindow {
    private TextArea memo;
    private Connection connection = new Connection();
    private MemoUpdater updater;

    public MainWindow() {
        MenuBar menuBar = new MenuBar();
        StackPane.setAlignment(menuBar, Pos.TOP_CENTER);

        Menu connectionMenu = new Menu("Connection");

        MenuItem connect = new MenuItem("Connect");
        connect.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog("91.122.30.115");
            dialog.setTitle("Text input");
            dialog.setHeaderText("Text input");
            dialog.setContentText("Please enter domain name or ip");

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(connectInform -> {
                try {
                    connection.connect(connectInform);

                    updater.startPrintAnswers(connection);

                    Dialog<Pair<String, String>> authenticationDialog = new AuthenticationDialog().getDialogWindow();

                    Optional<Pair<String, String>> authenticationOptional = authenticationDialog.showAndWait();

                    authenticationOptional.ifPresent(authenticationInform -> {
                        ConnectionActions actions = new ConnectionActions();

                        String loginCommand = "USER " + authenticationInform.getKey();
                        String passwordCommand = "PASS " + authenticationInform.getValue();

                        try {
                            updater.updateRequest(loginCommand + '\n');

                            actions.sendCommand(connection, loginCommand);

                            updater.updateRequest(passwordCommand + '\n');

                            actions.sendCommand(connection, passwordCommand);
                        } catch (IOException e) {
                            e.printStackTrace();

                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Error");
                            alert.setContentText("Connection problems!");
                        }
                    });
                } catch (IOException | ConnectionExistException e) {
                    e.printStackTrace();

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error");
                    alert.setContentText(e.getMessage());

                    alert.showAndWait();
                }
            });
        });

        MenuItem disconnect = new MenuItem("Disconnect");
        disconnect.setOnAction(event -> {
            try {
                connection.disconnect();

                updater.stopPrintAnswers();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Successful");
                alert.setHeaderText("Successful");
                alert.setContentText("Connection is closed!");

                alert.showAndWait();
            } catch (ConnectionNotExistException | IOException e) {
                e.printStackTrace();

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText(e.getMessage());

                alert.showAndWait();
            }
        });

        connectionMenu.getItems().addAll(connect, disconnect);

        Menu catalogueMenu = new Menu("Catalogue");

        Menu fileMenu = new Menu("File");

        menuBar.getMenus().addAll(connectionMenu, catalogueMenu, fileMenu);

        memo = new TextArea();
        memo.setEditable(false);

        updater = new MemoUpdater(memo);

        ScrollPane scrolling = new ScrollPane();
        scrolling.setContent(memo);
        scrolling.setFitToHeight(true);
        scrolling.setFitToWidth(true);

        StackPane.setAlignment(scrolling, Pos.CENTER);
        StackPane.setMargin(scrolling, new Insets(35, 5, 35, 5));

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(event -> close());

        StackPane.setAlignment(exitButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(exitButton, new Insets(5));

        StackPane pane = new StackPane(menuBar, scrolling, exitButton);
        pane.setPrefSize(500, 400);

        Scene scene = new Scene(pane);

        Stage stage = new Stage();

        stage.setOnCloseRequest(event -> close());

        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void addTextToMemo(String text) {
        memo.setText(memo.getText() + text);
    }

    private void close() {
        try {
            connection.disconnect();
        } catch (IOException | ConnectionNotExistException ignored) {
        } finally {
            Platform.exit();
        }
    }
}
