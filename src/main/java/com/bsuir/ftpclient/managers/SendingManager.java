package com.bsuir.ftpclient.managers;

import com.bsuir.ftpclient.connection.Connection;
import com.bsuir.ftpclient.connection.control.ControlStructure;
import com.bsuir.ftpclient.connection.database.DatabaseConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class SendingManager {
    private final Queue<Thread> listenerThreads = new LinkedList<>();
    private Connection controlConnection;

    public SendingManager(Connection controlConnection) {
        this.controlConnection = controlConnection;
    }

    private class Listener implements Runnable {
        private String request;

        private Listener(String request) {
            this.request = request;
        }

        @Override
        public void run() {
            try {
                Socket socket = controlConnection.getSocket();

                PrintStream output = new PrintStream(socket.getOutputStream());
                output.println(request);

                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                StringBuilder response = new StringBuilder(input.readLine());

                boolean isMultipleLine = "-".equals(response.substring(3, 4));

                if (isMultipleLine) {
                    String code = response.substring(0, 3);
                    String currentLine;

                    do {
                        currentLine = input.readLine();

                        response.append(currentLine);
                    } while (code.equals(currentLine.substring(0, 3)));
                }

                ControlStructure controlStructure = new ControlStructure(request, response.toString());
                DatabaseConnection databaseConnection = new DatabaseConnection();

                databaseConnection.insertControlStructure(controlStructure);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(String request) {
        Listener listener = new Listener(request);

        Thread listenerThread = new Thread(listener);

        listenerThread.start();

        synchronized (listenerThreads) {
            listenerThreads.add(listenerThread);
        }
    }

    public void killLastListener() {
        synchronized (listenerThreads) {
            Thread listenerThread = listenerThreads.poll();

            if (listenerThread != null) {
                listenerThread.interrupt();
            }
        }
    }

    public void killAllListeners() {
        synchronized (listenerThreads) {
            for (Thread listenerThread : listenerThreads) {
                if (listenerThread != null) {
                    listenerThread.interrupt();
                }
            }

            listenerThreads.clear();
        }
    }
}
