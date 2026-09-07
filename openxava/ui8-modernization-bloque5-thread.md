# Bloque 5 — Botones y navegación: Hilo de trabajo

## Objetivo

Modernizar la barra de botones superior y el selector de formato de lista (estilo Attio/Linear/Notion), reutilizando los tokens del sistema de diseño. Solo CSS + tokens; sin tocar renderers Java ni markup.

---

## Decisiones de diseño

### Barra de botones (`.ox-button-bar`)
- Layout con flexbox (`justify-content: space-between`), sin floats; grupos internos `inline-flex` con `gap: var(--space-1)`.
- Fondo = `--button-bar-background` (ahora = `--module-header-background`: blanco en light, my-dark en dark) + hairline inferior (`1px var(--frame-border)`) en vez de `box-shadow`. Continuidad con la fila de pestañas, separación con la lista.
- Botones ghost pill: `border-radius: var(--radius-md)`, `font-weight: 500` (era bold), padding `6px var(--space-3)`, icono + label con `gap: var(--space-2)` (eliminados los micro-ajustes legacy: `margin-left: -2px`, `padding: 4px` en label, `padding-left: 7px` en img).
- Hover: `--action-hover-background` (--accent-soft) + `--action-hover-color` (acento); transición estándar; `:focus-visible` con anillo de 3px `--focus-ring-color` (antes no había ring en la barra).
- "Borrar": hover rojo (`--delete-button-hover-color`), patrón Linear.
- Acción primaria ("Nuevo" en modo lista): rellena con `--accent-color` y `--accent-contrast-color`, hover `--accent-color-hover`. Es la única isla de color sólido de la barra.

### Selector de formato de lista (segmented control)
- `.ox-list-formats` como segmented pill: contenedor con fondo `--segmented-background` (6% del color de texto), padding 2px; cada formato una pill de 32x28px con `--radius-sm`; activo con `--segmented-active-background` + `--elevation-1`.
- Iconos de 18px (antes 200%), color muted `--list-formats-color`.
- Eliminado el subrayado de 3px del formato activo.
- Nuevos tokens `--segmented-background/-active-background/-active-color`; los `--calendar-view-switcher-*` (Bloque 3b) ahora se derivan de ellos.

### Grupo derecho (ayuda, suscripción e-mail)
- Botones cuadrados uniformes 32x32 con `--radius-md` y hover pill; iconos 18px.

### Subcontrolador ("Mis informes")
- Botón = pill ghost como el resto; chevron alineado.
- Dropdown: separación de 4px del botón (antes 14px), items como menú moderno (flex, `--radius-sm`, accent-soft en hover), fondo `--background`. Mantiene `--radius-md`/`--elevation-2`/-border del autocomplete (Bloque 2d).
- Botón abierto (`ox-subcontroller-select`): fondo `--action-hover-background`.

### Botones inferiores (`.ox-bottom-buttons`, detalle y diálogos)
- Mismo componente para detalle y diálogos: comparten diseño y solo difieren en espaciado (las reglas `.ui-dialog` existentes).
- Botones: radio `--radius-md` (era pill `--radius-full`), peso 500 (era bold), padding `9px 16px` (~38px de alto, igualando los campos del Bloque 2b), anillo de foco de 3px como la barra.
- Acción primaria (por defecto, ENTER): relleno plano `--accent-color` + texto `--accent-contrast-color`, hover plano `--accent-color-hover` (eliminado el gradiente de oscurecimiento y la sombra en hover). Tokens `--default-action-button-*` centralizados en `base.css` con el acento, válidos también para dark (antes dark usaba `--my-blue`).
- Acciones secundarias: ghost `--action-color` con hover `--accent-soft` + acento (mismo patrón que la barra superior); la acción de borrado sigue con hover rojo.
- Layout flex con `flex-wrap` y `gap: var(--space-2)` en lugar de márgenes; margen superior e izquierdo/derecho `var(--space-3) var(--space-4) 0` para alinear con los marcos/campos de `.ox-detail`. Para diálogos, welcome y sign-in el margen se neutraliza (`margin: 0`) para respetar sus propios contenedores.
- Eliminados los tokens muertos `--default-action-button-shadow` y `--default-action-button-hover-shadow`.

---

## Tokens CSS

**Nuevos:** `--segmented-background`, `--segmented-active-background`, `--segmented-active-color`

**Defaults cambiados:**
- `--button-bar-background`: `var(--module-header-background)` (era my-lightgray / my-lightdark por tema)
- `--button-bar-button-hover-border`: `transparent` (era white)
- `--highlight-bar-action-background/color/hover-*`: acento (era action-color gris/negro)
- `--subcontroller-background`: `--background`; `--subcontroller-select-background`: `--action-hover-background`
- `--module-header-selected-background`: `color-mix(... 6%)` (desacoplado de la barra; las pestañas se modernizan en su propio item de este bloque)
- `--default-action-button-background/-color/-hover-*`: `var(--accent-color)` + `var(--accent-contrast-color)` + `var(--accent-color-hover)` plano, centralizados en base.css (antes: fondo por tema — acento en light / `--my-blue` en dark —, color `--background`, hover con gradiente de oscurecimiento)

**Eliminados:** `--button-bar-shadow`, `--default-action-button-shadow`, `--default-action-button-hover-shadow`

---

## Archivos modificados

| Archivo | Cambio |
|---|---|
| `base.css` | Tokens + rework completo de `.ox-button-bar`, botones, list-formats, subcontrolador, ayuda/suscripción + rework de `.ox-bottom-buttons` |
| `light.css` | Eliminados `--button-bar-background` y `--default-action-button-*` (centralizados en base.css) |
| `dark-overrides.css` | Eliminados `--button-bar-background`, `--button-bar-shadow` y `--default-action-button-*` |
| `changelog.txt` | 6 entradas nuevas |

## Estado

Implementación concluida y revisión visual realizada:

- **Barra de botones superior**: terminada y probada en los 3 temas.
- **Selector de formato de lista**: terminado y probado en los 3 temas.
- **Subcontroladores**: alineación de iconos/imágenes/etiquetas corregida y verificada (Invoice → InvoicePrint).

**Pendiente:** resto del bloque (pestañas de módulos) y revisión visual de los botones inferiores en los 3 temas y modo phone antes de pasar los tests y fusionar.
