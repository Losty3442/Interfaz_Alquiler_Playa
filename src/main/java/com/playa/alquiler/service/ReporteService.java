package com.playa.alquiler.service;

import com.playa.alquiler.db.ConexionDB;
import com.playa.alquiler.model.RecursoUso;
import com.playa.alquiler.model.TarifaRecurso;
import com.playa.alquiler.dao.TarifaRecursoDAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ReporteService {
    private static final Logger LOGGER = Logger.getLogger(ReporteService.class.getName());
    public List<RecursoUso> usoRecursosTopN(int topN) throws SQLException {
        String sql = "SELECT TOP ? d.recurso_id, COUNT(*) AS veces FROM detalle_alquiler d GROUP BY d.recurso_id ORDER BY veces DESC, recurso_id";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, topN);
            List<RecursoUso> lista = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int recursoId = rs.getInt("recurso_id");
                    long veces = rs.getLong("veces");
                    lista.add(new RecursoUso(recursoId, veces));
                }
            }
            LOGGER.info("Top " + topN + " recursos más alquilados: " + lista.size());
            return lista;
        }
    }

    public BigDecimal horasUsadasPorRecurso(int recursoId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(cantidad_horas),0) FROM detalle_alquiler WHERE recurso_id=?";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recursoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        }
    }

    public List<Integer> recursosEnMantenimiento() throws SQLException {
        String sql = "SELECT id_recurso FROM Recursos WHERE estado='En Mantenimiento'";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            List<Integer> ids = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt(1));
            }
            LOGGER.info("Recursos en mantenimiento: " + ids.size());
            return ids;
        }
    }

    public TarifaRecurso tarifaVigente(int recursoId, LocalDate fecha) throws SQLException {
        TarifaRecurso t = new TarifaRecursoDAO().obtenerTarifaVigente(recursoId, fecha);
        if (t != null) LOGGER.info("Tarifa vigente recurso=" + recursoId + " precio=" + t.getPrecioPorHora());
        return t;
    }
}
