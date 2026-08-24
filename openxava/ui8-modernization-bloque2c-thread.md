# UI8 Modernization - Bloque 2c: Popups

## Objetivo
Adaptar el popup calendar de fecha (flatpickr) al nuevo sistema de diseño de OpenXava 8.0, usando los tokens definidos en `base.css` (`--radius-*`, `--elevation-*`, `--accent-color`, `--font-size-*`, `--transition-*`), sin tocar el CSS vendor ni cambiar la interacción. El segundo ítem del bloque (snackbar/toast para mensajes de éxito) aún no se ha empezado.

## Decisión de diseño: no Material Design 3

### Discusión inicial
El plan original decía "Adapt popup calendar to Material Design 3", copiado del tracker de OpenXava. Se debatió si el estilo actual al que se está migrando es exactamente MD3.

### Acuerdo
No se adapta a MD3 literal. Se adapta al **sistema de diseño propio de OpenXava 8.0** (tokens en `base.css`), buscando coherencia con el resto de la UI modernizada en los bloques 1, 2a y 2b. No se cambia la estética ni la interacción del calendario, solo se ajustan detalles (redondeo, colores, transiciones, tipografía) para que sea coherente.

### Item del plan reformulado
`ui8-modernization-plan.md` líneas 71-81 — cambió de "Adapt popup calendar to Material Design 3" a "Adapt popup calendar (flatpickr) to the new design system. Overrides con tokens en `base.css`, sin tocar el CSS vendor ni cambiar la interacción".

## Cambios realizados (pendientes de revisión visual)

### 1. Tokens nuevos en `base.css`
**Archivo**: `src/main/resources/META-INF/resources/xava/style/base.css` (líneas 300-303)

```css
--date-popup-background: white;
--date-popup-hover-background: var(--accent-soft);
--date-popup-hover-color: var(--accent-color);
--date-popup-muted-color: color-mix(in srgb, var(--color) 45%, transparent);
```

Estos tokens se añaden junto a los tokens de calendario existentes (`--calendar-selected-day`, `--calendar-popup-background`, etc.).

### 2. Overrides completos de flatpickr en `base.css`
**Archivo**: `src/main/resources/META-INF/resources/xava/style/base.css` (líneas 910-1084)

Se sustituyeron los 2 overrides mínimos existentes (`.flatpickr-day.selected` y `.flatpickr-calendar.open`) por un bloque completo. **Se usa `!important`** porque `flatpickr.css` (CSS vendor) carga después que `base.css` en el HTML (confirmado en `ModulePageRenderer.java`: los CSS de editores se inyectan después del CSS de tema).

Detalle de los overrides:

- **Popup**: fondo `--date-popup-background`, borde `--input-border`, `--radius-lg`, `--elevation-2`, fuente Inter y `--font-size-md`. Flecha (caret) recoloreada para coincidir con borde y fondo.
- **Cabecera mes/año**: `--font-size-lg`, peso 600, color `--color`. Dropdown de meses con `appearance: none`, chevron propio (`--select-chevron-icon` del Bloque 2b), hover con `--accent-soft`. Flechas de navegación (prev/next month) con hover de acento (antes rojo del tema vendor).
- **Días de la semana**: `--date-popup-muted-color`, `--font-size-xs`, peso 600.
- **Días**: `tabular-nums`, transición 150ms en hover. Días de otros meses atenuados (`--date-popup-muted-color`). **Hoy** con borde y color de `--accent-color`, peso 600. **Seleccionado** con `--calendar-selected-day` + `--accent-contrast-color` + peso 600. Hover con `--accent-soft` y `--date-popup-hover-color`.
- **Fila de hora**: separador con `--input-border`, inputs con `tabular-nums`, hover suave con `--accent-soft`. AM/PM con hover coherente.

### 3. Override para tema oscuro en `dark-overrides.css`
**Archivo**: `src/main/resources/META-INF/resources/xava/style/dark-overrides.css` (línea 78)

```css
--date-popup-background: var(--my-lightdark);
```

Sin esto, el popup sería blanco también en tema oscuro. `--calendar-selected-day` ya estaba sobreescrito a `var(--my-blue)` en la línea 77.

### 4. Changelog
**Archivo**: `changelog.txt` (líneas 4-5)

```
- Date calendar popup (flatpickr) adapted to the new design system: rounded corners, elevation, accent color for selected day and today indicator, hover transitions, custom chevron in month dropdown and full dark theme support.
- New CSS variables for date popup customization: --date-popup-background, --date-popup-hover-background, --date-popup-hover-color, --date-popup-muted-color.
```

## Archivos modificados en esta sesión

| Archivo | Cambios |
|---------|---------|
| `src/main/resources/META-INF/resources/xava/style/base.css` | 4 tokens nuevos (líneas 300-303) + bloque completo de overrides flatpickr (líneas 910-1084) |
| `src/main/resources/META-INF/resources/xava/style/dark-overrides.css` | `--date-popup-background: var(--my-lightdark)` (línea 78) |
| `changelog.txt` | 2 entradas nuevas al inicio (líneas 4-5) |
| `ui8-modernization-plan.md` | Item del Bloque 2c reformulado (líneas 71-81) |

## Archivos NO modificados (importante)

- `src/main/resources/META-INF/resources/xava/editors/style/flatpickr.css` — CSS vendor, no se toca.
- `src/main/resources/META-INF/resources/xava/editors/js/dateCalendarEditor.js` — lógica JS, no se toca.
- `src/main/resources/META-INF/resources/xava/style/light.css` — no necesita overrides; los tokens por defecto en `base.css` ya funcionan para tema claro.

## Pendiente de verificación manual

- **Revisión visual del popup** en un campo fecha y uno fecha-hora, en los 3 temas (Auto/Light/Dark) y modo phone.
- **Pasar `DateCalendarTest.txt`** — test manual referenciado en `dateCalendarEditor.js` (cabecera `// WARNING: IF YOU CHANGE THIS PASS DateCalendarTest.txt`). Aunque el cambio es solo CSS, por convención conviene pasarlo.
- **Marcar el item como `[x]`** en `ui8-modernization-plan.md` tras la verificación.

## Segundo ítem del Bloque 2c (no empezado)

- **Mensajes de éxito como snackbar/toast con auto-cierre** (errores siguen persistentes). Plan línea 82.
- No se ha investigado nada aún. Cuando se empiece, buscar dónde se renderizan los mensajes de éxito (`--messages-background: #6fc664` en `base.css` línea 305) y cómo se muestran actualmente.

## Notas
- La versión de OpenXava es 8.0.
- El usuario prefiere testing manual desde el IDE.
- El usuario hizo un push con los cambios de esta sesión antes de cambiar de máquina.
- Los cambios son solo CSS; no hay cambios en Java ni JS.
