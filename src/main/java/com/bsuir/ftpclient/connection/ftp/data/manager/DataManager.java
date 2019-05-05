package com.bsuir.ftpclient.connection.ftp.data.manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataManager {
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public void manageWork(Runnable managing) {
        executorService.execute(managing);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
