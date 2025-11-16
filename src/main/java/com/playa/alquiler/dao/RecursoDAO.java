package com.playa.alquiler.dao;

import com.playa.alquiler.db.ConexionDB;
import com.playa.alquiler.model.Recurso;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecursoDAO {
    public Recurso crear(Recurso r) throws SQLException {
        String sql = "INSERT INTO Recursos (nombre_recurso, descripcion, estado, tipo_de_recurso) VALUES (?,?,?,?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getNombreRecurso());
            ps.setString(2, r.getDescripcion());
            ps.setString(3, r.getEstado());
            ps.setString(4, r.getTipoDeRecurso());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) r.setIdRecurso(rs.getInt(1));
            }
            return r;
        }
    }

    public Recurso obtenerPorId(int id) throws SQLException {
        String sql = "SELECT id_recurso, nombre_recurso, descripcion, estado, tipo_de_recurso FROM Recursos WHERE id_recurso=?";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Recurso r = new Recurso();
                    r.setIdRecurso(rs.getInt("id_recurso"));
                    r.setNombreRecurso(rs.getString("nombre_recurso"));
                    r.setDescripcion(rs.getString("descripcion"));
                    r.setEstado(rs.getString("estado"));
                    r.setTipoDeRecurso(rs.getString("tipo_de_recurso"));
                    return r;
                }
            }
            return null;
        }
    }

    public List<Recurso> listarTodos() throws SQLException {
        String sql = "SELECT id_recurso, nombre_recurso, descripcion, estado, tipo_de_recurso FROM Recursos";
        try (Connection conn = ConexionDB.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            List<Recurso> lista = new ArrayList<>();
            while (rs.next()) {
                Recurso r = new Recurso();
                r.setIdRecurso(rs.getInt("id_recurso"));
                r.setNombreRecurso(rs.getString("nombre_recurso"));
                r.setDescripcion(rs.getString("descripcion"));
                r.setEstado(rs.getString("estado"));
                r.setTipoDeRecurso(rs.getString("tipo_de_recurso"));
                lista.add(r);
            }
            return lista;
        }
    }

    public List<Recurso> buscarRecursosDisponibles() throws SQLException {
        String sql = "SELECT id_recurso, nombre_recurso, descripcion, estado, tipo_de_recurso FROM Recursos WHERE estado = 'Disponible'";
        try (Connection conn = ConexionDB.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            List<Recurso> lista = new ArrayList<>();
            while (rs.next()) {
                Recurso r = new Recurso();
                r.setIdRecurso(rs.getInt("id_recurso"));
                r.setNombreRecurso(rs.getString("nombre_recurso"));
                r.setDescripcion(rs.getString("descripcion"));
                r.setEstado(rs.getString("estado"));
                r.setTipoDeRecurso(rs.getString("tipo_de_recurso"));
                lista.add(r);
            }
            return lista;
        }
    }

    public boolean actualizar(Recurso r) throws SQLException {
        String sql = "UPDATE Recursos SET nombre_recurso=?, descripcion=?, estado=?, tipo_de_recurso=? WHERE id_recurso=?";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getNombreRecurso());
            ps.setString(2, r.getDescripcion());
            ps.setString(3, r.getEstado());
            ps.setString(4, r.getTipoDeRecurso());
            ps.setInt(5, r.getIdRecurso());
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
}