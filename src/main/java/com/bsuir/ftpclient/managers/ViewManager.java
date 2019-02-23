package com.bsuir.ftpclient.managers;

import com.bsuir.ftpclient.connection.Connection;
import com.bsuir.ftpclient.connection.ConnectionListener;
import com.bsuir.ftpclient.updaters.MemoUpdater;
import javafx.animation.AnimationTimer;
import javafx.scene.control.TextArea;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ViewManager {
    private AnimationTimer timer;
    private MemoUpdater memoUpdater;

    public ViewManager(TextArea memo) {
        this.memoUpdater = new MemoUpdater(memo);
    }

    public void startCheckingForAnswers(Connection connection) {
        Exchanger<String> serverAnswerExchanger = new Exchanger<>();

        new Thread(new ConnectionListener(connection, serverAnswerExchanger)).start();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                checkForAnswer(serverAnswerExchanger);
            }
        };

        timer.start();
    }

    private void checkForAnswer(Exchanger<String> serverAnswerExchanger) {
        try {
            int timeout = 1;

            String serverAnswer = serverAnswerExchanger.exchange(null, timeout, TimeUnit.MILLISECONDS);

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
