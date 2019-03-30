package com.bsuir.ftpclient.connection.ftp.data.manager;

import com.bsuir.ftpclient.connection.ftp.Connection;
import com.bsuir.ftpclient.connection.ftp.data.DataConnectionListener;
import com.bsuir.ftpclient.ui.updater.TreeUpdater;
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
                update(dataExchanger);
            }
        };

        timer.start();
    }

    private void update(Exchanger<String> dataExchanger) {
        try {
            String nodeInformation = dataExchanger.exchange(null, 1, TimeUnit.MILLISECONDS);

            treeUpdater.addNewNode(nodeInformation);
        } catch (TimeoutException ignored) {
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopCheckingForFileNames() {
        timer.stop();
    }
}
