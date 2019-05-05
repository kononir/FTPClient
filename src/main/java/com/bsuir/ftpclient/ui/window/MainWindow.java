package com.bsuir.ftpclient.ui.window;

import com.bsuir.ftpclient.connection.ftp.data.DataType;
import com.bsuir.ftpclient.connection.ftp.data.file.ServerFile;
import com.bsuir.ftpclient.ui.alert.ConnectionErrorAlert;
import com.bsuir.ftpclient.ui.alert.DisconnectAlert;
import com.bsuir.ftpclient.ui.dialog.*;
import com.bsuir.ftpclient.ui.dialog.choose.*;
import com.bsuir.ftpclient.ui.manager.MainWindowManager;
import com.bsuir.ftpclient.ui.tree.TreeUpdater;
import com.bsuir.ftpclient.ui.tree.TypedTreeItem;
import com.bsuir.ftpclient.ui.window.exception.MainControllerException;
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
    private MainWindowManager mainWindowManager;

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

        MenuItem saveCatalogue = new MenuItem("Save");
        saveCatalogue.setOnAction(event -> saveCatalogue());

        Menu catalogueMenu = new Menu("Catalogue");
        catalogueMenu.getItems().addAll(
                createCatalogue,
                deleteCatalogue,
                new SeparatorMenuItem(),
                loadCatalogue,
                saveCatalogue
        );

        MenuItem deleteFile = new MenuItem("Delete");
        deleteFile.setOnAction(event -> deleteFile());

        MenuItem loadFile = new MenuItem("Load");
        loadFile.setOnAction(event -> loadFile());

        MenuItem saveFile = new MenuItem("Save");
        saveFile.setOnAction(event -> saveFile());

        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(
                deleteFile,
                new SeparatorMenuItem(),
                loadFile,
                saveFile
        );

        MenuItem changeDataType = new MenuItem("Data type");
        changeDataType.setOnAction(event -> changeDataType());

        Menu optionsMenu = new Menu("Options");
        optionsMenu.getItems().addAll(
                changeDataType
        );

        menuBar.getMenus().addAll(connectionMenu, catalogueMenu, fileMenu, optionsMenu);

        TextArea answerMemo = new TextArea();
        answerMemo.setEditable(false);

        mainWindowManager = new MainWindowManager(answerMemo, new WaitingDialog(controller));

        ScrollPane memoScrolling = new ScrollPane();
        memoScrolling.setContent(answerMemo);
        memoScrolling.setFitToHeight(true);
        memoScrolling.setFitToWidth(true);

        TreeItem<String> root = new TypedTreeItem<>("/", true);
        TreeView<String> fileTree = new TreeView<>(root);
        fileTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            TypedTreeItem<String> node = (TypedTreeItem<String>) newValue;
            if (node != null && node.isPackage()) {
                if (node.isLeaf()) {
                    loadFileList(newValue);
                }

                changeWorkingDirectory(newValue);
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
                mainWindowManager.startShowingServerAnswers();

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

    private void loadFileList(TreeItem<String> node) {
        try {
            String path = fileTreeUpdater.getAbsolutePath(node);
            List<ServerFile> fileComponents = controller.controlLoadingFileList(path);
            fileTreeUpdater.addAllComponents(fileComponents, node);
        } catch (MainControllerException e) {
            new ConnectionErrorAlert(e);
        }
    }

    private void changeWorkingDirectory(TreeItem<String> node) {
        try {
            String path = fileTreeUpdater.getAbsolutePath(node);
            controller.controlChangeWorkingDirectory(path);
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
                    controller.controlLoadingCatalogue(fromPath, toPath + "/" + fromPath);
                } catch (MainControllerException e) {
                    new ConnectionErrorAlert(e);
                }
            });
        });
    }

    private void saveCatalogue() {
        Optional<String> optionalFrom = new ClientCatalogueDialog().chooseCataloguePath();
        optionalFrom.ifPresent(fromPath -> {
            Optional<String> optionalTo = new ServerCatalogueDialog().showAndWait();
            optionalTo.ifPresent(toPath -> {
                try {
                    controller.controlSavingCatalogue(fromPath, toPath);
                } catch (MainControllerException e) {
                    new ConnectionErrorAlert(e);
                }
            });
        });
    }

    private void deleteFile() {
        Optional<String> result = new ServerFileDialog().showAndWait();
        result.ifPresent(fileName -> controller.controlDeletingFile(fileName));
    }

    private void loadFile() {
        Optional<String> optionalFrom = new ServerFileDialog().showAndWait();
        optionalFrom.ifPresent(fromPath -> {
            Optional<String> optionalTo = new ClientCatalogueDialog().chooseCataloguePath();
            optionalTo.ifPresent(toPath -> {
                try {
                    controller.controlLoadingFile(fromPath, toPath + '/' + fromPath);
                } catch (MainControllerException e) {
                    new ConnectionErrorAlert(e);
                }
            });
        });
    }

    private void saveFile() {
        Optional<String> optionalFrom = new ClientFileDialog().chooseFilePath();
        optionalFrom.ifPresent(fromFile -> {
            Optional<String> optionalTo = new ServerFileDialog().showAndWait();
            optionalTo.ifPresent(toFile -> {
                try {
                    controller.controlSavingFile(fromFile, toFile);
                } catch (MainControllerException e) {
                    new ConnectionErrorAlert(e);
                }
            });
        });
    }

    private void changeDataType() {
        Optional<String> optionalDataType = new ChoiceDataTypeDialog().showAndWait();
        optionalDataType.ifPresent(dataType -> controller.controlChangeDataType(DataType.valueOf(dataType)));
    }

    private void close() {
        controller.controlClose();
        mainWindowManager.stopShowingServerAnswers();
        Platform.exit();
    }
}
