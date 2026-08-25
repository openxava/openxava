# UI8 Modernization - Bloque 2c: Popups

## Objetivo
Adaptar el popup calendar de fecha (flatpickr) al nuevo sistema de diseño de OpenXava 8.0, usando los tokens definidos en `base.css` (`--radius-*`, `--elevation-*`, `--accent-color`, `--font-size-*`, `--transition-*`), sin tocar el CSS vendor ni cambiar la interacción. El segundo ítem del bloque (snackbar/toast para mensajes de éxito) está implementado y verificado.

## Decisión de diseño: no Material Design 3

### Discusión inicial
El plan original decía "Adapt popup calendar to Material Design 3", copiado del tracker de OpenXava. Se debatió si el estilo actual al que se está migrando es exactamente MD3.

### Acuerdo
No se adapta a MD3 literal. Se adapta al **sistema de diseño propio de OpenXava 8.0** (tokens en `base.css`), buscando coherencia con el resto de la UI modernizada en los bloques 1, 2a y 2b. No se cambia la estética ni la interacción del calendario, solo se ajustan detalles (redondeo, colores, transiciones, tipografía) para que sea coherente.

### Item del plan reformulado
`ui8-modernization-plan.md` líneas 71-81 — cambió de "Adapt popup calendar to Material Design 3" a "Adapt popup calendar (flatpickr) to the new design system. Overrides con tokens en `base.css`, sin tocar el CSS vendor ni cambiar la interacción".

## Cambios realizados

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

## Verificación

- **Revisión visual del popup** en campos fecha y fecha-hora, en los 3 temas (Auto/Light/Dark) y modo phone: **OK**.
- **`DateCalendarTest`** (incluyendo `testDateTime_onChange_twoDigitYear_dateTimeSeparated_srDateTime`) **pasa en verde** tras ajustar un click tapado por el popup.
- **Primer punto del Bloque 2c marcado como `[x]`** en `ui8-modernization-plan.md`.

## Segundo ítem del Bloque 2c (implementado y verificado)

- **Mensajes de éxito como snackbar/toast con auto-cierre** (errores siguen persistentes). Plan línea 82.

### Cambios realizados

**`src/main/resources/META-INF/resources/xava/js/openxava.js`**:
- Nueva función `openxava.scheduleMessagesAutoClose(messagesDiv)` (líneas 361-381): programa el `fadeOut` del contenedor de mensajes tras `openxava.messagesAutoCloseDelay` (nueva propiedad, 5000ms por defecto). Pausa la cuenta atrás al hacer hover (mouseenter cancela, mouseleave reprograma). Si había un timeout previo en el mismo contenedor, lo cancela (vía `$messages.data('autoCloseTimeout')`).
- `initMessages` (líneas 350-357): además del handler de cierre manual, escanea `div[id$="__messages"]:visible` con tabla dentro y programa auto-cierre. Cubre la carga de página completa (mensajes renderizados por servidor con `MessagesRenderer`).
- `showNotification` (línea 543): si `type === "messages"`, programa auto-cierre. Cubre `showMessage()` usado por `listEditor` (mensaje con enlace undo) y otros clientes JS. Los errores (`type === "errors"`) no se auto-cierran.
- `showMessages` (líneas 555-562): tras `effectShow` de mensajes, programa auto-cierre. Cubre las respuestas AJAX. Nota: `initMessages` no lo cubre aquí porque `effectShow` oculta el contenedor (`hide()` + `fadeIn()` asíncrono) y el filtro `:visible` fallaría.

**`src/main/resources/META-INF/resources/xava/style/base.css`** (líneas 2290-2297):
- Animación de entrada `ox-message-in` (fade + slide-down 8px, `--transition-normal`) en `.ox-messages-wrapper .ox-message-box`, coherente con `xava-loading-in` del Bloque 1. Solo mensajes, errores intactos.

**`changelog.txt`** (línea 4): entrada nueva.

### Decisiones
- Se auto-cierran mensajes de éxito, warnings e infos (conviven en el mismo contenedor `.ox-messages-wrapper` renderizado por `MessagesRenderer`); solo los errores (`ox-errors-wrapper`) siguen persistentes.
- No se cambia la posición (top, junto al centro) ni la estética pill existente (ya modernizada con `--radius-lg` y `--elevation-2`).
- El mensaje de undo de `listEditor` también se auto-cierra a los 5s; el hover lo pausa.

