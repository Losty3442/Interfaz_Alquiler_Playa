package com.playa.alquiler.style;

import javafx.scene.Scene;

public class ThemeManager {
    public static void applyLight(Scene scene) {
        scene.getStylesheets().setAll(
                ThemeManager.class.getResource("/com/playa/alquiler/style/design-system.css").toExternalForm(),
                ThemeManager.class.getResource("/com/playa/alquiler/style/theme-light.css").toExternalForm()
        );
    }
}
