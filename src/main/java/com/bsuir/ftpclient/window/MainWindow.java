package com.bsuir.ftpclient.window;

import com.bsuir.ftpclient.alert.ConnectionErrorAlert;
import com.bsuir.ftpclient.alert.DisconnectAlert;
import com.bsuir.ftpclient.connection.ftp.control.exception.ControlConnectionException;
import com.bsuir.ftpclient.dialog.AuthenticationDialog;
import com.bsuir.ftpclient.dialog.CatalogueWorkingDialog;
import com.bsuir.ftpclient.dialog.HostnameDialog;
import com.bsuir.ftpclient.connection.ftp.exception.ConnectionExistException;
import com.bsuir.ftpclient.connection.ftp.exception.ConnectionNotExistException;
import com.bsuir.ftpclient.manager.GeneralViewManager;
import com.bsuir.ftpclient.window.controller.MainWindowController;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.Optional;

public class MainWindow {
    private TreeView<String> fileTree;

    private GeneralViewManager generalViewManager;

    private MainWindowController controller = new MainWindowController();

    public MainWindow() {
        MenuBar menuBar = new MenuBar();
        StackPane.setAlignment(menuBar, Pos.TOP_CENTER);

        MenuItem connect = new MenuItem("Connect");
        connect.setOnAction(event -> {
            Optional<String> result = new HostnameDialog().showAndWait();

            result.ifPresent(connectInformation -> {
                try {
                    controller.controlConnecting(connectInformation);

                    Optional<Pair<String, String>> authenticationOptional = new AuthenticationDialog().showAndWait();

                    if (authenticationOptional.isPresent()) {
                        controller.controlAuthenticating(authenticationOptional.get());

                        generalViewManager.startShowingServerAnswers();
                    } else {
                        disconnect();
                    }
                } catch (ConnectionExistException | ControlConnectionException | ConnectionNotExistException e) {
                    new ConnectionErrorAlert(e);
                }
            });
        });

        MenuItem disconnect = new MenuItem("Disconnect");
        disconnect.setOnAction(event -> {
            try {
                disconnect();

                new DisconnectAlert();
            } catch (ConnectionNotExistException | ControlConnectionException e) {
                new ConnectionErrorAlert(e);
            }
        });

        Menu connectionMenu = new Menu("Server");
        connectionMenu.getItems().addAll(connect, disconnect);

        MenuItem createCatalogue = new MenuItem("Create");
        createCatalogue.setOnAction(event -> {
            Optional<String> result = new CatalogueWorkingDialog().showAndWait();

            result.ifPresent(catalogueName -> controller.controlCreatingCatalogue(catalogueName));
        });

        MenuItem deleteCatalogue = new MenuItem("Delete");
        deleteCatalogue.setOnAction(event -> {
            Optional<String> result = new CatalogueWorkingDialog().showAndWait();

            result.ifPresent(catalogueName -> controller.controlDeletingCatalogue(catalogueName));
        });

        Menu catalogueMenu = new Menu("Catalogue");
        catalogueMenu.getItems().addAll(createCatalogue, deleteCatalogue);

        Menu fileMenu = new Menu("File");

        menuBar.getMenus().addAll(connectionMenu, catalogueMenu, fileMenu);

        TextArea answerMemo = new TextArea();
        generalViewManager = new GeneralViewManager(answerMemo);

        answerMemo.setEditable(false);

        ScrollPane memoScrolling = new ScrollPane();
        memoScrolling.setContent(answerMemo);
        memoScrolling.setFitToHeight(true);
        memoScrolling.setFitToWidth(true);

        fileTree = new TreeView<>();

        ScrollPane treeScrolling = new ScrollPane();
        treeScrolling.setContent(fileTree);
        treeScrolling.setFitToHeight(true);
        treeScrolling.setFitToWidth(true);

        HBox activePlace = new HBox(memoScrolling, treeScrolling);
        activePlace.setSpacing(10);

        StackPane.setMargin(activePlace, new Insets(35, 5, 35, 5));
        StackPane.setAlignment(treeScrolling, Pos.CENTER);

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(event -> close());

        StackPane.setAlignment(exitButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(exitButton, new Insets(5));

        StackPane pane = new StackPane(menuBar, activePlace, exitButton);
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
            disconnect();
        } catch (ConnectionNotExistException | ControlConnectionException ignored) {
        } finally {
            Platform.exit();
        }
    }

    private void disconnect() throws ConnectionNotExistException, ControlConnectionException {
        controller.controlDisconnecting();
        controller.controlStoppingCheckForAnswers(generalViewManager);
    }
}
