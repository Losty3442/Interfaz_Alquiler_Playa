package com.playa.alquiler.model;

import java.math.BigDecimal;

public class ResumenVentas {
    private final String nombreVendedor;
    private final BigDecimal total;
    private final int cantidadAlquileres;

    public ResumenVentas(String nombreVendedor, BigDecimal total, int cantidadAlquileres) {
        this.nombreVendedor = nombreVendedor;
        this.total = total;
        this.cantidadAlquileres = cantidadAlquileres;
    }

    public String getNombreVendedor() {
        return nombreVendedor;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public int getCantidadAlquileres() {
        return cantidadAlquileres;
    }
}