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

## Próximos pasos sugeridos
- Completar DAOs restantes (`AlquilerDAO`, `DetalleAlquilerDAO`, `PromocionDAO`, `TarifaRecursoDAO`).
- Implementar `AlquilerService.crearAlquiler(...)` con validaciones, tarifas, promociones y transacciones.
- Diseñar pantallas de gestión (crear/modificar/cancelar alquileres, CRUD de usuarios/recursos/promociones/tarifas, reportes).