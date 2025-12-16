package com.playa.alquiler.service;

import com.playa.alquiler.db.ConexionDB;
import com.playa.alquiler.dao.AlquilerDAO;
import com.playa.alquiler.model.ResumenVentas;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Logger;

public class PagoService {
    private static final Logger LOGGER = Logger.getLogger(PagoService.class.getName());

    public BigDecimal calcularTotal(int alquilerId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_a_pagar),0) FROM detalle_alquiler WHERE alquiler_id=?";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, alquilerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        }
    }

    public void marcarPagado(int alquilerId) throws SQLException {
        try (Connection conn = ConexionDB.getConnection()) {
            new AlquilerDAO().actualizarEstado(conn, alquilerId, "Pagado");
            LOGGER.info("Alquiler marcado como Pagado: " + alquilerId);
        }
    }

    public ResumenVentas ventasDiariasPorVendedor(int usuarioId, LocalDate fecha) throws SQLException {
        String sql = "SELECT u.nombre_usuario, COALESCE(SUM(d.total_a_pagar),0) AS total, COUNT(DISTINCT a.alquiler_id) AS cantidad "
                +
                "FROM Alquileres a " +
                "JOIN Usuario u ON a.usuario_id = u.usuario_id " +
                "JOIN detalle_alquiler d ON a.alquiler_id = d.alquiler_id " +
                "WHERE a.usuario_id = ? AND a.fecha = ? " +
                "GROUP BY u.nombre_usuario";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setDate(2, java.sql.Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nombre = rs.getString("nombre_usuario");
                    BigDecimal total = rs.getBigDecimal("total");
                    int cantidad = rs.getInt("cantidad");
                    LOGGER.info("Ventas diarias usuario=" + usuarioId + " fecha=" + fecha + " total=" + total
                            + " cantidad=" + cantidad);
                    return new ResumenVentas(nombre, total, cantidad);
                }
            }
            return new ResumenVentas("Desconocido", BigDecimal.ZERO, 0);
        }
    }
}