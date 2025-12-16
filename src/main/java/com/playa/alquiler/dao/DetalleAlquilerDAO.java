package com.playa.alquiler.dao;

import com.playa.alquiler.model.DetalleAlquiler;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class DetalleAlquilerDAO {
    public DetalleAlquiler crear(Connection conn, DetalleAlquiler d) throws SQLException {
        String sql = "INSERT INTO detalle_alquiler (alquiler_id, recurso_id, cantidad_horas, promocion_id, total_a_pagar) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, d.getAlquilerId());
            ps.setInt(2, d.getRecursoId());
            ps.setBigDecimal(3, d.getCantidadHoras());
            if (d.getPromocionId() == null)
                ps.setNull(4, Types.INTEGER);
            else
                ps.setInt(4, d.getPromocionId());
            ps.setBigDecimal(5, d.getTotalAPagar());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    d.setDetalleId(rs.getInt(1));
            }
            return d;
        }
    }

    public java.util.List<DetalleAlquiler> listarPorAlquilerId(Connection conn, int alquilerId) throws SQLException {
        String sql = "SELECT detalle_id, alquiler_id, recurso_id, cantidad_horas, promocion_id, total_a_pagar FROM detalle_alquiler WHERE alquiler_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, alquilerId);
            try (ResultSet rs = ps.executeQuery()) {
                java.util.List<DetalleAlquiler> lista = new java.util.ArrayList<>();
                while (rs.next()) {
                    DetalleAlquiler d = new DetalleAlquiler();
                    d.setDetalleId(rs.getInt("detalle_id"));
                    d.setAlquilerId(rs.getInt("alquiler_id"));
                    d.setRecursoId(rs.getInt("recurso_id"));
                    d.setCantidadHoras(rs.getBigDecimal("cantidad_horas"));
                    int pid = rs.getInt("promocion_id");
                    d.setPromocionId(rs.wasNull() ? null : pid);
                    d.setTotalAPagar(rs.getBigDecimal("total_a_pagar"));
                    lista.add(d);
                }
                return lista;
            }
        }
    }

    public java.util.List<DetalleAlquiler> listarPorAlquiler(int alquilerId) throws SQLException {
        try (Connection conn = com.playa.alquiler.db.ConexionDB.getConnection()) {
            return listarPorAlquilerId(conn, alquilerId);
        }
    }
}