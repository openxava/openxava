# UI8 Modernization - Bloque 10: Dashboards

Hilo de trabajo para modernizar los dashboards (entidades con miembros `@LargeDisplay`, `@SimpleList` y `@Chart`). Objetivo: aspecto moderno estilo Attio/Notion/Linear, coherente con la nueva UI. Primera fase: arreglar lo roto por los bloques anteriores para que todo se vea en su sitio y dentro de la página sin desplazar.

Dashboard de prueba: `StaffDashboard` (openxavatest, `org.openxava.test.dashboards.StaffDashboard`).

## Contexto técnico relevante

- Los dashboards **no usan flow layout** (`flowLayout` por defecto es `false` en `XavaPreferences`). Cada fila de miembros se renderiza en `.ox-layout-detail` (`display: table` + `white-space: nowrap` en `layout.css`), con celdas `.ox-layout-aligned-cell` (etiqueta) y `.ox-layout-not-aligned-cell` (editor).
- El editor `LargeDisplay` (`largeDisplayEditor.jsp`) tiene `default-label-format="no-label"`: la etiqueta la pinta el propio editor, no la celda de etiqueta (que queda vacía).
- Los marcos de colección usan `.ox-full-frame` (`width: calc(100% - 27px)`) y `.ox-half-frame` (`calc(50% - 27px)`).
- `.ox-frame` actual: `float: left`, `padding: var(--space-4)`, `margin-right: var(--space-2)`, `border-radius: var(--radius-lg)`.
- `.ox-editor-wrapper` tiene `padding: 1px 12px 5px 0` y `.xava_editor` es `inline-flex` (shrink-to-fit) — importante: un `width: 100%` en el contenido resuelve de forma circular si el padre es shrink-to-fit.
- `.ox-collection .ox-frame-content` tiene márgenes negativos `-var(--space-3)` a los lados.

## Cambios realizados

### 1. LargeDisplay: etiqueta dentro del marco

**Problema**: la etiqueta se renderizaba fuera del marco y `.ox-large-display` usaba márgenes negativos (`margin-top: -25px; margin-left: -5px`) que ya no cuadraban con el nuevo `.ox-frame`, dejando las etiquetas desalineadas respecto a chart y simple lists.

**Fix** en `editors/largeDisplayEditor.jsp`:
- La etiqueta (`.ox-large-display-label`) ahora va **dentro** del div `.ox-large-display.ox-frame`, arriba a la izquierda como en el original.
- Icono + prefijo + valor + sufijo envueltos en nuevo `.ox-large-display-content`.

**Fix** en `style/base.css`:
- `.ox-large-display`: eliminados márgenes negativos; `padding: var(--space-3)`; `min-width: 150px` (antes 200px/20px, para que la fila quepa).
- Nuevo `.ox-large-display-content`: `display: flex; justify-content: center; align-items: baseline` (el flex se movió del contenedor principal al contenido).
- `.ox-large-display-label`: `font-size: var(--font-size-md); font-weight: 600` (antes heredaba el `xxx-large` del contexto).

### 2. Fila de LargeDisplays: ancho completo y alineación

**Problema**: la fila desbordaba por la derecha (la tabla `nowrap` no puede envolver) y luego, al estrechar las tarjetas, quedaba un hueco a la derecha y huecos desiguales entre tarjetas.

**Fix** en `base.css` (solo aplica a filas que contienen large displays, vía `:has()`):

```css
.ox-layout-detail:has(.ox-large-display) {
	display: flex;
	flex-wrap: wrap;
	width: 100%;
	column-gap: var(--space-2);
}
.ox-layout-detail:has(.ox-large-display) .ox-layout-new-line {
	flex-basis: 100%;
	height: 0;
}
.ox-layout-detail:has(.ox-large-display) .ox-layout-aligned-cell {
	display: none;   /* celda de etiqueta vacía: generaba un gap extra a la izquierda */
}
.ox-layout-detail:has(.ox-large-display) .ox-layout-not-aligned-cell {
	flex: 1 1 0;
	min-width: 0;
	padding: 0;      /* elimina el padding de .ox-editor-wrapper */
}
.ox-layout-detail:has(.ox-large-display) .xava_editor {
	width: 100%;     /* el span inline-flex ya no encoge la tarjeta */
}
.ox-layout-detail .ox-large-display {
	width: 100%;
	margin-right: 0;
	box-sizing: border-box;
}
```

Resultado: las tarjetas se reparten la fila a partes iguales, con separación uniforme (`column-gap`), alineadas a izquierda y derecha con el chart. Detalle iterativo: la celda de etiqueta vacía seguía siendo un flex item y creaba un `column-gap` extra a la izquierda — resuelto con `display: none`.

### 3. Altura del chart

**Problema**: el chart ocupaba ~619px de alto (`size.height: Math.max(320, window.innerHeight - 300)`), el dashboard requería scroll. En la UI original era ~340px.

**Fix** en `editors/js/collectionChartEditor.js`:
- `size.height` → `Math.max(300, window.innerHeight - 550)` (~370px en ventana típica, mínimo 300px).

## Pendiente / conocido

- **Chart desborda su marco ~2mm a la derecha**: el svg de c3 se genera con ancho fijo en px (1020) mayor que el contenido de `.ox-full-frame` (`calc(100% - 27px)`), y `.ox-frame` no tiene `overflow: hidden`. La fila de tarjetas está bien; es el chart el que sobresale. Opciones: hacer el chart responsive (c3 al ancho real del contenedor + resize) o ampliar el marco a `100%` y contener el svg. Recomendada la segunda para dashboard.
- **SimpleList**: pendiente de revisión/estilo.
- Aspecto moderno general del dashboard (estilo Attio/Notion/Linear) una vez todo esté en su sitio.
- `pending.txt`: changelog y documentar custom-style.

## Archivos tocados

- `openxava/src/main/resources/META-INF/resources/xava/editors/largeDisplayEditor.jsp`
- `openxava/src/main/resources/META-INF/resources/xava/style/base.css`
- `openxava/src/main/resources/META-INF/resources/xava/editors/js/collectionChartEditor.js`
