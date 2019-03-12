package com.bsuir.ftpclient.connection.database;

import com.bsuir.ftpclient.connection.ftp.control.ControlStructure;
import com.bsuir.ftpclient.connection.database.exception.ControlStructureException;
import com.mysql.cj.jdbc.Driver;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private Connection connection;

    private static final String URL = "jdbc:mysql://localhost:3306/request_response" +
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
        } catch (SQLException e) {
            System.out.println("Connection problems!");
            e.printStackTrace();
        }
    }

    private void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Problems with disconnect!");
            e.printStackTrace();
        }
    }

    public void insertControlStructure(ControlStructure controlStructure) throws ControlStructureException {
        connect();

        try {
            String sqlQuery = "INSERT INTO ftp_control (request, response) values(?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, controlStructure.getRequest());
            preparedStatement.setString(2, controlStructure.getResponse());

            preparedStatement.execute();
        } catch (SQLException e) {
            throw new ControlStructureException("Error when saving request/response to database!", e);
        } finally {
            disconnect();
        }
    }

    public List<ControlStructure> selectControlStructures() throws ControlStructureException {
        List<ControlStructure> controlStructures = new ArrayList<>();

        connect();

        try {
            Statement selectStatement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            String sqlQuerySelect = "SELECT * FROM ftp_control";
            ResultSet resultSet = selectStatement.executeQuery(sqlQuerySelect);

            while (resultSet.next()) {
                String request = resultSet.getString("request");
                String response = resultSet.getString("response");
                controlStructures.add(new ControlStructure(request, response));

                resultSet.deleteRow();
            }
        } catch (SQLException e) {
            throw new ControlStructureException("Error when load control structures from database", e);
        } finally {
            disconnect();
        }

        return controlStructures;
    }
}
