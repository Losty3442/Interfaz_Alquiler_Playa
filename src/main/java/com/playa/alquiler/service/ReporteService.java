package com.playa.alquiler.service;

import com.playa.alquiler.dao.AlquilerDAO;
import com.playa.alquiler.db.ConexionDB;
import com.playa.alquiler.model.DetalleReporteDTO;
import com.playa.alquiler.model.RecursoUso;
import com.playa.alquiler.model.ResumenVentas;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ReporteService {
    private static final Logger LOGGER = Logger.getLogger(ReporteService.class.getName());
    private final AlquilerDAO alquilerDAO = new AlquilerDAO();

    public List<com.playa.alquiler.model.VendedorReporteDTO> generarReporteDetallado(LocalDate fecha)
            throws SQLException {
        List<DetalleReporteDTO> detalles = alquilerDAO.listarAlquileresConDetallesPorFecha(fecha);

        // Map<Vendedor, Map<AlquilerId, List<Detalles>>>
        Map<String, Map<Integer, List<DetalleReporteDTO>>> grouped = detalles.stream()
                .collect(Collectors.groupingBy(DetalleReporteDTO::getNombreVendedor,
                        Collectors.groupingBy(DetalleReporteDTO::getAlquilerId)));

        List<com.playa.alquiler.model.VendedorReporteDTO> reporte = new ArrayList<>();

        for (Map.Entry<String, Map<Integer, List<DetalleReporteDTO>>> entryVendedor : grouped.entrySet()) {
            String vendedor = entryVendedor.getKey();
            Map<Integer, List<DetalleReporteDTO>> rentalsMap = entryVendedor.getValue();

            List<com.playa.alquiler.model.AlquilerReporteDTO> alquileresDTO = new ArrayList<>();
            BigDecimal totalVendedor = BigDecimal.ZERO;

            for (Map.Entry<Integer, List<DetalleReporteDTO>> entryAlquiler : rentalsMap.entrySet()) {
                List<DetalleReporteDTO> items = entryAlquiler.getValue();
                if (items.isEmpty())
                    continue;

                DetalleReporteDTO first = items.get(0);
                BigDecimal totalAlquiler = items.stream()
                        .map(DetalleReporteDTO::getMontoDetalle)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                totalVendedor = totalVendedor.add(totalAlquiler);

                alquileresDTO.add(new com.playa.alquiler.model.AlquilerReporteDTO(
                        first.getAlquilerId(),
                        first.getHora(),
                        first.getNombreTurista(),
                        totalAlquiler,
                        items));
            }

            // Sort rentals by time desc
            alquileresDTO.sort((a, b) -> b.getHora().compareTo(a.getHora()));

            reporte.add(new com.playa.alquiler.model.VendedorReporteDTO(
                    vendedor,
                    totalVendedor,
                    alquileresDTO.size(),
                    alquileresDTO));
        }

        return reporte;
    }

    public List<RecursoUso> usoRecursosTopN(int topN) throws SQLException {
        String sql = "SELECT d.recurso_id, COUNT(*) AS veces FROM detalle_alquiler d GROUP BY d.recurso_id ORDER BY veces DESC, recurso_id LIMIT ?";
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
                if (rs.next())
                    return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        }
    }

    public List<Integer> recursosEnMantenimiento() throws SQLException {
        String sql = "SELECT id_recurso FROM Recursos WHERE estado='En Mantenimiento'";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            List<Integer> ids = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    ids.add(rs.getInt(1));
            }
            LOGGER.info("Recursos en mantenimiento: " + ids.size());
            return ids;
        }
    }

    public BigDecimal tarifaVigente(int recursoId, LocalDate fecha) throws SQLException {
        // Adaptacion: La tarifa ahora es fija en el recurso.
        // Simulamos obtener la "tarifa vigente" devolviendo la tarifa actual del
        // recurso.
        com.playa.alquiler.model.Recurso r = new com.playa.alquiler.dao.RecursoDAO().obtenerPorId(recursoId);
        if (r != null && r.getTarifa() != null) {
            LOGGER.info("Tarifa recurso=" + recursoId + " precio=" + r.getTarifa());
            return r.getTarifa();
        }
        return BigDecimal.ZERO;
    }
}
