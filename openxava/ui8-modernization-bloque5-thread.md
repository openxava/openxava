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

---

## Tokens CSS

**Nuevos:** `--segmented-background`, `--segmented-active-background`, `--segmented-active-color`

**Defaults cambiados:**
- `--button-bar-background`: `var(--module-header-background)` (era my-lightgray / my-lightdark por tema)
- `--button-bar-button-hover-border`: `transparent` (era white)
- `--highlight-bar-action-background/color/hover-*`: acento (era action-color gris/negro)
- `--subcontroller-background`: `--background`; `--subcontroller-select-background`: `--action-hover-background`
- `--module-header-selected-background`: `color-mix(... 6%)` (desacoplado de la barra; las pestañas se modernizan en su propio item de este bloque)

**Eliminados:** `--button-bar-shadow`

---

## Archivos modificados

| Archivo | Cambio |
|---|---|
| `base.css` | Tokens + rework completo de `.ox-button-bar`, botones, list-formats, subcontrolador, ayuda/suscripción |
| `light.css` | Eliminado `--button-bar-background` (centralizado en base.css) |
| `dark-overrides.css` | Eliminados `--button-bar-background` y `--button-bar-shadow` |
| `changelog.txt` | 4 entradas nuevas |

## Estado

Implementación concluida. **Pendiente:** revisión visual en los 3 temas (Auto/Light/Dark) y modo phone + resto del bloque (botones inferiores, pestañas de módulos) antes de pasar los tests y fusionar.
