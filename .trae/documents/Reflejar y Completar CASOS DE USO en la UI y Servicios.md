## Gap detectado
- Faltan pantallas/acciones visibles para: Controlar Pagos (Mi Caja, marcar Pagado), Reportes Operativos/Inventario/Tarifas, Consultas/Cancelación de alquileres.
- La configuración de BD en runtime usa `password=user`, causando errores de login y bloqueando validación.

## Entregables por Caso de Uso
### Gestionar Alquiler
- UI: Añadir pestaña "Consultar/Cancelar" en `VendorView.fxml` y/o `AlquilerView.fxml` para listar alquileres por estado y cancelar.
- Servicio/DAO: Extender `AlquilerService`/`AlquilerDAO` con consultas y cancelación (`estado_alquiler='Cancelado'`).
- Validaciones: estados permitidos, recursos afectados.
- Referencias: `src/main/java/com/playa/alquiler/service/AlquilerService.java:39,107`, `src/main/java/com/playa/alquiler/dao/AlquilerDAO.java:24`.

### Controlar Pagos (Vendedor/Admin)
- UI Vendedor: Botones en `VendorView.fxml` para:
  - "Marcar Pagado" (usa `PagoService.marcarPagado`).
  - "Mi Caja (Hoy)" mostrando total y cantidad (usa `PagoService.ventasDiariasPorVendedor`).
- UI Admin: Pestaña "Pagos/Auditoría" en `AdminView.fxml` para listar alquileres `Finalizado/Pagado` por rango fechas y usuario.
- Servicio: `PagoService` existente para calcular total, marcar pagado y resumen diario.
- Referencias: `src/main/java/com/playa/alquiler/service/PagoService.java:13,23,29`, `src/main/java/com/playa/alquiler/controller/VendorController.java:52,63`.

### Generar Reportes (Operativos/Financieros)
- UI Admin: Panel "Reportes" con sub-secciones:
  - Uso de recursos (Top N), horas usadas por recurso, recursos en mantenimiento.
  - Tarifas vigentes por recurso/fecha.
- Servicio: `ReporteService` existente con métodos para consultas; exponer resultados en tablas/gráficos.
- Referencias: `src/main/java/com/playa/alquiler/service/ReporteService.java:14,22,32,40`, `src/main/java/com/playa/alquiler/controller/AdminController.java:184`.

### Mantenimiento de Datos (Admin)
- UI ya presente para usuarios, promociones, tarifas en `AdminView.fxml`; completar acciones de edición y validación.
- Referencias: `src/main/java/com/playa/alquiler/controller/AdminController.java:200,249,308`.

## Cambios específicos en la UI (FXML/Controllers)
- `VendorView.fxml`:
  - Añadir botones: `onMarcarPagado`, `onMiCajaHoy` y una tabla de consulta de alquileres con filtro por estado/fecha.
  - Mostrar `Label miCajaLabel` con el resumen.
- `AlquilerView.fxml`:
  - Sección "Consultar/Cancelar": tabla de alquileres, botón `Cancelar` → `AlquilerService`.
- `AdminView.fxml`:
  - Subtab "Pagos" con filtros de fecha/usuario y tabla de alquileres; `Auditar` muestra totales y discrepancias.
  - Subtab "Reportes": tablas para Top recursos, horas usadas, mantenimiento, tarifas vigentes.

## Persistencia y Configuración BD
- Actualizar `src/main/resources/db.properties` con credenciales reales; reconstruir para que `target/classes/db.properties` quede correcto.
- Si instancia nombrada: usar `instanceName=SQLEXPRESS` o puerto distinto al 1433.
- Referencias: `src/main/java/com/playa/alquiler/db/ConexionDB.java:18`, `src/main/resources/db.properties:1`.

## Validación, Errores y Logging
- Validaciones en UI (IDs numéricos, horas > 0) y Servicios (disponibilidad, tarifas, promo activa).
- Logging `java.util.logging` ya configurado (`src/main/resources/logging.properties`) y aplicado en servicios.
- Mejoras de mensajes en `LoginController` para mostrar causa real (hecho en `src/main/java/com/playa/alquiler/controller/LoginController.java:129`).

## Pruebas
- Unitarias: cálculos de promoción y totales.
- Integración: crear/finalizar alquiler, marcar pagado, Mi Caja, reportes.
- Ejecutan contra `AlquilerPlayaDB` y se saltan si no hay conexión.
- Referencias: `src/test/java/com/playa/alquiler/service/PagoServiceTest.java:1`, `src/test/java/com/playa/alquiler/service/ReporteServiceTest.java:1`.

## Mapeo a "CASOS DE USO" del documento
- Reportes Operativos/Inventario/Tarifas: `ReporteService` + UI en Admin.
- Mi Caja (Vendedor): `PagoService.ventasDiariasPorVendedor` + UI en Vendor.
- Controlar Pagos: `calcularTotal`, `marcar Pagado/Finalizado`, auditoría en Admin.
- Gestionar Alquiler: registrar/consultar/cancelar y finalizar; ya soportado y se completará la parte de consulta/cancelación.

## Secuencia de trabajo
1. Corregir `db.properties` con credenciales reales y reconstruir.
2. Actualizar `VendorView.fxml` y `AlquilerView.fxml` con secciones de pagos y consulta/cancelación.
3. Añadir subtab de auditoría y reportes en `AdminView.fxml` y enlazar a `ReporteService`.
4. Completar validaciones en controladores.
5. Ejecutar pruebas y ajustar.

¿Autorizas proceder con estas modificaciones para que los CASOS DE USO queden visibles y funcionales en la UI, además de arreglar la conexión a la BD?