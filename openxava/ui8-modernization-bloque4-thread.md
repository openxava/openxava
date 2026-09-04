# Bloque 4 — Loading moderno: Hilo de trabajo

## Objetivo

Modernizar el indicador global de carga AJAX de OpenXava, reemplazando el indicador heredado del Bloque 1 (pill flotante con spinner y texto "Loading...", estilo GMail clásico) por una técnica moderna coherente con aplicaciones como Linear, YouTube, GitHub o Notion.

---

## Análisis previo: cómo indican la carga las apps modernas

### Patrones identificados

1. **Barra de progreso superior fina**: línea de 2-3px pegada al borde superior del viewport, con shimmer indeterminado fluyendo de izquierda a derecha. No es modal, es periférica, informa sin exigir atención. Usada por Linear y GitHub.
2. **Skeleton screens**: bloques grises con animación de onda que imitan la forma del contenido que viene. Usadas por Notion y Attio. Da sensación de velocidad percibida y evita saltos de layout.
3. **UI optimista**: cambios inmediatos en pantalla con sincronización en segundo plano. Usada por Linear. Requiere revertir estado en el cliente.

### Decisión

Se descartó **skeleton loading** porque OpenXava usa un patrón Hotwire: el servidor decide qué parte de la pantalla hay que cambiar tras cada acción, por lo que no se sabe de antemano dónde colocar el skeleton.

Se descartó **UI optimista** por requerir un cambio de arquitectura, no solo de UI.

Se implementó un **sistema de dos niveles**:

- **Nivel 1 (operación rápida, <200ms)**: sin indicador. Nada.
- **Nivel 2 (operación con retardo, >200ms)**: barra fina de 3px en el borde superior con shimmer indeterminado.

---

## Arquitectura del cambio

### Archivos modificados

1. **`ModulePageRenderer.java`** — markup HTML del indicador.
2. **`base.css`** — estilos del indicador y variables CSS.
3. **`openxava.js`** — lógica de mostrar/ocultar con retardo y mínimo visible.

### Markup: `ModulePageRenderer.java`

El indicador anterior era un `<div id="xava_loading">` con un icono `mdi-autorenew` (spinner) y texto i18n "Loading...". Se sustituye por:

```html
<div id="xava_loading" role="progressbar" aria-label="Loading...">
    <div class="ox-loading-progress"></div>
</div>
```

Se elimina `#xava_loading2`, un fallback para navegadores sin `position: fixed` cuyo guardián `Style.isFixedPositionSupported()` devuelve siempre `true`. Era código muerto.

### CSS: `base.css`

**Antes**: pill flotante centrado arriba con `border-radius: var(--radius-full)`, `box-shadow: var(--elevation-2)`, fondo sólido `--accent-color`, texto en `--accent-contrast-color`, animación de entrada `xava-loading-in` (translateY + opacity).

**Después**: barra de 3px fija en el borde superior, a todo lo ancho del viewport:

- `position: fixed; top: 0; left: 0; right: 0; height: 3px`
- Pista tenue: `background: color-mix(in srgb, var(--xava-loading-background-color) 25%, transparent)`
- Franja deslizante del 40% de ancho con degradado suave: `linear-gradient(90deg, transparent, accent 30%, accent 70%, transparent)`
- Animación `xava-loading-slide 1.1s ease-in-out infinite` (de `left: -40%` a `left: 100%`)
- `transition: opacity 150ms ease-out` para el fade-out
- `@media (prefers-reduced-motion: reduce)`: barra estática tenue (sin animación, `opacity: 0.5`)

Variable CSS eliminada: `--xava-loading-color` (el nuevo indicador no tiene texto). Se conserva `--xava-loading-background-color` (defaults to `--accent-color`) para temas personalizados.

### JavaScript: `openxava.js`

**Antes**: `openxava.request()` llamaba `openxava.fadeIn('#xava_loading', 1000)` inmediatamente al iniciar la petición, y `$('#xava_loading').hide()` al recibir la respuesta. No había umbral de retardo: el indicador aparecía siempre, con un fade de 1s que simulaba retardo.

**Después**: tres funciones centralizan la lógica:

```javascript
openxava.loadingDelay = 200;    // ms antes de mostrar
openxava.loadingTimeout = null;
openxava.loadingShownAt = 0;

openxava.scheduleLoading()      // llamada al iniciar petición
  // clearTimeout anterior; setTimeout(200ms): show(), registrar loadingShownAt

openxava.hideLoading()          // llamada al recibir respuesta/error
  // clearTimeout; si no visible, return
  // remaining = max(0, 400 - (now - shownAt))
  // setTimeout(remaining): opacity=0; setTimeout(150ms): hide(), opacity=1
```

**Retardo de 200ms**: si la petición completa en menos de 200ms, el indicador nunca aparece. Evita parpadeos en operaciones rápidas.

**Mínimo visible de 400ms**: si el indicador aparece, se garantiza que permanezca visible al menos 400ms antes de empezar el fade-out. Evita destellos subliminales.

**Fade-out sin jQuery**: se usa `css("opacity", 0)` + `setTimeout(150)` → `.hide()` en lugar de `fadeOut(150)`. Motivo: HtmlUnit no procesa animaciones jQuery y el test `OrderDocumentTest` comprueba `isDisplayed()`. La transición CSS `opacity 150ms` da el fade visual en navegadores reales; el `setTimeout` garantiza `display: none` en HtmlUnit.

**Cobertura de errores**: `hideLoading()` se llama en todos los caminos de error:
- Respuesta con prefijo `"ERROR:"`
- Excepción al parsear JSON
- `openxava.systemError()`

---

## Tests afectados

Se revisaron todos los tests de `openxavatest` que referencian `xava_loading`:

1. **`WebDriverTestBase.java:95-104`** — `wait(driver)`: espera visibilidad (300ms) luego invisibilidad (10s). Compatible con el retardo de 200ms + mínimo de 400ms + fade de 150ms. **Sin cambios.**
2. **`OrderDocumentTest.java:29`** — `assertFalse(isDisplayed())` tras `execute()`. HtmlUnit no procesa `fadeOut` jQuery, por eso se sustituyó por `css("opacity", 0)` + `setTimeout` → `.hide()`. **Sin cambios en el test.**
3. **`JourneyTest.java` / `ProductTest.java`** — referencias a `xava_loading_more_elements` (paginación de cards). No relacionado. **Sin cambios.**

No fue necesario modificar ningún test.

---

## Verificación

- Compilación Java: `mvn compile` OK.
- Tests de openxavatest: pasados.
- Estética revisada en los 3 temas (Auto/Light/Dark) y modo phone.

---

## Pendiente / Futuro

- **Skeleton loading regional**: descartado por ahora (patrón Hotwire), pero podría reconsiderarse si se añaden endpoints que devuelvan partials predecibles.
- **UI optimista**: requiere cambio de arquitectura, fuera del alcance de la modernización visual.
