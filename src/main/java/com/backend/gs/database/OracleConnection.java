package com.backend.gs.database;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class OracleConnection {

    @Value("${oracle.url}")
    private String url;

    @Value("${oracle.username}")
    private String username;

    @Value("${oracle.password}")
    private String password;

    private Connection connection = null;

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                connection = DriverManager.getConnection(url, username, password);
                System.out.println("Oracle connection established successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Oracle JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error connecting to Oracle: " + e.getMessage());
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Oracle connection closed successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error closing Oracle connection: " + e.getMessage());
        }
    }

    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Error testing Oracle connection: " + e.getMessage());
            return false;
        }
    }
}