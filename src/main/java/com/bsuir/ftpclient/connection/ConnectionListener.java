package com.bsuir.ftpclient.connection;

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
        String serviceNotAvailable = "421";

        try {
            Socket socket = connection.getSocket();

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            boolean controlConnectionIsOpen;

            do {
                answer = br.readLine();

                serverAnswerExchanger.exchange(answer);

                answerCode = answer.substring(0, 3);

                controlConnectionIsOpen = !serverCloseControlConnection.equals(answerCode)
                        && !serviceNotAvailable.equals(answerCode);
            } while (controlConnectionIsOpen);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException ignored) {
        }
    }
}
