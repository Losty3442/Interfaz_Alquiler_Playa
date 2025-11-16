package com.playa.alquiler.dao;

import com.playa.alquiler.db.ConexionDB;
import com.playa.alquiler.model.Turista;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TuristaDAO {
    public Turista crear(Turista t) throws SQLException {
        String sql = "INSERT INTO Turista (nombres, apellidos, email, telefono, nacionalidad) VALUES (?,?,?,?,?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getNombres());
            ps.setString(2, t.getApellidos());
            ps.setString(3, t.getEmail());
            ps.setString(4, t.getTelefono());
            ps.setString(5, t.getNacionalidad());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) t.setIdTurista(rs.getInt(1));
            }
            return t;
        }
    }

    public Turista obtenerPorId(int id) throws SQLException {
        String sql = "SELECT id_turista, nombres, apellidos, email, telefono, nacionalidad FROM Turista WHERE id_turista=?";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Turista t = new Turista();
                    t.setIdTurista(rs.getInt("id_turista"));
                    t.setNombres(rs.getString("nombres"));
                    t.setApellidos(rs.getString("apellidos"));
                    t.setEmail(rs.getString("email"));
                    t.setTelefono(rs.getString("telefono"));
                    t.setNacionalidad(rs.getString("nacionalidad"));
                    return t;
                }
            }
            return null;
        }
    }

    public List<Turista> listarTodos() throws SQLException {
        String sql = "SELECT id_turista, nombres, apellidos, email, telefono, nacionalidad FROM Turista";
        try (Connection conn = ConexionDB.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            List<Turista> lista = new ArrayList<>();
            while (rs.next()) {
                Turista t = new Turista();
                t.setIdTurista(rs.getInt("id_turista"));
                t.setNombres(rs.getString("nombres"));
                t.setApellidos(rs.getString("apellidos"));
                t.setEmail(rs.getString("email"));
                t.setTelefono(rs.getString("telefono"));
                t.setNacionalidad(rs.getString("nacionalidad"));
                lista.add(t);
            }
            return lista;
        }
    }

    public boolean actualizar(Turista t) throws SQLException {
        String sql = "UPDATE Turista SET nombres=?, apellidos=?, email=?, telefono=?, nacionalidad=? WHERE id_turista=?";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getNombres());
            ps.setString(2, t.getApellidos());
            ps.setString(3, t.getEmail());
            ps.setString(4, t.getTelefono());
            ps.setString(5, t.getNacionalidad());
            ps.setInt(6, t.getIdTurista());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM Turista WHERE id_turista=?";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public Turista obtenerPorTelefono(String telefono) throws SQLException {
        String sql = "SELECT id_turista, nombres, apellidos, email, telefono, nacionalidad FROM Turista WHERE telefono=?";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, telefono);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Turista t = new Turista();
                    t.setIdTurista(rs.getInt("id_turista"));
                    t.setNombres(rs.getString("nombres"));
                    t.setApellidos(rs.getString("apellidos"));
                    t.setEmail(rs.getString("email"));
                    t.setTelefono(rs.getString("telefono"));
                    t.setNacionalidad(rs.getString("nacionalidad"));
                    return t;
                }
            }
            return null;
        }
    }

    public Turista obtenerPorEmail(String email) throws SQLException {
        String sql = "SELECT id_turista, nombres, apellidos, email, telefono, nacionalidad FROM Turista WHERE email=?";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Turista t = new Turista();
                    t.setIdTurista(rs.getInt("id_turista"));
                    t.setNombres(rs.getString("nombres"));
                    t.setApellidos(rs.getString("apellidos"));
                    t.setEmail(rs.getString("email"));
                    t.setTelefono(rs.getString("telefono"));
                    t.setNacionalidad(rs.getString("nacionalidad"));
                    return t;
                }
            }
            return null;
        }
    }
}