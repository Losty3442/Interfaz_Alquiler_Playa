package com.playa.alquiler.model;

public class Turista {
    private int idTurista;
    private String nombres;
    private String apellidos;
    private String email;
    private String telefono;
    private String nacionalidad;

    public Turista() {}

    public Turista(int idTurista, String nombres, String apellidos, String email, String telefono, String nacionalidad) {
        this.idTurista = idTurista;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
        this.nacionalidad = nacionalidad;
    }

    public int getIdTurista() { return idTurista; }
    public void setIdTurista(int idTurista) { this.idTurista = idTurista; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }
}