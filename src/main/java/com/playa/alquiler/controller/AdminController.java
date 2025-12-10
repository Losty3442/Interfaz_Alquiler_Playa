package com.playa.alquiler.controller;

import com.playa.alquiler.dao.RolDAO;
import com.playa.alquiler.dao.PromocionDAO;
import com.playa.alquiler.dao.TarifaRecursoDAO;
import com.playa.alquiler.dao.AlquilerDAO;
import com.playa.alquiler.model.Promocion;
import com.playa.alquiler.model.TarifaRecurso;
import com.playa.alquiler.service.CurrentSession;
import com.playa.alquiler.dao.UsuarioDAO;
import com.playa.alquiler.model.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Arrays;

public class AdminController {
    @FXML private TableView<Usuario> usuariosTable;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, String> colRol;

    @FXML private TextField nuevoUsuarioField;
    @FXML private TextField nuevoEmailField;
    @FXML private PasswordField nuevoContrasenaField;
    @FXML private ComboBox<String> rolCombo;
    @FXML private Label estadoAdminLabel;
    @FXML private Label currentUserLabel;

    // Paneles
    @FXML private javafx.scene.layout.VBox usuariosPane;
    @FXML private javafx.scene.layout.VBox promocionesPane;
    @FXML private javafx.scene.layout.VBox tarifasPane;
    @FXML private javafx.scene.layout.VBox recursosPane;
    @FXML private javafx.scene.layout.VBox reportesPane;

    // Promociones
    @FXML private TableView<Promocion> promosTable;
    @FXML private TableColumn<Promocion, Integer> colPromoId;
    @FXML private TableColumn<Promocion, String> colPromoNombre;
    @FXML private TableColumn<Promocion, String> colPromoTipo;
    @FXML private TableColumn<Promocion, Double> colPromoValor;
    @FXML private TableColumn<Promocion, String> colPromoEstado;
    @FXML private TextField promoNombreField;
    @FXML private ComboBox<String> promoTipoCombo;
    @FXML private TextField promoValorField;
    @FXML private ComboBox<String> promoEstadoCombo;

    // Tarifas
    @FXML private TextField tarifaRecursoIdField;
    @FXML private TableView<TarifaRecurso> tarifasTable;
    @FXML private TableColumn<TarifaRecurso, Integer> colTarifaId;
    @FXML private TableColumn<TarifaRecurso, java.math.BigDecimal> colTarifaPrecio;
    @FXML private TableColumn<TarifaRecurso, java.time.LocalDate> colTarifaInicio;
    @FXML private TableColumn<TarifaRecurso, java.time.LocalDate> colTarifaFin;
    @FXML private TextField tarifaPrecioField;
    @FXML private javafx.scene.control.DatePicker tarifaInicioPicker;
    @FXML private javafx.scene.control.DatePicker tarifaFinPicker;

    // Reportes
    @FXML private Label lblEnCurso;
    @FXML private Label lblFinalizados;
    
    @FXML private ListView<String> listaMantenimiento;
    
    @FXML private javafx.scene.control.DatePicker desdePicker;
    @FXML private javafx.scene.control.DatePicker hastaPicker;
    @FXML private Label lblMasAlquilado;
    @FXML private javafx.scene.chart.BarChart<String, Number> barMontos;
    @FXML private javafx.scene.chart.LineChart<String, Number> lineTendencias;
    @FXML private javafx.scene.control.ListView<String> listaClientesFrecuentes;
    @FXML private javafx.scene.control.ProgressIndicator exportProgress;
    @FXML private javafx.scene.control.Label exportStatus;

    // Dashboard nuevos elementos
    @FXML private javafx.scene.control.Label metricStudents;
    @FXML private javafx.scene.control.Label metricTeachers;
    @FXML private javafx.scene.control.Label metricParents;
    @FXML private javafx.scene.control.Label metricEarnings;
    @FXML private javafx.scene.chart.BarChart<String, Number> examResultsBar;
    @FXML private javafx.scene.chart.PieChart genderPie;
    @FXML private javafx.scene.control.ListView<String> dashboardNotifications;
    @FXML private javafx.scene.layout.VBox dashboardPane;
    @FXML private javafx.scene.layout.StackPane contenidoStack;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final RolDAO rolDAO = new RolDAO();
    private final PromocionDAO promocionDAO = new PromocionDAO();
    private final TarifaRecursoDAO tarifaDAO = new TarifaRecursoDAO();
    private final ObservableList<Usuario> usuarios = FXCollections.observableArrayList();
    private final ObservableList<Promocion> promociones = FXCollections.observableArrayList();
    private final ObservableList<TarifaRecurso> tarifas = FXCollections.observableArrayList();
    private final ObservableList<com.playa.alquiler.model.Recurso> recursos = FXCollections.observableArrayList();
    @FXML private TableView<com.playa.alquiler.model.Recurso> recursosTable;
    @FXML private TableColumn<com.playa.alquiler.model.Recurso, Integer> colRecId;
    @FXML private TableColumn<com.playa.alquiler.model.Recurso, String> colRecNombre;
    @FXML private TableColumn<com.playa.alquiler.model.Recurso, String> colRecTipo;
    @FXML private TableColumn<com.playa.alquiler.model.Recurso, String> colRecEstado;
    @FXML private TextField recursoNombreField;
    @FXML private TextField recursoDescripcionField;
    @FXML private TextField recursoTipoField;
    @FXML private ComboBox<String> recursoEstadoCombo;
    @FXML private TableView<TarifaRecurso> tarifasRecursoTable;
    @FXML private TextField tarifaPrecioField2;
    @FXML private javafx.scene.control.DatePicker tarifaInicioPicker2;
    @FXML private javafx.scene.control.DatePicker tarifaFinPicker2;

