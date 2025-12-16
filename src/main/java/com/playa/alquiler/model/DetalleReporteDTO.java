package com.playa.alquiler.model;

import java.math.BigDecimal;
import java.time.LocalTime;

public class DetalleReporteDTO {
    private String nombreVendedor;
    private int alquilerId;
    private LocalTime hora;
    private String nombreTurista;
    private String nombreRecurso;
    private BigDecimal cantidadHoras;
    private BigDecimal montoDetalle;

    public DetalleReporteDTO(String nombreVendedor, int alquilerId, LocalTime hora, String nombreTurista,
            String nombreRecurso, BigDecimal cantidadHoras, BigDecimal montoDetalle) {
        this.nombreVendedor = nombreVendedor;
        this.alquilerId = alquilerId;
        this.hora = hora;
        this.nombreTurista = nombreTurista;
        this.nombreRecurso = nombreRecurso;
        this.cantidadHoras = cantidadHoras;
        this.montoDetalle = montoDetalle;
    }

    public String getNombreVendedor() {
        return nombreVendedor;
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

    public String getNombreRecurso() {
        return nombreRecurso;
    }

    public BigDecimal getCantidadHoras() {
        return cantidadHoras;
    }

    public BigDecimal getMontoDetalle() {
        return montoDetalle;
    }
}
