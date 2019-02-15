package main.java.bsuir.ftpclient.dialogs;

import javafx.scene.control.TextInputDialog;
import main.java.bsuir.ftpclient.alerts.ConnectionErrorAlert;
import main.java.bsuir.ftpclient.connection.Connection;
import main.java.bsuir.ftpclient.dialogs.controllers.HostnameDialogController;
import main.java.bsuir.ftpclient.exceptions.ConnectionExistException;
import main.java.bsuir.ftpclient.manager.AnswerManager;
import main.java.bsuir.ftpclient.manager.MemoUpdater;

import java.io.IOException;
import java.util.Optional;

public class HostnameDialog {
    private TextInputDialog dialogWindow;
    private AnswerManager answerManager;

    public HostnameDialog() {
        dialogWindow = new TextInputDialog("localhost");
        dialogWindow.setTitle("Text input");
        dialogWindow.setHeaderText("Text input");
        dialogWindow.setContentText("Please enter domain name or ip");
    }

    public AnswerManager connect(Connection connection, MemoUpdater memoUpdater) {
        Optional<String> result = dialogWindow.showAndWait();

        result.ifPresent(connectInform -> {
            try {
                answerManager = new HostnameDialogController(connectInform).controlConnect(connection, memoUpdater);
            } catch (IOException | ConnectionExistException e) {
                new ConnectionErrorAlert(e);
            }
        });

        return answerManager;
    }
}
