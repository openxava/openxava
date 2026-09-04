# Bloque 3c — Listas de módulo: Hilo de trabajo

## Objetivo

Modernizar el aspecto visual de las listas de módulo (tabs) de OpenXava 8 —cabeceras, filas, totales, celdas editables, acciones de fila, resize handles, checkboxes e indicador de carga— siguiendo la línea visual de Attio/Linear/Notion y los tokens del sistema de diseño.

---

## Arquitectura del cambio

La lista de módulo se renderiza principalmente en `listEditor.jsp` con clases CSS de `base.css`. Los estilos se controlan mediante:

1. **`base.css`**: tokens de lista (`--list-*`) y reglas para `.ox-list`, `.ox-list-header`, `.ox-list-pair`/`.ox-list-odd`, `.ox-total-row`, `.ox-editable-cell`, `.ox-list-info`, etc.
2. **`listEditor.jsp`**: genera las filas, cabeceras, filtros y totales. Aplica las clases `ox-editable-cell` / `ox-readonly-cell` a las celdas de datos según `tab.isPropertyEditable()`.
3. **`Style.java`**: proporciona nombres de clases y flags de estilo. `isAlignHeaderAsData()` ahora devuelve `true` para que las cabeceras numéricas se alineen a la derecha.
4. **`openxava.js`**: `openxava.markListsAsLoading()` añade la clase `ox-loading` a las listas antes de una petición AJAX; se elimina al refrescar.
5. **`dark-overrides.css`** / **`light.css`**: overrides de tema. Los tokens de zebra obsoletos se eliminaron de ambos.

---

## Decisiones de diseño

### Zebra striping eliminado
- Se removieron los tokens `--list-column2-background`, `--list-even-column-background`, `--list-row-bottom-color`, `--list-row-a-color`, `--list-header-arrows-color` de `base.css`, `light.css` y `dark-overrides.css`.
- Las filas se separan ahora con `border-bottom: 1px solid var(--frame-border)` (hairline).
- Altura de fila: 42px.

### Cabeceras
- `th.ox-list-header`: uppercase, `font-size-xs`, peso 600, letter-spacing 0.05em, color `--label-color`, fondo transparente.
- `Style.isAlignHeaderAsData()` = `true` → cabeceras numéricas alineadas a la derecha, coherentes con los datos.

### Hover y selección
- Hover: `color-mix(in srgb, var(--color) 4%, transparent)` — plano, sin degradados.
- Selección: `color-mix(in srgb, var(--accent-color) 10%, transparent)` — sutil, accent.
- Transición `background-color var(--transition-fast)` en filas y celdas.

### Resize handles (CSS puro)
- `.xava_resizable .ui-resizable-e::after`: línea vertical 2px centrada en el borde derecho del `<th>`, width 6px.
- Visible on hover del `<th>` o focus del handle; color accent en hover del handle.
- `handle_vertical.png` **borrado** — ya no se usa en ningún CSS.

### Celdas editables (spreadsheet-like)
- `listEditor.jsp` marca cada celda con `ox-editable-cell` o `ox-readonly-cell`.
- Inputs en celdas editables: `border-color: transparent`, `background-color: transparent`, `border-radius: var(--radius-sm)`.
- `:focus-within` en la celda: `box-shadow: inset 0 0 0 2px var(--accent-color)` — anillo de acento.
- Hover de celda editable: `--element-collection-cell-hover-background` (acento al 12%).

### Acciones de fila
- En element collections: `opacity: 0` → `1` on `tr:hover` o `:focus-within` (transición `opacity var(--transition-fast)`).
- **Pendiente**: aplicar la misma lógica a las listas de módulo (`.ox-list .ox-list-action-cell`).

### Totales
- `.ox-total-row`: `border-bottom: none`.
- Primera fila de totales: `border-top: 1px solid var(--frame-border)`, `padding-top: var(--space-2)`.
- Última fila de totales: `font-weight: 600`, `padding-bottom: var(--space-2)`.
- Label cell: `text-align: right`, `color: var(--label-color)`, `font-weight: 500`.

