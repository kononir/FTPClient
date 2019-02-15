package main.java.bsuir.ftpclient.dialogs.controllers;

import main.java.bsuir.ftpclient.manager.AnswerManager;
import main.java.bsuir.ftpclient.connection.Connection;
import main.java.bsuir.ftpclient.exceptions.ConnectionExistException;
import main.java.bsuir.ftpclient.manager.MemoUpdater;

import java.io.IOException;

public class HostnameDialogController {
    private String dialogInformation;

    public HostnameDialogController(String dialogInformation) {
        this.dialogInformation = dialogInformation;
    }

    public AnswerManager controlConnect(Connection connection, MemoUpdater memoUpdater)
            throws IOException, ConnectionExistException {
        connection.connect(dialogInformation);

        AnswerManager answerManager = new AnswerManager(memoUpdater);
        answerManager.startCheckingForAnswers(connection);

        return answerManager;
    }


}
