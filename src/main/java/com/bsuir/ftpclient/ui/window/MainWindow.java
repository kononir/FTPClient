package com.bsuir.ftpclient.ui.window;

import com.bsuir.ftpclient.connection.ftp.data.file.ServerFile;
import com.bsuir.ftpclient.ui.alert.ConnectionErrorAlert;
import com.bsuir.ftpclient.ui.alert.DisconnectAlert;
import com.bsuir.ftpclient.ui.dialog.*;
import com.bsuir.ftpclient.ui.memo.MemoManager;
import com.bsuir.ftpclient.ui.tree.TreeUpdater;
import com.bsuir.ftpclient.ui.tree.TypedTreeItem;
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
    private MemoManager memoManager;

    private MainWindowController controller = new MainWindowController();

    public MainWindow() {
        MenuBar menuBar = new MenuBar();
        StackPane.setAlignment(menuBar, Pos.TOP_CENTER);

        MenuItem connect = new MenuItem("Connect");
        connect.setOnAction(event -> connect());

        MenuItem disconnect = new MenuItem("Disconnect");
        disconnect.setOnAction(event -> disconnect());

        Menu connectionMenu = new Menu("Server");
        connectionMenu.getItems().addAll(connect, disconnect);

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

        memoManager = new MemoManager(answerMemo);

        ScrollPane memoScrolling = new ScrollPane();
        memoScrolling.setContent(answerMemo);
        memoScrolling.setFitToHeight(true);
        memoScrolling.setFitToWidth(true);

        TreeItem<String> root = new TypedTreeItem<>("/", true);
        TreeView<String> fileTree = new TreeView<>(root);
        fileTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            TypedTreeItem<String> node = (TypedTreeItem<String>) newValue;
            if (node != null && node.isLeaf() && node.isPackage()) {
                loadFileList(newValue);
            }
        });

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
                memoManager.startShowingServerAnswers();

                Optional<Pair<String, String>> authenticationOptional = new AuthenticationDialog().showAndWait();

                if (authenticationOptional.isPresent()) {
                    controller.controlAuthenticating(authenticationOptional.get());

                    loadFileList(fileTreeUpdater.getTree().getRoot());
                } else {
                    controller.controlDisconnecting();
                }
            } catch (MainControllerException e) {
                new ConnectionErrorAlert(e);
            }
        });
    }

    private void disconnect() {
        fileTreeUpdater.clearTree();
        controller.controlDisconnecting();

        new DisconnectAlert();
    }

    private String getAbsolutePath(TreeItem<String> node) {
        TreeItem<String> parent = node.getParent();

        if ((parent != null)) {
            return  getAbsolutePath(parent) + "/" + node.getValue();
        } else {
            return "";
        }
    }

    private void loadFileList(TreeItem<String> node) {
        try {
            String path = getAbsolutePath(node);
            List<ServerFile> fileComponents = controller.controlLoadingFileList(path);
            fileTreeUpdater.addAllComponents(fileComponents, node);
        } catch (MainControllerException e) {
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
                } catch (MainControllerException e) {
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
                } catch (MainControllerException e) {
                    new ConnectionErrorAlert(e);
                }
            });
        });
    }

    private void close() {
        controller.controlClose();
        memoManager.stopShowingServerAnswers();
        Platform.exit();
    }
}
