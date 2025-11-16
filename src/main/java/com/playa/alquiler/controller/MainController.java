package com.playa.alquiler.controller;

import com.playa.alquiler.db.ConexionDB;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainController {

    @FXML
    public void onTestConnection(ActionEvent event) {
        boolean ok = ConexionDB.testConnection();
        Alert alert = new Alert(ok ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle("Prueba de Conexión");
        alert.setHeaderText(null);
        alert.setContentText(ok ? "Conexión exitosa a SQL Server" : "No se pudo conectar. Verifique db.properties y SQL Server.");
        alert.showAndWait();
    }

    @FXML
    public void onOpenAlquiler(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/playa/alquiler/view/AlquilerView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Crear Alquiler");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo abrir la vista de Alquiler");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}