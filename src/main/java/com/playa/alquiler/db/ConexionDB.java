package com.playa.alquiler.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionDB {
    private static final String PROPERTIES_PATH = "/application.properties";
    private static volatile HikariDataSource dataSource;
    private static volatile boolean initialized = false;
    private static volatile String lastError = null;
    private static final Object lock = new Object();

    // Lazy initialization - solo inicializa cuando se necesita
    private static void ensureInitialized() {
        if (!initialized) {
            synchronized (lock) {
                if (!initialized) {
                    try {
                        initializePool();
                        initialized = true;
                        lastError = null;
                    } catch (Exception e) {
                        lastError = e.getMessage();
                        System.err.println("[ConexionDB] Error de inicialización: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void initializePool() throws Exception {
        System.out.println("[ConexionDB] Inicializando Pool de Conexiones (HikariCP)...");

        // Forzar IPv4 antes de cualquier conexión
        System.setProperty("java.net.preferIPv4Stack", "true");

        Properties props = new Properties();
        try (InputStream is = ConexionDB.class.getResourceAsStream(PROPERTIES_PATH)) {
            if (is == null) {
                throw new IllegalStateException("Archivo application.properties no encontrado en resources");
            }
            props.load(is);
        }

        // Priority: Environment variables > application.properties
        String dbUrl = System.getenv("DATABASE_URL");
        String dbUser = System.getenv("DATABASE_USERNAME");
        String dbPass = System.getenv("DATABASE_PASSWORD");

        String finalUrl = dbUrl != null ? dbUrl : props.getProperty("spring.datasource.url");
        String finalUser = dbUser != null ? dbUser : props.getProperty("spring.datasource.username");
        String finalPass = dbPass != null ? dbPass : props.getProperty("spring.datasource.password");

        System.out.println("[ConexionDB] URL: " + finalUrl);
        System.out.println("[ConexionDB] Usuario: " + finalUser);
        System.out.println("[ConexionDB] Usando env vars: " + (dbUrl != null ? "SI" : "NO"));

        // Cargar driver PostgreSQL
        Class.forName("org.postgresql.Driver");

        // Configurar HikariCP optimizado para velocidad
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(finalUrl);
        config.setUsername(finalUser);
        config.setPassword(finalPass);
        config.setDriverClassName("org.postgresql.Driver");

        // Pool optimizado: mantener conexiones listas para uso inmediato
        config.setMaximumPoolSize(10); // Más conexiones disponibles
        config.setMinimumIdle(3); // Mantener 3 conexiones listas (evita espera)
        config.setIdleTimeout(120000); // 2 min antes de cerrar conexiones idle
        config.setConnectionTimeout(10000); // 10 seg max para obtener conexión
        config.setValidationTimeout(3000); // 3 seg para validar conexión
        config.setMaxLifetime(300000); // 5 min vida máxima de conexión
        config.setInitializationFailTimeout(30000); // 30 seg para inicializar

        // Optimizaciones de PostgreSQL
        config.addDataSourceProperty("ssl", "true");
        config.addDataSourceProperty("sslmode", "require");
        config.addDataSourceProperty("socketTimeout", "30");
        config.addDataSourceProperty("connectTimeout", "10");
        config.addDataSourceProperty("prepareThreshold", "5"); // Cache prepared statements
        config.addDataSourceProperty("preparedStatementCacheQueries", "256");
        config.addDataSourceProperty("preparedStatementCacheSizeMiB", "5");

        // Pool name para debugging
        config.setPoolName("AlquilerPlayaPool");

        dataSource = new HikariDataSource(config);
        System.out.println("[ConexionDB] ✓ Pool HikariCP inicializado correctamente con " +
                config.getMinimumIdle() + " conexiones idle mínimas.");
    }

    public static Connection getConnection() throws SQLException {
        ensureInitialized();

        if (dataSource == null) {
            throw new SQLException("No se pudo inicializar el pool de conexiones. Error: " +
                    (lastError != null ? lastError : "desconocido"));
        }

        return dataSource.getConnection();
    }

    public static String getLastError() {
        return lastError;
    }

    public static boolean isInitialized() {
        return initialized && dataSource != null;
    }

    public static boolean testConnection() {
        try {
            ensureInitialized();
            if (dataSource == null)
                return false;

            try (Connection conn = dataSource.getConnection()) {
                return conn != null && !conn.isClosed();
            }
        } catch (Exception e) {
            System.err.println("[ConexionDB] Test fallido: " + e.getMessage());
            return false;
        }
    }

    public static void closePool() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
            initialized = false;
        }
    }
}