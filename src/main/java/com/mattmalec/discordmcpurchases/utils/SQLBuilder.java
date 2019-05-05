package com.mattmalec.discordmcpurchases.utils;

import java.sql.*;

public class SQLBuilder {

    private String host;
    private int port;
    private String username;
    private String password;
    private String database;

    public SQLBuilder(String host, int port, String username, String password, String database) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    public boolean canConnect() {
        try {
            Connection connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%d?user=%s&password=%s", host, port, username, password));
            if (!connection.isClosed() || connection.isValid(0)) {
                return true;
            }
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
            return false;
        }
        return false;
    }

    public ResultSet prepareQuery(String sql, boolean hideErrors, String... values) {
        try {
            Connection connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%d?user=%s&password=%s", host, port, username, password));
            PreparedStatement prep = connection.prepareCall(sql);
            if(values != null) {
                for (int i = 0; i < values.length; i++) {
                    prep.setString(i + 1, values[i]);
                }
            }
            ResultSet resultSet = prep.executeQuery();
            resultSet.beforeFirst();
            resultSet.next();
            return resultSet;
        } catch (SQLException e) {
            if (!hideErrors) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean prepareExecute(String sql, boolean hideErrors, String... values) {
        try {
            Connection connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%d?user=%s&password=%s", host, port, username, password));
            PreparedStatement prep = connection.prepareCall(sql);
            if(values != null) {
                for (int i = 0; i < values.length; i++) {
                    prep.setString(i + 1, values[i]);
                }
            }
            return prep.execute();
        } catch (SQLException e) {
            if (!hideErrors) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public boolean prepareExecute(String database, String sql, boolean hideErrors, String... values) {
        try {
            Connection connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%d?user=%s&password=%s", host, port, username, password));
            connection.setCatalog(database);
            PreparedStatement prep = connection.prepareCall(sql);
            if(values != null) {
                for (int i = 0; i < values.length; i++) {
                    prep.setString(i + 1, values[i]);
                }
            }
            return prep.execute();
        } catch (SQLException e) {
            if (!hideErrors) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String getDatabase() {
        return database;
    }
}
