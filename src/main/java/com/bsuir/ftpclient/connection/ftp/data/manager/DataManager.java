package com.bsuir.ftpclient.connection.ftp.data.manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataManager {
    private static final int ONE_THREAD = 1;
    private ExecutorService executorService = Executors.newFixedThreadPool(ONE_THREAD);

    public void manageWork(Runnable managing) {
        executorService.execute(managing);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
