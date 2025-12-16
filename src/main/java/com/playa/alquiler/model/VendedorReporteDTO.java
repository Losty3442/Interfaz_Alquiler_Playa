package com.playa.alquiler.model;

import java.math.BigDecimal;
import java.util.List;

public class VendedorReporteDTO {
    private String nombreVendedor;
    private BigDecimal totalVentas;
    private int cantidadAlquileres;
    private List<AlquilerReporteDTO> alquileres;

    public VendedorReporteDTO(String nombreVendedor, BigDecimal totalVentas, int cantidadAlquileres,
            List<AlquilerReporteDTO> alquileres) {
        this.nombreVendedor = nombreVendedor;
        this.totalVentas = totalVentas;
        this.cantidadAlquileres = cantidadAlquileres;
        this.alquileres = alquileres;
    }

    public String getNombreVendedor() {
        return nombreVendedor;
    }

    public BigDecimal getTotalVentas() {
        return totalVentas;
    }

    public int getCantidadAlquileres() {
        return cantidadAlquileres;
    }

    public List<AlquilerReporteDTO> getAlquileres() {
        return alquileres;
    }
}
