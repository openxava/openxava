# UI8 Modernization - Bloque 10: Dashboards

**Estado: CERRADO** — verificado visualmente por el usuario; tokens documentados en `custom-style_en/es.html` y entradas añadidas al changelog.

Hilo de trabajo para modernizar los dashboards (entidades con miembros `@LargeDisplay`, `@SimpleList` y `@Chart`). Objetivo: aspecto moderno estilo Attio/Notion/Linear, coherente con la nueva UI. Primera fase: arreglar lo roto por los bloques anteriores para que todo se vea en su sitio y dentro de la página sin desplazar.

Dashboard de prueba: `StaffDashboard` (openxavatest, `org.openxava.test.dashboards.StaffDashboard`).

## Contexto técnico relevante

- Los dashboards **no usan flow layout** (`flowLayout` por defecto es `false` en `XavaPreferences`). Cada fila de miembros se renderiza en `.ox-layout-detail` (`display: table` + `white-space: nowrap` en `layout.css`), con celdas `.ox-layout-aligned-cell` (etiqueta) y `.ox-layout-not-aligned-cell` (editor).
- El editor `LargeDisplay` (`largeDisplayEditor.jsp`) tiene `default-label-format="no-label"`: la etiqueta la pinta el propio editor, no la celda de etiqueta (que queda vacía).
- Los marcos de colección usan `.ox-full-frame` (`width: calc(100% - 27px)`) y `.ox-half-frame` (`calc(50% - 27px)`).
- `.ox-frame` actual: `float: left`, `padding: var(--space-4)`, `margin-right: var(--space-2)`, `border-radius: var(--radius-lg)`.
- **No hay reset global de `box-sizing`**: los frames usan `content-box`, así que `width` define la caja de contenido y la caja visible = width + padding + border. Esto era la causa raíz del desajuste a la derecha (ver sección 4).
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
	width: calc(100% - 27px);   /* igual que el borde visible de .ox-full-frame */
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

Resultado: las tarjetas se reparten la fila a partes iguales, con separación uniforme (`column-gap`), alineadas a izquierda y derecha con el chart. Detalles iterativos: la celda de etiqueta vacía seguía siendo un flex item y creaba un `column-gap` extra a la izquierda — resuelto con `display: none`. El ancho final es `calc(100% - 27px)` para coincidir con el borde visible del marco del chart (ver sección 4).

### 3. Altura del chart

**Problema**: el chart ocupaba ~619px de alto (`size.height: Math.max(320, window.innerHeight - 300)`), el dashboard requería scroll. En la UI original era ~340px.

**Fix** en `editors/js/collectionChartEditor.js`:
- `size.height` → `Math.max(300, window.innerHeight - 550)` (~370px en ventana típica, mínimo 300px).

### 4. Alineación derecha: la causa raíz era `box-sizing`

**Síntoma**: la fila de tarjetas siempre quedaba corta a la derecha respecto al chart — ~2mm con la fila al 100%, ~1cm con la fila a `calc(100% - 27px)`. Daban vueltas en círculo porque el marco del chart era ~34px más ancho de lo esperado.

**Causa raíz**: `.ox-full-frame` tiene `width: calc(100% - 27px)` pero `.ox-frame` usa `box-sizing: content-box` (no hay reset global). El `width` definía la **caja de contenido**, así que la caja visible (borde) = `100% - 27px + padding(32px) + border(2px)` = **100% + 7px**: el marco desbordaba `.ox-detail` por ~7px. Además el svg de c3 medía el contenedor ya estirado (1020px), reforzando el desajuste.

**Fix** en `base.css`:
- `.ox-full-frame` y `.ox-half-frame` → `box-sizing: border-box`. Ahora el `calc()` define la caja visible completa y coincide con la fila de tarjetas (`calc(100% - 27px)`). También corrige los half-frame (cada uno era 34px más ancho de lo declarado).

**Fix** en `collectionChartEditor.js`:
- Tras `c3.generate()`, `setTimeout(..., 0)` con `chart.resize({ width: chartElement.width() })` para que el svg se regenere al ancho real del contenedor una vez aplicado el layout.

Resultado: tarjetas, chart y simple lists terminan en el mismo borde derecho. Verificado por el usuario.

### 5. Half frames: alineación derecha

**Problema**: las colecciones `.ox-half-frame` (`turnoverByYear`, `moreSeniorWorkers`) quedaban ~19px (medio centímetro) cortas a la derecha respecto al marco del chart y las tarjetas.

