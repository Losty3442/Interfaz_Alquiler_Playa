package com.playa.alquiler.model;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

public class AlquilerReporteDTO {
    private int alquilerId;
    private LocalTime hora;
    private String nombreTurista;
    private BigDecimal total;
    private List<DetalleReporteDTO> detalles;

    public AlquilerReporteDTO(int alquilerId, LocalTime hora, String nombreTurista, BigDecimal total,
            List<DetalleReporteDTO> detalles) {
        this.alquilerId = alquilerId;
        this.hora = hora;
        this.nombreTurista = nombreTurista;
        this.total = total;
        this.detalles = detalles;
    }

    public int getAlquilerId() {
        return alquilerId;
    }

    public LocalTime getHora() {
        return hora;
    }

    public String getNombreTurista() {
        return nombreTurista;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public List<DetalleReporteDTO> getDetalles() {
        return detalles;
    }
}
