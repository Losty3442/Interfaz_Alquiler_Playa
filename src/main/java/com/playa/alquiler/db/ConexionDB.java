package com.playa.alquiler.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionDB {
    private static final String PROPERTIES_PATH = "/application.properties";
    private static String url;
    private static String user;
    private static String password;

    static {
        try {
            System.out.println("[ConexionDB] Inicializando modo BASICO (DriverManager)...");
            Properties props = new Properties();
            try (InputStream is = ConexionDB.class.getResourceAsStream(PROPERTIES_PATH)) {
                if (is == null) {
                    throw new IllegalStateException("Archivo application.properties no encontrado en resources");
                }
                props.load(is);
            }

            // Cargar Driver de Postgres explicitamente
            try {
                Class.forName("org.postgresql.Driver");
                System.out.println("[ConexionDB] Driver PostgreSQL cargado correctamente.");
            } catch (ClassNotFoundException e) {
                System.err.println("[ConexionDB] ERROR FATAL: No se encuentra org.postgresql.Driver");
                e.printStackTrace();
            }

            url = props.getProperty("spring.datasource.url");
            user = props.getProperty("spring.datasource.username");
            password = props.getProperty("spring.datasource.password");

            if (url == null) {
                System.err.println("[ConexionDB] ERROR: spring.datasource.url no encontrada en properties.");
            }

        } catch (Exception e) {
            System.err.println("[ConexionDB] ERROR CRITICO EN STATIC BLOCK:");
            e.printStackTrace();
            throw new RuntimeException("Error inicializando la conexión a BD: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        // Conexion directa y simple para evitar errores de librerias
        return DriverManager.getConnection(url, user, password);
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}