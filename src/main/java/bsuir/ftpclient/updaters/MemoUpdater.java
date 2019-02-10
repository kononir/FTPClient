package main.java.bsuir.ftpclient.updaters;

import javafx.animation.AnimationTimer;
import javafx.scene.control.TextArea;
import main.java.bsuir.ftpclient.connection.Connection;
import main.java.bsuir.ftpclient.connection.ConnectionListener;
import main.java.bsuir.ftpclient.connection.ConnectionListenerController;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeoutException;

public class MemoUpdater {

    private TextArea memo;
    private AnimationTimer timer;

    public MemoUpdater(TextArea memo) {
        this.memo = memo;
    }

    public void startPrintAnswers(Connection connection) {
        Exchanger<String> serverAnswerExchanger = new Exchanger<>();

        ConnectionListener listener = new ConnectionListener(connection, serverAnswerExchanger);

        ConnectionListenerController controller = new ConnectionListenerController(listener);
        controller.controlStartingListening(serverAnswerExchanger);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateAnswer(controller);
            }
        };

        timer.start();
    }

    public void stopPrintAnswers() {
        timer.stop();
    }

    private void updateAnswer(ConnectionListenerController controller) {
        try {
            String serverAnswer = controller.controlGettingServerAnswer();

            addTextToMemo(serverAnswer + '\n');
        } catch (InterruptedException e) {
            e.printStackTrace();

            timer.stop();
        } catch (TimeoutException ignored) {
        }
    }

    public void updateRequest(String request) {
        addTextToMemo(request);
    }

    synchronized private void addTextToMemo(String text) {
        memo.setText(memo.getText() + text);
    }
}
