## Alcance
Implementar exportación de los reportes (panel `reportesPane`) a PDF y Excel (.xlsx) con buen formato, metadatos y feedback al usuario.

## Cambios en UI
1. Agregar en `reportesPane` un `SplitMenuButton` o `MenuButton` con opciones:
   - "Exportar a PDF" → handler `onExportarPDF()`
   - "Exportar a Excel" → handler `onExportarExcel()`
2. Añadir un `ProgressIndicator` (visibilidad controlada) y deshabilitar botones mientras se exporta.
3. Usar `FileChooser` para que el usuario elija nombre y carpeta.

## Dependencias (Maven)
1. PDF: `org.apache.pdfbox:pdfbox` (Apache 2.0) para crear PDF, texto, tablas simples y embebido de imágenes.
2. Excel: `org.apache.poi:poi-ooxml` para generar archivos `.xlsx` con estilos.

## Recolección de datos del reporte
1. Fuente: los mismos datos que se muestran actualmente en `reportesPane`:
   - Período seleccionado (`desdePicker`, `hastaPicker`).
   - Recurso más alquilado (`lblMasAlquilado`).
   - Montos generados por tipo (serie de `barMontos`).
   - Clientes frecuentes (`listaClientesFrecuentes`).
   - Recursos en mantenimiento (`listaMantenimiento`).
2. Captura opcional de gráficos como imagen para PDF:
   - Usar `Node.snapshot(...)` sobre `barMontos` y `lineTendencias` y `genderPie` cuando estén visibles.

## Exportación a PDF (PDFBox)
1. Crear `PDDocument` y una o más `PDPage` tamaño A4.
2. Escribir metadatos: título ("Reportes operativos y de gestión"), fecha/hora generación, período.
3. Maquetar contenido:
   - Encabezados con fuente en negrita.
   - Tabla "Montos por tipo" (tipo, monto) con renglones alternos.
   - Lista "Clientes frecuentes" y "Recursos en mantenimiento".
   - Insertar imágenes de los gráficos capturados (escalados proporcionalmente).
4. Manejar salto de página automáticamente cuando sobrepase el alto.
5. Guardar en la ruta elegida por el usuario.

## Exportación a Excel (.xlsx, Apache POI)
1. Crear `XSSFWorkbook` con hojas:
   - `Resumen` (metadatos y período).
   - `MontosPorTipo` (columnas: Tipo, Monto) con estilos de encabezado, formato monetario.
   - `ClientesFrecuentes` (Nombre, Veces).
   - `Mantenimiento` (Recurso/ID).
2. Aplicar estilos:
   - Encabezados con fondo tenue y negrita.
   - Formato de moneda en la columna Monto.
   - Auto-size de columnas.
3. Insertar opcionalmente la imagen del gráfico en la hoja `Resumen` (PNG a `DrawingPatriarch`).
4. Guardar `.xlsx` en la ruta seleccionada.

## Validación y feedback
1. Verificar que el número de filas exportadas coincida con las observables mostradas.
2. Mostrar notificación de éxito con ruta del archivo.
3. En caso de error:
   - Capturar la excepción y mostrar `Alert` con el mensaje.
   - Registrar el detalle en `estadoAdminLabel`.
4. Durante exportación: `ProgressIndicator` visible y botones deshabilitados.

## Compatibilidad y formato
- PDF: fuente estándar, A4, márgenes razonables, sin dependencias nativas.
- Excel: `.xlsx` compatible con Excel/LibreOffice.
- Metadatos: título, autor (usuario actual), fecha/hora.

## Entregables
1. Handlers `onExportarPDF()` y `onExportarExcel()` en `AdminController`.
2. Botón de exportación en `AdminView.fxml` dentro de `reportesPane`.
3. Utilidades internas para convertir `WritableImage` a `BufferedImage` y embebido en PDF/Excel.
4. Dependencias nuevas en `pom.xml` (`pdfbox`, `poi-ooxml`).

## Pruebas
- Exportar con período sin datos: archivos válidos con secciones vacías y metadatos.
- Exportar con datos: comparación de totales y conteos.
- Confirmar ausencia de pérdida de información y correcta legibilidad.

¿Procedo a implementar según este plan (UI, controladores, dependencias y generación de archivos)?