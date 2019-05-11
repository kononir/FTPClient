package com.bsuir.ftpclient.ui.window;

import com.bsuir.ftpclient.connection.ftp.data.DataType;
import com.bsuir.ftpclient.connection.ftp.data.file.ServerFile;
import com.bsuir.ftpclient.ui.alert.ConnectionErrorAlert;
import com.bsuir.ftpclient.ui.dialog.AuthenticationDialog;
import com.bsuir.ftpclient.ui.dialog.HostnameDialog;
import com.bsuir.ftpclient.ui.dialog.WaitingDialog;
import com.bsuir.ftpclient.ui.dialog.choose.*;
import com.bsuir.ftpclient.ui.manager.MainWindowManager;
import com.bsuir.ftpclient.ui.tree.TreeUpdater;
import com.bsuir.ftpclient.ui.tree.TypedTreeItem;
import com.bsuir.ftpclient.ui.window.exception.MainControllerException;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class MainWindow {
    private TreeUpdater fileTreeUpdater;
    private MainWindowManager mainWindowManager;

    private MainWindowController controller = new MainWindowController();

    //----- For tests -----
    private static final String ANY = "any";
    private static final String ANY_PATH = "any/any";

    @Before
    public void setUp() {
        controller = mock(MainWindowController.class);
        fileTreeUpdater = mock(TreeUpdater.class);
        mainWindowManager = mock(MainWindowManager.class);
    }
    //---------------------

    public void show(Stage stage) {
        MenuBar menuBar = new MenuBar();
        StackPane.setAlignment(menuBar, Pos.TOP_CENTER);

        MenuItem connect = new MenuItem("Connect");
        connect.setOnAction(event -> connect(
                new HostnameDialog(),
                new AuthenticationDialog(),
                new ConnectionErrorAlert()
        ));

        MenuItem disconnect = new MenuItem("Disconnect");
        disconnect.setOnAction(event -> disconnect());

        Menu connectionMenu = new Menu("Server");
        connectionMenu.getItems().addAll(connect, disconnect);

        MenuItem createCatalogue = new MenuItem("Create");
        createCatalogue.setOnAction(event -> createCatalogue(new ServerCatalogueDialog()));

        MenuItem deleteCatalogue = new MenuItem("Delete");
        deleteCatalogue.setOnAction(event -> deleteCatalogue(new ServerCatalogueDialog()));

        MenuItem loadCatalogue = new MenuItem("Load");
        loadCatalogue.setOnAction(event -> loadCatalogue(
                new ServerCatalogueDialog(),
                new ClientCatalogueDialog(),
                new ConnectionErrorAlert()
        ));

        MenuItem saveCatalogue = new MenuItem("Save");
        saveCatalogue.setOnAction(event -> saveCatalogue(
                new ClientCatalogueDialog(),
                new ServerCatalogueDialog(),
                new ConnectionErrorAlert()
        ));

        Menu catalogueMenu = new Menu("Catalogue");
        catalogueMenu.getItems().addAll(
                createCatalogue,
                deleteCatalogue,
                new SeparatorMenuItem(),
                loadCatalogue,
                saveCatalogue
        );

        MenuItem deleteFile = new MenuItem("Delete");
        deleteFile.setOnAction(event -> deleteFile(
                new ServerFileDialog()
        ));

        MenuItem loadFile = new MenuItem("Load");
        loadFile.setOnAction(event -> loadFile(
                new ServerFileDialog(),
                new ClientCatalogueDialog(),
                new ConnectionErrorAlert()
        ));

        MenuItem saveFile = new MenuItem("Save");
        saveFile.setOnAction(event -> saveFile(
                new ClientFileDialog(),
                new ServerFileDialog(),
                new ConnectionErrorAlert()
        ));

        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(
                deleteFile,
                new SeparatorMenuItem(),
                loadFile,
                saveFile
        );

        MenuItem changeDataType = new MenuItem("Data type");
        changeDataType.setOnAction(event -> changeDataType(
                new ChoiceDataTypeDialog()
        ));

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
                    loadFileList(newValue, new ConnectionErrorAlert());
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

        stage.setOnCloseRequest(event -> close());
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testShow() throws InterruptedException {
        new Thread(() -> {
            new JFXPanel();
            Platform.runLater(() -> {
                Stage stage = new Stage();

                show(stage);

                stage.close();
            });
        }).start();

        Thread.sleep(5000);
    }

    private void connect(HostnameDialog hostnameDialog, AuthenticationDialog authenticationDialog,
                         ConnectionErrorAlert connectionErrorAlert) {
        Optional<String> result = hostnameDialog.showDialog();

        result.ifPresent(connectInformation -> {
            try {
                controller.controlConnecting(connectInformation);
                mainWindowManager.startShowingServerAnswers();

                Optional<Pair<String, String>> authenticationOptional = authenticationDialog.showDialog();

                if (authenticationOptional.isPresent()) {
                    controller.controlAuthenticating(authenticationOptional.get());

                    loadFileList(fileTreeUpdater.getTree().getRoot(), new ConnectionErrorAlert());
                } else {
                    controller.controlDisconnecting();
                }
            } catch (MainControllerException e) {
                connectionErrorAlert.show(e.getMessage());
            }
        });
    }

    @Test
    public void testConnect() throws MainControllerException {
        HostnameDialog hostnameDialog = mock(HostnameDialog.class);
        when(hostnameDialog.showDialog()).thenReturn(Optional.of(ANY));
        AuthenticationDialog authenticationDialog = mock(AuthenticationDialog.class);
        when(authenticationDialog.showDialog()).thenReturn(Optional.of(new Pair<>(ANY, ANY)));

        TreeView<String> treeView = (TreeView<String>) mock(TreeView.class);
        when(fileTreeUpdater.getTree()).thenReturn(treeView);

        connect(hostnameDialog, authenticationDialog, null);

        verify(controller, atLeastOnce()).controlConnecting(ANY);
        verify(controller, atLeastOnce()).controlAuthenticating(any());
        verify(mainWindowManager, atLeastOnce()).startShowingServerAnswers();
        verify(fileTreeUpdater, atLeastOnce()).getTree();
    }

    private void disconnect() {
        fileTreeUpdater.clearTree();
        controller.controlDisconnecting();
    }

    @Test
    public void testDisconnect() {
        disconnect();

        verify(fileTreeUpdater, atLeastOnce()).clearTree();
        verify(controller, atLeastOnce()).controlDisconnecting();
    }

    private void loadFileList(TreeItem<String> node, ConnectionErrorAlert connectionErrorAlert) {
        try {
            String path = fileTreeUpdater.getAbsolutePath(node);
            List<ServerFile> fileComponents = controller.controlLoadingFileList(path);
            fileTreeUpdater.addAllComponents(fileComponents, node);
        } catch (MainControllerException e) {
            connectionErrorAlert.show(e.getMessage());
        }
    }

    private void changeWorkingDirectory(TreeItem<String> node) {
        String path = fileTreeUpdater.getAbsolutePath(node);
        controller.controlChangeWorkingDirectory(path);
    }

    private void createCatalogue(ServerCatalogueDialog serverCatalogueDialog) {
        Optional<String> result = serverCatalogueDialog.showDialog();
        result.ifPresent(catalogueName -> controller.controlCreatingCatalogue(catalogueName));
    }

    private void deleteCatalogue(ServerCatalogueDialog serverCatalogueDialog) {
        Optional<String> result = serverCatalogueDialog.showDialog();
        result.ifPresent(catalogueName -> controller.controlDeletingCatalogue(catalogueName));
    }

    private void loadCatalogue(ServerCatalogueDialog serverCatalogueDialog,
                               ClientCatalogueDialog clientCatalogueDialog,
                               ConnectionErrorAlert connectionErrorAlert) {
        Optional<String> optionalFrom = serverCatalogueDialog.showDialog();
        optionalFrom.ifPresent(fromPath -> {
            Optional<String> optionalTo = clientCatalogueDialog.chooseCataloguePath();
            optionalTo.ifPresent(toPath -> {
                try {
                    controller.controlLoadingCatalogue(fromPath, toPath + "/" + fromPath);
                } catch (MainControllerException e) {
                    connectionErrorAlert.show(e.getMessage());
                }
            });
        });
    }

    @Test
    public void testLoadCatalogue() throws MainControllerException {
        ServerCatalogueDialog serverCatalogueDialog = mock(ServerCatalogueDialog.class);
        when(serverCatalogueDialog.showDialog()).thenReturn(Optional.of(ANY));
        ClientCatalogueDialog clientCatalogueDialog = mock(ClientCatalogueDialog.class);
        when(clientCatalogueDialog.chooseCataloguePath()).thenReturn(Optional.of(ANY));

        loadCatalogue(serverCatalogueDialog, clientCatalogueDialog, null);

        verify(controller, atLeastOnce()).controlLoadingCatalogue(ANY, ANY_PATH);
    }

    private void saveCatalogue(ClientCatalogueDialog clientCatalogueDialog,
                               ServerCatalogueDialog serverCatalogueDialog,
                               ConnectionErrorAlert connectionErrorAlert) {
        Optional<String> optionalFrom = clientCatalogueDialog.chooseCataloguePath();
        optionalFrom.ifPresent(fromPath -> {
            Optional<String> optionalTo = serverCatalogueDialog.showDialog();
            optionalTo.ifPresent(toPath -> {
                try {
                    controller.controlSavingCatalogue(fromPath, toPath);
                } catch (MainControllerException e) {
                    connectionErrorAlert.show(e.getMessage());
                }
            });
        });
    }

    @Test
    public void testSaveCatalogue() throws MainControllerException {
        ServerCatalogueDialog serverCatalogueDialog = mock(ServerCatalogueDialog.class);
        when(serverCatalogueDialog.showDialog()).thenReturn(Optional.of(ANY));
        ClientCatalogueDialog clientCatalogueDialog = mock(ClientCatalogueDialog.class);
        when(clientCatalogueDialog.chooseCataloguePath()).thenReturn(Optional.of(ANY));

        saveCatalogue(clientCatalogueDialog, serverCatalogueDialog, null);

        verify(controller, atLeastOnce()).controlSavingCatalogue(ANY, ANY);
    }

    private void deleteFile(ServerFileDialog serverFileDialog) {
        Optional<String> result = serverFileDialog.showDialog();
        result.ifPresent(fileName -> controller.controlDeletingFile(fileName));
    }

    private void loadFile(ServerFileDialog serverFileDialog, ClientCatalogueDialog clientCatalogueDialog,
                          ConnectionErrorAlert connectionErrorAlert) {
        Optional<String> optionalFrom = serverFileDialog.showDialog();
        optionalFrom.ifPresent(fromPath -> {
            Optional<String> optionalTo = clientCatalogueDialog.chooseCataloguePath();
            optionalTo.ifPresent(toPath -> {
                try {
                    controller.controlLoadingFile(fromPath, toPath + '/' + fromPath);
                } catch (MainControllerException e) {
                    connectionErrorAlert.show(e.getMessage());
                }
            });
        });
    }

    @Test
    public void testLoadFile() throws MainControllerException {
        ServerFileDialog serverFileDialog = mock(ServerFileDialog.class);
        when(serverFileDialog.showDialog()).thenReturn(Optional.of(ANY));
        ClientCatalogueDialog clientCatalogueDialog = mock(ClientCatalogueDialog.class);
        when(clientCatalogueDialog.chooseCataloguePath()).thenReturn(Optional.of(ANY));

        loadFile(serverFileDialog, clientCatalogueDialog, null);

        verify(controller, atLeastOnce()).controlLoadingFile(ANY, ANY_PATH);
    }

    private void saveFile(ClientFileDialog clientFileDialog, ServerFileDialog serverFileDialog,
                          ConnectionErrorAlert connectionErrorAlert) {
        Optional<String> optionalFrom = clientFileDialog.chooseFilePath();
        optionalFrom.ifPresent(fromFile -> {
            Optional<String> optionalTo = serverFileDialog.showDialog();
            optionalTo.ifPresent(toFile -> {
                try {
                    controller.controlSavingFile(fromFile, toFile);
                } catch (MainControllerException e) {
                    connectionErrorAlert.show(e.getMessage());
                }
            });
        });
    }

    @Test
    public void testSaveFile() throws MainControllerException {
        ClientFileDialog clientFileDialog = mock(ClientFileDialog.class);
        when(clientFileDialog.chooseFilePath()).thenReturn(Optional.of(ANY));
        ServerFileDialog serverFileDialog = mock(ServerFileDialog.class);
        when(serverFileDialog.showDialog()).thenReturn(Optional.of(ANY));

        saveFile(clientFileDialog, serverFileDialog, null);

        verify(controller, atLeastOnce()).controlSavingFile(ANY, ANY);
    }

    private void changeDataType(ChoiceDataTypeDialog choiceDataTypeDialog) {
        Optional<String> optionalDataType = choiceDataTypeDialog.showDialog();
        optionalDataType.ifPresent(dataType -> controller.controlChangeDataType(DataType.valueOf(dataType)));
    }

    private void close() {
        controller.controlClose();
        mainWindowManager.stopShowingServerAnswers();
        Platform.exit();
    }
}
