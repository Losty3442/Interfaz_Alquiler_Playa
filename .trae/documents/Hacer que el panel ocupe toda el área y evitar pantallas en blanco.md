## Causa probable
- El `StackPane` central expande, pero sus hijos (en especial `dashboardPane` y las `VBox` dentro del `GridPane`) no se están estirando al ancho/alto de la vista.
- En los paneles de gestión, al conmutar, el contenedor no tiene reglas de crecimiento y se percibe como "en blanco".

## Cambios FXML (solo estructura y crecimiento)
1. En `AdminView.fxml`, en el `StackPane` central, establecer `alignment="TOP_LEFT"` para que el panel activo se ancle arriba-izquierda.
2. En `dashboardPane` (`VBox`): `fillWidth="true"` y `maxWidth="Infinity"`.
3. En el `GridPane` del dashboard:
   - En cada `VBox` hijo (barras, donut, tabla, notificaciones) añadir `GridPane.hgrow="ALWAYS"` y `GridPane.vgrow="ALWAYS"` (solo los de la fila superior necesitan `vgrow`).
4. En cada panel funcional (`usuariosPane`, `promocionesPane`, `recursosPane`, `reportesPane`):
   - Encapsular contenido en un `ScrollPane` interno con `fitToWidth="true"` para evitar recortes y permitir ocupación completa.
   - Asignar `visible="false" managed="false"` por defecto.

## Cambios en controlador
5. `switchTo(Node target)` (ya existe):
   - Tras mostrar el `target`, si el central es `ScrollPane`, hacer `setVvalue(0)` para llevar al inicio.
   - No cambia la lógica actual.

## Estilos
6. Mantener `design-system.css`; sin cambios de paleta.

## Verificación
7. Compilar `mvn -DskipTests package` y ejecutar `mvn javafx:run`.
8. Validar:
   - Dashboard ocupa todo el centro (sin recortes laterales).
   - Usuarios/Promociones/Recursos/Reportes se muestran al pulsar, sin quedar en blanco.

¿Aplico estos cambios estructurales para que el panel se vea completo y la navegación no deje áreas en blanco?