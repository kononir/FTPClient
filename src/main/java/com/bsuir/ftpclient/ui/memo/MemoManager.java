package com.bsuir.ftpclient.ui.memo;

import com.bsuir.ftpclient.connection.ftp.control.ControlStructure;
import com.bsuir.ftpclient.ui.memo.controller.MemoManagerController;
import javafx.animation.AnimationTimer;
import javafx.scene.control.TextArea;

import java.util.List;
import java.util.concurrent.TimeoutException;

public class MemoManager {
    private AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            showLastAnswers(controller);
        }
    };

    private MemoUpdater memoUpdater;

    private MemoManagerController controller = new MemoManagerController();

    public MemoManager(TextArea memo) {
        this.memoUpdater = new MemoUpdater(memo);
    }

    public void startShowingServerAnswers() {
        controller.controlStartingListening();

        timer.start();
    }

    private void showLastAnswers(MemoManagerController controller) {
        try {
            List<ControlStructure> controlStructures = controller.controlGettingControlStructures();

            workWithControlStructures(controlStructures);
        } catch (TimeoutException | InterruptedException ignored) {
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
        String serverCloseControlConnection = "221";
        String serviceNotAvailable = "421";

        String answerCode = serverAnswer.substring(0, 3);

        boolean controlConnectionIsClosed = serverCloseControlConnection.equals(answerCode)
                || serviceNotAvailable.equals(answerCode);

        if (controlConnectionIsClosed) {
            stopShowingServerAnswers();
        }
    }

    public void stopShowingServerAnswers() {
        controller.controlStoppingListening();

        timer.stop();
    }
}
