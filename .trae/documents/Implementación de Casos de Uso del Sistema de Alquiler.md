## Alcance y Casos de Uso
- Gestionar Alquiler: registrar, aplicar tarifas/promociones, consultar, cancelar, finalizar.
- Controlar Pagos: calcular total, marcar como "Pagado"/"Finalizado", auditar, anular/reembolsar.
- Mantenimiento de Datos: CRUD de `Usuario`, `Recursos`, `Promociones`, `Tarifa_recurso`.
- Generar Reportes: operativos y financieros.
- Reportes Operativos: uso de recursos, actividad de turistas, rendimiento de vendedores.
- Reporte de Inventario: recursos más alquilados, horas usadas, en mantenimiento.
- Reporte de Tarifas: tarifas vigentes.
- Mi Caja (Vendedor): ventas diarias por vendedor y fecha actual.

## Arquitectura y Ubicación en Proyecto
- Proyecto JavaFX con Maven; entrada `MainApp` (`src/main/java/com/playa/alquiler/MainApp.java`).
- Persistencia JDBC a SQL Server mediante `ConexionDB.getConnection()` (`src/main/java/com/playa/alquiler/db/ConexionDB.java:35`).
- Capas existentes:
  - Controladores JavaFX (`controller/*`), Servicios (`service/*`), DAOs (`dao/*`), Modelos (`model/*`).
- Principio: cada caso de uso se implementa como servicio independiente, expuesto a controladores, usando exclusivamente DAOs sobre `AlquilerPlayaDB` (`src/main/resources/db.properties:1`).

## Diseño por Módulo (Servicios/DAOs/Vistas)
- Gestionar Alquiler
  - Servicio: extender `AlquilerService` (`src/main/java/com/playa/alquiler/service/AlquilerService.java:39,107`) con: consultar listados (activos/pasados/reservados), cancelar (`estado_alquiler='Cancelado'`).
  - DAOs: usar `AlquilerDAO`, `DetalleAlquilerDAO`, `RecursoDAO`, `TarifaRecursoDAO`, `PromocionDAO` ya existentes.
  - Controlador/Vista: `AlquilerController.fxml` y `AlquilerController` para registrar/cancelar/consultar.
- Controlar Pagos
  - Servicio: `PagoService` (nuevo) para:
    - Calcular `total_a_pagar` de un alquiler (suma de `detalle_alquiler.total_a_pagar`).
    - Registrar pago: `estado_alquiler='Pagado'` o `Finalizado` en `Alquileres`.
    - Auditoría: listar alquileres con `estado_alquiler IN ('Finalizado','Pagado')` por rango de fechas.
    - Reembolso: transición de estado y reversión con bitácora.
  - DAO: utilizar `AlquilerDAO.actualizarEstado` (`src/main/java/com/playa/alquiler/dao/AlquilerDAO.java:24`) y consultas agregadas.
  - Controladores: acciones en `VendorView.fxml` y panel de admin.
- Mantenimiento de Datos (Admin)
  - Servicios: `UsuarioService`, `RecursoService`, `PromocionService`, `TarifaService` (CRUD).
  - DAOs existentes: `UsuarioDAO`, `RolDAO`, `RecursoDAO`, `PromocionDAO`, `TarifaRecursoDAO`.
  - Controladores/Vistas: `AdminController` con pestañas para usuarios, recursos, promociones, tarifas.
- Reportes
  - Servicio: `ReporteService` con métodos:
    - Uso de recursos: TOP N por cantidad de horas y veces alquilado (JOIN `detalle_alquiler` + `Recursos`).
    - Actividad de turistas: alquileres y horas por turista (`Alquileres` + `detalle_alquiler`).
    - Rendimiento de vendedores: monto total, conteo de alquileres por `usuario_id` y fecha.
    - Inventario: recursos en mantenimiento (`Recursos.estado='En Mantenimiento'`).
    - Tarifas vigentes: consulta `tarifa_recurso` por recurso/fecha.
    - Financieros: ingresos totales y descuentos aplicados (considerando `Promociones`).
  - Controladores/Vistas: tablas y gráficos en `AdminView.fxml` y `VendorView.fxml`.

