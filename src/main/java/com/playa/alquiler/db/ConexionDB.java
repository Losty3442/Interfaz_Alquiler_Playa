package com.playa.alquiler.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionDB {
    private static final String PROPERTIES_PATH = "/application.properties";
    private static HikariDataSource dataSource;

    static {
        try {
            Properties props = new Properties();
            try (InputStream is = ConexionDB.class.getResourceAsStream(PROPERTIES_PATH)) {
                if (is == null) {
                    throw new IllegalStateException("Archivo application.properties no encontrado en resources");
                }
                props.load(is);
            }

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("spring.datasource.url"));
            config.setUsername(props.getProperty("spring.datasource.username"));
            config.setPassword(props.getProperty("spring.datasource.password"));
            config.setDriverClassName("org.postgresql.Driver");

            // Optimizaciones del Pool
            config.setMaximumPoolSize(10); // Máximo de conexiones simultáneas
            config.setMinimumIdle(2); // Conexiones mínimas en espera
            config.setIdleTimeout(30000); // 30 segundos de inactividad antes de cerrar
            config.setConnectionTimeout(30000); // 30 segundos esperando conexión libre
            config.setMaxLifetime(1800000); // 30 minutos vida máxima de una conexión

            dataSource = new HikariDataSource(config);

        } catch (Exception e) {
            throw new RuntimeException("Error inicializando el Pool de Conexiones a BD: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}