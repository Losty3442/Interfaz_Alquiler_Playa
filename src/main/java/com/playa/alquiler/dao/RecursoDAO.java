package com.playa.alquiler.dao;

import com.playa.alquiler.db.ConexionDB;
import com.playa.alquiler.model.Recurso;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RecursoDAO {
    public Recurso crear(Recurso r) throws SQLException {
        String sql = "INSERT INTO Recursos (nombre_recurso, descripcion, estado, tipo_de_recurso, tarifa, imagen, unidades) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = ConexionDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getNombreRecurso());
            ps.setString(2, r.getDescripcion());
            ps.setString(3, r.getEstado());
            ps.setString(4, r.getTipoDeRecurso());
            ps.setBigDecimal(5, r.getTarifa());
            ps.setString(6, r.getImagen());
            ps.setInt(7, r.getUnidades());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    r.setIdRecurso(rs.getInt(1));
            }
            return r;
        }
    }

    public Recurso obtenerPorId(int id) throws SQLException {
        String sql = "SELECT id_recurso, nombre_recurso, descripcion, estado, tipo_de_recurso, tarifa, imagen, unidades FROM Recursos WHERE id_recurso=?";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToRecurso(rs);
                }
            }
            return null;
        }
    }

    public List<Recurso> listarTodos() throws SQLException {
        String sql = "SELECT id_recurso, nombre_recurso, descripcion, estado, tipo_de_recurso, tarifa, imagen, unidades FROM Recursos";
        try (Connection conn = ConexionDB.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            List<Recurso> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapRowToRecurso(rs));
            }
            return lista;
        }
    }

    public List<Recurso> buscarRecursosDisponibles() throws SQLException {
        // Mostramos solo si hay unidades > 0 y el estado general es Disponible
        String sql = "SELECT id_recurso, nombre_recurso, descripcion, estado, tipo_de_recurso, tarifa, imagen, unidades FROM Recursos WHERE estado = 'Disponible' AND unidades > 0";
        try (Connection conn = ConexionDB.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            List<Recurso> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapRowToRecurso(rs));
            }
            return lista;
        }
    }

    public boolean actualizar(Recurso r) throws SQLException {
        String sql = "UPDATE Recursos SET nombre_recurso=?, descripcion=?, estado=?, tipo_de_recurso=?, tarifa=?, imagen=?, unidades=? WHERE id_recurso=?";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getNombreRecurso());
            ps.setString(2, r.getDescripcion());
            ps.setString(3, r.getEstado());
            ps.setString(4, r.getTipoDeRecurso());
            ps.setBigDecimal(5, r.getTarifa());
            ps.setString(6, r.getImagen());
            ps.setInt(7, r.getUnidades());
            ps.setInt(8, r.getIdRecurso());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM Recursos WHERE id_recurso=?";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizarEstado(Connection conn, int id, String estado) throws SQLException {
        String sql = "UPDATE Recursos SET estado=? WHERE id_recurso=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean decrementarUnidades(Connection conn, int id) throws SQLException {
        String sql = "UPDATE Recursos SET unidades = unidades - 1 WHERE id_recurso = ? AND unidades > 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                // Check if units reached 0
                String checkSql = "SELECT unidades FROM Recursos WHERE id_recurso = ?";
                try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                    checkPs.setInt(1, id);
                    try (ResultSet rs = checkPs.executeQuery()) {
                        if (rs.next()) {
                            int unidades = rs.getInt("unidades");
                            if (unidades == 0) {
                                actualizarEstado(conn, id, "No disponible");
                            }
                        }
                    }
                }
            }
            return rows > 0;
        }
    }

    public boolean incrementarUnidades(Connection conn, int id) throws SQLException {
        String sql = "UPDATE Recursos SET unidades = unidades + 1 WHERE id_recurso = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                // Check if we need to make it Available (e.g. if it was unavailable/0)
                // We always ensure it's "Disponible" if units > 0
                // Assuming "No disponible" was due to lack of units.
                // If it was "En Mantenimiento", maybe we shouldn't?
                // Requirements say: "se libera y esta disponible".
                // Let's force "Disponible" if it was "No disponible".

                String checkSql = "SELECT unidades, estado FROM Recursos WHERE id_recurso = ?";
                try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                    checkPs.setInt(1, id);
                    try (ResultSet rs = checkPs.executeQuery()) {
                        if (rs.next()) {
                            int unidades = rs.getInt("unidades");
                            String estado = rs.getString("estado");
                            // Solo cambiamos a disponible si tiene unidades y estaba como "No disponible" o
                            // "Alquilado" (legacy)
                            // Evitamos cambiar si esta "En Mantenimiento" a menos que la logica lo
                            // requiera.
                            // El usuario dijo: "se pone como no disponible automaticamente... se libera y
                            // esta disponible"
                            if (unidades > 0 && ("No disponible".equals(estado) || "Alquilado".equals(estado))) {
                                actualizarEstado(conn, id, "Disponible");
                            }
                        }
                    }
                }
            }
            return rows > 0;
        }
    }

    private Recurso mapRowToRecurso(ResultSet rs) throws SQLException {
        Recurso r = new Recurso();
        r.setIdRecurso(rs.getInt("id_recurso"));
        r.setNombreRecurso(rs.getString("nombre_recurso"));
        r.setDescripcion(rs.getString("descripcion"));
        r.setEstado(rs.getString("estado"));
        r.setTipoDeRecurso(rs.getString("tipo_de_recurso"));
        r.setTarifa(rs.getBigDecimal("tarifa"));
        r.setImagen(rs.getString("imagen"));
        r.setUnidades(rs.getInt("unidades"));
        return r;
    }
}