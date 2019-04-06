package com.bsuir.ftpclient.ui.window;

import com.bsuir.ftpclient.connection.ftp.control.exception.ControlConnectionException;
import com.bsuir.ftpclient.connection.ftp.data.file.FileComponent;
import com.bsuir.ftpclient.connection.ftp.exception.ConnectionExistException;
import com.bsuir.ftpclient.connection.ftp.exception.ConnectionNotExistException;
import com.bsuir.ftpclient.ui.alert.ConnectionErrorAlert;
import com.bsuir.ftpclient.ui.alert.DisconnectAlert;
import com.bsuir.ftpclient.ui.dialog.*;
import com.bsuir.ftpclient.ui.manager.ControlConnectionViewManager;
import com.bsuir.ftpclient.ui.updater.TreeUpdater;
import com.bsuir.ftpclient.ui.window.controller.MainWindowController;
import com.bsuir.ftpclient.ui.window.controller.exception.MainControllerException;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.List;
import java.util.Optional;

public class MainWindow {
    private TreeUpdater fileTreeUpdater;
    private ControlConnectionViewManager controlConnectionViewManager;

    private MainWindowController controller = new MainWindowController();

    public MainWindow() {
        MenuBar menuBar = new MenuBar();
        StackPane.setAlignment(menuBar, Pos.TOP_CENTER);

        MenuItem connect = new MenuItem("Connect");
        connect.setOnAction(event -> connect());

        MenuItem disconnect = new MenuItem("Disconnect");
        disconnect.setOnAction(event -> disconnect());

        MenuItem filesList = new MenuItem("Files list");
        filesList.setOnAction(event -> loadFileList());

        Menu connectionMenu = new Menu("Server");
        connectionMenu.getItems().addAll(connect, disconnect, filesList);

        MenuItem createCatalogue = new MenuItem("Create");
        createCatalogue.setOnAction(event -> createCatalogue());

        MenuItem deleteCatalogue = new MenuItem("Delete");
        deleteCatalogue.setOnAction(event -> deleteCatalogue());

        MenuItem loadCatalogue = new MenuItem("Load");
        loadCatalogue.setOnAction(event -> loadCatalogue());

        Menu catalogueMenu = new Menu("Catalogue");
        catalogueMenu.getItems().addAll(
                createCatalogue,
                deleteCatalogue,
                new SeparatorMenuItem(),
                loadCatalogue
        );

        MenuItem loadFile = new MenuItem("Load");
        loadFile.setOnAction(event -> loadFile());

        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(
                loadFile
        );

        menuBar.getMenus().addAll(connectionMenu, catalogueMenu, fileMenu);

        TextArea answerMemo = new TextArea();
        answerMemo.setEditable(false);

        controlConnectionViewManager = new ControlConnectionViewManager(answerMemo);

        ScrollPane memoScrolling = new ScrollPane();
        memoScrolling.setContent(answerMemo);
        memoScrolling.setFitToHeight(true);
        memoScrolling.setFitToWidth(true);

        TreeItem<String> root = new TreeItem<>("/");
        TreeView<String> fileTree = new TreeView<>(root);
        fileTreeUpdater = new TreeUpdater(fileTree);

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

    private void connect() {
        Optional<String> result = new HostnameDialog().showAndWait();

        result.ifPresent(connectInformation -> {
            try {
                controller.controlConnecting(connectInformation);
                controlConnectionViewManager.startShowingServerAnswers();

                Optional<Pair<String, String>> authenticationOptional = new AuthenticationDialog().showAndWait();

                if (authenticationOptional.isPresent()) {
                    controller.controlAuthenticating(authenticationOptional.get());

                    loadFileList();
                } else {
                    controller.controlDisconnecting();
                }
            } catch (ConnectionExistException | ControlConnectionException | ConnectionNotExistException e) {
                new ConnectionErrorAlert(e);
            }
        });
    }

    private void disconnect() {
        try {
            controller.controlDisconnecting();

            new DisconnectAlert();
        } catch (ConnectionNotExistException | ControlConnectionException e) {
            new ConnectionErrorAlert(e);
        }
    }

    private void loadFileList() {
        try {
            List<FileComponent> fileComponents = controller.controlLoadingFileList();
            fileTreeUpdater.addAllToTree(fileComponents);
        } catch (ControlConnectionException | ConnectionExistException | MainControllerException e) {
            new ConnectionErrorAlert(e);
        }
    }

    private void createCatalogue() {
        Optional<String> result = new ServerCatalogueDialog().showAndWait();
        result.ifPresent(catalogueName -> controller.controlCreatingCatalogue(catalogueName));
    }

    private void deleteCatalogue() {
        Optional<String> result = new ServerCatalogueDialog().showAndWait();
        result.ifPresent(catalogueName -> controller.controlDeletingCatalogue(catalogueName));
    }

    private void loadCatalogue() {
        Optional<String> optionalFrom = new ServerCatalogueDialog().showAndWait();
        optionalFrom.ifPresent(fromPath -> {
            Optional<String> optionalTo = new ClientCatalogueDialog().chooseCataloguePath();
            optionalTo.ifPresent(toPath -> {
                try {
                    controller.controlLoadingCatalogue(fromPath, toPath);
                } catch (ConnectionExistException | ControlConnectionException
                        | MainControllerException e) {
                    new ConnectionErrorAlert(e);
                }
            });
        });
    }

    private void loadFile() {
        Optional<String> optionalFrom = new ServerFileDialog().showAndWait();
        optionalFrom.ifPresent(fromPath -> {
            Optional<String> optionalTo = new ClientCatalogueDialog().chooseCataloguePath();
            optionalTo.ifPresent(toPath -> {
                try {
                    controller.controlLoadingFile(fromPath, toPath);
                } catch (ConnectionExistException | ControlConnectionException
                        | MainControllerException e) {
                    new ConnectionErrorAlert(e);
                }
            });
        });
    }

    private void close() {
        try {
            controlConnectionViewManager.stopShowingServerAnswers();

            controller.controlDisconnecting();
            controller.controlClose();
        } catch (ConnectionNotExistException | ControlConnectionException ignored) {
        } finally {
            Platform.exit();
        }
    }
}
