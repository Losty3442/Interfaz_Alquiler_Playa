## Problema
- Al pulsar casos de uso (Usuarios, Promociones, Recursos, Reportes) el centro queda en blanco.
- Causa probable: el `dashboardPane` está fuera del `StackPane` que controla visibilidad, y el alternar entre `dashboardPane` y `contenidoStack` no siempre rellena el área central.
- También puede haber colisiones de inyección y tamaños/crecimientos que dejan el `StackPane` sin contenido visible.

## Solución Técnica
### Unificar Contenido en Un Solo `StackPane`
1. En `src/main/resources/com/playa/alquiler/view/AdminView.fxml`:
   - Mover `dashboardPane` dentro de `contenidoStack` como primer hijo.
   - Dejar `usuariosPane`, `promocionesPane`, `recursosPane`, `reportesPane` como hijos del mismo `StackPane`.
   - Eliminar el `VBox` externo para el dashboard dentro del `ScrollPane`, y en su lugar crear `ScrollPane` por panel si se necesita scroll.
   - Asegurar `StackPane` con `VBox.vgrow="ALWAYS"` y cada panel con `prefHeight` razonable y `VBox.vgrow="ALWAYS"` para que ocupe espacio.

### Conmutador de Vistas en el Controlador
2. En `src/main/java/com/playa/alquiler/controller/AdminController.java`:
   - Declarar `@FXML private StackPane contenidoStack;` y `@FXML` para `dashboardPane`.
   - Añadir método `private void switchTo(javafx.scene.Node target)` que: 
     - Recorre los hijos de `contenidoStack` y pone `visible=false` / `managed=false`.
     - Al `target`: `visible=true` / `managed=true`.
   - En `initialize()`: `switchTo(dashboardPane)`.
   - Actualizar handlers:
     - `mostrarDashboard()` → `switchTo(dashboardPane)`.
     - `mostrarGestionUsuarios()` → `switchTo(usuariosPane)` + `recargarUsuarios()`.
     - `mostrarGestionPromociones()` → `switchTo(promocionesPane)` + `recargarPromociones()`.
     - `mostrarGestionRecursos()` → `switchTo(recursosPane)` + `recargarRecursos()`.
     - `mostrarReportes()` → `switchTo(reportesPane)` + lógica actual de reportes.

### Datos y Tablas
3. Evitar IDs compartidos entre la tabla del dashboard y la de gestión.
   - Mantener `usuariosDashTable` sin `fx:id` en columnas para no colisionar con `colId/colNombre/...`.
   - Confirmar `PropertyValueFactory` en `usuariosPane` (ya está) y que `recargarUsuarios()` se ejecuta tras `switchTo`.

### Estilo y Accesibilidad
4. Mantener el modo claro y el diseño actual.
   - Sin cambios aquí, salvo asegurar contraste y focus visible (ya aplicados).

### Verificación
5. Compilar y probar:
   - `mvn -DskipTests package`.
   - Ejecutar y navegar: Home/Admin (dashboard) → Usuarios/Promociones/Recursos/Reportes.
   - Confirmar que cada panel aparece y que el centro no se queda en blanco.

## Entregables
- FXML actualizado con `StackPane` único que contiene todas las vistas.
- Controlador con `switchTo(...)` y handlers ajustados.
- Validación de compilación exitosa.

¿Confirmas que aplique estos cambios?