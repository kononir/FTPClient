package main.java.bsuir.ftpclient.windows;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import main.java.bsuir.ftpclient.alerts.ConnectionErrorAlert;
import main.java.bsuir.ftpclient.alerts.DisconnectAlert;
import main.java.bsuir.ftpclient.dialogs.AuthenticationDialog;
import main.java.bsuir.ftpclient.dialogs.CatalogueWorkingDialog;
import main.java.bsuir.ftpclient.dialogs.HostnameDialog;
import main.java.bsuir.ftpclient.exceptions.ConnectionExistException;
import main.java.bsuir.ftpclient.exceptions.ConnectionNotExistException;
import main.java.bsuir.ftpclient.managers.ViewManager;
import main.java.bsuir.ftpclient.windows.controllers.MainWindowController;

import java.io.IOException;
import java.util.Optional;

public class MainWindow {
    private TreeView<String> fileTree;

    private ViewManager viewManager;

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
                    controller.controlStartingCheckForAnswers(viewManager);
                } catch (IOException | ConnectionExistException e) {
                    new ConnectionErrorAlert(e);
                }
            });
        });

        MenuItem authenticate = new MenuItem("Authenticate");
        authenticate.setOnAction(event -> {
            Optional<Pair<String, String>> authenticationOptional = new AuthenticationDialog().showAndWait();

            authenticationOptional.ifPresent(authenticationInformation -> {
                try {
                    controller.controlAuthenticating(authenticationInformation);
                    controller.controlStartingCheckForFileNames(fileTree);
                } catch (IOException | ConnectionNotExistException | ConnectionExistException e) {
                    new ConnectionErrorAlert(e);
                }
            });
        });

        MenuItem disconnect = new MenuItem("Disconnect");
        disconnect.setOnAction(event -> {
            try {
                controller.controlDisconnecting();

                new DisconnectAlert();
            } catch (ConnectionNotExistException | IOException e) {
                new ConnectionErrorAlert(e);
            }
        });

        Menu connectionMenu = new Menu("Server");
        connectionMenu.getItems().addAll(connect, disconnect, new SeparatorMenuItem(), authenticate);

        MenuItem createCatalogue = new MenuItem("Create");
        createCatalogue.setOnAction(event -> {
            Optional<String> result = new CatalogueWorkingDialog().showAndWait();

            result.ifPresent(catalogueName -> {
                try {
                    controller.controlCreatingCatalogue(catalogueName);
                } catch (IOException | ConnectionNotExistException e) {
                    new ConnectionErrorAlert(e);
                }
            });
        });

        MenuItem deleteCatalogue = new MenuItem("Delete");
        deleteCatalogue.setOnAction(event -> {
            Optional<String> result = new CatalogueWorkingDialog().showAndWait();

            result.ifPresent(catalogueName -> {
                try {
                    controller.controlDeletingCatalogue(catalogueName);
                } catch (IOException | ConnectionNotExistException e) {
                    new ConnectionErrorAlert(e);
                }
            });
        });

        Menu catalogueMenu = new Menu("Catalogue");
        catalogueMenu.getItems().addAll(createCatalogue, deleteCatalogue);

        Menu fileMenu = new Menu("File");

        menuBar.getMenus().addAll(connectionMenu, catalogueMenu, fileMenu);

        TextArea answerMemo = new TextArea();
        viewManager = new ViewManager(answerMemo);

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
            controller.controlDisconnecting();
            controller.controlStoppingCheckForAnswers(viewManager);
        } catch (IOException | ConnectionNotExistException ignored) {
        } finally {
            Platform.exit();
        }
    }
}
