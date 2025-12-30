package com.playa.alquiler.controller;

import com.playa.alquiler.dao.PromocionDAO;
import com.playa.alquiler.dao.RecursoDAO;
import com.playa.alquiler.db.ConexionDB;
import com.playa.alquiler.model.Recurso;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Controller
public class WebPublicController {

    private final RecursoDAO recursoDAO;
    private final PromocionDAO promocionDAO;

    public WebPublicController(RecursoDAO recursoDAO, PromocionDAO promocionDAO) {
        this.recursoDAO = recursoDAO;
        this.promocionDAO = promocionDAO;
    }

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/test-db")
    @ResponseBody
    public String testDatabase() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== DIAGNÓSTICO DE BASE DE DATOS ===\n\n");

        // Mostrar configuración
        String dbUrl = System.getenv("DATABASE_URL");
        String dbUser = System.getenv("DATABASE_USERNAME");
        sb.append("Variables de entorno:\n");
        sb.append("  DATABASE_URL: ")
                .append(dbUrl != null ? "CONFIGURADA" : "NO CONFIGURADA (usando application.properties)").append("\n");
        sb.append("  DATABASE_USERNAME: ")
                .append(dbUser != null ? "CONFIGURADO" : "NO CONFIGURADO (usando application.properties)")
                .append("\n\n");

        // Probar conexión
        sb.append("Probando conexión...\n");
        try {
            long start = System.currentTimeMillis();
            Connection conn = ConexionDB.getConnection();
            long elapsed = System.currentTimeMillis() - start;

            if (conn != null && !conn.isClosed()) {
                sb.append("✅ CONEXIÓN EXITOSA en ").append(elapsed).append("ms\n");
                sb.append("  - Base de datos: ").append(conn.getCatalog()).append("\n");
                sb.append("  - Schema: ").append(conn.getSchema()).append("\n");
                conn.close();
            } else {
                sb.append("❌ Conexión fallida: conexión nula o cerrada\n");
            }
        } catch (SQLException e) {
            sb.append("❌ ERROR SQL: ").append(e.getMessage()).append("\n");
            sb.append("  - SQLState: ").append(e.getSQLState()).append("\n");
            sb.append("  - ErrorCode: ").append(e.getErrorCode()).append("\n");
        } catch (Exception e) {
            sb.append("❌ ERROR: ").append(e.getClass().getSimpleName()).append(": ").append(e.getMessage())
                    .append("\n");
            if (e.getCause() != null) {
                sb.append("  - Causa: ").append(e.getCause().getMessage()).append("\n");
            }
        }

        return sb.toString();
    }

    @GetMapping("/catalogo")
    public String catalogo(
            @RequestParam(required = false, defaultValue = "false") boolean showUnavailable,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fecha,
            Model model) {
        List<Recurso> recursos = List.of();
        try {
            if (showUnavailable) {
                recursos = recursoDAO.listarTodos();
            } else {
                recursos = recursoDAO.buscarRecursosDisponibles();
            }

            // Filter by type if specified
            if (tipo != null && !tipo.isEmpty()) {
                final String tipoFinal = tipo;
                recursos = recursos.stream()
                        .filter(r -> tipoFinal.equalsIgnoreCase(r.getTipoDeRecurso()))
                        .collect(java.util.stream.Collectors.toList());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar recursos: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error de conexión: " + e.getMessage());
        }
        model.addAttribute("recursos", recursos);
        model.addAttribute("showUnavailable", showUnavailable);
        model.addAttribute("tipoSeleccionado", tipo);
        model.addAttribute("fechaSeleccionada", fecha);
        return "catalogo";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/promociones")
    public String promociones(Model model) {
        try {
            model.addAttribute("promociones", promocionDAO.listarTodas());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "promociones";
    }
}
