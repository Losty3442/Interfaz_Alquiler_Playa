package com.playa.alquiler.dao;

import com.playa.alquiler.db.ConexionDB;
import com.playa.alquiler.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    public Usuario crear(Usuario u) throws SQLException {
        String sql = "INSERT INTO Usuario (nombre_usuario, email, contraseña, rol_id) VALUES (?,?,?,?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombreUsuario());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getContrasena());
            if (u.getRolId() == null) ps.setNull(4, Types.INTEGER); else ps.setInt(4, u.getRolId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) u.setUsuarioId(rs.getInt(1));
            }
            return u;
        }
    }

    public Usuario obtenerPorId(int id) throws SQLException {
        String sql = "SELECT usuario_id, nombre_usuario, email, contraseña, rol_id FROM Usuario WHERE usuario_id = ?";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setUsuarioId(rs.getInt("usuario_id"));
                    u.setNombreUsuario(rs.getString("nombre_usuario"));
                    u.setEmail(rs.getString("email"));
                    u.setContrasena(rs.getString("contraseña"));
                    int rolId = rs.getInt("rol_id");
                    u.setRolId(rs.wasNull() ? null : rolId);
                    return u;
                }
            }
            return null;
        }
    }

    public List<Usuario> listarTodos() throws SQLException {
        String sql = "SELECT usuario_id, nombre_usuario, email, contraseña, rol_id FROM Usuario";
        try (Connection conn = ConexionDB.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            List<Usuario> lista = new ArrayList<>();
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setUsuarioId(rs.getInt("usuario_id"));
                u.setNombreUsuario(rs.getString("nombre_usuario"));
                u.setEmail(rs.getString("email"));
                u.setContrasena(rs.getString("contraseña"));
                int rolId = rs.getInt("rol_id");
                u.setRolId(rs.wasNull() ? null : rolId);
                lista.add(u);
            }
            return lista;
        }
    }

    public boolean actualizar(Usuario u) throws SQLException {
        String sql = "UPDATE Usuario SET nombre_usuario=?, email=?, contraseña=?, rol_id=? WHERE usuario_id=?";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getNombreUsuario());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getContrasena());
            if (u.getRolId() == null) ps.setNull(4, Types.INTEGER); else ps.setInt(4, u.getRolId());
            ps.setInt(5, u.getUsuarioId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM Usuario WHERE usuario_id=?";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Usuario> listar() throws SQLException {
        String sql = "SELECT usuario_id, nombre_usuario, email, contraseña, rol_id FROM Usuario ORDER BY usuario_id";
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection conn = ConexionDB.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setUsuarioId(rs.getInt("usuario_id"));
                u.setNombreUsuario(rs.getString("nombre_usuario"));
                u.setEmail(rs.getString("email"));
                u.setContrasena(rs.getString("contraseña"));
                int rolId = rs.getInt("rol_id");
                u.setRolId(rs.wasNull() ? null : rolId);
                usuarios.add(u);
            }
        }
        return usuarios;
    }

    public Usuario obtenerPorCredenciales(String nombreUsuario, String contrasena) throws SQLException {
        String sql = "SELECT usuario_id, nombre_usuario, email, contraseña, rol_id FROM Usuario WHERE nombre_usuario=? AND contraseña=?";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, contrasena);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setUsuarioId(rs.getInt("usuario_id"));
                    u.setNombreUsuario(rs.getString("nombre_usuario"));
                    u.setEmail(rs.getString("email"));
                    u.setContrasena(rs.getString("contraseña"));
                    int rolId = rs.getInt("rol_id");
                    u.setRolId(rs.wasNull() ? null : rolId);
                    return u;
                }
            }
            return null;
        }
    }
}