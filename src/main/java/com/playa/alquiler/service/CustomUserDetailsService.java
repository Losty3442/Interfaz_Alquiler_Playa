package com.playa.alquiler.service;

import com.playa.alquiler.dao.RolDAO;
import com.playa.alquiler.dao.UsuarioDAO;
import com.playa.alquiler.model.Usuario;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioDAO usuarioDAO;
    private final RolDAO rolDAO;

    public CustomUserDetailsService(UsuarioDAO usuarioDAO, RolDAO rolDAO) {
        this.usuarioDAO = usuarioDAO;
        this.rolDAO = rolDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // Búsqueda directa por nombre de usuario (optimizado - sin cargar todos los
            // usuarios)
            Usuario usuario = usuarioDAO.obtenerPorNombreUsuario(username);
            if (usuario == null) {
                throw new UsernameNotFoundException("Usuario no encontrado: " + username);
            }

            String rolNombre = rolDAO.obtenerNombreRolPorId(usuario.getRolId());
            System.out.println(
                    "DEBUG LOGIN - User: " + username + ", Rol ID: " + usuario.getRolId() + ", Rol Name: " + rolNombre);

            if (rolNombre == null)
                rolNombre = "USER";

            // Normalización de Roles para Spring Security (Prefijo ROLE_)
            // Mapeamos lo que viene de BD a estandares de Spring pero respetando nombre del
            // requerimiento
            String roleAuthority;
            if (rolNombre.equalsIgnoreCase("Administrador")) {
                roleAuthority = "ROLE_ADMINISTRADOR";
            } else if (rolNombre.equalsIgnoreCase("Vendedor")) {
                roleAuthority = "ROLE_VENDEDOR";
            } else {
                roleAuthority = "ROLE_" + rolNombre.toUpperCase();
            }

            return User.builder()
                    .username(usuario.getNombreUsuario())
                    .password("{noop}" + usuario.getContrasena())
                    .authorities(roleAuthority)
                    .build();

        } catch (SQLException e) {
            throw new RuntimeException("Error al acceder a la base de datos", e);
        }
    }
}
