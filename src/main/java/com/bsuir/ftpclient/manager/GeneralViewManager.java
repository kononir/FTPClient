package com.bsuir.ftpclient.manager;

import com.bsuir.ftpclient.connection.ftp.control.ControlStructure;
import com.bsuir.ftpclient.manager.controller.ViewManagerController;
import com.bsuir.ftpclient.updater.MemoUpdater;
import javafx.animation.AnimationTimer;
import javafx.scene.control.TextArea;

import java.util.List;
import java.util.concurrent.TimeoutException;

public class GeneralViewManager {
    private AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            showLastAnswers(controller);
        }
    };

    private MemoUpdater memoUpdater;

    private ViewManagerController controller = new ViewManagerController();

    public GeneralViewManager(TextArea memo) {
        this.memoUpdater = new MemoUpdater(memo);
    }

    public void startShowingServerAnswers() {
        controller.controlStartingListening();

        timer.start();
    }

    private void showLastAnswers(ViewManagerController controller) {
        try {
            List<ControlStructure> controlStructures = controller.controlGettingControlStructures();

            workWithControlStructures(controlStructures);
        } catch (TimeoutException | InterruptedException ignored) {
        }
    }

    private void workWithControlStructures(List<ControlStructure> controlStructures) {
        for (ControlStructure controlStructure : controlStructures) {
            String request = controlStructure.getRequest();
            memoUpdater.addTextToMemo(request + '\n');

            String response = controlStructure.getResponse();
            memoUpdater.addTextToMemo(response + '\n');

            workWithResponseCode(response);
        }
    }

    private void workWithResponseCode(String serverAnswer) {
        String serverCloseControlConnection = "221";
        String serviceNotAvailable = "421";

        String answerCode = serverAnswer.substring(0, 3);

        boolean controlConnectionIsClosed = serverCloseControlConnection.equals(answerCode)
                || serviceNotAvailable.equals(answerCode);

        if (controlConnectionIsClosed) {
            stopCheckingForAnswers();
        }
    }

    public void stopCheckingForAnswers() {
        controller.controlStoppingListening();

        timer.stop();
    }
}