## Persistencia: Exclusividad DB
- Todas las operaciones usan `ConexionDB` y DAOs JDBC sobre `AlquilerPlayaDB` (`db.properties`).
- No se introduce ORM ni bases alternas (sin H2, sin SQLite). Sólo SQL Server con `mssql-jdbc`.

## Validación, Errores y Logging
- Validación
  - Controladores: validar IDs numéricos, horas > 0, estados permitidos; mensajes a usuario.
  - Servicios: validar disponibilidad (`RecursoDAO.obtenerPorId`), tarifas vigentes (`TarifaRecursoDAO.obtenerTarifaVigente`), promoción activa (`PromocionDAO.obtenerActivaPorId`).
- Manejo de errores
  - Transacciones: `conn.setAutoCommit(false)` y rollback ante `SQLException` (`AlquilerService.java:51,97`).
  - Propagar excepciones con mensajes claros; controlar en UI con diálogos.
- Logging
  - Introducir `java.util.logging.Logger` en `service/*` y `dao/*`; archivo `logging.properties` en `resources` (rotación y nivel).
  - Reemplazar `printStackTrace()` por logs estructurados (referencia `LoginController.java:82`).

## Pruebas (Unitarias e Integración)
- Añadir JUnit 5 (jupiter) en Maven; estructura `src/test/java`.
- Unitarias
  - Prueban reglas de negocio (cálculo de total y aplicación de promociones en `AlquilerService.aplicarPromocion`).
- Integración (DB real)
  - Ejecutan contra `AlquilerPlayaDB` usando datos semillas del script.
  - Estrategia: preparar datos en `@BeforeEach`, limpiar en `@AfterEach` con transacciones.
  - Casos: crear alquiler completo, finalizar alquiler, calcular pagos, reportes por vendedor/fecha.

## Documentación de API (Servicios)
- Formato: Markdown en `docs/` describiendo métodos de servicios como "API" interna.
- Incluye firma, parámetros, validaciones, ejemplos de solicitud/respuesta:
  - Ejemplo (pseudo-solicitud):
    - Solicitud: `crearAlquiler(alquiler, detalles)`
    - Respuesta: `{ alquilerId: 123, estado: "En Curso" }`
  - Ejemplo (cálculo pago):
    - Solicitud: `calcularTotal(alquilerId)`
    - Respuesta: `{ total: 45.00, moneda: "PEN" }`

## Verificación de Requisitos
- Funcionales: cubrir todos los actores y operaciones definidas en los documentos `DB`/`CASOS DE USO`.
- No funcionales:
  - Confiabilidad: transacciones y validaciones exhaustivas.
  - Rendimiento: consultas con índices (uso de claves primarias); `SELECT TOP` donde aplique.
  - Seguridad: evitar logs de credenciales; considerar hash de contraseñas en futuro.

## Entregables y Secuencia
1. Servicios/DAOs para pagos y reportes.
2. Controladores y vistas actualizadas para Vendedor/Admin.
3. Validación y logging en capas existentes.
4. Suite de pruebas (unitarias + integración) contra `AlquilerPlayaDB`.
5. Documentación de API de servicios con ejemplos.

## Referencias de Código
- Conexión: `src/main/java/com/playa/alquiler/db/ConexionDB.java:35`.
- DB config: `src/main/resources/db.properties:1`.
- Alquiler Service: `src/main/java/com/playa/alquiler/service/AlquilerService.java:39,107`.
- DAOs:
  - `src/main/java/com/playa/alquiler/dao/AlquilerDAO.java:8,24`
  - `src/main/java/com/playa/alquiler/dao/DetalleAlquilerDAO.java:8,24`
  - `src/main/java/com/playa/alquiler/dao/RecursoDAO.java:27,100`
  - `src/main/java/com/playa/alquiler/dao/TarifaRecursoDAO.java:10,36`
  - `src/main/java/com/playa/alquiler/dao/PromocionDAO.java:10,39`
- Controladores: `src/main/java/com/playa/alquiler/controller/AlquilerController.java:81,122`, `src/main/java/com/playa/alquiler/controller/LoginController.java:48`.

¿Confirmas este plan para proceder con la implementación y pruebas?