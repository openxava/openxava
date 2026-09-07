# Bloque 6 — Secciones, marcos y acciones en vista: Hilo de trabajo

## Objetivo

Modernizar secciones (pestañas), marcos y acciones en vista al estilo 2026 (Attio/Notion/Linear), manteniendo la UX de aplicación LOB y coherencia con los bloques anteriores. Solo CSS + tokens salvo una clase nueva en el renderer para el contador.

---

## Secciones (pestañas de datos)

### Decisiones de diseño

- **Underlined tabs** (patrón Notion/Linear), deliberadamente distintas de las pestañas de módulo del Bloque 5 (metáfora de pestaña de navegador). Las pestañas de módulo son navegación global; las de secciones organizan datos dentro del detalle.
- **Tira de pestañas** (`.ox-section`): fondo transparente (era `--frame-background`) con hairline inferior de 1px (`--section-border`, por defecto `--frame-border`) a todo el ancho. Elimina el bloque sólido y da aire al detalle.
- **Pestaña activa**: indicador de subrayado de 2px en `--active-section-tab-bottom-color` (ahora `--accent-color`, era el color de texto), texto `--color` a peso 500 (era bold). El subrayado se apoya sobre el hairline de la tira.
- **Pestañas inactivas**: texto muted (`--section-color` = `--action-color`), hover con pill neutra (`--section-hover-background` = tinte 6% del color de texto, coherente con el hover de las pestañas de módulo) y color `--color` en hover. Radio `--radius-md` solo en esquinas superiores, para fundirse con el hairline.
- **Contador de colección** (`(n)`): nueva clase `ox-section-count` en `SectionsRenderer` (ambas rutas, activa e inactiva) que lo estiliza como texto pequeño muted (`--font-size-sm`, `--section-count-color`, peso 400). Se conserva el formato `(n)` porque `InvoiceCalculatedDetailsInSectionTest` aserta las etiquetas con paréntesis y `HotwireServlet` actualiza el texto con ese formato. Con `:empty` se oculta cuando no hay contador.
- **Layout**: la tabla interna se convierte a flexbox (`display: flex` en `table/tbody/tr/td`), gap de 2px entre pestañas; eliminados los floats. Los espacios en blanco entre spans del markup no generan cajas (whitespace-only text nodes no se renderizan en contenedores flex).
- **Overflow**: si las pestañas no caben, la tira hace scroll horizontal (`overflow-x: auto`, scrollbar fina) en lugar de romper el layout; las pestañas no se comprimen (`flex-shrink: 0`).
- **Foco de teclado**: anillo de 3px `--focus-ring-color` en `.ox-section-link` (`:focus-visible`), igual que en las pestañas de módulo.
- **Espaciados**: padding de pestaña `6px 12px` (era `7px 11px`), márgenes de la tira intactos para no romper la alineación con los campos ni los diálogos (`.ui-dialog`).

### Tokens CSS

**Nuevos:** `--section-border`, `--section-count-color`

**Defaults cambiados:**
- `--section-background`: `transparent` (era `--frame-background`)
- `--section-hover-color`: `var(--color)` (era `--action-hover-color`)
- `--section-hover-background`: tinte neutro 6% (era `--accent-soft`)
- `--active-section-tab-bottom-color`: `var(--accent-color)` (era `var(--color)`)

---

## Marcos (frames)

### Decisiones de diseño

- **Solo CSS + tokens**, sin tocar markup ni Java: el renderer ya genera una estructura usable (`.ox-frame` > `.ox-frame-title` flex + contenido) y los tests asertan ese markup.
- **Superficie intacta**: se conserva `--frame-background` (#f1f5f9 en light, translúcido en dark) y el hairline `--frame-border`; coherente con lo ya revisado en bloques anteriores. No se añade sombra: todo el sistema usa hairlines planas (barra de botones, secciones, listas).
- **Marcos anidados transparentes**: `.ox-frame .ox-frame` pierde el fondo propio (nuevo token `--frame-nested-background`, por defecto `transparent`) y queda solo con el borde. Resuelve el efecto "cajas dentro de cajas" sin cambiar la jerarquía ni los paddings; la profundidad se lee por el contorno, como los toggles anidados de Notion.
- **Cabecera flexbox**: `.ox-frame-title` pasa de floats/inline a `display: flex; align-items: center; gap: var(--space-1)`. `.ox-frame-title-label` se convierte en inline-flex para alinear etiqueta, contador `(n)`, acción de nuevo elemento y totales. El contador/totales se quedan sin clase (no se toca Java).
- **Chevron como toggle tipo Notion/Linear**: `.ox-frame-actions` ya no flota, usa `order: -1` para quedar a la izquierda de la etiqueta. El enlace es un botón icono compacto: icono 20px (era 200% con `margin: -5px`), color muted `--frame-toggle-color` (`--action-color`), hover con pill neutra 8% (`--frame-toggle-hover-background`, mismo tinte que el hover del botón cerrar de diálogos) y color acento (`--frame-toggle-hover-color` = `--action-hover-color`), anillo de foco 2px `--focus-ring-color` en `:focus-visible`. Se mantienen `mdi-menu-down/right` y el mecanismo hide/show de `FrameActionsRenderer` + `openxava.initFrames` (intocable sin tocar JS).
- **Iconos en título**: `.ox-frame-title .ox-image-link i` pierde los márgenes negativos (-8px) y queda a 20px con 2px de padding, alineado por flex. Aplica al "+" de nuevo elemento en colecciones y a las acciones de referencia en cabecera.
- **Peso de título 600** (era bold), coherente con cabeceras de lista e inter de la UI.
- **Padding inferior del marco** de `--space-2` a `--space-3` (12px) para respirar un poco más; el horizontal sigue en `--space-4` para no romper los márgenes negativos de secciones (`-space-4`) ni el contenido de colecciones (`-space-3`).
- **No se toca**: el layout float de `.ox-frame` entre sí (sistema de anchos `ox-full/half-frame`), los toggle por JS (no se hace rotación animada porque el estado se cambia intercambiando dos spans con `ox-display-none`), ni las reglas `.firefox .ox-frame` (la clase `firefox` aún se emite desde `XavaStyle`; no es CSS muerto).

### Tokens CSS

**Nuevos:** `--frame-nested-background`, `--frame-toggle-color`, `--frame-toggle-hover-color`, `--frame-toggle-hover-background`

**Eliminados:** ninguno. Se eliminan las reglas hack `.ox-frame-title a { padding }`, los márgenes negativos de `.ox-frame-title .ox-image-link i` y el `font-size: 200%` de `.ox-frame-actions i`.

---

## Archivos modificados

| Archivo | Cambio |
|---|---|
| `base.css` | Tokens + rework completo de `.ox-section`, `.ox-section-tab`, `.ox-section-link`, nueva `.ox-section-count`; tokens + rework de `.ox-frame`, `.ox-frame-title`, `.ox-frame-title-label`, `.ox-frame-actions` y marcos anidados |
| `SectionsRenderer.java` | Clase `ox-section-count` en los spans del contador de colección |
| `changelog.txt` | 2 entradas nuevas (secciones) + 2 entradas nuevas (marcos) |

## Estado

**Secciones:** concluidas. Tests pasados y revisión visual realizada en los 3 temas (Auto/Light/Dark), diálogos con secciones y modo phone.

**Marcos:** concluidos. Revisión visual aprobada por el usuario.

**Pendiente en el bloque:** acciones en vista.
