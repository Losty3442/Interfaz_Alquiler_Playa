package com.playa.alquiler.dao;

import com.playa.alquiler.db.ConexionDB;
import com.playa.alquiler.model.TarifaRecurso;

import java.sql.*;
import java.time.LocalDate;

public class TarifaRecursoDAO {
    public TarifaRecurso obtenerTarifaVigente(int idRecurso, LocalDate fechaReferencia) throws SQLException {
        String sql = "SELECT TOP 1 id_tarifa, id_recurso, precio_por_hora, fecha_inicio, fecha_fin " +
                "FROM tarifa_recurso " +
                "WHERE id_recurso = ? AND (fecha_fin IS NULL OR (fecha_inicio <= ? AND fecha_fin >= ?)) " +
                "ORDER BY fecha_inicio DESC, id_tarifa DESC";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            Date ref = Date.valueOf(fechaReferencia);
            ps.setInt(1, idRecurso);
            ps.setDate(2, ref);
            ps.setDate(3, ref);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TarifaRecurso t = new TarifaRecurso();
                    t.setIdTarifa(rs.getInt("id_tarifa"));
                    t.setIdRecurso(rs.getInt("id_recurso"));
                    t.setPrecioPorHora(rs.getBigDecimal("precio_por_hora"));
                    t.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                    Date fin = rs.getDate("fecha_fin");
                    t.setFechaFin(fin == null ? null : fin.toLocalDate());
                    return t;
                }
            }
            return null;
        }
    }

    public java.util.List<TarifaRecurso> listarPorRecurso(int idRecurso) throws SQLException {
        String sql = "SELECT id_tarifa, id_recurso, precio_por_hora, fecha_inicio, fecha_fin FROM tarifa_recurso WHERE id_recurso=? ORDER BY fecha_inicio DESC, id_tarifa DESC";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idRecurso);
            try (ResultSet rs = ps.executeQuery()) {
                java.util.List<TarifaRecurso> lista = new java.util.ArrayList<>();
                while (rs.next()) {
                    TarifaRecurso t = new TarifaRecurso();
                    t.setIdTarifa(rs.getInt("id_tarifa"));
                    t.setIdRecurso(rs.getInt("id_recurso"));
                    t.setPrecioPorHora(rs.getBigDecimal("precio_por_hora"));
                    t.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                    java.sql.Date fin = rs.getDate("fecha_fin");
                    t.setFechaFin(fin == null ? null : fin.toLocalDate());
                    lista.add(t);
                }
                return lista;
            }
        }
    }

    public TarifaRecurso crear(TarifaRecurso t) throws SQLException {
        String sql = "INSERT INTO tarifa_recurso (id_recurso, precio_por_hora, fecha_inicio, fecha_fin) VALUES (?,?,?,?)";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, t.getIdRecurso());
            ps.setBigDecimal(2, t.getPrecioPorHora());
            ps.setDate(3, java.sql.Date.valueOf(t.getFechaInicio()));
            if (t.getFechaFin() == null) ps.setNull(4, java.sql.Types.DATE); else ps.setDate(4, java.sql.Date.valueOf(t.getFechaFin()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) t.setIdTarifa(rs.getInt(1)); }
            return t;
        }
    }
}