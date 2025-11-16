package com.playa.alquiler.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionDB {
    private static final String PROPERTIES_PATH = "/db.properties";
    private static String url;
    private static String user;
    private static String password;

    static {
        try {
            Properties props = new Properties();
            try (InputStream is = ConexionDB.class.getResourceAsStream(PROPERTIES_PATH)) {
                if (is == null) {
                    throw new IllegalStateException("Archivo db.properties no encontrado en resources");
                }
                props.load(is);
            }
            // Cargar explícitamente el driver (opcional para JDBC 4+)
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            url = props.getProperty("url");
            user = props.getProperty("user");
            password = props.getProperty("password");
        } catch (Exception e) {
            throw new RuntimeException("Error inicializando la conexión a BD: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}