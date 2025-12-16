package com.playa.alquiler.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;

public class ConexionDB {
    private static final String PROPERTIES_PATH = "/application.properties";
    private static DataSource dataSource; // Use interface
    private static String url;
    private static String user;
    private static String password;
    private static boolean usePool = false;

    static {
        try {
            System.out.println("[ConexionDB] Inicializando...");
            Properties props = new Properties();
            try (InputStream is = ConexionDB.class.getResourceAsStream(PROPERTIES_PATH)) {
                if (is == null) {
                    throw new IllegalStateException("Archivo application.properties no encontrado en resources");
                }
                props.load(is);
            }

            url = props.getProperty("spring.datasource.url");
            user = props.getProperty("spring.datasource.username");
            password = props.getProperty("spring.datasource.password");

            // Validar driver de Postgres
            Class.forName("org.postgresql.Driver");

            // INTENTO DE CARGAR HIKARI CP
            try {
                // Verificar si la clase existe
                Class.forName("com.zaxxer.hikari.HikariDataSource");
                System.out.println("[ConexionDB] HikariCP encontrado. Configurando pool...");

                com.zaxxer.hikari.HikariConfig config = new com.zaxxer.hikari.HikariConfig();
                config.setJdbcUrl(url);
                config.setUsername(user);
                config.setPassword(password);
                config.setDriverClassName("org.postgresql.Driver");

                // Optimizaciones Railway
                config.setMaximumPoolSize(10);
                config.setMinimumIdle(2);
                config.setIdleTimeout(30000);
                config.setConnectionTimeout(30000);
                config.setMaxLifetime(1800000);

                dataSource = new com.zaxxer.hikari.HikariDataSource(config);
                usePool = true;
                System.out.println("[ConexionDB] Pool de conexiones iniciado correctamente.");

            } catch (Throwable t) {
                // Captura ClassNotFoundException, NoClassDefFoundError, etc.
                System.err.println("[ConexionDB] ADVERTENCIA: No se pudo iniciar HikariCP explícitamente.");
                System.err.println("[ConexionDB] Causa: " + t.toString());
                System.err.println("[ConexionDB] Se usará DriverManager estándar (más lento pero seguro).");
                usePool = false;
            }

        } catch (Exception e) {
            System.err.println("[ConexionDB] ERROR CRITICO DE INICIALIZACION:");
            e.printStackTrace();
            // No lanzamos excepcion aquí para permitir intentar conexión y ver error real
            // en runtime
        }
    }

    public static Connection getConnection() throws SQLException {
        if (usePool && dataSource != null) {
            try {
                return dataSource.getConnection();
            } catch (SQLException e) {
                System.err
                        .println("[ConexionDB] Error obteniendo conexión del pool. Reintentando con DriverManager...");
                // Fallback si el pool falla
                return DriverManager.getConnection(url, user, password);
            }
        }
        // Fallback or default
        if (url == null) {
            throw new SQLException("La URL de conexión es nula. Falló la inicialización de propiedades.");
        }
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