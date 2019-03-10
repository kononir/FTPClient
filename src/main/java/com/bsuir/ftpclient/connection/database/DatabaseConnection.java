package com.bsuir.ftpclient.connection.database;

import com.bsuir.ftpclient.connection.control.ControlStructure;
import com.bsuir.ftpclient.connection.database.exception.LoadingControlStructuresException;
import com.mysql.jdbc.Driver;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private Connection connection;

    private static final String URL = "jdbc:mysql://localhost:3306/ftp-request-response" +
            "?useUnicode=true" +
            "&useJDBCCompliantTimezoneShift=true" +
            "&useLegacyDatetimeCode=false" +
            "&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private void connect() {
        try {
            Driver driver = new Driver();
            DriverManager.registerDriver(driver);

            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            if (!connection.isClosed()) {
                System.out.println("Connection is established!");
            }
        } catch (SQLException e) {
            System.out.println("Connection problems!");
            e.printStackTrace();
        }
    }

    private void disconnect() {
        try {
            connection.close();

            if (connection.isClosed()) {
                System.out.println("Connection is closed!");
            }
        } catch (SQLException e) {
            System.out.println("Problems with disconnect!");
            e.printStackTrace();
        }
    }

    public void insertControlStructure(ControlStructure controlStructure) {
        Runnable insertion = () -> {
            connect();

            try {
                String sqlQuery = "INSERT INTO control_structure (request, response) values(?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

                preparedStatement.setString(1, controlStructure.getRequest());
                preparedStatement.setString(2, controlStructure.getResponse());
            } catch (SQLException e) {
                System.out.println("Error when saving request/response to database!");
                e.printStackTrace();
            } finally {
                disconnect();
            }
        };

        new Thread(insertion).start();
    }

    public List<ControlStructure> selectControlStructures() throws LoadingControlStructuresException {
        List<ControlStructure> result = new ArrayList<>();

        connect();

        try {
            Statement statement = connection.createStatement();
            String sqlQuery = "SELECT * FROM control_structure";
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {
                String request = resultSet.getString("request");
                String response = resultSet.getString("response");

                result.add(new ControlStructure(request, response));
            }
        } catch (SQLException e) {
            throw new LoadingControlStructuresException("Error when load control structures from database", e);
        } finally {
            disconnect();
        }

        return result;
    }
}
