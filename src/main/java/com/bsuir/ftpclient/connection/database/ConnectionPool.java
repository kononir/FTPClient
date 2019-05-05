package com.bsuir.ftpclient.connection.database;

import com.bsuir.ftpclient.connection.database.exception.ConnectionPoolException;
import com.mysql.cj.jdbc.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ConnectionPool {
    private static volatile ConnectionPool instance;

    private static final int CONNECTIONS_NUMBER = 2;
    private static final String URL = "jdbc:mysql://localhost:3306/request_response" +
            "?useUnicode=true" +
            "&useJDBCCompliantTimezoneShift=true" +
            "&useLegacyDatetimeCode=false" +
            "&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private BlockingQueue<Connection> connections = new ArrayBlockingQueue<>(CONNECTIONS_NUMBER);

    private ConnectionPool() throws ConnectionPoolException {
        try {
            Driver driver = new Driver();
            DriverManager.registerDriver(driver);

            for (int i = 0; i < CONNECTIONS_NUMBER; i++) {
                connections.put(DriverManager.getConnection(URL, USER, PASSWORD));
            }
        } catch (SQLException | InterruptedException e) {
            throw new ConnectionPoolException("Problems with making connections", e);
        }
    }

    // Double Checked Locking & volatile
    public static ConnectionPool getInstance() throws ConnectionPoolException {
        ConnectionPool localInstance = instance;
        if (localInstance == null) {
            synchronized (ConnectionPool.class) {
                localInstance = instance;
                if (instance == null) {
                    instance = localInstance = new ConnectionPool();
                }
            }
        }

        return localInstance;
    }

    public Connection getConnection() throws ConnectionPoolException {
        try {
            return connections.poll(5, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new ConnectionPoolException("Getting pool connection error", e);
        }
    }

    public void returnConnection(Connection connection) throws ConnectionPoolException {
        try {
            connections.put(connection);
        } catch (InterruptedException e) {
            throw new ConnectionPoolException("Returning connection to pool error", e);
        }
    }
}
