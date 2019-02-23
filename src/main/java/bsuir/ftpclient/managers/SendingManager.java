package main.java.bsuir.ftpclient.managers;

import java.util.LinkedList;
import java.util.Queue;

public class SendingManager {
    private final Queue<Sender> senders = new LinkedList<>();
    private int lastSenderIndex;

    private class Sender implements Runnable {
        private String message;
        private int currSenderIndex;

        private Sender(String message, int currSenderIndex) {
            this.message = message;
            this.currSenderIndex = currSenderIndex;
        }

        @Override
        public void run() {

        }

        private void kill() {

        }
    }

    public void send(String message) {
        Sender sender = new Sender(message, lastSenderIndex++);

        synchronized (senders) {
            senders.add(sender);
        }

        new Thread(sender);
    }

    public void killLastSender() {
        synchronized (senders) {
            Sender sender = senders.poll();

            if (sender != null) {
                sender.kill();
            }
        }
    }

    public void killAllSenders() {
        synchronized (senders) {
            for (Sender sender: senders) {
                if (sender != null) {
                    sender.kill();
                }
            }

            senders.clear();
        }
    }
}
