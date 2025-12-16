package com.playa.alquiler.service;

import com.playa.alquiler.db.ConexionDB;
import com.playa.alquiler.dao.RecursoDAO;

import com.playa.alquiler.model.Recurso;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ReporteServiceTest {
    @BeforeEach
    void checkDb() {
        Assumptions.assumeTrue(ConexionDB.testConnection());
    }

    @Test
    void tarifaVigenteYHorasUsadas() throws Exception {
        RecursoDAO recursoDAO = new RecursoDAO();

        Recurso r = new Recurso();
        r.setNombreRecurso("Sombrilla Test");
        r.setDescripcion("Sombrilla");
        r.setEstado("Disponible");
        r.setTipoDeRecurso("Mobiliario");
        r.setTarifa(new BigDecimal("10.00"));
        recursoDAO.crear(r);

        ReporteService rs = new ReporteService();
        BigDecimal vigente = rs.tarifaVigente(r.getIdRecurso(), LocalDate.now());
        assertNotNull(vigente);
        assertEquals(new BigDecimal("10.00"), vigente);

        BigDecimal horas = rs.horasUsadasPorRecurso(r.getIdRecurso());
        assertNotNull(horas);
    }
}