package com.bsuir.ftpclient.ui.window;

import com.bsuir.ftpclient.connection.ftp.data.DataType;
import com.bsuir.ftpclient.logic.MainController;
import com.bsuir.ftpclient.ui.dialog.AuthenticationDialog;
import com.bsuir.ftpclient.ui.dialog.HostnameDialog;
import com.bsuir.ftpclient.ui.dialog.choose.*;
import com.bsuir.ftpclient.ui.manager.MainWindowManager;
import com.bsuir.ftpclient.ui.tree.TreeUpdater;
import com.bsuir.ftpclient.ui.tree.TypedTreeItem;
import com.bsuir.ftpclient.ui.window.exception.MainControllerException;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.atLeastOnce;

public class MainWindowTests {
    private static final String ANY = "any";
    private static final String ANY_PATH = "any/any";
    private static final String ASCII = "ASCII";

    private static final boolean IS_PACKAGE = true;

    private MainWindow mainWindow;

    private TreeUpdater fileTreeUpdater;
    private MainWindowManager mainWindowManager;
    private MainController controller;

    @Before
    public void setUp() {
        fileTreeUpdater = mock(TreeUpdater.class);
        mainWindowManager = mock(MainWindowManager.class);
        controller = mock(MainController.class);

        mainWindow = new MainWindow(fileTreeUpdater, mainWindowManager, controller);
    }

    @Test
    public void testShow() throws InterruptedException {
        new Thread(() -> {
            new JFXPanel();
            Platform.runLater(() -> {
                Stage stage = new Stage();

                mainWindow.show(stage);

                stage.close();
            });
        }).start();

        Thread.sleep(5000);
    }

    @Test
    public void testConnect() throws MainControllerException {
        HostnameDialog hostnameDialog = mock(HostnameDialog.class);
        when(hostnameDialog.showDialog()).thenReturn(Optional.of(ANY));
        AuthenticationDialog authenticationDialog = mock(AuthenticationDialog.class);
        when(authenticationDialog.showDialog()).thenReturn(Optional.of(new Pair<>(ANY, ANY)));

        TreeView<String> treeView = (TreeView<String>) mock(TreeView.class);
        when(fileTreeUpdater.getTree()).thenReturn(treeView);

        mainWindow.connect(hostnameDialog, authenticationDialog, null);

        verify(controller, atLeastOnce()).controlConnecting(ANY);
        verify(controller, atLeastOnce()).controlAuthenticating(any());
        verify(mainWindowManager, atLeastOnce()).startManaging();
        verify(fileTreeUpdater, atLeastOnce()).getTree();
    }

    @Test
    public void testDisconnect() {
        mainWindow.disconnect();

        verify(fileTreeUpdater, atLeastOnce()).clearTree();
        verify(controller, atLeastOnce()).controlDisconnecting();
    }

    @Test
    public void testLoadCatalogue() throws MainControllerException {
        ServerCatalogueDialog serverCatalogueDialog = mock(ServerCatalogueDialog.class);
        when(serverCatalogueDialog.showDialog()).thenReturn(Optional.of(ANY));
        ClientCatalogueDialog clientCatalogueDialog = mock(ClientCatalogueDialog.class);
        when(clientCatalogueDialog.chooseCataloguePath()).thenReturn(Optional.of(ANY));

        mainWindow.loadCatalogue(serverCatalogueDialog, clientCatalogueDialog, null);

        verify(controller, atLeastOnce()).controlLoadingCatalogue(ANY, ANY_PATH);
    }

    @Test
    public void testSaveCatalogue() throws MainControllerException {
        ServerCatalogueDialog serverCatalogueDialog = mock(ServerCatalogueDialog.class);
        when(serverCatalogueDialog.showDialog()).thenReturn(Optional.of(ANY));
        ClientCatalogueDialog clientCatalogueDialog = mock(ClientCatalogueDialog.class);
        when(clientCatalogueDialog.chooseCataloguePath()).thenReturn(Optional.of(ANY));

        mainWindow.saveCatalogue(clientCatalogueDialog, serverCatalogueDialog, null);

        verify(controller, atLeastOnce()).controlSavingCatalogue(ANY, ANY);
    }

    @Test
    public void testLoadFile() throws MainControllerException {
        ServerFileDialog serverFileDialog = mock(ServerFileDialog.class);
        when(serverFileDialog.showDialog()).thenReturn(Optional.of(ANY));
        ClientCatalogueDialog clientCatalogueDialog = mock(ClientCatalogueDialog.class);
        when(clientCatalogueDialog.chooseCataloguePath()).thenReturn(Optional.of(ANY));

        mainWindow.loadFile(serverFileDialog, clientCatalogueDialog, null);

        verify(controller, atLeastOnce()).controlLoadingFile(ANY, ANY_PATH);
    }

    @Test
    public void testSaveFile() throws MainControllerException {
        ClientFileDialog clientFileDialog = mock(ClientFileDialog.class);
        when(clientFileDialog.chooseFilePath()).thenReturn(Optional.of(ANY));
        ServerFileDialog serverFileDialog = mock(ServerFileDialog.class);
        when(serverFileDialog.showDialog()).thenReturn(Optional.of(ANY));

        mainWindow.saveFile(clientFileDialog, serverFileDialog, null);

        verify(controller, atLeastOnce()).controlSavingFile(ANY, ANY);
    }

    @Test
    public void testChangeWorkingDirectory() {
        TreeItem<String> node = new TypedTreeItem<>(ANY, IS_PACKAGE);
        when(fileTreeUpdater.getAbsolutePath(node)).thenReturn(ANY);

        mainWindow.changeWorkingDirectory(node);

        verify(controller, atLeastOnce()).controlChangeWorkingDirectory(ANY);
    }

    @Test
    public void testCreateCatalogue() {
        ServerCatalogueDialog serverCatalogueDialog = mock(ServerCatalogueDialog.class);
        when(serverCatalogueDialog.showDialog()).thenReturn(Optional.of(ANY));

        mainWindow.createCatalogue(serverCatalogueDialog);

        verify(controller, atLeastOnce()).controlCreatingCatalogue(ANY);
    }

    @Test
    public void testDeleteCatalogue() {
        ServerCatalogueDialog serverCatalogueDialog = mock(ServerCatalogueDialog.class);
        when(serverCatalogueDialog.showDialog()).thenReturn(Optional.of(ANY));

        mainWindow.deleteCatalogue(serverCatalogueDialog);

        verify(controller, atLeastOnce()).controlDeletingCatalogue(ANY);
    }

    @Test
    public void testDeleteFile() {
        ServerFileDialog serverFileDialog = mock(ServerFileDialog.class);
        when(serverFileDialog.showDialog()).thenReturn(Optional.of(ANY));

        mainWindow.deleteFile(serverFileDialog);

        verify(controller, atLeastOnce()).controlDeletingFile(ANY);
    }

    @Test
    public void testChangeDataType() {
        ChoiceDataTypeDialog choiceDataTypeDialog = mock(ChoiceDataTypeDialog.class);
        when(choiceDataTypeDialog.showDialog()).thenReturn(Optional.of(ASCII));

        mainWindow.changeDataType(choiceDataTypeDialog);

        verify(controller, atLeastOnce()).controlChangeDataType(DataType.ASCII);
    }
}