### Skeleton loading
- `@keyframes ox-list-loading-pulse`: `0%,100% { opacity: 1 } 50% { opacity: 0.45 }`.
- `.ox-list.ox-loading .ox-list-pair, .ox-list.ox-loading .ox-list-odd`: `animation: ox-list-loading-pulse 1.2s ease-in-out 250ms infinite`.
- `prefers-reduced-motion: reduce`: `animation: none; opacity: 0.55`.
- `openxava.markListsAsLoading()` añade `ox-loading` antes de AJAX; `$('.ox-list.ox-loading').removeClass('ox-loading')` al refrescar.

### Checkboxes y radio buttons
- `accent-color: var(--accent-color)`, 15px de tamaño, `margin-top: 5px`.

### Popup menu
- `.ox-popup-menu`: `background: var(--list-background)` — sólido, no translúcido.

### List info
- `.ox-list-info`: `font-size: var(--font-size-sm)`, `color: var(--label-color)`.

---

## Tokens CSS

**Nuevos / defaults cambiados:**
- `--list-row-hover-background`: `color-mix(in srgb, var(--color) 4%, transparent)`
- `--selected-row-background`: `color-mix(in srgb, var(--accent-color) 10%, transparent)`
- `--list-header-background`: `transparent`

**Eliminados:**
- `--list-column2-background`, `--list-even-column-background`, `--list-row-bottom-color`, `--list-row-a-color`, `--list-header-arrows-color`

---

## Archivos modificados

| Archivo | Cambio |
|---|---|
| `base.css` | Tokens de lista, cabeceras, hairlines, altura 42px, hover/selection, celdas editables, totales, resize handles CSS, skeleton loading, checkboxes, popup menu, list info |
| `dark-overrides.css` | Eliminados tokens de zebra obsoletos |
| `light.css` | Tokens de zebra ya no definidos (limpiados en sesión anterior) |
| `listEditor.jsp` | Clases `ox-editable-cell` / `ox-readonly-cell` en celdas de datos |
| `Style.java` | `isAlignHeaderAsData()` devuelve `true` |
| `openxava.js` | `markListsAsLoading()` + limpieza de `ox-loading` al refrescar |
| `handle_vertical.png` | **Borrado** |
| `changelog.txt` | 4 entradas nuevas |
| `migration_en.html` | Sección "Module lists restyled" |
| `migration_es.html` | Sección "Listas de módulo rediseñadas" |

**Estado**: Implementación concluida. `mvn compile` OK. Revisión visual realizada — ver sección "Pendiente de pulir".

---

## Pendiente de pulir

Tras la revisión visual comparando con Attio/Linear/Notion, se identifican 5 puntos de refinamiento:

1. ~~**Iconos de acción de fila visibles siempre en listas de módulo**~~ ✅ **Hecho** — Cambiado `opacity: 0.5` → `0` en `.ox-list-action-cell i, .ox-list-action-cell img` de `base.css`. Ahora los iconos solo aparecen on `tr:hover` o `:focus-within`, igual que en element collections.

2. ~~**Fila de filtro (subheader) demasiado alta/boxy**~~ ✅ **Hecho** — Inputs de filtro reducidos a 28px con borde sutil `var(--frame-border)` y fondo transparente. Contenido centrado verticalmente con `vertical-align: middle` y `padding-top: var(--space-1)`. `padding-bottom: var(--space-1)` equilibrado.

3. ~~**Checkbox de fila algo tosco**~~ ✅ **Hecho** — Checkbox reducido a 14px, eliminado `margin-top: 5px`, centrado con `vertical-align: middle`. Añadido `color-scheme: dark/light` en `dark-overrides.css`/`light.css` para que los controles nativos se rendericen con colores coherentes en cada tema.

4. ~~**Borde exterior de la lista (card)**~~ ✅ **Hecho** — Eliminado `border: 1px solid var(--frame-border)` de `.ox-list`, ahora `border: none`. Coherente con element collections (que ya tenían `border: none`). Los hairlines internos llegan a ancho completo sin ser recortados.

5. ~~**Paginación / list-info algo plana**~~ ✅ **Hecho** — Pills compactas con `padding: 2px 8px`, `font-weight: 500`, seleccionada con `accent-soft`. Flechas con ancho fijo 24px, `opacity: 0.3` cuando deshabilitadas (evita layout shift). Select de filas por página borderless con borde en hover/focus. Padding-left alineado con el marco. Todo verticalmente alineado con `vertical-align: middle`.

