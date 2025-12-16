package com.playa.alquiler.dao;

import com.playa.alquiler.model.Alquiler;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class AlquilerDAO {
    // ... existing imports ...

    // Listar alquileres con detalles para reporte de cierre de caja
    public java.util.List<com.playa.alquiler.model.DetalleReporteDTO> listarAlquileresConDetallesPorFecha(
            java.time.LocalDate fecha) throws SQLException {
        String sql = "SELECT u.nombre_usuario, a.alquiler_id, a.hora_inicio, " +
                "t.nombres || ' ' || t.apellidos AS nombre_turista, " +
                "r.nombre_recurso, d.cantidad_horas, d.total_a_pagar " +
                "FROM Alquileres a " +
                "JOIN Usuario u ON a.usuario_id = u.usuario_id " +
                "JOIN detalle_alquiler d ON a.alquiler_id = d.alquiler_id " +
                "JOIN Turista t ON a.id_turista = t.id_turista " +
                "JOIN Recursos r ON d.recurso_id = r.id_recurso " +
                "WHERE a.fecha = ?";

        try (Connection conn = com.playa.alquiler.db.ConexionDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));

            try (ResultSet rs = ps.executeQuery()) {
                java.util.List<com.playa.alquiler.model.DetalleReporteDTO> lista = new java.util.ArrayList<>();
                while (rs.next()) {
                    lista.add(new com.playa.alquiler.model.DetalleReporteDTO(
                            rs.getString("nombre_usuario"),
                            rs.getInt("alquiler_id"),
                            rs.getTime("hora_inicio").toLocalTime(),
                            rs.getString("nombre_turista"),
                            rs.getString("nombre_recurso"),
                            rs.getBigDecimal("cantidad_horas"),
                            rs.getBigDecimal("total_a_pagar")));
                }
                return lista;
            }
        }
    }

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
                if (rs.next())
                    a.setAlquilerId(rs.getInt(1));
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
        try (Connection conn = com.playa.alquiler.db.ConexionDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
            return 0;
        }
    }

    public java.util.List<Alquiler> listarPorEstado(String estado) throws SQLException {
        String sql = "SELECT alquiler_id, fecha, hora_inicio, estado_alquiler, id_turista, usuario_id FROM Alquileres WHERE estado_alquiler=? ORDER BY fecha DESC, alquiler_id DESC";
        try (Connection conn = com.playa.alquiler.db.ConexionDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
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

    public java.util.List<Alquiler> listarUltimos(int limite) throws SQLException {
        String sql = "SELECT a.alquiler_id, a.fecha, a.hora_inicio, a.estado_alquiler, a.id_turista, a.usuario_id, u.nombre_usuario "
                +
                "FROM Alquileres a " +
                "LEFT JOIN Usuario u ON a.usuario_id = u.usuario_id " +
                "ORDER BY a.fecha DESC, a.hora_inicio DESC LIMIT ?";

        try (Connection conn = com.playa.alquiler.db.ConexionDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limite);
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
                    a.setNombreVendedor(rs.getString("nombre_usuario"));
                    lista.add(a);
                }
                return lista;
            }
        }
    }

    public java.util.List<Alquiler> listarPorVendedor(int usuarioId) throws SQLException {
        String sql = "SELECT a.alquiler_id, a.fecha, a.hora_inicio, a.estado_alquiler, a.id_turista, a.usuario_id, t.nombres || ' ' || t.apellidos as nombre_turista "
                +
                "FROM Alquileres a " +
                "LEFT JOIN Turista t ON a.id_turista = t.id_turista " +
                "WHERE a.usuario_id = ? AND a.estado_alquiler != 'Archivado' " +
                "ORDER BY a.fecha DESC, a.hora_inicio DESC";

        try (Connection conn = com.playa.alquiler.db.ConexionDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
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
                    a.setNombreTurista(rs.getString("nombre_turista"));
                    lista.add(a);
                }
                return lista;
            }
        }
    }

    public Alquiler obtenerPorId(int id) throws SQLException {
        String sql = "SELECT a.alquiler_id, a.fecha, a.hora_inicio, a.estado_alquiler, a.id_turista, a.usuario_id, " +
                "t.nombres, t.apellidos, t.nacionalidad, t.telefono, t.email, " +
                "u.nombre_usuario " +
                "FROM Alquileres a " +
                "LEFT JOIN Turista t ON a.id_turista = t.id_turista " +
                "LEFT JOIN Usuario u ON a.usuario_id = u.usuario_id " +
                "WHERE a.alquiler_id = ?";

        try (Connection conn = com.playa.alquiler.db.ConexionDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Alquiler a = new Alquiler();
                    a.setAlquilerId(rs.getInt("alquiler_id"));
                    a.setFecha(rs.getDate("fecha").toLocalDate());
                    a.setHoraInicio(rs.getTime("hora_inicio").toLocalTime());
                    a.setEstadoAlquiler(rs.getString("estado_alquiler"));
                    a.setIdTurista(rs.getInt("id_turista"));
                    a.setUsuarioId(rs.getInt("usuario_id"));

                    // Populate extra info if needed, e.g. for receipt
                    a.setNombreTurista(rs.getString("nombres") + " " + rs.getString("apellidos"));
                    a.setNombreVendedor(rs.getString("nombre_usuario"));

                    return a;
                }
                return null;
            }
        }
    }

    public boolean actualizarHoraInicio(Connection conn, int alquilerId, Time nuevaHora) throws SQLException {
        String sql = "UPDATE Alquileres SET hora_inicio=? WHERE alquiler_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTime(1, nuevaHora);
            ps.setInt(2, alquilerId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int alquilerId) throws SQLException {
        try (Connection conn = com.playa.alquiler.db.ConexionDB.getConnection()) {
            conn.setAutoCommit(false);
            try {
                boolean result = eliminar(conn, alquilerId);
                conn.commit();
                return result;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public boolean eliminar(Connection conn, int alquilerId) throws SQLException {
        String sqlDetalle = "DELETE FROM detalle_alquiler WHERE alquiler_id=?";
        String sqlAlquiler = "DELETE FROM Alquileres WHERE alquiler_id=?";

        try (PreparedStatement ps = conn.prepareStatement(sqlDetalle)) {
            ps.setInt(1, alquilerId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement(sqlAlquiler)) {
            ps.setInt(1, alquilerId);
            return ps.executeUpdate() > 0;
        }
    }

    // Overload for Admin: userId can be null or 0 to show ALL
    public java.util.List<Alquiler> listarPorFiltros(Integer usuarioId, java.time.LocalDate fecha, String estado)
            throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT a.alquiler_id, a.fecha, a.hora_inicio, a.estado_alquiler, a.id_turista, a.usuario_id, " +
                        "t.nombres || ' ' || t.apellidos as nombre_turista, u.nombre_usuario as nombre_vendedor " +
                        "FROM Alquileres a " +
                        "LEFT JOIN Turista t ON a.id_turista = t.id_turista " +
                        "LEFT JOIN Usuario u ON a.usuario_id = u.usuario_id " +
                        "WHERE 1=1");

        if (usuarioId != null && usuarioId > 0) {
            sql.append(" AND a.usuario_id = ?");
        }
        if (fecha != null) {
            sql.append(" AND a.fecha = ?");
        }
        if (estado != null && !estado.isEmpty()) {
            sql.append(" AND a.estado_alquiler LIKE ?");
        }
        sql.append(" ORDER BY a.fecha DESC, a.hora_inicio DESC");

        try (Connection conn = com.playa.alquiler.db.ConexionDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (usuarioId != null && usuarioId > 0) {
                ps.setInt(paramIndex++, usuarioId);
            }
            if (fecha != null) {
                ps.setDate(paramIndex++, Date.valueOf(fecha));
            }
            if (estado != null && !estado.isEmpty()) {
                ps.setString(paramIndex++, "%" + estado + "%");
            }

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
                    a.setNombreTurista(rs.getString("nombre_turista"));
                    a.setNombreVendedor(rs.getString("nombre_vendedor"));
                    lista.add(a);
                }
                return lista;
            }
        }
    }

    public void cerrarCaja(Connection conn, java.time.LocalDate fecha) throws SQLException {
        String sql = "UPDATE Alquileres SET estado_alquiler = 'Archivado' WHERE fecha = ? AND estado_alquiler != 'Archivado'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            ps.executeUpdate();
        }
    }
}