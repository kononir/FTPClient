package com.bsuir.ftpclient.managers;

import com.bsuir.ftpclient.connection.Connection;
import com.bsuir.ftpclient.connection.ConnectionActions;

import java.util.LinkedList;
import java.util.Queue;

public class SendingManager {
    private final Queue<Listener> listeners = new LinkedList<>();
    private int lastSenderIndex;
    private Connection controlConnection;

    public SendingManager(Connection controlConnection) {
        this.controlConnection = controlConnection;
    }

    private class Listener implements Runnable {
        private String message;
        private int currSenderIndex;

        private Listener(int currSenderIndex) {
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
        Listener listener = new Listener(lastSenderIndex++);

        synchronized (listeners) {
            listeners.add(listener);
        }

        new Thread(listener);
    }

    public void killLastListener() {
        synchronized (listeners) {
            Listener listener = listeners.poll();

            if (listener != null) {
                listener.kill();
            }
        }
    }

    public void killAllSenders() {
        synchronized (listeners) {
            for (Listener listener : listeners) {
                if (listener != null) {
                    listener.kill();
                }
            }

            listeners.clear();
        }
    }
}
