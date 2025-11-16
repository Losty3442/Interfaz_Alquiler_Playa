package com.playa.alquiler.dao;

import com.playa.alquiler.db.ConexionDB;

import java.sql.*;

public class RolDAO {
    public String obtenerNombreRolPorId(Integer rolId) throws SQLException {
        if (rolId == null) return null;
        String sql = "SELECT nombre_rol FROM roles WHERE rol_id=?";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rolId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
            return null;
        }
    }

    public Integer obtenerRolIdPorNombre(String nombreRol) throws SQLException {
        String sql = "SELECT rol_id FROM roles WHERE nombre_rol=?";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreRol);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
            return null;
        }
    }

    public java.util.List<String> listarNombresRoles() throws SQLException {
        String sql = "SELECT nombre_rol FROM roles ORDER BY nombre_rol";
        try (Connection conn = ConexionDB.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            java.util.List<String> lista = new java.util.ArrayList<>();
            while (rs.next()) {
                lista.add(rs.getString(1));
            }
            return lista;
        }
    }
}