**Causa**: cada half-frame medía `calc(50% - 27px)` y además `.ox-frame` aplica `margin-right: var(--space-2)` (8px). El segundo marco terminaba en `100% - 54px + 8px = 100% - 46px`, mientras el `.ox-full-frame` termina en `100% - 27px`.

**Fix** en `base.css`: `.ox-half-frame` → `width: calc(50% - 17.5px)`, de modo que 2 mitades + el margin de 8px = `100% - 27px`, mismo borde derecho que el full frame.

Resultado: colecciones alineadas a la derecha con el chart y las tarjetas. Verificado por el usuario.

### 6. Tarjetas KPI con jerarquía moderna

**Cambio** en `largeDisplayEditor.jsp`: el icono se mueve dentro de `.ox-large-display-label` (junto al texto, envuelto en `.ox-large-display-label-text`), ya no compite con el número.

**Cambio** en `base.css` (`.ox-large-display*`):
- Label: `--font-size-xs`, uppercase, `letter-spacing: 0.05em`, color muted (`color-mix 55%`), icono de `--font-size-md` con `--large-display-icon-color` (ahora `var(--accent-color)` por defecto).
- Número: de `xxx-large` a `--font-size-3xl` (32px), peso 600, `tabular-nums`.
- Prefijo/sufijo (`%`, `€`): `--font-size-lg`, peso 500, muted — ya no `x-large` compitiendo con el valor.
- Contenido alineado a la izquierda (`justify-content: flex-start`) en vez de centrado — patrón KPI estándar en Linear/Attio/Notion/Stripe.
- Sin `box-shadow`: las tarjetas quedan planas con el mismo borde que los frames de listas y charts (coherencia; esas apps reservan la elevación para elementos flotantes).

**Alineación vertical icono/label** (resuelto tras varias iteraciones): el glifo MDI se pinta en `i.mdi::before` y el vendor `materialdesignicons.css` fija `.mdi:before { font-size: 18px }`, así que el glifo desbordaba la caja del `i` (14px) por arriba y ni flex ni `vertical-align` lo centraban. Fix final en `base.css`:
- `.ox-large-display .ox-large-display-label i.mdi::before { font-size: var(--font-size-md) }` — iguala el glifo a la caja.
- En el `i.mdi`: `line-height: 0.9; vertical-align: top` — la caja de línea queda más baja que el glifo, que desborda uniformemente y queda centrado ópticamente respecto al label en mayúsculas (solución ajustada manualmente por el usuario).

### 7. SimpleList: estilo "quiet" + barra inline

Decisión de diseño: las `@SimpleList` **no** deben parecer grids editables (esa fue la motivación original de la anotación). Se aplica el patrón "summary list" de Odoo/Linear/GA: sin chrome de tabla, denso, el dato es el protagonista.

**Cambio** en `base.css` (`.ox-simple-list*`):
- Eliminados márgenes hardcodeados (`0px 10px 5px 25px`) y `font-size: larger`; ahora `margin: 0`, `width: 100%`, tamaño base.
- Cabecera: `--font-size-xs`, uppercase, `letter-spacing`, `--simple-list-header-color` (nuevo default muted, antes `inherit`).
- Filas: hairline `border-top` entre filas (`--frame-border`), padding `--space-1 --space-2`, `tabular-nums` en celdas.
- Sin hover de fila ni affordances de interacción.

**Cambio** en `simpleListEditor.jsp`:
- Cabeceras de columnas numéricas alineadas a la derecha (`ox-text-align-right` también en `th`).
- **Barra inline por convención**: en columnas numéricas (`p.isNumber() && !hasValidValues()`), el `td` lleva un `linear-gradient(to left, --simple-list-bar-color X%, transparent X%)` proporcional al máximo absoluto de la columna. Sin anotación: se aplica a toda columna numérica con max > 0.
- Nuevo token `--simple-list-bar-color` (`color-mix` del acento al 12%, funciona en light y dark).

## Pendiente / conocido

- Nada pendiente: revisión visual hecha por el usuario, changelog y `custom-style_en/es.html` actualizados.

## Archivos tocados

- `openxava/src/main/resources/META-INF/resources/xava/editors/largeDisplayEditor.jsp`
- `openxava/src/main/resources/META-INF/resources/xava/editors/simpleListEditor.jsp`
- `openxava/src/main/resources/META-INF/resources/xava/style/base.css`
- `openxava/src/main/resources/META-INF/resources/xava/editors/js/collectionChartEditor.js`
