package com.playa.alquiler.controller;

import com.playa.alquiler.dao.RecursoDAO;
import com.playa.alquiler.model.Alquiler;
import com.playa.alquiler.model.DetalleAlquiler;
import com.playa.alquiler.model.Recurso;
import com.playa.alquiler.service.AlquilerService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import com.playa.alquiler.service.CurrentSession;

public class AlquilerController {
    @FXML
    private TextField turistaIdField;
    @FXML
    private TextField usuarioIdField;
    @FXML
    private TextField turistaTelefonoField;
    @FXML
    private TextField nombresField;
    @FXML
    private TextField apellidosField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField telefonoField;
    @FXML
    private TextField nacionalidadField;
    @FXML
    private ComboBox<Recurso> recursoCombo;
    @FXML
    private TextField horasField;
    @FXML
    private ComboBox<com.playa.alquiler.model.Promocion> promoCombo;
    @FXML
    private TableView<DetalleAlquiler> detallesTable;
    @FXML
    private TableColumn<DetalleAlquiler, Integer> colRecursoId;
    @FXML
    private TableColumn<DetalleAlquiler, BigDecimal> colHoras;
    @FXML
    private TableColumn<DetalleAlquiler, Integer> colPromo;
    @FXML
    private Label statusLabel;
    @FXML
    private TextField alquilerIdField;
    @FXML
    private Label totalLabel;
    @FXML
    private TableView<Alquiler> alquileresTable;
    @FXML
    private TableColumn<Alquiler, Integer> colAlqId;
    @FXML
    private TableColumn<Alquiler, String> colAlqEstado;
    @FXML
    private ComboBox<String> estadoFiltroCombo;
    @FXML
    private javafx.scene.control.ScrollPane alquilerScroll;

