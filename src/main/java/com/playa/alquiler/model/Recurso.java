package com.playa.alquiler.model;

public class Recurso {
    private int idRecurso;
    private String nombreRecurso;
    private String descripcion;
    private String estado;
    private String tipoDeRecurso;
    private java.math.BigDecimal tarifa;
    private String imagen;

    private int unidades;

    public Recurso() {
    }

    public Recurso(int idRecurso, String nombreRecurso, String descripcion, String estado, String tipoDeRecurso,
            java.math.BigDecimal tarifa, String imagen, int unidades) {
        this.idRecurso = idRecurso;
        this.nombreRecurso = nombreRecurso;
        this.descripcion = descripcion;
        this.estado = estado;
        this.tipoDeRecurso = tipoDeRecurso;
        this.tarifa = tarifa;
        this.imagen = imagen;
        this.unidades = unidades;
    }

    public int getIdRecurso() {
        return idRecurso;
    }

    public void setIdRecurso(int idRecurso) {
        this.idRecurso = idRecurso;
    }

    public String getNombreRecurso() {
        return nombreRecurso;
    }

    public void setNombreRecurso(String nombreRecurso) {
        this.nombreRecurso = nombreRecurso;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipoDeRecurso() {
        return tipoDeRecurso;
    }

    public void setTipoDeRecurso(String tipoDeRecurso) {
        this.tipoDeRecurso = tipoDeRecurso;
    }

    public java.math.BigDecimal getTarifa() {
        return tarifa;
    }

    public void setTarifa(java.math.BigDecimal tarifa) {
        this.tarifa = tarifa;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getUnidades() {
        return unidades;
    }

    public void setUnidades(int unidades) {
        this.unidades = unidades;
    }

    @Override
    public String toString() {
        return nombreRecurso != null ? nombreRecurso : ("Recurso " + idRecurso);
    }
}