---

## Indicador visual de celdas editables en lista (2ª iteración)

### Problema

En la lista de módulo, la mayoría de columnas no son editables y solo 1–2 lo son. El usuario no tiene forma de saber cuáles son editables sin interactuar. Se comprobó empíricamente: al analizar un pantallazo, el indicador del `$` (calculadora) llevó a confundir *Descripción extendida* (combo con `▼`) como editable cuando no lo era. La señal visual actual (combo transparente, icono de calculadora) **no es fiable** como indicador de editabilidad.

### Análisis de apps modernas y LOB

**Apps modernas (Notion, Airtable, Attio, Linear):** ninguna usa indicador permanente. Confían en feedback dinámico: cursor `text`, hover de celda, foco con borde. El descubrimiento negativo (celdas no editables no responden al hover) es la señal.

**Apps LOB:**
- **SAP Fiori**: modo Edición explícito (botón Edit) que conmuta toda la tabla.
- **Salesforce Lightning**: lápiz on-hover de celda + doble click.
- **Excel / software contable (Sage, ContaPlus, Epicor, AG Grid)**: celda editable = blanca, celda bloqueada = gris tenue. Los usuarios empresariales lo leen instintivamente.
- **Dynamics / Power Apps**: sin indicador, doble click (considerado su punto débil).
- **Odoo**: click lleva la fila entera a modo edición.

### Decisiones

1. **Quitar el `$` de las propiedades editables numéricas** — pendiente de implementar. El chrome del editor contamina la detección.
2. **Quitar el icono del combo en celdas editables** — pendiente de implementar y de decidir (anotado en `pending.txt`).
3. **Lápiz on-hover de fila** — implementado. Un icono lápiz aparece superpuesto en la esquina superior derecha de cada celda editable cuando el ratón pasa por la fila. No es permanente (no ensucia la tabla), el trigger es probable (el usuario pasa el ratón por la fila para seleccionar), y es CSS puro.
4. **Lápiz en cabecera de columna** — propuesto pero pendiente de decisión. Sería el único indicador pasivo permanente (sin mover el ratón). Se evaluará después de probar el lápiz on-hover sin `$` y sin icono de combo.

### Implementación: lápiz on-hover de fila (base.css)

**Token:** se consideró inicialmente un data URI SVG (`--editable-cell-pencil-icon`), pero se descartó en favor de MDI por consistencia con el proyecto.

**Reglas CSS (base.css líneas ~2109-2132):**
- `.ox-list td.ox-editable-cell` → `position: relative` (contenedor para el icono absoluto).
- `.ox-list td.ox-editable-cell::after` → `content: '\F03EB'` (MDI `mdi-pencil`), `font-family: 'Material Design Icons'`, `font-size: 12px`, `position: absolute; top: 2px; right: 2px`, `opacity: 0`, `pointer-events: none`, `transition: opacity var(--transition-fast)`.
- `tr:hover` / `tr:focus-within` → `opacity: 0.5` (lápiz visible en todas las celdas editables de la fila).
- `tr:hover td.ox-editable-cell:hover` / `:focus-within` → `opacity: 1` (lápiz destacado en la celda concreta).

**Notas técnicas:**
- Posición absoluta para no alterar la altura de fila ni ocupar espacio inline (primera versión inline ensanchaba las celdas).
- `pointer-events: none` para que el icono no interfiera con el click en la celda.
- `font-size: 12px` para ser discreto.
- Funciona tanto en lista de módulo como en `ElementCollection` (ambas usan `ox-editable-cell`).
- Mismo patrón que el grip de resize del diálogo (`content: '\F045D'` + MDI en línea ~2640 de base.css).

### Estado

- **Hecho:** lápiz on-hover de fila implementado en `base.css`.
- **Pendiente:** quitar `$` y posiblemente icono de combo. Decidir sobre lápiz en cabecera. Revisión visual sin `$` y sin icono de combo.
- **No implementado:** `cursor: text` en `.ox-editable-cell` (punto 1 de la propuesta, ya se consideró que estaba hecho pero no se añadió explícitamente — verificar).
