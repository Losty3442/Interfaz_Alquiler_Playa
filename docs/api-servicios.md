# API de Servicios (Java)

## PagoService
- `BigDecimal calcularTotal(int alquilerId)`
  - Solicitud:
    - `alquilerId`: ID del alquiler
  - Respuesta:
    - `total`: suma de `detalle_alquiler.total_a_pagar`
  - Ejemplo:
    - Solicitud: `calcularTotal(1)`
    - Respuesta: `45.00`
- `void marcarPagado(int alquilerId)`
  - Solicitud: `alquilerId`
  - Respuesta: sin contenido
- `ResumenVentas ventasDiariasPorVendedor(int usuarioId, LocalDate fecha)`
  - Solicitud: `usuarioId`, `fecha`
  - Respuesta: `{ total: BigDecimal, cantidadAlquileres: int }`

## ReporteService
- `List<RecursoUso> usoRecursosTopN(int topN)`
  - Solicitud: `topN`
  - Respuesta: lista de `{ recursoId: int, veces: long }`
- `BigDecimal horasUsadasPorRecurso(int recursoId)`
  - Solicitud: `recursoId`
  - Respuesta: `horas: BigDecimal`
- `List<Integer> recursosEnMantenimiento()`
  - Respuesta: lista de IDs
- `TarifaRecurso tarifaVigente(int recursoId, LocalDate fecha)`
  - Solicitud: `recursoId`, `fecha`
  - Respuesta: entidad `TarifaRecurso`

## Flujos
- Registrar y finalizar alquiler: `AlquilerService.crearAlquiler`, `AlquilerService.finalizarAlquiler`
- Pago: `PagoService.calcularTotal` → `PagoService.marcarPagado`
- Mi Caja (Vendedor): `PagoService.ventasDiariasPorVendedor(usuarioId, hoy)`