package main.java.bsuir.ftpclient.windows;

import main.java.bsuir.ftpclient.alerts.ConnectionErrorAlert;
import main.java.bsuir.ftpclient.alerts.DisconnectAlert;
import main.java.bsuir.ftpclient.windows.controllers.DisconnectMenuItemController;
import main.java.bsuir.ftpclient.dialogs.CatalogueWorkingDialog;
import main.java.bsuir.ftpclient.manager.AnswerManager;
import main.java.bsuir.ftpclient.connection.Connection;
import main.java.bsuir.ftpclient.dialogs.AuthenticationDialog;
import main.java.bsuir.ftpclient.dialogs.HostnameDialog;
import main.java.bsuir.ftpclient.exceptions.ConnectionNotExistException;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import main.java.bsuir.ftpclient.manager.MemoUpdater;

import java.io.IOException;

public class MainWindow {
    private Connection connection = new Connection();
    private MemoUpdater memoUpdater;
    private AnswerManager answerManager;

    public MainWindow() {
        MenuBar menuBar = new MenuBar();
        StackPane.setAlignment(menuBar, Pos.TOP_CENTER);

        MenuItem connect = new MenuItem("Connect");
        connect.setOnAction(event -> answerManager = new HostnameDialog().connect(connection, memoUpdater));

        MenuItem authenticate = new MenuItem("Authenticate");
        authenticate.setOnAction(event -> new AuthenticationDialog().authenticate(connection));

        MenuItem disconnect = new MenuItem("Disconnect");
        disconnect.setOnAction(event -> {
            try {
                new DisconnectMenuItemController().controlDisconnect(connection, answerManager);

                new DisconnectAlert();
            } catch (ConnectionNotExistException | IOException e) {
                new ConnectionErrorAlert(e);
            }
        });

        Menu connectionMenu = new Menu("Server");
        connectionMenu.getItems().addAll(connect, disconnect, new SeparatorMenuItem(), authenticate);

        MenuItem createCatalogue = new MenuItem("Create");
        createCatalogue.setOnAction(event -> new CatalogueWorkingDialog().createCatalogue(connection));

        MenuItem deleteCatalogue = new MenuItem("Delete");
        deleteCatalogue.setOnAction(event -> new CatalogueWorkingDialog().deleteCatalogue(connection));

        Menu catalogueMenu = new Menu("Catalogue");
        catalogueMenu.getItems().addAll(createCatalogue, deleteCatalogue);

        Menu fileMenu = new Menu("File");

        menuBar.getMenus().addAll(connectionMenu, catalogueMenu, fileMenu);

        TextArea memo = new TextArea();
        memo.setEditable(false);

        memoUpdater = new MemoUpdater(memo);

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

    private void close() {
        try {
            connection.disconnect();
        } catch (IOException | ConnectionNotExistException ignored) {
        } finally {
            Platform.exit();
        }
    }
}
