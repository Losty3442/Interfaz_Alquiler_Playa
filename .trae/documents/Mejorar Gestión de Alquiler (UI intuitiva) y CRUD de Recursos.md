## Objetivo
- Hacer la gestión de alquiler en Vendedor 100% intuitiva sin IDs manuales: seleccionar turista por teléfono o crear nuevo, elegir promociones por nombre, calcular total visible.
- Añadir módulo completo de Gestión de Recursos (CRUD) en Admin.

## Cambios en Vendedor (Gestión de Alquiler)
### UI (AlquilerView.fxml)
- Sección "Turista":
  - Campo teléfono (`TextField`), botón "Buscar", muestra datos si existe.
  - Formulario para crear nuevo (nombres, apellidos, email, teléfono, nacionalidad) y botón "Registrar".
- Sección "Promoción":
  - `ComboBox` con promociones activas por nombre; opcional.
- Sección "Recursos":
  - `ComboBox` de recursos disponibles (ya existe), horas y botón "Agregar".
- Sección "Resumen":
  - Tabla de detalles con columnas recurso, horas, promoción aplicada.
  - Label con "Total a pagar" calculado con `PagoService.calcularTotal` o suma local.
- Botones: "Registrar Alquiler", "Finalizar", "Marcar Pagado".

### Controlador (AlquilerController)
- Métodos nuevos:
  - `onBuscarTuristaPorTelefono()`: llama `TuristaDAO.obtenerPorTelefono()`.
  - `onRegistrarTuristaNuevo()`: valida y usa `TuristaDAO.crear()`.
  - `onCargarPromocionesActivas()`: llena `ComboBox` con `PromocionDAO.listarActivas()`.
  - `onRecalcularTotal()`: suma de `DetalleAlquiler.total` tras cada agregado; o usa `PagoService.calcularTotal` post-inserción.
- Ajuste `onAddDetalle()`: usa promoción seleccionada en combo, no ID.
- Ajuste `onRegistrarAlquiler()`: obtiene `idTurista` del turista buscado o recién creado.

### DAO/Servicio
- `TuristaDAO`: agregar `obtenerPorTelefono(String telefono)`.
- `PromocionDAO`: agregar `listarActivas()`.
- `PagoService`: reutilizar `calcularTotal(alquilerId)` para resumen tras registrar.

## Gestión de Recursos (Admin)
### UI (AdminView.fxml)
- Añadir botón "Gestionar Recursos" y un panel nuevo:
  - Tabla de recursos (id, nombre, tipo, estado).
  - Formulario crear/editar (nombre, descripción, tipo, estado) con botones Crear/Actualizar/Eliminar.
  - Acciones rápidas: "Marcar En Mantenimiento", "Marcar Disponible".

### Controlador (AdminController)
- Métodos:
  - `mostrarGestionRecursos()` para cambiar de panel.
  - `onCrearRecurso()`, `onActualizarRecurso()`, `onEliminarRecurso()`.
  - `onMarcarEstado()` para cambiar estado.
- Usa `RecursoDAO` existente (`crear`, `actualizar`, `eliminar`, `listarTodos`, `actualizarEstado`).

## Validación, Errores y Logging
- Validar teléfono único al crear turista, email válido, horas > 0, selección de recurso disponible.
- Mensajes claros en UI; logs con `java.util.logging` ya configurado.

## Pruebas
- Integración:
  - `TuristaDAO.obtenerPorTelefono` y flujo crear/buscar.
  - CRUD de `RecursoDAO`.
  - Registrar alquiler con promoción por nombre y cálculo del total.

## Persistencia y DB
- Todo sobre la base `AlquilerPlayaDB` vía `ConexionDB` y DAOs.
- No se introducen bases ni ORMs adicionales.

## Entregables
1. Nuevos controles y UX en `AlquilerView.fxml` + controlador.
2. DAO extendidos para turista y promociones.
3. Panel CRUD de recursos en `AdminView.fxml` + controlador.
4. Pruebas integradas.

¿Autorizas que implemente estos cambios para que el Vendedor no maneje IDs y el Admin gestione recursos con un CRUD completo e intuitivo?