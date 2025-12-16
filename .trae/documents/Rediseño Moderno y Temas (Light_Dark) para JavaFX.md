## Principios de Diseño

* Coherencia visual, jerarquía clara y minimalismo funcional.

* Tarjetas (cards) con esquinas redondeadas y sombras sutiles.

* Navegación lateral + barra superior, como en la referencia.

## Paleta de Colores

* Tema claro:

  * Fondo `#F7F9FC`, Superficie `#FFFFFF`, Texto primario `#111827`, Texto secundario `#6B7280`.

  * Acentos: Azul `#3B82F6`, Morado `#8B5CF6`, Amarillo `#F59E0B`, Verde `#2DA44E`, Rojo `#EF4444`.

  * Bordes `#D0D7DE`, Sombras `rgba(0,0,0,0.10)`.

* Tema oscuro:

  * Fondo `#0F172A`, Superficie `#111827`, Texto primario `#E5E7EB`, Texto secundario `#9CA3AF`.

  * Acentos adaptados: Azul `#60A5FA`, Morado `#A78BFA`, Amarillo `#FBBF24`, Verde `#34D399`, Rojo `#F87171`.

* Tokens por nombre (ej.: `--color-bg`, `--color-surface`, `--color-primary`, `--color-success`).

## Tipografía

* Familia: `Segoe UI`, fallbacks `Arial, Helvetica` (consistente con Windows).

* Escala: `12/14/16/18/22/28` para `caption/body/subtitle/title/card-title/page-title`.

* Peso: `regular` para texto, `semibold` para encabezados.

## Layout y Espaciado

* Contenedores: `VBox/HBox/GridPane` con `Priority.ALWAYS` para crecimiento.

* Pauta de espaciado: `4/8/12/16/24` píxeles (`space-1..space-5`).

* Tarjetas modulares para métricas, gráficos y tablas; uso de `SplitPane` responsivo.

## Componentes Consistentes

* Botones: `btn-primary`, `btn-secondary`, `btn-outline`, `btn-danger`, `btn-ghost`.

* Formularios: `text-field`, `combo-box`, `date-picker` con radio 8 px y borde de enfoque accesible.

* Menú lateral: `sidebar` con item activo destacado y badges.

* Tablas: encabezado contrastado, filas hover, estados con chips (`chip-success`, `chip-warning`, `chip-danger`).

* Gráficos: colores por serie y leyendas limpias, labels rotadas cuando haya muchas categorías.

## Efectos Visuales

* Sombras: `shadow-sm`, `shadow-md` (10–16 px, baja opacidad).

* Transparencias sutiles en hover (`rgba(...)`), enfoque con `outline`.

* Animaciones discretas: transición de color/escala en hover mediante `Timeline` (JavaFX), sin exagerar.

## Responsividad

* Ajuste automático de columnas/cards con `GridPane` y `wrap` lógico por ancho de escena.

* Ejes y tamaños de gráficos se recalculan al `widthProperty/heightProperty` de la escena.

* Escalado tipográfico base (`.root { -fx-font-size }`) ligado al ancho para mantener legibilidad.

## Accesibilidad

* Contraste AA/AAA en temas claro/oscuro.

* Enfoque visible en todos los interactivos.

* No depender solo de color para estados; incluir iconos/etiquetas.

* Tooltips informativos y áreas de click generosas.

## Implementación Técnica

1. Crear `style/design-system.css` con tokens, tipografía, espaciado y componentes base.
2. Crear `style/theme-light.css` y `style/theme-dark.css` con paletas y sombras de cada tema.
3. Añadir `ThemeManager` (Java) para alternar hojas de estilo en `Scene` (`setAll(...)`).
4. Actualizar `LoginView.fxml`, `AdminView.fxml`, `AlquilerView.fxml` para aplicar `styleClass` de diseño (sin cambiar lógica).
5. Gráficos: aplicar paleta vía selectores `.default-colorN.chart-bar/line` y rotación de labels cuando existan muchas categorías.
6. Añadir un `ToggleButton` de tema en la barra superior; persistir preferencia en sesión.

## Entregables

* Tres CSS (`design-system.css`, `theme-light.css`, `theme-dark.css`), `ThemeManager.java` y updates mínimos en FXML para clases.

* Diseño responsive y accesible coherente con la captura.

## Confirmación

* ¿Confirmas el uso de temas claro/oscuro con la paleta propuesta y la creación de los tres CSS + `ThemeManager`? Si tienes colores corporativos, los integro en los tokens antes de implementar.

