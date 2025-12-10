package com.playa.alquiler.controller;

import com.playa.alquiler.service.AlquilerService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.playa.alquiler.service.CurrentSession;

public class VendorController {
    @FXML private TextField alquilerIdField;
    @FXML private Label estadoVendorLabel;
    @FXML private Label currentUserLabel;
    @FXML private Label miCajaLabel;

    @FXML
    public void initialize() {
        if (CurrentSession.getUsuario() != null) {
            currentUserLabel.setText("Usuario: " + CurrentSession.getUsuario().getNombreUsuario() + " (" + CurrentSession.getNombreRol() + ")");
        }
    }

    @FXML
    public void abrirCrearAlquiler(ActionEvent e) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/playa/alquiler/view/AlquilerView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Crear / Finalizar Alquiler");
            Scene scene = new Scene(root, 1000, 720);
            com.playa.alquiler.style.ThemeManager.applyLight(scene);
            stage.setScene(scene);
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception ex) {
            estadoVendorLabel.setText("Error abriendo ventana: " + ex.getMessage());
        }
    }

    @FXML
    public void onFinalizarDirecto(ActionEvent e) {
        String idText = alquilerIdField.getText();
        try {
            int id = Integer.parseInt(idText);
            new AlquilerService().finalizarAlquiler(id);
            estadoVendorLabel.setText("Alquiler finalizado: " + id);
        } catch (NumberFormatException nfe) {
            estadoVendorLabel.setText("ID inválido.");
        } catch (Exception ex) {
            estadoVendorLabel.setText("Error finalizando: " + ex.getMessage());
        }
    }

    @FXML
    public void onMarcarPagado(ActionEvent e) {
        String idText = alquilerIdField.getText();
        try {
            int id = Integer.parseInt(idText);
            new com.playa.alquiler.service.PagoService().marcarPagado(id);
            estadoVendorLabel.setText("Alquiler marcado como Pagado: " + id);
        } catch (NumberFormatException nfe) {
            estadoVendorLabel.setText("ID inválido.");
        } catch (Exception ex) {
            estadoVendorLabel.setText("Error marcando pago: " + ex.getMessage());
        }
    }

    @FXML
    public void onMiCajaHoy(ActionEvent e) {
        try {
            if (CurrentSession.getUsuario() == null) {
                estadoVendorLabel.setText("Sesión no disponible.");
                return;
            }
            int usuarioId = CurrentSession.getUsuario().getUsuarioId();
            var resumen = new com.playa.alquiler.service.PagoService().ventasDiariasPorVendedor(usuarioId, java.time.LocalDate.now());
            if (miCajaLabel != null) {
                miCajaLabel.setText("Mi Caja Hoy: S/ " + resumen.getTotal() + " en " + resumen.getCantidadAlquileres() + " alquiler(es)");
            } else {
                estadoVendorLabel.setText("Mi Caja Hoy: S/ " + resumen.getTotal() + " en " + resumen.getCantidadAlquileres() + " alquiler(es)");
            }
        } catch (Exception ex) {
            estadoVendorLabel.setText("Error calculando caja: " + ex.getMessage());
        }
    }

    @FXML
    public void onLogout(ActionEvent e) {
        CurrentSession.clear();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/playa/alquiler/view/LoginView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();
            ((Stage) currentUserLabel.getScene().getWindow()).close();
        } catch (Exception ex) {
            estadoVendorLabel.setText("Error cerrando sesión: " + ex.getMessage());
        }
    }
}
