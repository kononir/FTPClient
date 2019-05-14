package com.bsuir.ftpclient.ui.manager;

import com.bsuir.ftpclient.connection.ftp.control.ControlStructure;
import com.bsuir.ftpclient.logic.manager.MainWindowManagerController;
import com.bsuir.ftpclient.ui.alert.ConnectionErrorAlert;
import com.bsuir.ftpclient.ui.alert.DisconnectAlert;
import com.bsuir.ftpclient.ui.memo.MemoUpdater;
import javafx.animation.AnimationTimer;

import java.util.List;

public class MainWindowManager {
    private AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            showLastAnswers();
        }
    };

    private MemoUpdater memoUpdater;

    private MainWindowManagerController controller = new MainWindowManagerController();

    public MainWindowManager(MemoUpdater memoUpdater) {
        this.memoUpdater = memoUpdater;
    }

    public void startManaging() {
        controller.controlStartingListening();
        timer.start();
    }

    public void stopManaging() {
        controller.controlStoppingListening();
        timer.stop();
    }

    private void showLastAnswers() {
        List<ControlStructure> controlStructures = controller.controlGettingControlStructures();

        if (controlStructures != null) {
            workWithControlStructures(controlStructures);
        }
    }

    private void workWithControlStructures(List<ControlStructure> controlStructures) {
        for (ControlStructure controlStructure : controlStructures) {
            String request = controlStructure.getRequest();
            if (!request.isEmpty()) {
                memoUpdater.addTextToMemo(request + '\n');
            }

            String response = controlStructure.getResponse();
            memoUpdater.addTextToMemo(response + '\n');

            workWithResponseCode(response);
        }
    }

    private void workWithResponseCode(String serverAnswer) {
        String answerCode = serverAnswer.substring(0, 3);
        boolean controlConnectionIsClosed = "221".equals(answerCode) || "421".equals(answerCode);
        if (controlConnectionIsClosed) {
            timer.stop();
            new DisconnectAlert().show();
        }

        String firstDigit = serverAnswer.substring(0, 1);
        boolean errorAnswer = "4".equals(firstDigit) || "5".equals(firstDigit);
        if (errorAnswer) {
            new ConnectionErrorAlert().show(serverAnswer);
        }
    }
}
