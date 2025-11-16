package com.playa.alquiler.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Alquiler {
    private int alquilerId;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private String estadoAlquiler;
    private int idTurista;
    private int usuarioId;

    public Alquiler() {}

    public Alquiler(int alquilerId, LocalDate fecha, LocalTime horaInicio, String estadoAlquiler, int idTurista, int usuarioId) {
        this.alquilerId = alquilerId;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.estadoAlquiler = estadoAlquiler;
        this.idTurista = idTurista;
        this.usuarioId = usuarioId;
    }

    public int getAlquilerId() { return alquilerId; }
    public void setAlquilerId(int alquilerId) { this.alquilerId = alquilerId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public String getEstadoAlquiler() { return estadoAlquiler; }
    public void setEstadoAlquiler(String estadoAlquiler) { this.estadoAlquiler = estadoAlquiler; }

    public int getIdTurista() { return idTurista; }
    public void setIdTurista(int idTurista) { this.idTurista = idTurista; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
}