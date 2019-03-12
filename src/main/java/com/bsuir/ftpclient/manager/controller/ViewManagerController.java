package com.bsuir.ftpclient.manager.controller;

import com.bsuir.ftpclient.connection.database.DatabaseConnectionListener;
import com.bsuir.ftpclient.connection.ftp.control.ControlStructure;

import java.util.List;
import java.util.concurrent.*;

public class ViewManagerController {
    private Exchanger<List<ControlStructure>> exchanger = new Exchanger<>();

    private static final int CORE_POOL_SIZE = 1;
    private ScheduledExecutorService service = new ScheduledThreadPoolExecutor(CORE_POOL_SIZE);

    private static final long INITIAL_DELAY = 0;
    private static final long PERIOD = 1;

    private static final long TIMEOUT = 1;

    public void controlStartingListening() {
        DatabaseConnectionListener listener = new DatabaseConnectionListener(exchanger);

        service.scheduleAtFixedRate(listener, INITIAL_DELAY, PERIOD, TimeUnit.MILLISECONDS);
    }

    public void controlStoppingListening() {
        service.shutdown();
    }

    public List<ControlStructure> controlGettingControlStructures() throws TimeoutException, InterruptedException {
        return exchanger.exchange(null, TIMEOUT, TimeUnit.MILLISECONDS);
    }
}
