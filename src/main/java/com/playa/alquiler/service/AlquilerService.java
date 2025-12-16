package com.playa.alquiler.service;

import com.playa.alquiler.dao.AlquilerDAO;
import com.playa.alquiler.dao.DetalleAlquilerDAO;
import com.playa.alquiler.dao.PromocionDAO;
import com.playa.alquiler.dao.RecursoDAO;
import com.playa.alquiler.db.ConexionDB;
import com.playa.alquiler.model.Alquiler;
import com.playa.alquiler.model.DetalleAlquiler;
import com.playa.alquiler.model.Promocion;
import com.playa.alquiler.model.Recurso;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AlquilerService {

    private BigDecimal aplicarPromocion(BigDecimal base, Promocion promo) {
        if (promo == null)
            return base;
        String tipo = promo.getTipoDescuento();
        Double valor = promo.getValorDescuento();
        if (tipo == null || valor == null)
            return base;
        BigDecimal result = base;
        if ("Porcentaje".equalsIgnoreCase(tipo)) {
            BigDecimal descuento = base.multiply(BigDecimal.valueOf(valor / 100.0));
            result = base.subtract(descuento);
        } else if ("Monto Fijo".equalsIgnoreCase(tipo)) {
            result = base.subtract(BigDecimal.valueOf(valor));
        }
        // Evitar negativos
        return result.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : result;
    }

    public Alquiler crearAlquiler(Alquiler alquiler, List<DetalleAlquiler> detalles) throws SQLException {
        RecursoDAO recursoDAO = new RecursoDAO();
        PromocionDAO promoDAO = new PromocionDAO();
        AlquilerDAO alquilerDAO = new AlquilerDAO();
        DetalleAlquilerDAO detalleDAO = new DetalleAlquilerDAO();

        LocalDate fechaRef = alquiler.getFecha() != null ? alquiler.getFecha() : LocalDate.now();

        try (Connection conn = ConexionDB.getConnection()) {
            boolean prevAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                // Insertar cabecera de alquiler
                if (alquiler.getHoraInicio() == null) {
                    // Hora actual si no viene definida
                    alquiler.setHoraInicio(java.time.LocalTime.now());
                }
                if (alquiler.getEstadoAlquiler() == null) {
                    alquiler.setEstadoAlquiler("En Curso");
                }
                alquiler.setFecha(fechaRef);
                alquilerDAO.crear(conn, alquiler);

                // Procesar detalles
                for (DetalleAlquiler d : detalles) {
                    // Validar disponibilidad
                    Recurso r = recursoDAO.obtenerPorId(d.getRecursoId());
                    if (r == null)
                        throw new SQLException("Recurso ID " + d.getRecursoId() + " no existe");

                    // Logic Update: Check units implicitly via decrementarUnidades later,
                    // but we can check status here loosely.
                    if ("No disponible".equalsIgnoreCase(r.getEstado()) || "Alquilado".equalsIgnoreCase(r.getEstado())
                            || "En Mantenimiento".equalsIgnoreCase(r.getEstado())) {
                        // Double check units just in case status is stale?
                        // Check units > 0
                        if (r.getUnidades() <= 0) {
                            throw new SQLException(
                                    "Recurso ID " + d.getRecursoId() + " no está disponible (Unidades agotadas)");
                        }
                    }

                    // Usar tarifa directa del recurso
                    BigDecimal precioHora = r.getTarifa();
                    if (precioHora == null)
                        precioHora = BigDecimal.ZERO;

                    // Calcular total por detalle
                    BigDecimal base = precioHora.multiply(d.getCantidadHoras());
                    Promocion promo = null;
                    if (d.getPromocionId() != null) {
                        promo = promoDAO.obtenerActivaPorId(d.getPromocionId(), fechaRef);
                        if (promo == null) {
                            throw new SQLException(
                                    "Promoción ID " + d.getPromocionId() + " no está activa o no existe");
                        }
                    }
                    BigDecimal total = aplicarPromocion(base, promo);

                    d.setAlquilerId(alquiler.getAlquilerId());
                    d.setTotalAPagar(total);
                    detalleDAO.crear(conn, d);

                    // Decrementar Unidades (Maneja estado automaticamente)
                    if (!recursoDAO.decrementarUnidades(conn, d.getRecursoId())) {
                        throw new SQLException("No hay unidades suficientes para el recurso: " + r.getNombreRecurso());
                    }
                }

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(prevAutoCommit);
            }
        }

        return alquiler;
    }

    public void finalizarAlquiler(int alquilerId) throws SQLException {
        try (Connection conn = ConexionDB.getConnection()) {
            boolean prev = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                DetalleAlquilerDAO detalleDAO = new DetalleAlquilerDAO();
                RecursoDAO recursoDAO = new RecursoDAO();
                AlquilerDAO alquilerDAO = new AlquilerDAO();

                // Volver recursos a Disponible (Incrementar unidades)
                for (DetalleAlquiler d : detalleDAO.listarPorAlquilerId(conn, alquilerId)) {
                    recursoDAO.incrementarUnidades(conn, d.getRecursoId());
                }
                // Marcar alquiler como Finalizado
                alquilerDAO.actualizarEstado(conn, alquilerId, "Finalizado");
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(prev);
            }
        }
    }
}