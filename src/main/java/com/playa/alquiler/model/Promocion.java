package com.playa.alquiler.model;

public class Promocion {
    private int idPromocion;
    private String nombrePromocion;
    private String descripcion;
    private String tipoDescuento; // Porcentaje, Monto Fijo
    private Double valorDescuento;
    private String duracionPromocion;
    private String condicionMinima;
    private String estado;
    private Integer usuarioId;

    public int getIdPromocion() { return idPromocion; }
    public void setIdPromocion(int idPromocion) { this.idPromocion = idPromocion; }
    public String getNombrePromocion() { return nombrePromocion; }
    public void setNombrePromocion(String nombrePromocion) { this.nombrePromocion = nombrePromocion; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getTipoDescuento() { return tipoDescuento; }
    public void setTipoDescuento(String tipoDescuento) { this.tipoDescuento = tipoDescuento; }
    public Double getValorDescuento() { return valorDescuento; }
    public void setValorDescuento(Double valorDescuento) { this.valorDescuento = valorDescuento; }
    public String getDuracionPromocion() { return duracionPromocion; }
    public void setDuracionPromocion(String duracionPromocion) { this.duracionPromocion = duracionPromocion; }
    public String getCondicionMinima() { return condicionMinima; }
    public void setCondicionMinima(String condicionMinima) { this.condicionMinima = condicionMinima; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
}