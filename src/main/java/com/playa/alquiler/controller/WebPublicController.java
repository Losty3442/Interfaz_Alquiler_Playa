package com.playa.alquiler.controller;

import com.playa.alquiler.dao.PromocionDAO;
import com.playa.alquiler.dao.RecursoDAO;
import com.playa.alquiler.model.Recurso;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