    @FXML
    public void initialize() {
        // Mostrar usuario actual en la barra superior
        if (CurrentSession.getUsuario() != null) {
            currentUserLabel.setText("Usuario: " + CurrentSession.getUsuario().getNombreUsuario() + " (" + CurrentSession.getNombreRol() + ")");
        }

        colId.setCellValueFactory(new PropertyValueFactory<>("usuarioId"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        // Para mostrar nombre de rol, usaremos una cellFactory simple basada en rol_id
        colRol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    Usuario u = getTableView().getItems().get(getIndex());
                    try {
                        String nombreRol = rolDAO.obtenerNombreRolPorId(u.getRolId());
                        setText(nombreRol);
                    } catch (Exception e) {
                        setText("(error rol)");
                    }
                }
            }
        });
        usuariosTable.setItems(usuarios);

        // Cargar roles desde BD
        try {
            rolCombo.setItems(FXCollections.observableArrayList(rolDAO.listarNombresRoles()));
        } catch (SQLException e) {
            estadoAdminLabel.setText("Error cargando roles: " + e.getMessage());
        }

        // Configurar tabla promociones
        if (promosTable != null) {
            colPromoId.setCellValueFactory(new PropertyValueFactory<>("idPromocion"));
            colPromoNombre.setCellValueFactory(new PropertyValueFactory<>("nombrePromocion"));
            colPromoTipo.setCellValueFactory(new PropertyValueFactory<>("tipoDescuento"));
            colPromoValor.setCellValueFactory(new PropertyValueFactory<>("valorDescuento"));
            colPromoEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
            promosTable.setItems(promociones);
            recargarPromociones();
            promoTipoCombo.setItems(FXCollections.observableArrayList("Porcentaje", "Monto Fijo"));
            promoEstadoCombo.setItems(FXCollections.observableArrayList("Activa", "Inactiva"));
        }

        // Configurar tabla tarifas
        if (recursosTable != null) {
            colRecId.setCellValueFactory(new PropertyValueFactory<>("idRecurso"));
            colRecNombre.setCellValueFactory(new PropertyValueFactory<>("nombreRecurso"));
            colRecTipo.setCellValueFactory(new PropertyValueFactory<>("tipoDeRecurso"));
            colRecEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
            recursosTable.setItems(recursos);
            recursoEstadoCombo.setItems(FXCollections.observableArrayList("Disponible","En Mantenimiento","Alquilado"));
            recargarRecursos();

            // Configurar tabla de tarifas integradas
                if (tarifasRecursoTable != null) {
                    colTarifaId.setCellValueFactory(new PropertyValueFactory<>("idTarifa"));
                    colTarifaPrecio.setCellValueFactory(new PropertyValueFactory<>("precioPorHora"));
                    tarifasRecursoTable.setItems(tarifas);
                    if (colTarifaInicio != null) colTarifaInicio.setVisible(false);
                    if (colTarifaFin != null) colTarifaFin.setVisible(false);
                }

            recursosTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
                if (newSel != null) {
                    recargarTarifasRecurso(newSel.getIdRecurso());
                    // precargar formulario con estado actual del recurso
                    recursoNombreField.setText(newSel.getNombreRecurso());
                    recursoDescripcionField.setText(newSel.getDescripcion());
                    recursoTipoField.setText(newSel.getTipoDeRecurso());
                    recursoEstadoCombo.getSelectionModel().select(newSel.getEstado());
                }
            });
        }

        recargarUsuarios();
        switchTo(dashboardPane);

        // Métricas del dashboard
        try {
            int enCurso = new AlquilerDAO().contarPorEstado("En Curso");
            if (metricStudents != null) metricStudents.setText(String.valueOf(enCurso));
        } catch (SQLException ignored) {}
        try {
            int finalizados = new AlquilerDAO().contarPorEstado("Finalizado");
            if (metricTeachers != null) metricTeachers.setText(String.valueOf(finalizados));
        } catch (SQLException ignored) {}
        try {
            int disponibles = new com.playa.alquiler.dao.RecursoDAO().buscarRecursosDisponibles().size();
            if (metricParents != null) metricParents.setText(String.valueOf(disponibles));
        } catch (SQLException ignored) {}
        try {
            java.time.LocalDate h = java.time.LocalDate.now();
            java.time.LocalDate d = h.minusDays(30);
            java.math.BigDecimal total = java.math.BigDecimal.ZERO;
            try (java.sql.Connection conn = com.playa.alquiler.db.ConexionDB.getConnection(); java.sql.PreparedStatement ps = conn.prepareStatement("SELECT COALESCE(SUM(total_a_pagar),0) FROM detalle_alquiler d JOIN Alquileres a ON a.alquiler_id=d.alquiler_id WHERE a.fecha BETWEEN ? AND ?")) {
                ps.setDate(1, java.sql.Date.valueOf(d));
                ps.setDate(2, java.sql.Date.valueOf(h));
                try (java.sql.ResultSet rs = ps.executeQuery()) { if (rs.next()) total = rs.getBigDecimal(1); }
            }
            if (metricEarnings != null) metricEarnings.setText("S/" + total);
        } catch (Exception ignored) {}

        // Gráfico de tendencias simple en barra
        try {
            if (examResultsBar != null) {
                examResultsBar.getData().clear();
                java.time.LocalDate h = java.time.LocalDate.now();
                java.time.LocalDate d = h.minusMonths(6);
                java.util.Map<String, Integer> datos = new java.util.LinkedHashMap<>();
                try (java.sql.Connection conn = com.playa.alquiler.db.ConexionDB.getConnection(); java.sql.PreparedStatement ps = conn.prepareStatement("SELECT FORMAT(a.fecha,'yyyy-MM') AS mes, COUNT(*) AS c FROM Alquileres a WHERE a.fecha BETWEEN ? AND ? GROUP BY FORMAT(a.fecha,'yyyy-MM') ORDER BY mes")) {
                    ps.setDate(1, java.sql.Date.valueOf(d));
                    ps.setDate(2, java.sql.Date.valueOf(h));
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) datos.put(rs.getString(1), rs.getInt(2));
                    }
                }
                javafx.scene.chart.XYChart.Series<String, Number> s = new javafx.scene.chart.XYChart.Series<>();
                for (var e : datos.entrySet()) s.getData().add(new javafx.scene.chart.XYChart.Data<>(e.getKey(), e.getValue()));
                examResultsBar.getData().add(s);
                examResultsBar.setLegendVisible(false);
            }
        } catch (Exception ignored) {}

        // Pie de estado de recursos
        if (genderPie != null) {
            try {
                var rdao = new com.playa.alquiler.dao.RecursoDAO();
                int disp = rdao.buscarRecursosDisponibles().size();
                int mant;
                try (java.sql.Connection conn = com.playa.alquiler.db.ConexionDB.getConnection(); java.sql.PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM Recursos WHERE estado='En Mantenimiento'")) {
                    try (java.sql.ResultSet rs = ps.executeQuery()) { rs.next(); mant = rs.getInt(1); }
                }
                int alquilados;
                try (java.sql.Connection conn = com.playa.alquiler.db.ConexionDB.getConnection(); java.sql.PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM Recursos WHERE estado='Alquilado'")) {
                    try (java.sql.ResultSet rs = ps.executeQuery()) { rs.next(); alquilados = rs.getInt(1); }
                }
                genderPie.getData().setAll(
                        new javafx.scene.chart.PieChart.Data("Disponible", disp),
                        new javafx.scene.chart.PieChart.Data("Mantenimiento", mant),
                        new javafx.scene.chart.PieChart.Data("Alquilado", alquilados)
                );
                genderPie.setLegendVisible(false);
                genderPie.setLabelsVisible(true);
            } catch (Exception ignored) {}
        }

        // Notificaciones de ejemplo
        if (dashboardNotifications != null) {
            dashboardNotifications.setItems(FXCollections.observableArrayList(
                    "Nuevo recurso agregado",
                    "Tarifa actualizada",
                    "Promoción creada",
                    "Reporte mensual disponible"
            ));
        }
    }

    

    private void switchTo(javafx.scene.Node target) {
        if (contenidoStack == null || target == null) return;
        for (javafx.scene.Node child : contenidoStack.getChildren()) { child.setVisible(false); child.setManaged(false); }
        target.setVisible(true); target.setManaged(true);
    }
    @FXML public void mostrarDashboard() { switchTo(dashboardPane); }

    private void recargarUsuarios() {
        try {
            usuarios.setAll(usuarioDAO.listar());
            estadoAdminLabel.setText("Usuarios cargados: " + usuarios.size());
        } catch (SQLException e) {
            estadoAdminLabel.setText("Error cargando usuarios: " + e.getMessage());
        }
    }

    private void recargarPromociones() {
        try {
            promociones.setAll(promocionDAO.listar());
        } catch (SQLException e) {
            estadoAdminLabel.setText("Error cargando promociones: " + e.getMessage());
        }
    }

    @FXML
    public void mostrarGestionUsuarios(ActionEvent e) {
        switchTo(usuariosPane);
        recargarUsuarios();
    }

    @FXML
    public void mostrarGestionPromociones(ActionEvent e) {
        switchTo(promocionesPane);
        recargarPromociones();
    }

    @FXML
    public void mostrarGestionTarifas(ActionEvent e) {
        mostrarGestionRecursos(e);
    }

    @FXML
    public void mostrarReportes(ActionEvent e) {
        switchTo(reportesPane);
        try {
            AlquilerDAO adao = new AlquilerDAO();
            int enCurso = adao.contarPorEstado("En Curso");
            int fin = adao.contarPorEstado("Finalizado");
            lblEnCurso.setText("En curso: " + enCurso);
            lblFinalizados.setText("Finalizados: " + fin);
            if (desdePicker != null && hastaPicker != null) {
                if (desdePicker.getValue() == null) desdePicker.setValue(java.time.LocalDate.now().minusDays(30));
                if (hastaPicker.getValue() == null) hastaPicker.setValue(java.time.LocalDate.now());
            }
            onAplicarPeriodo(null);
        } catch (SQLException ex) {
            estadoAdminLabel.setText("Error cargando reportes: " + ex.getMessage());
        }
    }

    @FXML
    public void mostrarGestionRecursos(ActionEvent e) {
        switchTo(recursosPane);
        recargarRecursos();
    }

    private void recargarRecursos() {
        try {
            recursos.setAll(new com.playa.alquiler.dao.RecursoDAO().listarTodos());
        } catch (SQLException e) {
            estadoAdminLabel.setText("Error cargando recursos: " + e.getMessage());
        }
    }

    private void recargarTarifasRecurso(int recursoId) {
        try {
            tarifas.setAll(new TarifaRecursoDAO().listarPorRecurso(recursoId));
            estadoAdminLabel.setText("Tarifas cargadas para recurso " + recursoId);
        } catch (SQLException e) {
            estadoAdminLabel.setText("Error cargando tarifas: " + e.getMessage());
        }
    }

    @FXML
    public void onCrearRecurso(ActionEvent e) {
        try {
            var r = new com.playa.alquiler.model.Recurso();
            r.setNombreRecurso(recursoNombreField.getText());
            r.setDescripcion(recursoDescripcionField.getText());
            r.setTipoDeRecurso(recursoTipoField.getText());
            r.setEstado(recursoEstadoCombo.getValue());
            new com.playa.alquiler.dao.RecursoDAO().crear(r);
            estadoAdminLabel.setText("Recurso creado: " + r.getNombreRecurso());
            recargarRecursos();
        } catch (SQLException ex) {
            estadoAdminLabel.setText("Error creando recurso: " + ex.getMessage());
        }
    }

    @FXML
    public void onActualizarRecurso(ActionEvent e) {
        com.playa.alquiler.model.Recurso sel = recursosTable.getSelectionModel().getSelectedItem();
        if (sel == null) { estadoAdminLabel.setText("Seleccione un recurso"); return; }
        try {
            sel.setNombreRecurso(recursoNombreField.getText());
            sel.setDescripcion(recursoDescripcionField.getText());
            sel.setTipoDeRecurso(recursoTipoField.getText());
            sel.setEstado(recursoEstadoCombo.getValue());
            new com.playa.alquiler.dao.RecursoDAO().actualizar(sel);
            estadoAdminLabel.setText("Recurso actualizado: " + sel.getNombreRecurso());
            recargarRecursos();
        } catch (SQLException ex) {
            estadoAdminLabel.setText("Error actualizando recurso: " + ex.getMessage());
        }
    }

    @FXML
    public void onEliminarRecurso(ActionEvent e) {
        com.playa.alquiler.model.Recurso sel = recursosTable.getSelectionModel().getSelectedItem();
        if (sel == null) { estadoAdminLabel.setText("Seleccione un recurso"); return; }
        try {
            new com.playa.alquiler.dao.RecursoDAO().eliminar(sel.getIdRecurso());
            estadoAdminLabel.setText("Recurso eliminado: " + sel.getNombreRecurso());
            recargarRecursos();
        } catch (SQLException ex) {
            estadoAdminLabel.setText("Error eliminando recurso: " + ex.getMessage());
        }
    }

    @FXML
    public void onMarcarMantenimiento(ActionEvent e) {
        com.playa.alquiler.model.Recurso sel = recursosTable.getSelectionModel().getSelectedItem();
        if (sel == null) { estadoAdminLabel.setText("Seleccione un recurso"); return; }
        try (java.sql.Connection conn = com.playa.alquiler.db.ConexionDB.getConnection()) {
            new com.playa.alquiler.dao.RecursoDAO().actualizarEstado(conn, sel.getIdRecurso(), "En Mantenimiento");
            estadoAdminLabel.setText("Marcado en mantenimiento");
            recargarRecursos();
        } catch (SQLException ex) {
            estadoAdminLabel.setText("Error marcando mantenimiento: " + ex.getMessage());
        }
    }

    @FXML
    public void onMarcarDisponible(ActionEvent e) {
        com.playa.alquiler.model.Recurso sel = recursosTable.getSelectionModel().getSelectedItem();
        if (sel == null) { estadoAdminLabel.setText("Seleccione un recurso"); return; }
        try (java.sql.Connection conn = com.playa.alquiler.db.ConexionDB.getConnection()) {
            new com.playa.alquiler.dao.RecursoDAO().actualizarEstado(conn, sel.getIdRecurso(), "Disponible");
            estadoAdminLabel.setText("Marcado disponible");
            recargarRecursos();
        } catch (SQLException ex) {
            estadoAdminLabel.setText("Error marcando disponible: " + ex.getMessage());
        }
    }

    @FXML
    public void onAgregarTarifaRecurso(ActionEvent e) {
        if (com.playa.alquiler.service.CurrentSession.getNombreRol() == null ||
                !"Administrador".equalsIgnoreCase(com.playa.alquiler.service.CurrentSession.getNombreRol())) {
            estadoAdminLabel.setText("No autorizado para modificar tarifas");
            return;
        }
        com.playa.alquiler.model.Recurso sel = recursosTable.getSelectionModel().getSelectedItem();
        if (sel == null) { estadoAdminLabel.setText("Seleccione un recurso"); return; }
        try {
            java.math.BigDecimal precio = new java.math.BigDecimal(tarifaPrecioField2.getText());
            TarifaRecurso t = new TarifaRecurso();
            t.setIdRecurso(sel.getIdRecurso());
            t.setPrecioPorHora(precio);
            t.setFechaInicio(java.time.LocalDate.now());
            t.setFechaFin(null);
            new TarifaRecursoDAO().crear(t);
            estadoAdminLabel.setText("Tarifa actualizada para recurso " + sel.getIdRecurso());
            recargarTarifasRecurso(sel.getIdRecurso());
        } catch (NumberFormatException nfe) {
            estadoAdminLabel.setText("Precio inválido");
        } catch (SQLException ex) {
            estadoAdminLabel.setText("Error creando tarifa: " + ex.getMessage());
        }
    }

    

    @FXML
    public void onRecursosMantenimiento(ActionEvent e) {
        try {
            var ids = new com.playa.alquiler.service.ReporteService().recursosEnMantenimiento();
            ObservableList<String> items = FXCollections.observableArrayList();
            for (Integer id : ids) items.add("Recurso " + id);
            listaMantenimiento.setItems(items);
            estadoAdminLabel.setText("Recursos en mantenimiento: " + ids.size());
        } catch (SQLException ex) {
            estadoAdminLabel.setText("Error mantenimiento: " + ex.getMessage());
        }
    }

    

    @FXML
    public void onAplicarPeriodo(ActionEvent e) {
        try {
            java.time.LocalDate d = desdePicker.getValue();
            java.time.LocalDate h = hastaPicker.getValue();
            try (java.sql.Connection conn = com.playa.alquiler.db.ConexionDB.getConnection()) {
                java.sql.PreparedStatement psT = conn.prepareStatement("SELECT COUNT(*) FROM detalle_alquiler d JOIN Alquileres a ON a.alquiler_id=d.alquiler_id WHERE a.fecha BETWEEN ? AND ?");
                psT.setDate(1, java.sql.Date.valueOf(d));
                psT.setDate(2, java.sql.Date.valueOf(h));
                long totalGeneral = 0;
                try (java.sql.ResultSet rs = psT.executeQuery()) { if (rs.next()) totalGeneral = rs.getLong(1); }
                java.sql.PreparedStatement ps = conn.prepareStatement("SELECT TOP 1 d.recurso_id, COUNT(*) AS total FROM detalle_alquiler d JOIN Alquileres a ON a.alquiler_id=d.alquiler_id WHERE a.fecha BETWEEN ? AND ? GROUP BY d.recurso_id ORDER BY total DESC, d.recurso_id");
                ps.setDate(1, java.sql.Date.valueOf(d));
                ps.setDate(2, java.sql.Date.valueOf(h));
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int rid = rs.getInt(1);
                        long cant = rs.getLong(2);
                        String nombre = new com.playa.alquiler.dao.RecursoDAO().obtenerPorId(rid).getNombreRecurso();
                        double pct = totalGeneral == 0 ? 0.0 : (cant * 100.0) / totalGeneral;
                        lblMasAlquilado.setText("Recurso: " + nombre + " • Alquileres: " + cant + " • Utilización: " + String.format("%.1f", pct) + "%");
                    } else {
                        lblMasAlquilado.setText("Sin datos en período");
                    }
                }
            }
            cargarTendencias(d, h);
            cargarClientesFrecuentes(d, h);
        } catch (Exception ex) {
            estadoAdminLabel.setText("Error aplicando período: " + ex.getMessage());
        }
    }

    private void cargarTendencias(java.time.LocalDate d, java.time.LocalDate h) throws SQLException {
        java.util.Map<String, Integer> datos = new java.util.LinkedHashMap<>();
        try (java.sql.Connection conn = com.playa.alquiler.db.ConexionDB.getConnection(); java.sql.PreparedStatement ps = conn.prepareStatement("SELECT FORMAT(a.fecha,'yyyy-MM') AS mes, COUNT(*) AS c FROM Alquileres a WHERE a.fecha BETWEEN ? AND ? GROUP BY FORMAT(a.fecha,'yyyy-MM') ORDER BY mes")) {
            ps.setDate(1, java.sql.Date.valueOf(d));
            ps.setDate(2, java.sql.Date.valueOf(h));
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) datos.put(rs.getString(1), rs.getInt(2));
            }
        }
        if (lineTendencias != null) {
            lineTendencias.getData().clear();
            javafx.scene.chart.XYChart.Series<String, Number> s = new javafx.scene.chart.XYChart.Series<>();
            for (var entry : datos.entrySet()) s.getData().add(new javafx.scene.chart.XYChart.Data<>(entry.getKey(), entry.getValue()));
            lineTendencias.getData().add(s);
        }
    }

    private void cargarClientesFrecuentes(java.time.LocalDate d, java.time.LocalDate h) throws SQLException {
        javafx.collections.ObservableList<String> items = FXCollections.observableArrayList();
        try (java.sql.Connection conn = com.playa.alquiler.db.ConexionDB.getConnection(); java.sql.PreparedStatement ps = conn.prepareStatement("SELECT TOP 5 t.nombres + ' ' + t.apellidos AS nombre, COUNT(*) AS c FROM Alquileres a JOIN Turista t ON t.id_turista=a.id_turista WHERE a.fecha BETWEEN ? AND ? GROUP BY t.nombres, t.apellidos ORDER BY c DESC")) {
            ps.setDate(1, java.sql.Date.valueOf(d));
            ps.setDate(2, java.sql.Date.valueOf(h));
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) items.add(rs.getString(1) + " (" + rs.getInt(2) + ")");
            }
        }
        listaClientesFrecuentes.setItems(items);
    }

    @FXML
    public void onMontosGenerados(ActionEvent e) {
        try {
            java.time.LocalDate d = desdePicker.getValue();
            java.time.LocalDate h = hastaPicker.getValue();
            java.util.Map<String, java.math.BigDecimal> porTipo = new java.util.HashMap<>();
            java.math.BigDecimal total = java.math.BigDecimal.ZERO;
            try (java.sql.Connection conn = com.playa.alquiler.db.ConexionDB.getConnection(); java.sql.PreparedStatement ps = conn.prepareStatement("SELECT d.recurso_id, r.tipo_de_recurso, d.cantidad_horas FROM detalle_alquiler d JOIN Alquileres a ON a.alquiler_id=d.alquiler_id JOIN Recursos r ON r.id_recurso=d.recurso_id WHERE a.fecha BETWEEN ? AND ?")) {
                ps.setDate(1, java.sql.Date.valueOf(d));
                ps.setDate(2, java.sql.Date.valueOf(h));
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    com.playa.alquiler.dao.TarifaRecursoDAO trDAO = new com.playa.alquiler.dao.TarifaRecursoDAO();
                    while (rs.next()) {
                        int rid = rs.getInt(1);
                        String tipo = rs.getString(2);
                        java.math.BigDecimal horas = rs.getBigDecimal(3);
                        var t = trDAO.obtenerUltimaTarifa(rid);
                        if (t == null) continue;
                        java.math.BigDecimal monto = t.getPrecioPorHora().multiply(horas);
                        porTipo.merge(tipo == null ? "Sin Tipo" : tipo, monto, java.math.BigDecimal::add);
                        total = total.add(monto);
                    }
                }
            }
            if (barMontos != null) {
                barMontos.getData().clear();
                java.util.List<java.util.Map.Entry<String, java.math.BigDecimal>> entries = new java.util.ArrayList<>(porTipo.entrySet());
                entries.sort((a,b) -> b.getValue().compareTo(a.getValue()));
                javafx.scene.chart.XYChart.Series<String, Number> serie = new javafx.scene.chart.XYChart.Series<>();
                for (var entry : entries) serie.getData().add(new javafx.scene.chart.XYChart.Data<>(entry.getKey(), entry.getValue()));
                var xAxis = (javafx.scene.chart.CategoryAxis) barMontos.getXAxis();
                xAxis.setCategories(javafx.collections.FXCollections.observableArrayList(entries.stream().map(java.util.Map.Entry::getKey).collect(java.util.stream.Collectors.toList())));
                barMontos.setLegendVisible(false);
                barMontos.setCategoryGap(20);
                barMontos.setBarGap(6);
                var catAxis = (javafx.scene.chart.CategoryAxis) barMontos.getXAxis();
                catAxis.setTickLabelRotation(-20);
                catAxis.setTickLabelGap(8);
                var numAxis = (javafx.scene.chart.NumberAxis) barMontos.getYAxis();
                numAxis.setForceZeroInRange(true);
                barMontos.getData().add(serie);
            }
            estadoAdminLabel.setText("Monto total: S/ " + total);
        } catch (SQLException ex) {
            estadoAdminLabel.setText("Error montos: " + ex.getMessage());
        }
    }

    @FXML
    public void onExportarMontosCSV(ActionEvent e) {
        try {
            java.time.LocalDate d = desdePicker.getValue();
            java.time.LocalDate h = hastaPicker.getValue();
            java.util.Map<String, java.math.BigDecimal> porTipo = new java.util.HashMap<>();
            java.math.BigDecimal total = java.math.BigDecimal.ZERO;
            try (java.sql.Connection conn = com.playa.alquiler.db.ConexionDB.getConnection(); java.sql.PreparedStatement ps = conn.prepareStatement("SELECT d.recurso_id, r.tipo_de_recurso, d.cantidad_horas FROM detalle_alquiler d JOIN Alquileres a ON a.alquiler_id=d.alquiler_id JOIN Recursos r ON r.id_recurso=d.recurso_id WHERE a.fecha BETWEEN ? AND ?")) {
                ps.setDate(1, java.sql.Date.valueOf(d));
                ps.setDate(2, java.sql.Date.valueOf(h));
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    com.playa.alquiler.dao.TarifaRecursoDAO trDAO = new com.playa.alquiler.dao.TarifaRecursoDAO();
                    while (rs.next()) {
                        int rid = rs.getInt(1);
                        String tipo = rs.getString(2);
                        java.math.BigDecimal horas = rs.getBigDecimal(3);
                        var t = trDAO.obtenerUltimaTarifa(rid);
                        if (t == null) continue;
                        java.math.BigDecimal monto = t.getPrecioPorHora().multiply(horas);
                        porTipo.merge(tipo == null ? "Sin Tipo" : tipo, monto, java.math.BigDecimal::add);
                        total = total.add(monto);
                    }
                }
            }
            java.nio.file.Path dir = java.nio.file.Paths.get("reports");
            if (!java.nio.file.Files.exists(dir)) java.nio.file.Files.createDirectories(dir);
            java.nio.file.Path file = dir.resolve("montos_" + d + "_" + h + ".csv");
            StringBuilder sb = new StringBuilder();
            sb.append("tipo,monto\n");
            for (var entry : porTipo.entrySet()) sb.append(entry.getKey()).append(',').append(entry.getValue()).append('\n');
            sb.append("TOTAL,").append(total).append('\n');
            java.nio.file.Files.writeString(file, sb.toString());
            estadoAdminLabel.setText("Exportado: " + file.toString());
        } catch (Exception ex) {
            estadoAdminLabel.setText("Error exportando: " + ex.getMessage());
        }
    }

    @FXML
    public void onCrearUsuario(ActionEvent e) {
        String nombre = nuevoUsuarioField.getText();
        String email = nuevoEmailField.getText();
        String contrasena = nuevoContrasenaField.getText();
        String rolNombre = rolCombo.getValue();
        if (nombre == null || nombre.isBlank() || email == null || email.isBlank() || contrasena == null || contrasena.isBlank() || rolNombre == null) {
            estadoAdminLabel.setText("Complete todos los campos.");
            return;
        }
        try {
            Integer rolId = rolDAO.obtenerRolIdPorNombre(rolNombre);
            if (rolId == null) {
                estadoAdminLabel.setText("Rol no encontrado en DB.");
                return;
            }
            Usuario u = new Usuario();
            u.setNombreUsuario(nombre);
            u.setEmail(email);
            u.setContrasena(contrasena);
            u.setRolId(rolId);
            usuarioDAO.crear(u);
            estadoAdminLabel.setText("Usuario creado: " + nombre);
            nuevoUsuarioField.clear();
            nuevoEmailField.clear();
            nuevoContrasenaField.clear();
            rolCombo.getSelectionModel().clearSelection();
            recargarUsuarios();
        } catch (SQLException ex) {
            estadoAdminLabel.setText("Error creando usuario: " + ex.getMessage());
        }
    }

    @FXML
    public void onEliminarSeleccionado(ActionEvent e) {
        Usuario seleccionado = usuariosTable.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            estadoAdminLabel.setText("Seleccione un usuario.");
            return;
        }
        try {
            usuarioDAO.eliminar(seleccionado.getUsuarioId());
            estadoAdminLabel.setText("Usuario eliminado: " + seleccionado.getNombreUsuario());
            recargarUsuarios();
        } catch (SQLException ex) {
            estadoAdminLabel.setText("Error eliminando usuario: " + ex.getMessage());
        }
    }

    @FXML
    public void onCrearPromocion(ActionEvent e) {
        String nombre = promoNombreField.getText();
        String tipo = promoTipoCombo.getValue();
        String valorTxt = promoValorField.getText();
        String estado = promoEstadoCombo.getValue();
        if (nombre == null || nombre.isBlank() || tipo == null || valorTxt == null || valorTxt.isBlank() || estado == null) {
            estadoAdminLabel.setText("Complete los campos de promoción.");
            return;
        }
        try {
            double valor = Double.parseDouble(valorTxt);
            Promocion p = new Promocion();
            p.setNombrePromocion(nombre);
            p.setTipoDescuento(tipo);
            p.setValorDescuento(valor);
            p.setEstado(estado);
            p.setUsuarioId(CurrentSession.getUsuario() != null ? CurrentSession.getUsuario().getUsuarioId() : null);
            promocionDAO.crear(p);
            estadoAdminLabel.setText("Promoción creada: " + nombre);
            promoNombreField.clear();
            promoValorField.clear();
            promoTipoCombo.getSelectionModel().clearSelection();
            promoEstadoCombo.getSelectionModel().clearSelection();
            recargarPromociones();
        } catch (NumberFormatException nfe) {
            estadoAdminLabel.setText("Valor inválido.");
        } catch (SQLException ex) {
            estadoAdminLabel.setText("Error creando promoción: " + ex.getMessage());
        }
    }

    @FXML
    public void onEliminarPromocion(ActionEvent e) {
        Promocion sel = promosTable.getSelectionModel().getSelectedItem();
        if (sel == null) { estadoAdminLabel.setText("Seleccione una promoción."); return; }
        try {
            promocionDAO.eliminar(sel.getIdPromocion());
            estadoAdminLabel.setText("Promoción eliminada: " + sel.getNombrePromocion());
            recargarPromociones();
        } catch (SQLException ex) {
            estadoAdminLabel.setText("Error eliminando promoción: " + ex.getMessage());
        }
    }

    @FXML
    public void onListarTarifasPorRecurso(ActionEvent e) {
        try {
            int recursoId = Integer.parseInt(tarifaRecursoIdField.getText());
            tarifas.setAll(tarifaDAO.listarPorRecurso(recursoId));
            estadoAdminLabel.setText("Tarifas cargadas para recurso " + recursoId);
        } catch (NumberFormatException nfe) {
            estadoAdminLabel.setText("ID de recurso inválido.");
        } catch (SQLException ex) {
            estadoAdminLabel.setText("Error listando tarifas: " + ex.getMessage());
        }
    }

    @FXML
    public void onCrearTarifa(ActionEvent e) {
        try {
            int recursoId = Integer.parseInt(tarifaRecursoIdField.getText());
            java.math.BigDecimal precio = new java.math.BigDecimal(tarifaPrecioField.getText());
            java.time.LocalDate inicio = tarifaInicioPicker.getValue();
            java.time.LocalDate fin = tarifaFinPicker.getValue();
            TarifaRecurso t = new TarifaRecurso();
            t.setIdRecurso(recursoId);
            t.setPrecioPorHora(precio);
            t.setFechaInicio(inicio);
            t.setFechaFin(fin);
            tarifaDAO.crear(t);
            estadoAdminLabel.setText("Tarifa creada para recurso " + recursoId);
            onListarTarifasPorRecurso(null);
        } catch (NumberFormatException nfe) {
            estadoAdminLabel.setText("Precio o recurso inválido.");
        } catch (SQLException ex) {
            estadoAdminLabel.setText("Error creando tarifa: " + ex.getMessage());
        }
    }

    @FXML
    public void onLogout(ActionEvent e) {
        CurrentSession.clear();
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/com/playa/alquiler/view/LoginView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
            ((Stage) currentUserLabel.getScene().getWindow()).close();
        } catch (Exception ex) {
            estadoAdminLabel.setText("Error cerrando sesión: " + ex.getMessage());
        }
    }
    @FXML
    public void onExportarPDF(ActionEvent e) {
        try {
            if (exportProgress != null) { exportProgress.setVisible(true); exportProgress.setManaged(true); }
            if (exportStatus != null) { exportStatus.setText("Exportando PDF..."); }
            javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
            fc.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("PDF", "*.pdf"));
            fc.setInitialFileName("reporte_" + java.time.LocalDate.now() + ".pdf");
            java.io.File file = fc.showSaveDialog(currentUserLabel.getScene().getWindow());
            if (file == null) { if (exportProgress != null) { exportProgress.setVisible(false); exportProgress.setManaged(false); } return; }
            org.apache.pdfbox.pdmodel.PDDocument doc = new org.apache.pdfbox.pdmodel.PDDocument();
            try {
                org.apache.pdfbox.pdmodel.PDPage page = new org.apache.pdfbox.pdmodel.PDPage(org.apache.pdfbox.pdmodel.common.PDRectangle.LETTER);
                doc.addPage(page);
                org.apache.pdfbox.pdmodel.PDPageContentStream cs = new org.apache.pdfbox.pdmodel.PDPageContentStream(doc, page);
                float margin = 36f;
                float y = page.getMediaBox().getHeight() - margin;
                cs.beginText();
                cs.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 16);
                cs.newLineAtOffset(margin, y - 20);
                cs.showText("Reportes operativos y de gestión");
                cs.endText();
                y -= 40;
                cs.beginText();
                cs.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 11);
                cs.newLineAtOffset(margin, y);
                String periodo = "Período: " + (desdePicker != null ? String.valueOf(desdePicker.getValue()) : "-") + " a " + (hastaPicker != null ? String.valueOf(hastaPicker.getValue()) : "-");
                cs.showText(periodo);
                cs.endText();
                y -= 24;
                cs.beginText();
                cs.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 12);
                cs.newLineAtOffset(margin, y);
                cs.showText("Montos por tipo");
                cs.endText();
                y -= 18;
                if (barMontos != null && !barMontos.getData().isEmpty()) {
                    javafx.scene.chart.XYChart.Series<String, Number> s = barMontos.getData().get(0);
                    for (var d : s.getData()) {
                        cs.beginText();
                        cs.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 11);
                        cs.newLineAtOffset(margin, y);
                        cs.showText(d.getXValue() + ": " + d.getYValue());
                        cs.endText();
                        y -= 16;
                    }
                }
                y -= 10;
                cs.beginText();
                cs.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 12);
                cs.newLineAtOffset(margin, y);
                cs.showText("Clientes frecuentes");
                cs.endText();
                y -= 18;
                if (listaClientesFrecuentes != null && listaClientesFrecuentes.getItems() != null) {
                    for (String item : listaClientesFrecuentes.getItems()) {
                        cs.beginText();
                        cs.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 11);
                        cs.newLineAtOffset(margin, y);
                        cs.showText(item);
                        cs.endText();
                        y -= 16;
                    }
                }
                if (y < 160) {
                    cs.close();
                    page = new org.apache.pdfbox.pdmodel.PDPage(org.apache.pdfbox.pdmodel.common.PDRectangle.LETTER);
                    doc.addPage(page);
                    cs = new org.apache.pdfbox.pdmodel.PDPageContentStream(doc, page);
                    y = page.getMediaBox().getHeight() - margin;
                }
                if (barMontos != null) {
                    javafx.scene.SnapshotParameters sp = new javafx.scene.SnapshotParameters();
                    javafx.scene.image.WritableImage wi = new javafx.scene.image.WritableImage(800, 360);
                    javafx.scene.image.WritableImage snap = barMontos.snapshot(sp, wi);
                    java.awt.image.BufferedImage bi = javafx.embed.swing.SwingFXUtils.fromFXImage(snap, null);
                    org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject img = org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory.createFromImage(doc, bi);
                    float imgW = 520f;
                    float ratio = img.getWidth() / img.getHeight();
                    float imgH = imgW / ratio;
                    cs.drawImage(img, margin, y - imgH - 20, imgW, imgH);
                    y -= imgH + 40;
                }
                cs.close();
                doc.save(file);
                if (exportStatus != null) { exportStatus.setText("PDF exportado: " + file.getAbsolutePath()); }
            } finally {
                doc.close();
                if (exportProgress != null) { exportProgress.setVisible(false); exportProgress.setManaged(false); }
            }
        } catch (Exception ex) {
            if (exportProgress != null) { exportProgress.setVisible(false); exportProgress.setManaged(false); }
            if (exportStatus != null) { exportStatus.setText("Error exportando PDF: " + ex.getMessage()); }
            estadoAdminLabel.setText("Error exportando PDF: " + ex.getMessage());
        }
    }

    @FXML
    public void onExportarExcel(ActionEvent e) {
        try {
            if (exportProgress != null) { exportProgress.setVisible(true); exportProgress.setManaged(true); }
            if (exportStatus != null) { exportStatus.setText("Exportando Excel..."); }
            javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
            fc.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx"));
            fc.setInitialFileName("reporte_" + java.time.LocalDate.now() + ".xlsx");
            java.io.File file = fc.showSaveDialog(currentUserLabel.getScene().getWindow());
            if (file == null) { if (exportProgress != null) { exportProgress.setVisible(false); exportProgress.setManaged(false); } return; }
            org.apache.poi.xssf.usermodel.XSSFWorkbook wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            try {
                org.apache.poi.ss.usermodel.CellStyle header = wb.createCellStyle();
                org.apache.poi.ss.usermodel.Font hf = wb.createFont();
                hf.setBold(true);
                header.setFont(hf);
                org.apache.poi.ss.usermodel.CellStyle money = wb.createCellStyle();
                org.apache.poi.ss.usermodel.DataFormat df = wb.createDataFormat();
                money.setDataFormat(df.getFormat("#,##0.00"));
                var shResumen = wb.createSheet("Resumen");
                var r0 = shResumen.createRow(0);
                r0.createCell(0).setCellValue("Reportes operativos y de gestión");
                r0.getCell(0).setCellStyle(header);
                var r1 = shResumen.createRow(1);
                r1.createCell(0).setCellValue("Generado: " + java.time.LocalDateTime.now());
                var r2 = shResumen.createRow(2);
                r2.createCell(0).setCellValue("Período: " + (desdePicker != null ? String.valueOf(desdePicker.getValue()) : "-") + " a " + (hastaPicker != null ? String.valueOf(hastaPicker.getValue()) : "-"));
                var shMontos = wb.createSheet("MontosPorTipo");
                var hr = shMontos.createRow(0);
                hr.createCell(0).setCellValue("Tipo");
                hr.createCell(1).setCellValue("Monto");
                hr.getCell(0).setCellStyle(header);
                hr.getCell(1).setCellStyle(header);
                int i = 1;
                if (barMontos != null && !barMontos.getData().isEmpty()) {
                    var s = barMontos.getData().get(0);
                    for (var d : s.getData()) {
                        var row = shMontos.createRow(i++);
                        row.createCell(0).setCellValue(d.getXValue());
                        var c = row.createCell(1);
                        c.setCellValue(d.getYValue().doubleValue());
                        c.setCellStyle(money);
                    }
                }
                shMontos.autoSizeColumn(0); shMontos.autoSizeColumn(1);
                var shClientes = wb.createSheet("ClientesFrecuentes");
                var hc = shClientes.createRow(0);
                hc.createCell(0).setCellValue("Cliente (veces)");
                hc.getCell(0).setCellStyle(header);
                int j = 1;
                if (listaClientesFrecuentes != null && listaClientesFrecuentes.getItems() != null) {
                    for (String item : listaClientesFrecuentes.getItems()) {
                        var row = shClientes.createRow(j++);
                        row.createCell(0).setCellValue(item);
                    }
                }
                shClientes.autoSizeColumn(0);
                var shMant = wb.createSheet("Mantenimiento");
                var hm = shMant.createRow(0);
                hm.createCell(0).setCellValue("Recursos");
                hm.getCell(0).setCellStyle(header);
                int k = 1;
                if (listaMantenimiento != null && listaMantenimiento.getItems() != null) {
                    for (String item : listaMantenimiento.getItems()) {
                        var row = shMant.createRow(k++);
                        row.createCell(0).setCellValue(item);
                    }
                }
                shMant.autoSizeColumn(0);
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) { wb.write(fos); }
                if (exportStatus != null) { exportStatus.setText("Excel exportado: " + file.getAbsolutePath()); }
            } finally {
                wb.close();
                if (exportProgress != null) { exportProgress.setVisible(false); exportProgress.setManaged(false); }
            }
        } catch (Exception ex) {
            if (exportProgress != null) { exportProgress.setVisible(false); exportProgress.setManaged(false); }
            if (exportStatus != null) { exportStatus.setText("Error exportando Excel: " + ex.getMessage()); }
            estadoAdminLabel.setText("Error exportando Excel: " + ex.getMessage());
        }
    }
}
