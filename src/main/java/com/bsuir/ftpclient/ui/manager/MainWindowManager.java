package com.bsuir.ftpclient.ui.manager;

import com.bsuir.ftpclient.connection.ftp.control.ControlStructure;
import com.bsuir.ftpclient.ui.dialog.WaitingDialog;
import com.bsuir.ftpclient.ui.memo.MemoUpdater;
import javafx.animation.AnimationTimer;
import javafx.scene.control.TextArea;

import java.util.List;

public class MainWindowManager {
    private AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            showLastAnswers(controller);
        }
    };

    private WaitingDialog waitingDialog;
    private MemoUpdater memoUpdater;

    private MainWindowManagerController controller = new MainWindowManagerController();

    public MainWindowManager(TextArea memo, WaitingDialog waitingDialog) {
        this.memoUpdater = new MemoUpdater(memo);
        this.waitingDialog = waitingDialog;
    }

    public void startShowingServerAnswers() {
        controller.controlStartingListening();

        timer.start();
    }

    private void showLastAnswers(MainWindowManagerController controller) {
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
        }

        String firstDigit = serverAnswer.substring(0, 1);
        boolean expectedOneMoreCommand = "1".equals(firstDigit);
        if (expectedOneMoreCommand && !waitingDialog.isShowing()) {
            waitingDialog.show();
        } else if (waitingDialog != null && waitingDialog.isShowing()) {
            waitingDialog.close();
        }
    }

    public void stopShowingServerAnswers() {
        controller.controlStoppingListening();
        timer.stop();
    }
}
