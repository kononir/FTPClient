package main.java.bsuir.ftpclient.windows.controllers;

import main.java.bsuir.ftpclient.connection.Connection;
import main.java.bsuir.ftpclient.exceptions.ConnectionNotExistException;
import main.java.bsuir.ftpclient.manager.AnswerManager;

import java.io.IOException;

public class DisconnectMenuItemController {
    public void controlDisconnect(Connection connection, AnswerManager answerManager)
            throws IOException, ConnectionNotExistException {
        connection.disconnect();

        answerManager.stopCheckingForAnswers();
    }
}
