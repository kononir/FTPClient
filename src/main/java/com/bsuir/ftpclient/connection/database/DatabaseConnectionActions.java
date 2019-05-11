package com.bsuir.ftpclient.connection.database;

import com.bsuir.ftpclient.connection.database.exception.DatabaseConnectionException;
import com.bsuir.ftpclient.connection.ftp.control.ControlStructure;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnectionActions {

    public void insertControlStructure(ControlStructure controlStructure) throws DatabaseConnectionException {
        Connection connection = connect();
        try {
            String sqlQuery = "INSERT INTO ftp_control (request, response) values(?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, controlStructure.getRequest());
            preparedStatement.setString(2, controlStructure.getResponse());

            preparedStatement.execute();
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Error when saving request/response to database!", e);
        } finally {
            disconnect(connection);
        }
    }

    public List<ControlStructure> selectControlStructures() throws DatabaseConnectionException {
        List<ControlStructure> controlStructures = new ArrayList<>();

        Connection connection = connect();
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
            throw new DatabaseConnectionException("Error when load control structures from database", e);
        } finally {
            disconnect(connection);
        }

        return controlStructures;
    }

    private Connection connect() {
        ConnectionPool pool = ConnectionPool.getInstance();
        return pool.getConnection();
    }

    private void disconnect(Connection connection) {
        ConnectionPool pool = ConnectionPool.getInstance();
        pool.returnConnection(connection);
    }
}
