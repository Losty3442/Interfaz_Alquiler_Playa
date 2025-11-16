package com.playa.alquiler.model;

import java.math.BigDecimal;

public class DetalleAlquiler {
    private int detalleId;
    private int alquilerId;
    private int recursoId;
    private BigDecimal cantidadHoras;
    private Integer promocionId;
    private BigDecimal totalAPagar;

    public DetalleAlquiler() {}

    public DetalleAlquiler(int detalleId, int alquilerId, int recursoId, BigDecimal cantidadHoras, Integer promocionId, BigDecimal totalAPagar) {
        this.detalleId = detalleId;
        this.alquilerId = alquilerId;
        this.recursoId = recursoId;
        this.cantidadHoras = cantidadHoras;
        this.promocionId = promocionId;
        this.totalAPagar = totalAPagar;
    }

    public int getDetalleId() { return detalleId; }
    public void setDetalleId(int detalleId) { this.detalleId = detalleId; }

    public int getAlquilerId() { return alquilerId; }
    public void setAlquilerId(int alquilerId) { this.alquilerId = alquilerId; }

    public int getRecursoId() { return recursoId; }
    public void setRecursoId(int recursoId) { this.recursoId = recursoId; }

    public BigDecimal getCantidadHoras() { return cantidadHoras; }
    public void setCantidadHoras(BigDecimal cantidadHoras) { this.cantidadHoras = cantidadHoras; }

    public Integer getPromocionId() { return promocionId; }
    public void setPromocionId(Integer promocionId) { this.promocionId = promocionId; }

    public BigDecimal getTotalAPagar() { return totalAPagar; }
    public void setTotalAPagar(BigDecimal totalAPagar) { this.totalAPagar = totalAPagar; }
}