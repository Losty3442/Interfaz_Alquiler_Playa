package com.playa.alquiler.service;

import com.playa.alquiler.db.ConexionDB;
import com.playa.alquiler.dao.UsuarioDAO;
import com.playa.alquiler.dao.TuristaDAO;
import com.playa.alquiler.dao.RecursoDAO;
import com.playa.alquiler.model.Usuario;
import com.playa.alquiler.model.Turista;
import com.playa.alquiler.model.Recurso;

import com.playa.alquiler.model.Alquiler;
import com.playa.alquiler.model.DetalleAlquiler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PagoServiceTest {
    @BeforeEach
    void checkDb() {
        Assumptions.assumeTrue(ConexionDB.testConnection());
    }

    @Test
    void calcularTotalYMarcarPagado() throws Exception {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        TuristaDAO turistaDAO = new TuristaDAO();
        RecursoDAO recursoDAO = new RecursoDAO();

        Usuario u = new Usuario();
        u.setNombreUsuario("tester");
        u.setEmail("tester@playa.com");
        u.setContrasena("1234");
        u.setRolId(2);
        usuarioDAO.crear(u);

        Turista t = new Turista();
        t.setNombres("Ana");
        t.setApellidos("Prueba");
        t.setEmail("ana.prueba@playa.com");
        t.setTelefono("000");
        t.setNacionalidad("PE");
        turistaDAO.crear(t);

        Recurso r1 = new Recurso();
        r1.setNombreRecurso("Silla Test");
        r1.setDescripcion("Silla");
        r1.setEstado("Disponible");
        r1.setTipoDeRecurso("Mobiliario");
        r1.setTarifa(new BigDecimal("5.00"));
        recursoDAO.crear(r1);

        Alquiler alquiler = new Alquiler();
        alquiler.setFecha(LocalDate.now());
        alquiler.setIdTurista(t.getIdTurista());
        alquiler.setUsuarioId(u.getUsuarioId());

        DetalleAlquiler d1 = new DetalleAlquiler();
        d1.setRecursoId(r1.getIdRecurso());
        d1.setCantidadHoras(new BigDecimal("3.0"));

        new AlquilerService().crearAlquiler(alquiler, List.of(d1));

        var pagoService = new PagoService();
        BigDecimal total = pagoService.calcularTotal(alquiler.getAlquilerId());
        assertEquals(new BigDecimal("15.00"), total);

        pagoService.marcarPagado(alquiler.getAlquilerId());
        BigDecimal totalHoy = pagoService.ventasDiariasPorVendedor(u.getUsuarioId(), LocalDate.now()).getTotal();
        assertTrue(totalHoy.compareTo(BigDecimal.ZERO) >= 0);
    }
}