### Verificación
- **Tests de UI**: añadido `MessagesTest#testMessagesAutoClose` en `openxavatest/src/test/java/org/openxava/test/tests/byfeature/MessagesTest.java`. Verifica:
  - Mensajes de éxito se auto-cierran tras `openxava.messagesAutoCloseDelay` mientras errores permanecen visibles.
  - Hover pausa el auto-cierre (`mouseenter`/`mouseleave`).
  - Se usó Selenium porque `ModuleTestBase.execute()` en HtmlUnit ejecuta `waitForBackgroundJavaScriptStartingBefore(12000)`, que consume el `setTimeout` de auto-cierre y hace imposible observar el estado intermedio. Selenium no tiene ese bloqueo y permite esperar el tiempo necesario con `Thread.sleep`.
  - Notas técnicas: `fadeIn()`/`fadeOut()` de jQuery tardan 400ms, por lo que el test usa `openxava.messagesAutoCloseDelay = 1500` y esperas de 600ms (fadeIn) / 2000ms (auto-close + fadeOut) para estabilizar las animaciones.
- **Revisión visual**: guardar entidad (mensaje verde), provocar error (rojo persistente), undo en lista editable, modo phone, 3 temas. OK.
- **Ítem marcado `[x]`** en `ui8-modernization-plan.md` línea 82.

## Rediseño visual de los mensajes (sesión posterior)

El usuario revisó el pantallazo `ui8-screenshots/messages-light-bloque2b.png` y pidió modernizar el aspecto de los mensajes (tipo de letra, color, forma y posición), que seguía siendo el original: barras de color sólido con texto blanco en negrita, divididas en dos pilas convergiendo al centro superior que tapaban la barra de herramientas.

### Cambios realizados

**`src/main/resources/META-INF/resources/xava/style/base.css`**:
- Tokens: eliminados `--message-box-color`, `--message-box-a-color`, `--message-box-close-hover-color`, `--message-box-shadow`, `--message-box-close-color` y los fondos sólidos `#6fc664`/`#DC4A38`/`#ffb347`/`#a1a4a5`. Nuevos acentos semánticos `--messages-accent` (#16a34a), `--errors-accent` (#dc2626), `--warnings-accent` (#d97706), `--infos-accent` (#64748b), de los que se derivan con `color-mix` fondo (tinte 12-14% sobre `--background`), borde (30-40%) y color de texto (mezcla con `--color`), por lo que se adaptan solos a light/dark. `--customize-controls-color` ahora apunta a `--messages-accent`.
- Posición: nueva clase `.ox-notifications` (fixed, top-right, `z-index 9999999`, `max-width: min(440px, 100vw - 32px)`, `pointer-events: none` con `pointer-events: auto` en las cajas para no bloquear clicks en la cabecera). Errores y mensajes forman una única pila. Eliminado el posicionamiento `right: 50%` / `left: 50%` de `.ox-messages-wrapper` / `.ox-errors-wrapper`.
- Caja: flex con gap, peso 500 (antes bold), `--radius-md`, borde 1px semántico, fondo soft, `--elevation-2`. Icono de cierre con opacidad 0.6→1 en hover (antes `float: right` y gris claro). Enlaces heredan el color semántico.

**`src/main/java/org/openxava/web/render/CoreRenderer.java`**: los divs `__errors` y `__messages` se envuelven en `<div class='ox-notifications'>` (líneas 59-67).

**`MessagesRenderer.java` / `ErrorsRenderer.java`**: icono de severidad (`ox-message-icon`) tras el icono de cierre (que sigue primero en el DOM): `mdi-alert-outline` (warning), `mdi-check-circle-outline` (éxito), `mdi-information-outline` (info), `mdi-alert-circle-outline` (error).

**`openxava.js`**:
- `showNotification`: añade el icono de severidad al HTML generado (check-circle para messages, alert-circle para errors).
- `initMessages`: el handler de cierre pasa de `$('.ox-message-box i')` a `$('.ox-message-box i.mdi-close')` para que el icono de severidad no cierre el mensaje.

**`dark-overrides.css`**: acentos más claros para fondo oscuro (`--messages-accent: #4ade80`, `--errors-accent: #f87171`, `--warnings-accent: #fbbf24`, `--infos-accent: #94a3b8`).

**`changelog.txt`**: 2 entradas nuevas al inicio (líneas 4-5).

### Decisiones
- Se mantiene el icono de cierre como primer `<i>` en el DOM para no romper tests que hacen click en el primer icono del mensaje.
- En `#sign_in_box` los iconos siguen ocultos (regla preexistente `#sign_in_box .ox-message-box i { display: none }`).
- Los mensajes desde diálogos aparecen también top-right del viewport (el contenedor es fixed dentro del diálogo; `.ui-dialog` ya tenía `overflow: visible`).

### Verificación
- Revisión visual del usuario (guardar entidad, error, warning, info, undo en lista editable, 3 temas, modo phone) y `MessagesTest`: **OK**.
- Tests con `assertMessage()` de `ModuleTestBase` y `WebDriverTestBase`: **OK**.

## Notas
- La versión de OpenXava es 8.0.
- El usuario prefiere testing manual desde el IDE.
- El usuario hizo un push con los cambios de esta sesión antes de cambiar de máquina.
- El primer ítem fue solo CSS; el segundo ítem (toast) toca CSS y JS (`openxava.js`), sin cambios en Java.
