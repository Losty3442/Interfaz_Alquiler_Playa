package com.playa.alquiler.model;

public class Usuario {
    private int usuarioId;
    private String nombreUsuario;
    private String email;
    private String contrasena;
    private Integer rolId;

    public Usuario() {}

    public Usuario(int usuarioId, String nombreUsuario, String email, String contrasena, Integer rolId) {
        this.usuarioId = usuarioId;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.contrasena = contrasena;
        this.rolId = rolId;
    }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public Integer getRolId() { return rolId; }
    public void setRolId(Integer rolId) { this.rolId = rolId; }
}