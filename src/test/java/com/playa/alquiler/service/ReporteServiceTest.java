package com.playa.alquiler.service;

import com.playa.alquiler.db.ConexionDB;
import com.playa.alquiler.dao.RecursoDAO;
import com.playa.alquiler.dao.TarifaRecursoDAO;
import com.playa.alquiler.model.Recurso;
import com.playa.alquiler.model.TarifaRecurso;
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
        TarifaRecursoDAO tarifaDAO = new TarifaRecursoDAO();
        Recurso r = new Recurso();
        r.setNombreRecurso("Sombrilla Test");
        r.setDescripcion("Sombrilla");
        r.setEstado("Disponible");
        r.setTipoDeRecurso("Mobiliario");
        recursoDAO.crear(r);

        TarifaRecurso tr = new TarifaRecurso();
        tr.setIdRecurso(r.getIdRecurso());
        tr.setPrecioPorHora(new BigDecimal("10.00"));
        tr.setFechaInicio(LocalDate.now());
        tr.setFechaFin(null);
        tarifaDAO.crear(tr);

        ReporteService rs = new ReporteService();
        TarifaRecurso vigente = rs.tarifaVigente(r.getIdRecurso(), LocalDate.now());
        assertNotNull(vigente);
        assertEquals(new BigDecimal("10.00"), vigente.getPrecioPorHora());

        BigDecimal horas = rs.horasUsadasPorRecurso(r.getIdRecurso());
        assertNotNull(horas);
    }
}