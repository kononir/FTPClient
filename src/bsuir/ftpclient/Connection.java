package bsuir.ftpclient;

import java.io.*;
import java.net.Socket;

public class Connection {
    private Socket socket;

    public String connect(String connectInform) {
        try {
            socket = new Socket(connectInform, 21);

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            return br.readLine() + '\n';
        } catch (IOException e) {
            return "Connection problems\n";
        }
    }

    public String disconnect() {
        String answer = "Connection isn't established!\n";

        try {
            if (socket != null) {
                socket.close();

                answer = "Disconnect successful\n";
            }
        } catch (IOException e) {
            answer = "Connection problems\n";
            e.printStackTrace();
        }

        return answer;
    }

    public boolean isClosed() {
        if (socket == null) {
            return true;
        } else {
            return socket.isClosed();
        }
    }
}
