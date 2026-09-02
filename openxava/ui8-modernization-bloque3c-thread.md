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

1. **Iconos de acción de fila visibles siempre en listas de módulo** — El CSS de `opacity: 0 → 1 on tr:hover` se aplicó a element collections pero **no a `.ox-list .ox-list-action-cell`**. En Attio/Linear los iconos solo aparecen on hover. Hay que extender la regla.

2. **Fila de filtro (subheader) demasiado alta/boxy** — Los inputs de filtro son 32px con bordes completos. En Linear/Attio los filtros son chips compactos o inputs borderless que parecen texto hasta hover. Reducir altura a 28px y quitar borde hasta hover/focus.

3. **Checkbox de fila algo tosco** — 15px con `margin-top: 5px` se ve desplazado. En Notion/Linear el checkbox está perfectamente centrado verticalmente y es más sutil (14px, sin margen extra).

4. **Borde exterior de la lista (card)** — El `border: 1px solid var(--frame-border)` es correcto pero algo pesado. En Linear las tablas no tienen borde exterior, solo hairlines internos. Attio usa un borde muy sutil. Podría afinarse.

5. **Paginación / list-info algo plana** — Funciona pero visualmente es plano. En Linear/Attio la paginación es más compacta, con botones pill y el contador de filas en gris muy claro a la derecha.
