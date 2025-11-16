package com.playa.alquiler.model;

import java.math.BigDecimal;

public class ResumenVentas {
    private final BigDecimal total;
    private final int cantidadAlquileres;

    public ResumenVentas(BigDecimal total, int cantidadAlquileres) {
        this.total = total;
        this.cantidadAlquileres = cantidadAlquileres;
    }

    public BigDecimal getTotal() { return total; }
    public int getCantidadAlquileres() { return cantidadAlquileres; }
}