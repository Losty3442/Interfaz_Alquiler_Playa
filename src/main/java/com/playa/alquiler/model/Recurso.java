package com.playa.alquiler.model;

public class Recurso {
    private int idRecurso;
    private String nombreRecurso;
    private String descripcion;
    private String estado;
    private String tipoDeRecurso;

    public Recurso() {}

    public Recurso(int idRecurso, String nombreRecurso, String descripcion, String estado, String tipoDeRecurso) {
        this.idRecurso = idRecurso;
        this.nombreRecurso = nombreRecurso;
        this.descripcion = descripcion;
        this.estado = estado;
        this.tipoDeRecurso = tipoDeRecurso;
    }

    public int getIdRecurso() { return idRecurso; }
    public void setIdRecurso(int idRecurso) { this.idRecurso = idRecurso; }

    public String getNombreRecurso() { return nombreRecurso; }
    public void setNombreRecurso(String nombreRecurso) { this.nombreRecurso = nombreRecurso; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getTipoDeRecurso() { return tipoDeRecurso; }
    public void setTipoDeRecurso(String tipoDeRecurso) { this.tipoDeRecurso = tipoDeRecurso; }

    @Override
    public String toString() {
        return nombreRecurso != null ? nombreRecurso : ("Recurso " + idRecurso);
    }
}