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
            System.out.println("[ConexionDB] Inicializando Pool de Conexiones (HikariCP)...");
            Properties props = new Properties();
            try (InputStream is = ConexionDB.class.getResourceAsStream(PROPERTIES_PATH)) {
                if (is == null) {
                    throw new IllegalStateException("Archivo application.properties no encontrado en resources");
                }
                props.load(is);
            }

            HikariConfig config = new HikariConfig();

            // Priority: Environment variables > application.properties
            String dbUrl = System.getenv("DATABASE_URL");
            String dbUser = System.getenv("DATABASE_USERNAME");
            String dbPass = System.getenv("DATABASE_PASSWORD");

            config.setJdbcUrl(dbUrl != null ? dbUrl : props.getProperty("spring.datasource.url"));
            config.setUsername(dbUser != null ? dbUser : props.getProperty("spring.datasource.username"));
            config.setPassword(dbPass != null ? dbPass : props.getProperty("spring.datasource.password"));
            config.setDriverClassName(
                    props.getProperty("spring.datasource.driver-class-name", "org.postgresql.Driver"));

            // Configuración del Pool - optimizada para Railway/Cloud
            config.setMaximumPoolSize(5); // Máximo 5 conexiones (menor para hosting gratuito)
            config.setMinimumIdle(1); // Mínimo 1 en espera
            config.setIdleTimeout(30000); // 30 segundos
            config.setConnectionTimeout(60000); // 60 segundos para obtener conexión (más tiempo para cloud)
            config.setLeakDetectionThreshold(2000);

            // Configuración adicional para conexiones remotas
            config.addDataSourceProperty("socketTimeout", "60");
            config.addDataSourceProperty("connectTimeout", "60");

            dataSource = new HikariDataSource(config);
            System.out.println("[ConexionDB] Pool HikariCP inicializado correctamente.");

        } catch (Exception e) {
            System.err.println("[ConexionDB] ERROR CRITICO AL INICIALIZAR POOL:");
            e.printStackTrace();
            throw new RuntimeException("Error inicializando HikariCP: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para apagar el pool al detener la app
    public static void closePool() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}