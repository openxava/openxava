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

## Archivos modificados

| Archivo | Cambio |
|---|---|
| `base.css` | Tokens + rework completo de `.ox-section`, `.ox-section-tab`, `.ox-section-link`, nueva `.ox-section-count` |
| `SectionsRenderer.java` | Clase `ox-section-count` en los spans del contador de colección |
| `changelog.txt` | 2 entradas nuevas |

## Estado

**Secciones:** concluidas. Tests pasados y revisión visual realizada en los 3 temas (Auto/Light/Dark), diálogos con secciones y modo phone.

**Pendiente en el bloque:** marcos y acciones en vista.
