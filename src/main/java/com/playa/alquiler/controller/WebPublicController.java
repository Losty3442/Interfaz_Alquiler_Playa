package com.playa.alquiler.controller;

import com.playa.alquiler.dao.PromocionDAO;
import com.playa.alquiler.dao.RecursoDAO;
import com.playa.alquiler.model.Recurso;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
    public String catalogo(Model model) {
        List<Recurso> recursos = List.of();
        try {
            recursos = recursoDAO.listarTodos();
        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar recursos: " + e.getMessage());
        }
        model.addAttribute("recursos", recursos);
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