    private final ObservableList<DetalleAlquiler> detalles = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            RecursoDAO recursoDAO = new RecursoDAO();
            recursoCombo.setItems(FXCollections.observableArrayList(recursoDAO.buscarRecursosDisponibles()));
        } catch (SQLException e) {
            showError("Error cargando recursos disponibles: " + e.getMessage());
        }

        // Prellenar el usuario logueado (si existe)
        if (CurrentSession.getUsuario() != null) {
            usuarioIdField.setText(String.valueOf(CurrentSession.getUsuario().getUsuarioId()));
        }

        detallesTable.setItems(detalles);
        colRecursoId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("recursoId"));
        colHoras.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("cantidadHoras"));
        colPromo.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("promocionId"));

        try {
            var promos = new com.playa.alquiler.dao.PromocionDAO().listarActivas();
            if (promoCombo != null) {
                promoCombo.setItems(FXCollections.observableArrayList(promos));
                promoCombo.setCellFactory(cb -> new javafx.scene.control.ListCell<>() {
                    @Override
                    protected void updateItem(com.playa.alquiler.model.Promocion item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? null : item.getNombrePromocion());
                    }
                });
                promoCombo.setButtonCell(new javafx.scene.control.ListCell<>() {
                    @Override
                    protected void updateItem(com.playa.alquiler.model.Promocion item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? null : item.getNombrePromocion());
                    }
                });
            }
        } catch (SQLException e) {
            showError("Error cargando promociones: " + e.getMessage());
        }

        if (alquileresTable != null) {
            colAlqId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("alquilerId"));
            colAlqEstado.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("estadoAlquiler"));
            estadoFiltroCombo
                    .setItems(FXCollections.observableArrayList("En Curso", "Reservado", "Finalizado", "Cancelado"));
        }

        if (alquilerScroll != null) {
            alquilerScroll.addEventFilter(javafx.scene.input.ScrollEvent.SCROLL, e -> {
                double v = alquilerScroll.getVvalue();
                double speed = 0.003;
                double next = v - e.getDeltaY() * speed;
                if (next < 0)
                    next = 0;
                else if (next > 1)
                    next = 1;
                alquilerScroll.setVvalue(next);
                e.consume();
            });
        }
    }

    @FXML
    public void onAddDetalle(ActionEvent event) {
        Recurso recurso = recursoCombo.getValue();
        if (recurso == null) {
            showError("Seleccione un recurso disponible");
            return;
        }
        BigDecimal horas;
        try {
            horas = new BigDecimal(horasField.getText());
            if (horas.compareTo(BigDecimal.ZERO) <= 0)
                throw new NumberFormatException();
        } catch (Exception ex) {
            showError("Ingrese horas válidas (> 0)");
            return;
        }

        Integer promoId = promoCombo != null && promoCombo.getValue() != null ? promoCombo.getValue().getIdPromocion()
                : null;

        DetalleAlquiler d = new DetalleAlquiler();
        d.setRecursoId(recurso.getIdRecurso());
        d.setCantidadHoras(horas);
        d.setPromocionId(promoId);
        detalles.add(d);
        horasField.clear();
        if (promoCombo != null)
            promoCombo.getSelectionModel().clearSelection();
        statusLabel.setText("Detalle agregado");
        recalcularTotalEstimado();
    }

    @FXML
    public void onRegistrarAlquiler(ActionEvent event) {
        Integer turistaId;
        int usuarioId;
        try {
            turistaId = parseTuristaId();
            usuarioId = Integer.parseInt(usuarioIdField.getText());
        } catch (NumberFormatException e) {
            showError("Ingrese usuario válido o seleccione/registre turista");
            return;
        }
        if (turistaId == null)
            return;
        if (detalles.isEmpty()) {
            showError("Agregue al menos un detalle");
            return;
        }

        Alquiler alquiler = new Alquiler();
        alquiler.setFecha(LocalDate.now());
        alquiler.setIdTurista(turistaId);
        alquiler.setUsuarioId(usuarioId);

        try {
            new AlquilerService().crearAlquiler(alquiler, detalles);
            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setTitle("Alquiler creado");
            ok.setHeaderText(null);
            ok.setContentText("Alquiler ID: " + alquiler.getAlquilerId());
            ok.showAndWait();
            detalles.clear();
            statusLabel.setText("Alquiler registrado");
            alquilerIdField.setText(String.valueOf(alquiler.getAlquilerId()));
            totalLabel.setText("Total estimado: S/ -");
        } catch (SQLException e) {
            showError("Error creando alquiler: " + e.getMessage());
        }
    }

    private Integer parseTuristaId() {
        try {
            if (turistaIdField != null && turistaIdField.getText() != null && !turistaIdField.getText().isBlank()) {
                return Integer.parseInt(turistaIdField.getText());
            }
        } catch (NumberFormatException ignored) {
        }
        try {
            String tel = telefonoField != null ? telefonoField.getText() : null;
            if (tel != null && !tel.isBlank()) {
                var t = new com.playa.alquiler.dao.TuristaDAO().obtenerPorTelefono(tel);
                if (t != null)
                    return t.getIdTurista();
            }
        } catch (SQLException ignored) {
        }
        showError("Seleccione o registre un turista");
        return null;
    }

    @FXML
    public void onBuscarTuristaPorTelefono(ActionEvent event) {
        String telefono = turistaTelefonoField.getText();
        if (telefono == null || telefono.isBlank()) {
            showError("Ingrese teléfono");
            return;
        }
        try {
            var t = new com.playa.alquiler.dao.TuristaDAO().obtenerPorTelefono(telefono);
            if (t == null) {
                statusLabel.setText("No encontrado");
                return;
            }
            nombresField.setText(t.getNombres());
            apellidosField.setText(t.getApellidos());
            emailField.setText(t.getEmail());
            telefonoField.setText(t.getTelefono());
            nacionalidadField.setText(t.getNacionalidad());
            turistaIdField.setText(String.valueOf(t.getIdTurista()));
            statusLabel.setText("Turista encontrado");
        } catch (SQLException e) {
            showError("Error buscando turista: " + e.getMessage());
        }
    }

    @FXML
    public void onRegistrarTuristaNuevo(ActionEvent event) {
        try {
            String nombres = nombresField.getText();
            String apellidos = apellidosField.getText();
            String email = emailField.getText();
            String tel = telefonoField.getText();
            String nac = nacionalidadField.getText();
            if (nombres == null || nombres.isBlank() || apellidos == null || apellidos.isBlank() || email == null
                    || email.isBlank() || tel == null || tel.isBlank()) {
                showError("Complete datos de turista");
                return;
            }
            var dao = new com.playa.alquiler.dao.TuristaDAO();
            com.playa.alquiler.model.Turista existente = dao.obtenerPorTelefono(tel);
            if (existente == null)
                existente = dao.obtenerPorEmail(email);
            if (existente != null) {
                turistaIdField.setText(String.valueOf(existente.getIdTurista()));
                nombresField.setText(existente.getNombres());
                apellidosField.setText(existente.getApellidos());
                emailField.setText(existente.getEmail());
                telefonoField.setText(existente.getTelefono());
                nacionalidadField.setText(existente.getNacionalidad());
                statusLabel.setText("Turista ya existe. Datos cargados.");
                return;
            }
            var t = new com.playa.alquiler.model.Turista();
            t.setNombres(nombres);
            t.setApellidos(apellidos);
            t.setEmail(email);
            t.setTelefono(tel);
            t.setNacionalidad(nac);
            dao.crear(t);
            turistaIdField.setText(String.valueOf(t.getIdTurista()));
            statusLabel.setText("Turista registrado");
        } catch (SQLException e) {
            showError("Error registrando turista: " + e.getMessage());
        }
    }

    private void recalcularTotalEstimado() {
        try {
            java.math.BigDecimal total = java.math.BigDecimal.ZERO;
            var recursoDAO = new com.playa.alquiler.dao.RecursoDAO();
            var promoDAO = new com.playa.alquiler.dao.PromocionDAO();
            for (DetalleAlquiler d : detalles) {
                var recurso = recursoDAO.obtenerPorId(d.getRecursoId());
                if (recurso == null || recurso.getTarifa() == null)
                    continue;
                java.math.BigDecimal base = recurso.getTarifa().multiply(d.getCantidadHoras());
                com.playa.alquiler.model.Promocion promo = null;
                if (d.getPromocionId() != null)
                    promo = promoDAO.obtenerActivaPorId(d.getPromocionId(), java.time.LocalDate.now());
                java.math.BigDecimal t = aplicarPromocionLocal(base, promo);
                total = total.add(t);
            }
            if (totalLabel != null)
                totalLabel.setText("Total estimado: S/ " + total);
        } catch (Exception ignored) {
        }
    }

    private java.math.BigDecimal aplicarPromocionLocal(java.math.BigDecimal base,
            com.playa.alquiler.model.Promocion promo) {
        if (promo == null)
            return base;
        String tipo = promo.getTipoDescuento();
        Double valor = promo.getValorDescuento();
        if (tipo == null || valor == null)
            return base;
        java.math.BigDecimal result = base;
        if ("Porcentaje".equalsIgnoreCase(tipo)) {
            java.math.BigDecimal descuento = base.multiply(java.math.BigDecimal.valueOf(valor / 100.0));
            result = base.subtract(descuento);
        } else if ("Monto Fijo".equalsIgnoreCase(tipo)) {
            result = base.subtract(java.math.BigDecimal.valueOf(valor));
        }
        return result.compareTo(java.math.BigDecimal.ZERO) < 0 ? java.math.BigDecimal.ZERO : result;
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
        statusLabel.setText(msg);
    }

    @FXML
    public void onFinalizarAlquiler(ActionEvent event) {
        try {
            int id = Integer.parseInt(alquilerIdField.getText());
            new AlquilerService().finalizarAlquiler(id);
            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setTitle("Alquiler finalizado");
            ok.setHeaderText(null);
            ok.setContentText("Se finalizó el alquiler " + id + " y se liberaron recursos.");
            ok.showAndWait();
            statusLabel.setText("Alquiler finalizado");
        } catch (NumberFormatException e) {
            showError("Ingrese un ID de alquiler válido");
        } catch (SQLException e) {
            showError("Error finalizando alquiler: " + e.getMessage());
        }
    }

    @FXML
    public void onMarcarPagado(ActionEvent event) {
        try {
            int id = Integer.parseInt(alquilerIdField.getText());
            new com.playa.alquiler.service.PagoService().marcarPagado(id);
            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setTitle("Pago registrado");
            ok.setHeaderText(null);
            ok.setContentText("Alquiler " + id + " marcado como Pagado.");
            ok.showAndWait();
            statusLabel.setText("Alquiler marcado Pagado");
        } catch (NumberFormatException e) {
            showError("Ingrese un ID de alquiler válido");
        } catch (SQLException e) {
            showError("Error marcando pago: " + e.getMessage());
        }
    }

    @FXML
    public void onConsultarAlquileres(ActionEvent event) {
        String estado = estadoFiltroCombo.getValue();
        if (estado == null || estado.isBlank()) {
            showError("Seleccione estado");
            return;
        }
        try {
            java.util.List<Alquiler> lista = new com.playa.alquiler.dao.AlquilerDAO().listarPorEstado(estado);
            alquileresTable.setItems(FXCollections.observableArrayList(lista));
            statusLabel.setText("Cargados " + lista.size() + " alquiler(es)");
        } catch (SQLException e) {
            showError("Error consultando: " + e.getMessage());
        }
    }

    @FXML
    public void onCancelarSeleccionado(ActionEvent event) {
        Alquiler sel = alquileresTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Seleccione un alquiler");
            return;
        }
        try {
            try (java.sql.Connection conn = com.playa.alquiler.db.ConexionDB.getConnection()) {
                new com.playa.alquiler.dao.AlquilerDAO().actualizarEstado(conn, sel.getAlquilerId(), "Cancelado");
            }
            statusLabel.setText("Alquiler cancelado: " + sel.getAlquilerId());
            onConsultarAlquileres(null);
        } catch (SQLException e) {
            showError("Error cancelando: " + e.getMessage());
        }
    }
}