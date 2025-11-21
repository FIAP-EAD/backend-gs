package com.backend.gs.database;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class OracleConnection {

    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";

    @Value("${oracle.host:oracle.fiap.com.br}")
    private String host;

    @Value("${oracle.port:1521}")
    private String port;

    @Value("${oracle.sid:ORCL}")
    private String sid;

    @Value("${oracle.username}")
    private String username;

    @Value("${oracle.password}")
    private String password;

    public Connection getConnection() throws SQLException {
        try {
            Class.forName(DRIVER);
            
            // Monta a URL no mesmo formato do exemplo que funcionou
            String url = String.format("jdbc:oracle:thin:@%s:%s:%s", host, port, sid);
            
            System.out.println("Conectando ao Oracle:");
            System.out.println("  Host: " + host + ":" + port);
            System.out.println("  SID: " + sid);
            System.out.println("  Usuário: " + username);
            
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Oracle connection established successfully!");
            
            return connection;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver Oracle não encontrado", e);
        }
    }

    public void closeConnection(Connection con) {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
                System.out.println("Oracle connection closed successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }

    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            boolean isOpen = conn != null && !conn.isClosed();
            closeConnection(conn);
            return isOpen;
        } catch (SQLException e) {
            System.err.println("Error testing Oracle connection: " + e.getMessage());
            return false;
        }
    }
}