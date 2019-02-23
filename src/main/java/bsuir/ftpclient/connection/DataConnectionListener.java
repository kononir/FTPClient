package main.java.bsuir.ftpclient.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.Exchanger;

public class DataConnectionListener implements Runnable {
    private Connection dataConnection;
    private Exchanger<String> dataExchanger;

    public DataConnectionListener(Connection connection, Exchanger<String> exchanger) {
        this.dataConnection = connection;
        this.dataExchanger = exchanger;
    }

    @Override
    public void run() {
        try {
            Socket socket = dataConnection.getSocket();

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            do {
                String data = br.readLine();

                dataExchanger.exchange(data);
            } while (true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException ignored) {
        }
    }
}
