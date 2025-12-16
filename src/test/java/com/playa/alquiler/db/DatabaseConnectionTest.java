package com.playa.alquiler.db;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseConnectionTest {

    @Test
    public void testConnection() {
        System.out.println("TEST: Intentando conectar a la BD...");
        boolean result = ConexionDB.testConnection();
        if (result) {
            System.out.println("TEST: Conexión EXITOSA.");
        } else {
            System.out.println("TEST: Conexión FALLIDA.");
        }
        assertTrue(result, "La conexión a la base de datos falló.");

        try (Connection conn = ConexionDB.getConnection();
                java.sql.Statement st = conn.createStatement();
                java.sql.ResultSet rs = st.executeQuery("SELECT count(*) FROM pg_stat_activity")) {
            if (rs.next()) {
                System.out.println("TEST: Conexiones activas: " + rs.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
