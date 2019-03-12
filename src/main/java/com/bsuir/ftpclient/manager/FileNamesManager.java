package com.bsuir.ftpclient.manager;

import com.bsuir.ftpclient.connection.ftp.Connection;
import com.bsuir.ftpclient.connection.ftp.data.DataConnectionListener;
import com.bsuir.ftpclient.updater.TreeUpdater;
import javafx.animation.AnimationTimer;
import javafx.scene.control.TreeView;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FileNamesManager {
    private AnimationTimer timer;
    private TreeUpdater treeUpdater;

    public FileNamesManager(TreeView<String> tree) {
        this.treeUpdater = new TreeUpdater(tree);
    }

    public void startCheckingForFileNames(Connection dataConnection) {
        Exchanger<String> dataExchanger = new Exchanger<>();

        new Thread(new DataConnectionListener(dataConnection, dataExchanger)).start();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                checkForFileName(dataExchanger);
            }
        };

        timer.start();
    }

    private void checkForFileName(Exchanger<String> dataExchanger) {
        try {
            int timeout = 1;

            String nodeInformation = dataExchanger.exchange(null, timeout, TimeUnit.MILLISECONDS);

            update(nodeInformation);
        } catch (TimeoutException ignored) {
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopCheckingForFileNames() {
        timer.stop();
    }

    private void update(String nodeInformation) {
        treeUpdater.addNewNode(nodeInformation);
    }
}
