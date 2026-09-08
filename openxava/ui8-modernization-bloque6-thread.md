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

## Acciones en vista (inline y referencias)

### Contexto y problema

Las acciones en vista son los enlaces y botones que aparecen dentro de la vista de detalle, mezclados con los datos: acciones inline de texto (p. ej. "Prefix street") y acciones con icono junto a las referencias (buscar, crear, modificar, borrar). El problema era doble:

1. **Acciones inline de texto**: en el diseño anterior eran azul con subrayado — claramente pulsables. Tras la modernización perdieron el subrayado y en dark mode tenían el mismo color que las etiquetas, haciéndose indistinguibles.
2. **Iconos de referencia**: la lupa de búsqueda tenía un círculo blanco sólido agresivo (estilo 2000s), mientras crear/modificar/borrar eran iconos planos sin fondo. Inconsistencia visual y baja descubribilidad de los secundarios.

### Investigación de patrones

- **Apps modernas (Attio, Linear, Notion)**: no tienen iconos separados junto a las referencias. El campo mismo es un combobox typeahead. No aplicable a OpenXava porque maneja miles de registros y necesita diálogo de búsqueda avanzada.
- **Apps LOB (SAP Fiori, Salesforce, Dynamics 365)**: sí tienen un icono de búsqueda enfatizado junto al campo lookup. El patrón LOB confirma que enfatizar la lupa es correcto — fue validado con pruebas de usabilidad reales en OpenXava 6.0.

### Decisiones de diseño

#### Acciones inline de texto (`.ox-action-link`)

- **Light**: color `--action-color` (gris muted), peso 500, sin bold. Se distingue de las etiquetas por el tono y el peso. Hover con pill suave `--action-link-hover-background` (accent-soft), sin subrayado, color `--action-hover-color`.
- **Dark**: color `--action-link-color` (acento `--my-lightblue`) para diferenciar de las etiquetas, que en dark comparten el mismo gris. Hover igual que light: pill suave, sin subrayado.
- **Focus**: `:focus-visible` con outline 2px `color-mix(accent 40%, transparent)`, offset 1px.
- **Coherencia**: el hover se extiende a `.ui-widget-content` (diálogos) y `.xava_view_content` para comportamiento uniforme en todos los contextos.

#### Iconos de referencia (`.ox-image-link` + `.mdi-magnify`)

- **Lupa (buscar) — acción primaria**: soft accent fill (`color-mix(accent 15%, transparent)`) en reposo, icono en `--accent-color`, `--radius-sm` (4px). En hover, el fill sube a 25%. Sigue siendo visualmente distinta de los otros tres (tiene fondo, tiene color de acento), pero ya no es un círculo blanco agresivo — es un "soft badge" moderno.
- **Crear, modificar, borrar — acciones secundarias**: ghost planos sin fondo, color `--action-color`. Hover con pill suave `--action-link-hover-background` (sin cambios, ya funcionaba bien).
- **Jerarquía visual**: la lupa es la primaria (con fondo), los otros tres son secundarias (ghost). Todos comparten `--radius-sm` y transiciones — se ven como un conjunto coherente.
- **Bug del "huevo"**: al hacer hover sobre la lupa, el pill del enlace (`.ox-image-link:hover` con `--action-link-hover-background`) asomaba por los lados del badge de la lupa, creando una sombra fea. Solución: `.ox-image-link:has(i.mdi-magnify):hover { background: transparent }` — elimina el fondo del enlace solo para la lupa, donde el cambio de color del badge ya es feedback suficiente.

### Tokens CSS

**Cambiados en `base.css`:**
- `--reference-search-icon-background`: `color-mix(in srgb, var(--accent-color) 15%, transparent)` (era `var(--action-color)`)
- `--reference-search-icon-hover-background`: `color-mix(in srgb, var(--accent-color) 25%, transparent)` (era `var(--default-action-button-hover-background)`)
- `--reference-search-icon-color`: `var(--accent-color)` (era `white`)
- `--reference-search-icon-hover-color`: `var(--accent-color)` (era `white`)

**Eliminados en `dark-overrides.css`:**
- `--reference-search-icon-color: var(--my-dark)` — ya no necesita override porque el valor base (`var(--accent-color)`) funciona en ambos temas.

### Reglas CSS clave

```css
/* base.css — acciones inline */
.ox-action-link, .ui-widget-content .ox-action-link {
    margin-left: 6px; color: var(--action-color); font-weight: 500;
    font-size: 13px; line-height: 1.4; padding: 4px 8px; letter-spacing: 0.01em;
}
.ox-action-link, .ox-image-link {
    border-radius: var(--radius-md);
    transition: color var(--transition-fast), background-color var(--transition-fast);
}
.ox-action-link:focus-visible, .ox-image-link:focus-visible {
    outline: 2px solid color-mix(in srgb, var(--accent-color) 40%, transparent);
    outline-offset: 1px;
}
.ox-action-link:hover, .ox-image-link:hover, /* ... */
.ui-widget-content .ox-action-link:hover, .ui-widget-content .ox-image-link:hover,
.xava_view_content .ox-action-link:hover, .xava_view_content .ox-image-link:hover {
    background: var(--action-link-hover-background);
    text-decoration: none; color: var(--action-hover-color);
}

/* base.css — lupa de referencia */
.ox-image-link i.mdi-magnify {
    color: var(--reference-search-icon-color);
    background: var(--reference-search-icon-background);
    border-radius: var(--radius-sm); padding: 4px;
    margin-left: 5px; margin-right: 5px;
}
.ox-image-link:hover i.mdi-magnify {
    background: var(--reference-search-icon-hover-background);
    color: var(--reference-search-icon-hover-color);
}
.ox-image-link:has(i.mdi-magnify):hover { background: transparent; }

/* dark-overrides.css — acciones inline en dark */
.ox-action-link { color: var(--action-link-color); }
.ox-action-link:hover { text-decoration: none; color: var(--action-hover-color); }
```

---

## Archivos modificados

| Archivo | Cambio |
|---|---|
| `base.css` | Tokens + rework completo de `.ox-section`, `.ox-section-tab`, `.ox-section-link`, nueva `.ox-section-count`; tokens + rework de `.ox-frame`, `.ox-frame-title`, `.ox-frame-title-label`, `.ox-frame-actions` y marcos anidados; modernización de `.ox-action-link` (tipografía apagada, pill hover, focus-visible); modernización de `.mdi-magnify` (soft accent fill, radius-sm); fix del "huevo" con `:has()` |
| `dark-overrides.css` | `.ox-action-link` con color de acento en dark; eliminado `text-decoration: underline` del hover; eliminado override de `--reference-search-icon-color` |
| `SectionsRenderer.java` | Clase `ox-section-count` en los spans del contador de colección |
| `changelog.txt` | 2 entradas (secciones) + 2 entradas (marcos) |

## Estado

**Secciones:** concluidas. Tests pasados y revisión visual realizada en los 3 temas (Auto/Light/Dark), diálogos con secciones y modo phone.

**Marcos:** concluidos. Revisión visual aprobada por el usuario.

**Acciones en vista:** concluidas. Revisión visual aprobada por el usuario en los 3 temas (Auto/Light/Dark), dentro y fuera de marcos.

**Bloque 6 completo.**
