package com.playa.alquiler.controller;

import com.playa.alquiler.service.AuthService;
import com.playa.alquiler.service.CurrentSession;
import com.playa.alquiler.db.ConexionDB;
import java.net.URL;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField usuarioField;
    @FXML private PasswordField contrasenaField;
    @FXML private TextField contrasenaVisibleField;
    @FXML private Label estadoLabel;
    @FXML private ImageView logoImageView;

    @FXML
    public void initialize() {
        try {
            URL url = getClass().getResource("/com/playa/alquiler/style/logo.png");
            if (url != null) {
                logoImageView.setImage(new Image(url.toExternalForm()));
            } else {
                // Ocultar si no hay logo disponible
                if (logoImageView != null) {
                    logoImageView.setVisible(false);
                    logoImageView.setManaged(false);
                }
            }
        } catch (Exception ignored) {
            if (logoImageView != null) {
                logoImageView.setVisible(false);
                logoImageView.setManaged(false);
            }
        }
    }

    @FXML
    protected void onLogin(ActionEvent e) {
        String usuario = usuarioField.getText();
        String contrasena = getPassword();
        if (usuario == null || usuario.isBlank() || contrasena == null || contrasena.isBlank()) {
            estadoLabel.setText("Ingrese usuario y contraseña.");
            return;
        }
        try {
            AuthService.ResultadoLogin res = new AuthService().login(usuario, contrasena);
            if (res == null) {
                estadoLabel.setText("Credenciales inválidas.");
                return;
            }
            // Guardar sesión actual para otros paneles
            CurrentSession.set(res.usuario, res.nombreRol);
            estadoLabel.setText("Bienvenido " + res.usuario.getNombreUsuario() + " (" + res.nombreRol + ")");
            if ("Administrador".equalsIgnoreCase(res.nombreRol)) {
                abrirVentana("/com/playa/alquiler/view/AdminView.fxml", "Panel de Administrador");
            } else if ("Vendedor".equalsIgnoreCase(res.nombreRol)) {
                abrirVentana("/com/playa/alquiler/view/VendorView.fxml", "Panel de Vendedor");
            } else {
                estadoLabel.setText("Rol no soportado: " + res.nombreRol);
            }
            // Cerrar ventana de login
            ((Stage) estadoLabel.getScene().getWindow()).close();
        } catch (Exception ex) {
            // Mostrar causa real si viene envuelta en LoadException u otras
            Throwable cause = ex;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            String msg = cause.getMessage();
            if (msg == null || msg.isBlank()) msg = ex.toString();
            estadoLabel.setText("Error en login: " + msg);
            ex.printStackTrace();
        }
    }

    private String getPassword() {
        if (contrasenaField.isVisible()) {
            return contrasenaField.getText();
        } else {
            return contrasenaVisibleField.getText();
        }
    }

    @FXML
    protected void onTogglePassword(ActionEvent e) {
        // Alterna entre PasswordField y TextField, sincronizando el texto
        if (contrasenaField.isVisible()) {
            contrasenaVisibleField.setText(contrasenaField.getText());
            contrasenaVisibleField.setVisible(true);
            contrasenaVisibleField.setManaged(true);
            contrasenaField.setVisible(false);
            contrasenaField.setManaged(false);
            // Cambia el icono del botón a ojo tachado
            Object src = e.getSource();
            if (src instanceof javafx.scene.control.Button btn) {
                btn.setText("🙈");
            }
        } else {
            contrasenaField.setText(contrasenaVisibleField.getText());
            contrasenaField.setVisible(true);
            contrasenaField.setManaged(true);
            contrasenaVisibleField.setVisible(false);
            contrasenaVisibleField.setManaged(false);
            Object src = e.getSource();
            if (src instanceof javafx.scene.control.Button btn) {
                btn.setText("👁");
            }
        }
    }

    private void abrirVentana(String fxml, String titulo) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        Stage stage = new Stage();
        stage.setTitle(titulo);
        Scene scene = new Scene(root);
        com.playa.alquiler.style.ThemeManager.applyLight(scene);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void onTestConnection(ActionEvent e) {
        try {
            java.sql.Connection c = com.playa.alquiler.db.ConexionDB.getConnection();
            if (c != null) {
                c.close();
                estadoLabel.setText("Conexión OK");
            } else {
                estadoLabel.setText("Conexión fallida");
            }
        } catch (Exception ex) {
            Throwable cause = ex;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            String msg = cause.getMessage();
            if (msg == null || msg.isBlank()) msg = ex.toString();
            estadoLabel.setText("Conexión fallida: " + msg);
        }
    }
}
