package com.playa.alquiler.controller;

import com.playa.alquiler.dao.AlquilerDAO;
import com.playa.alquiler.dao.DetalleAlquilerDAO;
import com.playa.alquiler.dao.PromocionDAO;
import com.playa.alquiler.dao.RecursoDAO;
import com.playa.alquiler.dao.TuristaDAO;
import com.playa.alquiler.dao.UsuarioDAO;
import com.playa.alquiler.db.ConexionDB;
import com.playa.alquiler.model.Alquiler;
import com.playa.alquiler.model.DetalleAlquiler;
import com.playa.alquiler.model.Recurso;
import com.playa.alquiler.model.Turista;
import com.playa.alquiler.model.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/vendor")
public class WebVendorController {

    private final AlquilerDAO alquilerDAO;
    private final DetalleAlquilerDAO detalleDAO;
    private final PromocionDAO promocionDAO;
    private final RecursoDAO recursoDAO;
    private final TuristaDAO turistaDAO;
    private final UsuarioDAO usuarioDAO;

    public WebVendorController(AlquilerDAO alquilerDAO, DetalleAlquilerDAO detalleDAO, PromocionDAO promocionDAO,
            RecursoDAO recursoDAO, TuristaDAO turistaDAO, UsuarioDAO usuarioDAO) {
        this.alquilerDAO = alquilerDAO;
        this.detalleDAO = detalleDAO;
        this.promocionDAO = promocionDAO;
        this.recursoDAO = recursoDAO;
        this.turistaDAO = turistaDAO;
        this.usuarioDAO = usuarioDAO;
    }

