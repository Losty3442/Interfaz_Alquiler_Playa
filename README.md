# Alquiler de Equipos de Playa (JavaFX + SQL Server)

Aplicación de escritorio en Java (JavaFX) organizada en 3 capas (DAO, Servicios, UI), conectada a SQL Server.

## Requisitos
- JDK 17+
- Maven 3.8+
- SQL Server en ejecución
- Driver JDBC incluido vía Maven (`mssql-jdbc`)

## Configuración de BD
1. Ejecuta el script DDL/DML proporcionado para crear la base `AlquilerPlayaDB` y sus tablas.
2. Ajusta `src/main/resources/db.properties` con tus credenciales:
   - `url=jdbc:sqlserver://<host>:1433;databaseName=AlquilerPlayaDB;encrypt=false`
   - `user=<usuario>`
   - `password=<password>`

## Ejecutar la app
```bash
mvn javafx:run
```

Al iniciar, usa el botón "Probar conexión a SQL Server" para validar el acceso.

## Créditos de recursos
El logo del login utiliza el emoji "Beach with Umbrella" de OpenMoji – el proyecto de emojis open‑source. Licencia: CC BY‑SA 4.0. https://openmoji.org/

Imagen hero del login: foto "Parasol and the sea" por Kristina Kutleša en Unsplash. https://unsplash.com/photos/v2v8kjE41GI

## Estructura
- `db/ConexionDB.java`: Gestión de conexión JDBC leyendo `db.properties`.
- `model/*`: POJOs que representan tablas (`Usuario`, `Turista`, `Recurso`, `Alquiler`, `DetalleAlquiler`, `Promocion`, `TarifaRecurso`).
- `dao/*`: DAOs con CRUD de ejemplo para `Usuario`, `Turista`, `Recurso`.
- `service/AlquilerService.java`: Esqueleto para la lógica transaccional de alquiler.
- `view/MainView.fxml`: Vista básica para probar conexión.

## Rediseño UI (2025-11)
Se implementó un dashboard estilo "School Management" en `AdminView.fxml`, con:
- Sidebar persistente y topbar blanco con buscador.
- Tarjetas métricas de color (Estudiantes, Docentes, Padres, Ingresos).
- Gráficos: barras y pie tipo donut.
- Paneles de gestión existentes accesibles desde el sidebar.

### Modo oscuro eliminado
- Se removió por completo el modo oscuro.
- Archivos afectados: `ThemeManager.java` (simplificado solo a `applyLight`), `theme-dark.css` (eliminado), referencias a toggle en `AdminView.fxml` y `VendorView.fxml`.

### Accesibilidad (WCAG 2.1 AA)
- `accessibleText` en botones del sidebar.
- Focus visible reforzado en controles (`design-system.css`).
- Contrastes de color ajustados en `theme-light.css`.

### Responsividad
- Layouts con `ScrollPane`, `GridPane` y percent widths.
- Componentes principales con `vgrow/hgrow` para adaptarse a tamaño de ventana.

### Consistencia
- JavaFX garantiza consistencia en Windows/macOS/Linux con la paleta definida.

### Mantenimiento y guía de diseño
- Paleta y tokens: `design-system.css` y `theme-light.css`.
- Tarjetas métricas: clases `metric-students|teachers|parents|earnings`.
- Para modificar colores de gráficos: revisar `.default-colorN.chart-*` en `theme-light.css`.
- Para añadir métricas nuevas: declarar `Label` en FXML y enlazar en `AdminController.initialize()`.

### Control de versiones
- Cambios agrupados en commits por vista/estilo/controlador.
- Sugerencia: usar convenciones Conventional Commits para seguimiento (`feat(ui)`, `refactor(style)`, `fix(accessibility)`).
