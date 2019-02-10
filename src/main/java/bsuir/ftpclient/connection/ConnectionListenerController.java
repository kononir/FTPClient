package main.java.bsuir.ftpclient.connection;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ConnectionListenerController {
    private ConnectionListener connectionListener;
    private Exchanger<String> serverAnswerExchanger;

    public ConnectionListenerController(ConnectionListener listener) {
        connectionListener = listener;
    }

    public void controlStartingListening(Exchanger<String> serverAnswerExchanger) {
        this.serverAnswerExchanger = serverAnswerExchanger;

        new Thread(connectionListener).start();
    }

    public String controlGettingServerAnswer() throws InterruptedException, TimeoutException {
        int timeout = 1;

        return serverAnswerExchanger.exchange(null, timeout, TimeUnit.MILLISECONDS);
    }
}
