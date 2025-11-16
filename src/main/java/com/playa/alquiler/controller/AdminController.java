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
    @FXML private TextField topNField;
    @FXML private ListView<String> listaTopRecursos;
    @FXML private ListView<String> listaMantenimiento;
    @FXML private TextField recursoIdTarifaField;
    @FXML private javafx.scene.control.DatePicker fechaTarifaPicker;
    @FXML private Label lblTarifaVigente;

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
        if (tarifasTable != null) {
            colTarifaId.setCellValueFactory(new PropertyValueFactory<>("idTarifa"));
            colTarifaPrecio.setCellValueFactory(new PropertyValueFactory<>("precioPorHora"));
            colTarifaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
            colTarifaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
            tarifasTable.setItems(tarifas);
        }

        if (recursosTable != null) {
            colRecId.setCellValueFactory(new PropertyValueFactory<>("idRecurso"));
            colRecNombre.setCellValueFactory(new PropertyValueFactory<>("nombreRecurso"));
            colRecTipo.setCellValueFactory(new PropertyValueFactory<>("tipoDeRecurso"));
            colRecEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
            recursosTable.setItems(recursos);
            recursoEstadoCombo.setItems(FXCollections.observableArrayList("Disponible","En Mantenimiento","Alquilado"));
            recargarRecursos();
        }

        recargarUsuarios();
    }

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
        usuariosPane.setVisible(true); usuariosPane.setManaged(true);
        promocionesPane.setVisible(false); promocionesPane.setManaged(false);
        tarifasPane.setVisible(false); tarifasPane.setManaged(false);
        reportesPane.setVisible(false); reportesPane.setManaged(false);
        recargarUsuarios();
    }

    @FXML
    public void mostrarGestionPromociones(ActionEvent e) {
        usuariosPane.setVisible(false); usuariosPane.setManaged(false);
        promocionesPane.setVisible(true); promocionesPane.setManaged(true);
        tarifasPane.setVisible(false); tarifasPane.setManaged(false);
        reportesPane.setVisible(false); reportesPane.setManaged(false);
        recargarPromociones();
    }

    @FXML
    public void mostrarGestionTarifas(ActionEvent e) {
        usuariosPane.setVisible(false); usuariosPane.setManaged(false);
        promocionesPane.setVisible(false); promocionesPane.setManaged(false);
        tarifasPane.setVisible(true); tarifasPane.setManaged(true);
        recursosPane.setVisible(false); recursosPane.setManaged(false);
        reportesPane.setVisible(false); reportesPane.setManaged(false);
    }

    @FXML
    public void mostrarReportes(ActionEvent e) {
        usuariosPane.setVisible(false); usuariosPane.setManaged(false);
        promocionesPane.setVisible(false); promocionesPane.setManaged(false);
        tarifasPane.setVisible(false); tarifasPane.setManaged(false);
        recursosPane.setVisible(false); recursosPane.setManaged(false);
        reportesPane.setVisible(true); reportesPane.setManaged(true);
        try {
            AlquilerDAO adao = new AlquilerDAO();
            int enCurso = adao.contarPorEstado("En Curso");
            int fin = adao.contarPorEstado("Finalizado");
            lblEnCurso.setText("En curso: " + enCurso);
            lblFinalizados.setText("Finalizados: " + fin);
        } catch (SQLException ex) {
            estadoAdminLabel.setText("Error cargando reportes: " + ex.getMessage());
        }
    }

    @FXML
    public void mostrarGestionRecursos(ActionEvent e) {
        usuariosPane.setVisible(false); usuariosPane.setManaged(false);
        promocionesPane.setVisible(false); promocionesPane.setManaged(false);
        tarifasPane.setVisible(false); tarifasPane.setManaged(false);
        reportesPane.setVisible(false); reportesPane.setManaged(false);
        recursosPane.setVisible(true); recursosPane.setManaged(true);
        recargarRecursos();
    }

    private void recargarRecursos() {
        try {
            recursos.setAll(new com.playa.alquiler.dao.RecursoDAO().listarTodos());
        } catch (SQLException e) {
            estadoAdminLabel.setText("Error cargando recursos: " + e.getMessage());
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
    public void onTopRecursos(ActionEvent e) {
        try {
            int topN = Integer.parseInt(topNField.getText());
            var lista = new com.playa.alquiler.service.ReporteService().usoRecursosTopN(topN);
            ObservableList<String> items = FXCollections.observableArrayList();
            for (var r : lista) items.add("Recurso " + r.getRecursoId() + " = " + r.getVeces() + " veces");
            listaTopRecursos.setItems(items);
            estadoAdminLabel.setText("Top recursos cargado");
        } catch (NumberFormatException nfe) {
            estadoAdminLabel.setText("Top N inválido");
        } catch (SQLException ex) {
            estadoAdminLabel.setText("Error top recursos: " + ex.getMessage());
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
    public void onTarifaVigente(ActionEvent e) {
        try {
            int recursoId = Integer.parseInt(recursoIdTarifaField.getText());
            java.time.LocalDate fecha = fechaTarifaPicker.getValue();
            var t = new com.playa.alquiler.service.ReporteService().tarifaVigente(recursoId, fecha != null ? fecha : java.time.LocalDate.now());
            if (t == null) {
                lblTarifaVigente.setText("Sin tarifa vigente");
            } else {
                lblTarifaVigente.setText("Precio: S/ " + t.getPrecioPorHora());
            }
        } catch (NumberFormatException nfe) {
            estadoAdminLabel.setText("ID de recurso inválido");
        } catch (SQLException ex) {
            estadoAdminLabel.setText("Error tarifa vigente: " + ex.getMessage());
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
}