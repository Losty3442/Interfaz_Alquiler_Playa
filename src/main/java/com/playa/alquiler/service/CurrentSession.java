package com.playa.alquiler.service;

import com.playa.alquiler.model.Usuario;

public class CurrentSession {
    private static Usuario usuario;
    private static String nombreRol;

    public static void set(Usuario u, String rol) {
        usuario = u;
        nombreRol = rol;
    }

    public static Usuario getUsuario() {
        return usuario;
    }

    public static String getNombreRol() {
        return nombreRol;
    }

    public static void clear() {
        usuario = null;
        nombreRol = null;
    }
}