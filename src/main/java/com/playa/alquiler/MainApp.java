package com.playa.alquiler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/playa/alquiler/view/LoginView.fxml"));
        primaryStage.setTitle("Login - Alquiler de Equipos de Playa");
        Scene scene = new Scene(root, 800, 500);
        // Hoja de estilos global como respaldo
        try {
            String css = getClass().getResource("/com/playa/alquiler/style/app.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception ignored) {}
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}