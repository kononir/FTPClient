package main.java.bsuir.ftpclient.manager;

import javafx.animation.AnimationTimer;
import main.java.bsuir.ftpclient.connection.Connection;
import main.java.bsuir.ftpclient.connection.ConnectionListener;
import main.java.bsuir.ftpclient.connection.ConnectionListenerController;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeoutException;

public class AnswerManager {
    private AnimationTimer timer;
    private MemoUpdater memoUpdater;

    public AnswerManager(MemoUpdater memoUpdater) {
        this.memoUpdater = memoUpdater;
    }

    public void startCheckingForAnswers(Connection connection) {
        Exchanger<String> serverAnswerExchanger = new Exchanger<>();

        ConnectionListener listener = new ConnectionListener(connection, serverAnswerExchanger);

        ConnectionListenerController controller = new ConnectionListenerController(listener);
        controller.controlStartingListening(serverAnswerExchanger);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                checkedForAnswer(controller);
            }
        };

        timer.start();
    }

    private void checkedForAnswer(ConnectionListenerController controller) {
        try {
            String serverAnswer = controller.controlGettingServerAnswer();

            memoUpdater.addTextToMemo(serverAnswer + '\n');

            manageAnswer(serverAnswer);
        } catch (InterruptedException e) {
            e.printStackTrace();

            timer.stop();
        } catch (TimeoutException ignored) {
        }
    }

    private void manageAnswer(String serverAnswer) {
        String serverCloseControlConnection = "221";
        String serviceNotAvailable = "421";

        String answerCode = serverAnswer.substring(0, 3);

        boolean controlConnectionIsClosed = serverCloseControlConnection.equals(answerCode)
                || serviceNotAvailable.equals(answerCode);

        if (controlConnectionIsClosed) {
            timer.stop();
        }
    }

    public void stopCheckingForAnswers() {
        timer.stop();
    }
}
