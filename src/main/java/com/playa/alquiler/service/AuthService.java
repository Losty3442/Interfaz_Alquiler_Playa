package com.playa.alquiler.service;

import com.playa.alquiler.dao.RolDAO;
import com.playa.alquiler.dao.UsuarioDAO;
import com.playa.alquiler.model.Usuario;

public class AuthService {
    public static class ResultadoLogin {
        public final Usuario usuario;
        public final String nombreRol;
        public ResultadoLogin(Usuario usuario, String nombreRol) {
            this.usuario = usuario;
            this.nombreRol = nombreRol;
        }
    }

    public ResultadoLogin login(String nombreUsuario, String contrasena) throws Exception {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario u = usuarioDAO.obtenerPorCredenciales(nombreUsuario, contrasena);
        if (u == null) return null;
        String rolNombre = new RolDAO().obtenerNombreRolPorId(u.getRolId());
        return new ResultadoLogin(u, rolNombre);
    }
}