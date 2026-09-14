# Bloque 9 — Charts (c3/d3): Resumen del hilo

## Objetivo

Modernizar el aspecto visual de los gráficos de OpenXava —el formato "charts" de lista (`chartDataEditor`) y los gráficos de colecciones (`collectionChartEditor`)— con una estética de 2026 (Attio, Linear, Notion), coherente con los tokens y componentes de los bloques anteriores.

Restricciones acordadas con el usuario:

- No cambiar la librería (c3/d3 se mantienen) ni el comportamiento de cara al usuario: solo aspecto.
- No ejecutar tests automáticamente; la verificación visual y la suite las hace el usuario desde su IDE.

Punto de partida: `c3.css` vendor de 2013 con colores hardcodeados (`#000`, `#aaa`, `#4682b4`), `font: 10px sans-serif`, tooltip con sombra anticuada, y un layout de editor lleno de hacks (márgenes negativos, `calc(50vw - 160px)`, `calc(100vw - 385px)`, anchos fijos).

---

## Cambios implementados

### Tokens nuevos (`base.css`, overrides en `dark-overrides.css`)

- Paleta de series: `--chart-series-1` … `--chart-series-8` (índigo/sky/emerald/amber/rose/violet/teal/slate en light; variantes más luminosas en dark).
- Texto y ejes: `--chart-text-color`, `--chart-axis-color`, `--chart-grid-color`.
- Tooltip: `--chart-tooltip-background`, `--chart-tooltip-color`, `--chart-tooltip-border`.
- Arcos: `--chart-arc-stroke` (color de superficie; en dark apunta a `--my-dark`).

### Overrides de c3 (acotados a `.ox-chart-data` / `.xava_collection_chart`, sin tocar `c3.css`)

- Tipografía Inter heredada, tamaños `--font-size-*`, `tabular-nums` en ejes y tooltip.
- Ejes y grid con hairlines de tema en lugar de `#000`/`#aaa`; grid Y con dash sutil.
- Tooltip como tarjeta: `--radius-md`, `--elevation-2`, borde hairline, fondo de tema, cabecera semibold sin el gris `#aaa` + texto blanco.
- Leyenda con tamaño/color de tema; fondo de leyenda con tokens.
- Arcos de pie/donut con `stroke` de superficie (elimina las líneas blancas en dark).

### Paleta aplicada a las specs de c3

- Nuevo helper `openxava.chartSeriesColors()` en `openxava.js`: lee `--chart-series-1..8` con `getComputedStyle` y devuelve el array de colores.
- `chartDataEditor.js` y `collectionChartEditor.js`: si la paleta existe, se asigna a `color.pattern` de la spec.

### Selector de tipo de chart (`.ox-chart-type`)

- Reestilizado como segmented control, igual que `.ox-list-formats` del Bloque 5: fondo `--segmented-background`, pill activa con `--segmented-active-background`/`--segmented-active-color`, `--radius-md`, transición estándar.
- Eliminados `font-size: 200%`, paddings en píxeles, márgenes negativos y el fondo `--chart-type-background`.

### Layout del editor de charts (`.ox-charts`)

- `.ox-charts` es ahora el contenedor flex (`flex-wrap: wrap`, `gap` con `--space-*`, padding con tokens).
- Los wrappers intermedios (`.ox-layout-detail` y divs planos hijos directos) se aplastan con `display: contents` porque el frame del panel y las celdas de `chartData`/`xColumn` son hermanos, no hijos del layout-detail (verificado en `DetailViewRenderer.java` / `FrameLayout.java`).
- `.ox-layout-new-line` dentro de charts: `display: none` (el salto de fila lo produce la celda de `xColumn` con `flex-basis: 100%`).
- Celda del chart (`:has(.ox-chart-data)`): `flex: 1 1 auto; min-width: 0` — ocupa el espacio restante junto al panel.
- Celda del selector X (`:has(.ox-chart-x-column)`): `flex-basis: 100%; display: flex; justify-content: center` — fila propia, combo centrado bajo el gráfico.
- `.ox-charts .ox-full-frame`: `width: auto; flex: 0 0 auto` — el panel de controles toma su ancho natural en vez de `calc(100% - 27px)`.
- `.ox-charts .ox-element-collection [class*="collection_scroll"]`: `width: auto !important; max-width: 100%` — neutraliza el `width` inline que `openxava.setListsSize` fija al ancho de la botonera.
- Eliminados los hacks: `margin: -20px 0px 10px 9px`, `calc(100vw - 385px)`, `calc(50vw - 160px)`, anchos fijos de la colección de columnas, `.firefox .ct-chart { margin-left: -25px }`.

### Specs de c3

- `size.height: Math.max(320, window.innerHeight - 300)` en ambos editores — el chart crece con el viewport (c3 fijaba `max-height: 320px` inline por defecto).
- `grid.y.show: true` — líneas horizontales con `--chart-grid-color`.

### Documentación

- `changelog.txt`: entrada del Bloque 9.
- `custom-style_en.html` / `custom-style_es.html` (openxava-doc): nuevo grupo **Charts/Gráficos** en la lista de design tokens.
- `ui8-modernization-plan.md`: bloque marcado como concluido.

---

## Defectos corregidos durante la depuración del layout

| Defecto | Causa | Fix |
|---|---|---|
| Gráficos no aparecían | El DOM real no coincidía con lo asumido: el frame del panel y las celdas son hermanos dentro de `.ox-charts`, no hijos de `.ox-layout-detail` | `.ox-charts` como contenedor flex + `display: contents` en los wrappers |
| Gráfico debajo del panel | `.ox-full-frame` tenía `width: calc(100% - 27px)` y un `.ox-layout-new-line` espurio con `flex-basis: 100%` forzaba el salto | `width: auto; flex: 0 0 auto` en el frame; `display: none` en los new-line |
| Panel izquierdo gigante | `openxava.setListsSize` fija `width: 1069px` inline a `.collection_scroll` | `width: auto !important; max-width: 100%` acotado a `.ox-charts` |
| Combo de columna X no centrado bajo el gráfico | La celda envolvía a fila propia pero `span.xava_editor` es `inline-flex` con ancho de contenido; el `text-align: center` no tenía efecto | `display: flex; justify-content: center` en la celda |

## Archivos tocados

- `openxava/src/main/resources/META-INF/resources/xava/style/base.css`
- `openxava/src/main/resources/META-INF/resources/xava/style/dark-overrides.css`
- `openxava/src/main/resources/META-INF/resources/xava/js/openxava.js`
- `openxava/src/main/resources/META-INF/resources/xava/editors/js/chartDataEditor.js`
- `openxava/src/main/resources/META-INF/resources/xava/editors/js/collectionChartEditor.js`
- `openxava/changelog.txt`
- `openxava-doc/web/docs/custom-style_en.html`, `custom-style_es.html`

## Verificación

- Revisión visual del usuario en light y dark: panel de tipos/columnas a la izquierda, gráfico a la derecha, combo "Año" centrado debajo, tooltip y leyenda con el nuevo estilo.
- Tests HtmlUnit (`ChartsTest`, `CollectionChartTest`) ejecutados por el usuario desde su IDE.
- Pendiente fuera del bloque: verificar dashboard (posible bloque nuevo).