    private Usuario getCurrentUser(Authentication auth) {
        try {
            return usuarioDAO.listarTodos().stream()
                    .filter(u -> u.getNombreUsuario().equals(auth.getName()))
                    .findFirst().orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        Usuario u = getCurrentUser(auth);
        int userId = (u != null) ? u.getUsuarioId() : 0;

        BigDecimal miCaja = BigDecimal.ZERO;
        String sql = "SELECT COALESCE(SUM(d.total_a_pagar),0) FROM detalle_alquiler d " +
                "JOIN Alquileres a ON a.alquiler_id=d.alquiler_id " +
                "WHERE a.usuario_id=? AND a.fecha=?";

        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(LocalDate.now()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    miCaja = rs.getBigDecimal(1);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error calculando caja: " + e.getMessage());
        }

        model.addAttribute("miCaja", miCaja);
        model.addAttribute("usuario", u);
        return "vendor/dashboard";
    }

    @GetMapping("/nuevo-alquiler")
    public String nuevoAlquiler(Model model) {
        try {
            model.addAttribute("recursosDisponibles", recursoDAO.buscarRecursosDisponibles());
            model.addAttribute("promocionesActivas", promocionDAO.listarActivas());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "vendor/nuevo-alquiler";
    }

    // AJAX or Form helper to search tourist
    @PostMapping("/buscar-turista")
    public String buscarTurista(@RequestParam String dni, RedirectAttributes ra) {
        // En este diseño simple, redirigimos de vuelta con los datos si existen
        // Idealmente usaria AJAX, pero mantengamoslo "Server Side" puro como Spring MVC
        // clásico
        try {
            // Nota: TuristaDAO no tiene buscarPorDNI expuesto facil, tiene
            // buscarPorTelefono o Email.
            // Asumiremos que el "DNI" se busca en algun campo o se usa listar y filtrar por
            // ahora.
            // Ojo: El requerimiento pedia "Buscar/Registrar Turista".
            // Vamos a busar telefono como proxy de identificador rápido, o email.
            // Si queremos DNI, deberiamos añadir campo DNI a Turista, pero la clase Turista
            // tiene:
            // Nombres, Apellidos, Email, Telefono, Nacionalidad. NO TIENE DNI.
            // Usaremos EMAIL como identificador único para la busqueda.

            Turista t = turistaDAO.obtenerPorEmail(dni); // Usamos el input como email
            if (t == null)
                t = turistaDAO.obtenerPorTelefono(dni); // O telefono

            if (t != null) {
                ra.addFlashAttribute("turistaEncontrado", t);
                ra.addFlashAttribute("mensaje", "Turista encontrado: " + t.getNombres());
            } else {
                ra.addFlashAttribute("mensaje", "Turista no encontrado. Complete el formulario para registrarlo.");
                ra.addFlashAttribute("nuevoTuristaDato", dni); // Prellenar email/tel
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/vendor/nuevo-alquiler";
    }

    @PostMapping("/procesar-alquiler")
    public String procesarAlquiler(@RequestParam String nombres,
            @RequestParam String apellidos,
            @RequestParam String email,
            @RequestParam(required = false) String telefono,
            @RequestParam String nacionalidad,
            @RequestParam(required = false) List<Integer> idsRecurso,
            @RequestParam String accion,
            @RequestParam(required = false) String horaInicioReserva,
            @RequestParam(required = false) boolean pagadoCheck,
            @RequestParam(required = false) Integer idPromocion,
            jakarta.servlet.http.HttpServletRequest request,
            RedirectAttributes ra) {

        if (idsRecurso == null || idsRecurso.isEmpty()) {
            ra.addFlashAttribute("error", "Debe seleccionar al menos un recurso para alquilar.");
            // Re-fetch resources not strictly needed as redirect loads them, but good
            // practice if forwarding.
            // Since we redirect to /nuevo-alquiler, the GET method re-loads them.
            return "redirect:/vendor/nuevo-alquiler";
        }

        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            try {
                // 1. Buscar o Crear Turista
                String searchKey = (telefono != null && !telefono.isEmpty()) ? telefono : email;
                Turista turista = turistaDAO.obtenerPorTelefono(searchKey);
                if (turista == null)
                    turista = turistaDAO.obtenerPorEmail(searchKey);

                if (turista == null) {
                    turista = new Turista();
                    turista.setNombres(nombres);
                    turista.setApellidos(apellidos);
                    turista.setEmail(email);
                    turista.setTelefono(telefono);
                    turista.setNacionalidad(nacionalidad);
                    turista = turistaDAO.crear(turista);
                }

                // 2. Crear Alquiler
                Alquiler alquiler = new Alquiler();
                Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext()
                        .getAuthentication();
                Usuario vendedor = getCurrentUser(auth);
                // Fallback
                if (vendedor == null) {
                    vendedor = new Usuario();
                    vendedor.setUsuarioId(1); // Default admin/vendor
                }

                alquiler.setUsuarioId(vendedor.getUsuarioId());
                alquiler.setIdTurista(turista.getIdTurista());
                alquiler.setFecha(LocalDate.now());

                // Logic for Status and Time
                if ("reservar".equalsIgnoreCase(accion)) {
                    alquiler.setEstadoAlquiler(pagadoCheck ? "Reservado/Pagado" : "Reservado/Sin Pagar");
                    if (horaInicioReserva != null && !horaInicioReserva.isEmpty()) {
                        try {
                            alquiler.setHoraInicio(LocalTime.parse(horaInicioReserva));
                        } catch (Exception e) {
                            alquiler.setHoraInicio(LocalTime.now().plusHours(1)); // Default logic if parse fails
                        }
                    } else {
                        alquiler.setHoraInicio(LocalTime.now().plusHours(1));
                    }
                } else {
                    // Nuevo Alquiler
                    alquiler.setEstadoAlquiler(pagadoCheck ? "En curso/Pagado" : "En curso/Sin Pagar");
                    alquiler.setHoraInicio(LocalTime.now());
                }

                alquiler = alquilerDAO.crear(conn, alquiler);

                // 3. Detalle y Calculo (Multiple Recursos)
                BigDecimal totalAlquiler = BigDecimal.ZERO;

                for (int idRecurso : idsRecurso) {
                    Recurso recurso = recursoDAO.obtenerPorId(idRecurso);
                    if (recurso == null)
                        continue;

                    // Obtener horas especificas para este recurso
                    String horasParam = request.getParameter("horas_" + idRecurso);
                    int horas = 1;
                    if (horasParam != null && !horasParam.isEmpty()) {
                        try {
                            horas = Integer.parseInt(horasParam);
                        } catch (NumberFormatException e) {
                            horas = 1;
                        }
                    }

                    BigDecimal precioHora = recurso.getTarifa();
                    if (precioHora == null)
                        precioHora = BigDecimal.ZERO;

                    BigDecimal subtotal = precioHora.multiply(new BigDecimal(horas));
                    totalAlquiler = totalAlquiler.add(subtotal);

                    DetalleAlquiler detalle = new DetalleAlquiler();
                    detalle.setAlquilerId(alquiler.getAlquilerId());
                    detalle.setRecursoId(recurso.getIdRecurso());
                    detalle.setCantidadHoras(new BigDecimal(horas));
                    detalle.setTotalAPagar(subtotal);

                    detalleDAO.crear(conn, detalle);

                    // 4. Decrementar Unidades
                    boolean success = recursoDAO.decrementarUnidades(conn, recurso.getIdRecurso());

                    if (!success) {
                        // Si no se pudo decrementar (e.g. unidades era 0), hacemos rollback?
                        // O simplemente continuamos con el siguiente?
                        // Idealmente si alguien reservó y no hay, fallamos.
                        throw new SQLException(
                                "No hay unidades suficientes para el recurso: " + recurso.getNombreRecurso());
                    }

                    // Optional: Check if units became 0 to set 'Alquilado' status visually if
                    // desired,
                    // but the DAO filter handles availability.
                    // We can check if units are handled correctly.
                    // For now, simple decrement is enough.
                }

                // Apply promotion discount if selected
                if (idPromocion != null && idPromocion > 0) {
                    try {
                        com.playa.alquiler.model.Promocion promo = promocionDAO.obtenerActivaPorId(idPromocion,
                                LocalDate.now());
                        if (promo != null) {
                            String tipoDescuento = promo.getTipoDescuento();
                            Double valorDescuento = promo.getValorDescuento();
                            if (tipoDescuento != null && valorDescuento != null) {
                                if ("Porcentaje".equalsIgnoreCase(tipoDescuento)) {
                                    BigDecimal descuento = totalAlquiler
                                            .multiply(BigDecimal.valueOf(valorDescuento / 100.0));
                                    totalAlquiler = totalAlquiler.subtract(descuento);
                                } else if ("Monto Fijo".equalsIgnoreCase(tipoDescuento)) {
                                    totalAlquiler = totalAlquiler.subtract(BigDecimal.valueOf(valorDescuento));
                                }
                                if (totalAlquiler.compareTo(BigDecimal.ZERO) < 0) {
                                    totalAlquiler = BigDecimal.ZERO;
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Ignore promotion errors, use original total
                    }
                }

                conn.commit();
                ra.addFlashAttribute("success",
                        "Operación exitosa. Alquiler #" + alquiler.getAlquilerId()
                                + ". Estado: " + alquiler.getEstadoAlquiler() + ". Total: S/ "
                                + totalAlquiler);

                // Redirect to Confirmation / Receipt page
                return "redirect:/vendor/alquiler/" + alquiler.getAlquilerId() + "/confirmacion";

            } catch (SQLException e) {
                if (conn != null)
                    conn.rollback();
                e.printStackTrace();
                ra.addFlashAttribute("error", "Error Base de Datos: " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error procesando solicitud: " + e.getMessage());
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return "redirect:/vendor/dashboard";
    }

    @PostMapping("/alquiler/{id}/pagar")
    public String pagarAlquiler(@PathVariable int id, RedirectAttributes ra,
            jakarta.servlet.http.HttpServletRequest request) {
        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            Alquiler a = alquilerDAO.obtenerPorId(id);
            if (a != null) {
                String currentStatus = a.getEstadoAlquiler();
                String newStatus = currentStatus;

                if (currentStatus.contains("Sin Pagar")) {
                    newStatus = currentStatus.replace("Sin Pagar", "Pagado");
                } else if (!currentStatus.contains("Pagado")) {
                    newStatus = currentStatus + "/Pagado";
                }

                // Normalizar
                if ("Reservado".equals(currentStatus))
                    newStatus = "Reservado/Pagado";

                alquilerDAO.actualizarEstado(conn, id, newStatus);
                ra.addFlashAttribute("success", "Alquiler #" + id + " marcado como PAGADO.");
            } else {
                ra.addFlashAttribute("error", "Alquiler no encontrado.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al pagar: " + e.getMessage());
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception e) {
            }
        }

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/vendor/ventas");
    }

    @PostMapping("/alquiler/{id}/iniciar")
    public String iniciarReserva(@PathVariable int id, RedirectAttributes ra,
            jakarta.servlet.http.HttpServletRequest request) {
        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            Alquiler a = alquilerDAO.obtenerPorId(id);
            if (a != null && a.getEstadoAlquiler() != null && a.getEstadoAlquiler().startsWith("Reservado")) {
                // Change "Reservado" to "En curso"
                String nuevoEstado = a.getEstadoAlquiler().replace("Reservado", "En curso");

                // Update State
                alquilerDAO.actualizarEstado(conn, id, nuevoEstado);
                // Update Start Time to NOW
                alquilerDAO.actualizarHoraInicio(conn, id, Time.valueOf(LocalTime.now()));

                ra.addFlashAttribute("success", "Reserva iniciada. Tiempo corriendo desde ahora.");
            } else {
                ra.addFlashAttribute("error", "No se puede iniciar este alquiler (Estado incorrecto).");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al iniciar reserva: " + e.getMessage());
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception e) {
            }
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/vendor/ventas");
    }

    @GetMapping("/alquiler/{id}/confirmacion")
    public String confirmacionAlquiler(@PathVariable int id, Model model) {
        try {
            Alquiler alquiler = alquilerDAO.obtenerPorId(id);
            if (alquiler == null) {
                return "redirect:/vendor/dashboard";
            }
            List<DetalleAlquiler> detalles = detalleDAO.listarPorAlquiler(id);

            // Calculate total manually if needed or pass it
            BigDecimal total = BigDecimal.ZERO;
            for (DetalleAlquiler d : detalles) {
                total = total.add(d.getTotalAPagar());
            }

            model.addAttribute("alquiler", alquiler);
            model.addAttribute("detalles", detalles);
            model.addAttribute("total", total);

            return "vendor/confirmacion";
        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/vendor/dashboard";
        }
    }

    @GetMapping("/ventas")
    public String listarVentas(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String fecha,
            Model model) {
        Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication();
        Usuario vendedor = getCurrentUser(auth);

        if (vendedor != null) {
            try {
                LocalDate fechaFiltro = null;
                if (fecha != null && !fecha.isEmpty()) {
                    fechaFiltro = LocalDate.parse(fecha);
                }

                // Use the new filtered method
                List<Alquiler> ventas = alquilerDAO.listarPorFiltros(vendedor.getUsuarioId(), fechaFiltro, estado);
                model.addAttribute("ventas", ventas);
                model.addAttribute("filtroEstado", estado);
                model.addAttribute("filtroFecha", fecha);

            } catch (SQLException e) {
                e.printStackTrace();
                model.addAttribute("error", "Error al cargar ventas.");
            }
        }
        return "vendor/ventas";
    }
}
