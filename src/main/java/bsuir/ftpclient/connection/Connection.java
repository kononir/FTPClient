package main.java.bsuir.ftpclient.connection;

import java.io.*;
import java.net.Socket;

public class Connection {
    private Socket socket;

    public Socket getSocket() {
        return socket;
    }

    public String connect(String connectInform) throws IOException {
        socket = new Socket(connectInform, 21);

        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        return br.readLine() + '\n';
    }

    public void disconnect() throws IOException {
        socket.close();
    }

    public boolean isClosed() {
        if (socket == null) {
            return true;
        } else {
            return socket.isClosed();
        }
    }
}
