package com.playa.alquiler.dao;

import com.playa.alquiler.db.ConexionDB;
import com.playa.alquiler.model.Promocion;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;

@Repository
public class PromocionDAO {
    public Promocion obtenerActivaPorId(int id, LocalDate fechaReferencia) throws SQLException {
        String sql = "SELECT id_promocion, nombre_promocion, descripcion, tipo_descuento, valor_descuento, " +
                "duracion_promocion, condicion_minima, estado, usuario_id, fecha_inicio, fecha_fin " +
                "FROM Promociones WHERE id_promocion=? AND estado='Activa' AND (fecha_inicio <= ? AND fecha_fin >= ?)";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            Date ref = Date.valueOf(fechaReferencia);
            ps.setInt(1, id);
            ps.setDate(2, ref);
            ps.setDate(3, ref);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Promocion p = new Promocion();
                    p.setIdPromocion(rs.getInt("id_promocion"));
                    p.setNombrePromocion(rs.getString("nombre_promocion"));
                    p.setDescripcion(rs.getString("descripcion"));
                    p.setTipoDescuento(rs.getString("tipo_descuento"));
                    p.setValorDescuento(rs.getDouble("valor_descuento"));
                    p.setDuracionPromocion(rs.getString("duracion_promocion"));
                    p.setCondicionMinima(rs.getString("condicion_minima"));
                    p.setEstado(rs.getString("estado"));
                    int uid = rs.getInt("usuario_id");
                    p.setUsuarioId(rs.wasNull() ? null : uid);
                    return p;
                }
            }
            return null;
        }
    }

    public java.util.List<Promocion> listar() throws SQLException {
        String sql = "SELECT id_promocion, nombre_promocion, descripcion, tipo_descuento, valor_descuento, duracion_promocion, condicion_minima, estado, usuario_id FROM Promociones ORDER BY id_promocion";
        try (Connection conn = ConexionDB.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            java.util.List<Promocion> lista = new java.util.ArrayList<>();
            while (rs.next()) {
                Promocion p = new Promocion();
                p.setIdPromocion(rs.getInt("id_promocion"));
                p.setNombrePromocion(rs.getString("nombre_promocion"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setTipoDescuento(rs.getString("tipo_descuento"));
                p.setValorDescuento(rs.getDouble("valor_descuento"));
                p.setDuracionPromocion(rs.getString("duracion_promocion"));
                p.setCondicionMinima(rs.getString("condicion_minima"));
                p.setEstado(rs.getString("estado"));
                int uid = rs.getInt("usuario_id");
                p.setUsuarioId(rs.wasNull() ? null : uid);
                lista.add(p);
            }
            return lista;
        }
    }

    public Promocion crear(Promocion p) throws SQLException {
        String sql = "INSERT INTO Promociones (nombre_promocion, descripcion, tipo_descuento, valor_descuento, duracion_promocion, condicion_minima, estado, usuario_id) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = ConexionDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNombrePromocion());
            ps.setString(2, p.getDescripcion());
            ps.setString(3, p.getTipoDescuento());
            if (p.getValorDescuento() == null)
                ps.setNull(4, Types.DOUBLE);
            else
                ps.setDouble(4, p.getValorDescuento());
            ps.setString(5, p.getDuracionPromocion());
            ps.setString(6, p.getCondicionMinima());
            ps.setString(7, p.getEstado());
            if (p.getUsuarioId() == null)
                ps.setNull(8, Types.INTEGER);
            else
                ps.setInt(8, p.getUsuarioId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    p.setIdPromocion(rs.getInt(1));
            }
            return p;
        }
    }

    public boolean eliminar(int idPromocion) throws SQLException {
        String sql = "DELETE FROM Promociones WHERE id_promocion=?";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPromocion);
            return ps.executeUpdate() > 0;
        }
    }

    public java.util.List<Promocion> listarActivas() throws SQLException {
        String sql = "SELECT id_promocion, nombre_promocion, tipo_descuento, valor_descuento, estado FROM Promociones WHERE estado='Activa' ORDER BY nombre_promocion";
        try (Connection conn = ConexionDB.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            java.util.List<Promocion> lista = new java.util.ArrayList<>();
            while (rs.next()) {
                Promocion p = new Promocion();
                p.setIdPromocion(rs.getInt("id_promocion"));
                p.setNombrePromocion(rs.getString("nombre_promocion"));
                p.setTipoDescuento(rs.getString("tipo_descuento"));
                p.setValorDescuento(rs.getDouble("valor_descuento"));
                p.setEstado(rs.getString("estado"));
                lista.add(p);
            }
            return lista;
        }
    }

    public java.util.List<Promocion> listarTodas() throws SQLException {
        return listar();
    }
}