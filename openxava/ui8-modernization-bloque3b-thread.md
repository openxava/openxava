# Bloque 3b — Calendario: Hilo de trabajo

## Objetivo

Modernizar el aspecto del calendario del formato lista *Calendario* (FullCalendar v6.1.5, bundle que inyecta sus propios estilos CSS) y, en especial, de los eventos, siguiendo la línea visual de Attio/Notion/Linear y los tokens del sistema de diseño de OpenXava 8.

---

## Arquitectura del cambio

FullCalendar 6 define todos los colores vía variables CSS propias (`--fc-*` en `:root`) y aplica colores inline solo cuando se configura `eventColor`/`eventBackgroundColor`. Por tanto:

1. **`calendarEditor.js`**: se elimina `eventColor: 'var(--color)'`. Sin él, FullCalendar no aplica estilos inline a los eventos y todo el color se controla por CSS.
2. **`calendarEditor.css`**: se mapean las variables `--fc-*` a los tokens propios en el scope `#xava_calendar`:
   - `--fc-border-color` → `--calendar-cell-border-color`
   - `--fc-today-bg-color` → `--calendar-today-background`
   - `--fc-neutral-text-color` → `--calendar-muted-color`
   - `--fc-event-bg-color` → `--calendar-event-background`
   - `--fc-event-border-color` → `--calendar-event-accent` (Lo usa tanto el punto de los eventos de mes como los eventos de bloque)
   - `--fc-event-text-color` → `--calendar-event-color`
   - `--fc-now-indicator-color` → `--accent-color`

Los estilos inyectados por FullCalendar se insertan al principio de `<head>`, por lo que `calendarEditor.css` gana en igualdad de especificidad; para los bordes de radio dentro de `.fc-button-group` (FullCalendar usa selectores más específicos) se usa `.fc .fc-button-group > .fc-button.fc-button` (doble clase).

---

## Decisiones de diseño

### Toolbar
- Título (`fc-toolbar-title`): `--font-size-xl`, peso 600, letter-spacing -0.01em.
- Botones: transparentes, radius-sm, 30px de alto, hover con `--calendar-button-*`, focus-visible con `--focus-ring-color`. Se elimina el `box-shadow: none !important` global para poder dar focus ring.
- Grupos de botones (flechas de navegación y selector Mes/Semana/Día) como contenedor segmentado: fondo `--calendar-view-switcher-background`, padding 2px, botones radius-sm; el botón activo con fondo `--calendar-view-switcher-active-background` + `--elevation-1`.

### Rejilla
- Bordes hairline con `--frame-border` (antes `--action-hover-background`, y blanco en dark).
- Cabeceras de día: uppercase, `--font-size-xs`, peso 600, letter-spacing 0.07em, color `--calendar-header-color`.
- Números de día: `--font-size-sm`, peso 500, color `--calendar-muted-color`.
- Hoy: tinte de fondo muy sutil (`--calendar-today-background`, color-mix de acento al 7%) + chip circular de acento en el número (26px, `--accent-color` / `--accent-contrast-color`).
- Hover de día: `--calendar-day-hover-background` (acento al 5%).
- Afordancia "+": el icono `mdi-plus-circle` deja de estar siempre visible; aparece solo al hacer hover sobre la celda del día, en color acento con transición. (click en el día = crear evento, comportamiento existente).

### Eventos
- **Vista mes** (dot events): chip con fondo `--calendar-event-background` (`--accent-soft`), radius-md, punto de acento, hora en `--font-size-xs` 600 muted (`--calendar-event-time-color`), título `sm` 500. Hover con `--calendar-event-hover-background`.
- **Vistas semana/día y eventos de varios días** (`.fc-v-event`/`.fc-h-event`): fondo suave de acento con barra izquierda de 3px en `--accent-color` (estilo Linear/Notion), radius-sm.
- Etiquetas de hora del timegrid y axis en muted xs; indicador "ahora" en color acento.

### Popover y tooltip
- Popover "+n más": radius-lg, borde hairline, `--elevation-3`, cabecera con peso 600.
- Tooltip de evento (`.fc-event-tooltip`): reescrito usando los tokens de tooltip (`--tooltip-background`/`--tooltip-color`), radius-md, `--elevation-2`, sin flecha negra hardcodeada, `pointer-events: none` y ellipsis para títulos largos.

---

## Tokens CSS

Nuevos: `--calendar-header-color`, `--calendar-muted-color`, `--calendar-view-switcher-background`, `--calendar-view-switcher-active-background`, `--calendar-view-switcher-active-color`, `--calendar-event-accent`, `--calendar-event-hover-background`, `--calendar-event-color`, `--calendar-event-time-color`.

Redefinidos (default): `--calendar-cell-border-color` (ahora `--frame-border`), `--calendar-day-hover-background`, `--calendar-today-background`, `--calendar-event-background` (ahora `--accent-soft`), `--calendar-popup-background` (ahora `--background`; antes apuntaba a `--fc-natural-bg-color`, inexistente — era un typo de `--fc-neutral-bg-color`).

Eliminado: `--calendar-event-time-background` (sin referencias). Documentado en migration_en/es.html.

`--calendar-selected-day` se mantiene intacto: lo usa el popup de fecha (flatpickr), no el calendario de lista.

En `dark-overrides.css` se reemplazan los viejos overrides (`hsla` grises, fondo `--my-dark`) por color-mix proporcionales al acento oscuro.

---

## CSS muerto eliminado

- `.hoverEffect` (sin uso en el código).
- `.fc-event-main { background-color: ... }` y `.fc-button { box-shadow: none !important; }` (sustituidos por el nuevo esquema).

---

## Tests

No requieren cambios: `CalendarTest` usa clases DOM de FullCalendar (`fc-toolbar-title`, `fc-icon-chevron-right`, `fc-event`, `fc-daygrid-day-number`, `fc-daygrid-dot-event`, etc.) e IDs (`xava_calendar_date_preferences`), que no se han tocado. Solo cambian CSS y un option del JS.

## Archivos modificados

| Archivo | Cambio |
|---|---|
| `calendarEditor.css` | Reescrito: tokens `--fc-*` + diseño moderno |
| `calendarEditor.js` | Eliminado `eventColor: 'var(--color)'` |
| `base.css` | Bloque de tokens de calendario consolidado y redefinido |
| `dark-overrides.css` | Overrides de calendario para el nuevo set de tokens |
| `changelog.txt` | 2 entradas |
| `migration_en.html` / `migration_es.html` | Sección "Calendar list format restyled" |
| `ui8-modernization-plan.md` | Bloque 3b detallado |

**Estado**: Concluido. Tests pasados, estética revisada en los 3 temas (Auto/Light/Dark) y modo phone.
