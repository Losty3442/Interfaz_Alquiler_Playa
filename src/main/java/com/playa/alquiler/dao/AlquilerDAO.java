package com.playa.alquiler.dao;

import com.playa.alquiler.model.Alquiler;

import java.sql.*;

public class AlquilerDAO {
    public Alquiler crear(Connection conn, Alquiler a) throws SQLException {
        String sql = "INSERT INTO Alquileres (fecha, hora_inicio, estado_alquiler, id_turista, usuario_id) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, Date.valueOf(a.getFecha()));
            ps.setTime(2, Time.valueOf(a.getHoraInicio()));
            ps.setString(3, a.getEstadoAlquiler());
            ps.setInt(4, a.getIdTurista());
            ps.setInt(5, a.getUsuarioId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) a.setAlquilerId(rs.getInt(1));
            }
            return a;
        }
    }

    public boolean actualizarEstado(Connection conn, int alquilerId, String estado) throws SQLException {
        String sql = "UPDATE Alquileres SET estado_alquiler=? WHERE alquiler_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, alquilerId);
            return ps.executeUpdate() > 0;
        }
    }

    public int contarPorEstado(String estado) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Alquileres WHERE estado_alquiler=?";
        try (Connection conn = com.playa.alquiler.db.ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
            return 0;
        }
    }

    public java.util.List<Alquiler> listarPorEstado(String estado) throws SQLException {
        String sql = "SELECT alquiler_id, fecha, hora_inicio, estado_alquiler, id_turista, usuario_id FROM Alquileres WHERE estado_alquiler=? ORDER BY fecha DESC, alquiler_id DESC";
        try (Connection conn = com.playa.alquiler.db.ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                java.util.List<Alquiler> lista = new java.util.ArrayList<>();
                while (rs.next()) {
                    Alquiler a = new Alquiler();
                    a.setAlquilerId(rs.getInt("alquiler_id"));
                    a.setFecha(rs.getDate("fecha").toLocalDate());
                    a.setHoraInicio(rs.getTime("hora_inicio").toLocalTime());
                    a.setEstadoAlquiler(rs.getString("estado_alquiler"));
                    a.setIdTurista(rs.getInt("id_turista"));
                    a.setUsuarioId(rs.getInt("usuario_id"));
                    lista.add(a);
                }
                return lista;
            }
        }
    }
}