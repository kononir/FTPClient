package main.java.bsuir.ftpclient.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.Exchanger;

public class ConnectionListener implements Runnable {

    private Connection connection;
    private Exchanger<String> serverAnswerExchanger;

    public ConnectionListener(Connection connection, Exchanger<String> exchanger) {
        this.connection = connection;
        this.serverAnswerExchanger = exchanger;
    }

    @Override
    public void run() {
        String answer;
        String answerCode;
        String serverCloseControlConnection = "221";

        try {
            Socket socket = connection.getSocket();

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            do {
                answer = br.readLine();

                serverAnswerExchanger.exchange(answer);

                answerCode = answer.substring(0, 3);
            } while (!serverCloseControlConnection.equals(answerCode));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException ignored) {
        }
    }
}
