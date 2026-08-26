# Bloque 2d — Diálogos: resumen de estilo

## Objetivo

Modernizar los diálogos de OpenXava eliminando el tema jQuery UI (smoothness) y estilizándolos con CSS propio usando los design tokens de OpenXava 8.

## Ficheros modificados

- `src/main/resources/META-INF/resources/naviox/index.jsp` — eliminados `jquery-ui.css` y `smoothness/jquery-ui.css`; solo se carga `jquery-ui.structure.css`.
- `src/main/resources/META-INF/resources/xava/style/base.css` — estilos de diálogos y autocomplete.
- `src/main/resources/META-INF/resources/xava/style/dark-overrides.css` — backdrop más oscuro para dark mode.
- `src/main/resources/META-INF/resources/xava/js/openxava.js` — reemplazo del icono de cerrar jQuery UI por MDI.
- `openxavatest/.../ApplicantTest.java` — test de recursos actualizado.
- `openxava-doc/web/docs/migration_en.html` y `migration_es.html` — documentación de migración.
- `changelog.txt` — entradas de cambios.
- `ui8-modernization-plan.md` — plan actualizado.

## Ficheros eliminados

- `xava/style/jquery-ui.css` (31KB, bundle estructura+tema)
- `xava/style/smoothness/` (14KB + 11 imágenes de tema)

## CSS: variables nuevas (base.css `:root`)

```css
--dialog-background: var(--background);
--dialog-border: var(--frame-border);
--dialog-backdrop-background: rgba(15, 23, 42, 0.35);
--dialog-titlebar-background: var(--dialog-background);
--dialog-titlebar-color: var(--color);
--dialog-titlebar-button-hover-background: color-mix(in srgb, var(--color) 8%, transparent);
```

Variables eliminadas: `--dialog-shadow` (sin uso), `--dialog-close-icon` y `--dialog-resize-grip-icon` (SVG data URIs reemplazados por MDI).

## Dark mode (dark-overrides.css)

```css
--dialog-backdrop-background: rgba(0, 0, 0, 0.55);
```

## Estilos de diálogo (base.css)

### Contenedor

```css
.ui-dialog, #xava_processing_layer {
    border-radius: var(--radius-lg) !important;       /* 12px */
    border: 1px solid var(--dialog-border) !important; /* hairline */
    box-shadow: var(--elevation-3);
}
.ui-dialog {
    font-size: 1em !important;
    background: var(--dialog-background) !important;
    overflow: visible !important;
    animation: ox-dialog-in var(--transition-normal);  /* 200ms */
}
```

### Animación de entrada

```css
@keyframes ox-dialog-in {
    from { opacity: 0; transform: translateY(-10px) scale(0.98); }
    to   { opacity: 1; transform: none; }
}
```

### Backdrop / overlay

```css
.ui-widget-overlay {
    background: var(--dialog-backdrop-background);
    opacity: 1;
    backdrop-filter: blur(3px);
    -webkit-backdrop-filter: blur(3px);
    animation: ox-dialog-fade-in var(--transition-fast);  /* 150ms */
}
@keyframes ox-dialog-fade-in {
    from { opacity: 0; }
    to   { opacity: 1; }
}
```

### prefers-reduced-motion

```css
@media (prefers-reduced-motion: reduce) {
    .ui-dialog, .ui-widget-overlay { animation: none; }
}
```

### Contenido

```css
.ui-dialog .ui-dialog-content {
    color: var(--dialog-content-color) !important;
    border-radius: 0 0 var(--radius-lg) var(--radius-lg);
    padding: var(--space-2) var(--space-5) var(--space-4) var(--space-5);
}
```

### Titlebar

```css
.ui-dialog-titlebar {
    font-size: var(--font-size-lg);       /* 16px */
    font-weight: 600;
    padding: var(--space-4) var(--space-5) var(--space-2) var(--space-5);
    color: var(--dialog-titlebar-color) !important;
    background: var(--dialog-titlebar-background) !important;
    border-radius: var(--radius-lg) var(--radius-lg) 0 0;
    cursor: move;
}
```

### Botón de cerrar (MDI)

CSS:

```css
.ui-dialog .ui-dialog-titlebar-close {
    top: 50%;
    right: var(--space-2);           /* 8px del borde derecho */
    width: 28px;
    height: 28px;
    margin: 0;
    padding: 0;
    transform: translateY(-50%);
    border-radius: var(--radius-full);
    background: var(--dialog-titlebar-button-background) !important;
    transition: background-color var(--transition-fast);
    text-indent: 0;                  /* reset de ui-button-icon-only */
    display: flex;
    align-items: center;
    justify-content: center;
}
.ui-dialog .ui-dialog-titlebar-close:hover {
    background: var(--dialog-titlebar-button-hover-background) !important;
}
.ui-dialog .ui-dialog-titlebar-close:focus-visible {
    outline: 2px solid var(--focus-ring-color);
    outline-offset: 2px;
}
.ui-dialog .ui-dialog-titlebar-close .mdi {
    font-size: 18px;
    line-height: 1;
    color: var(--dialog-titlebar-color);  /* hereda color del tema */
}
```

JavaScript (openxava.js, en `getDialog`):

```js
dialog.parent().find(".ui-dialog-titlebar-close").empty().append('<i class="mdi mdi-close"></i>');
```

Notas:
- `.empty()` elimina tanto el `<span class="ui-icon">` de jQuery UI como el nodo de texto "Close".
- El atributo `title="Close"` del botón se conserva para accesibilidad (tooltip nativo).
- `text-indent: 0` es necesario porque `jquery-ui.structure.css` aplica `text-indent:-9999px` a `.ui-button-icon-only`.
- `display: flex` centra el icono MDI dentro del botón circular.

### Grip de resize (MDI)

```css
.ui-dialog .ui-resizable-se {
    right: var(--space-2);
    bottom: var(--space-2);
}
.ui-dialog .ui-resizable-se::after {
    content: "\F045D";                          /* mdi-resize-bottom-right */
    font-family: "Material Design Icons";
    font-size: 14px;
    color: var(--frame-border);
    position: absolute;
    right: 2px;
    bottom: 2px;
}
```

### Diálogo sin cerrar (.no-close)

```css
.ui-dialog.no-close {
    border-radius: var(--radius-lg) !important;
}
.no-close .ui-dialog-titlebar-close { display: none; }
```

## Autocomplete (independiente del tema jQuery UI)

### Contenedor

```css
.ox-subcontroller, .ui-autocomplete {
    padding: var(--space-1);           /* 4px */
    margin: 14px 0px 0px -4px;
    box-shadow: var(--elevation-2);
    border-radius: var(--radius-md);   /* 8px */
    border: 1px solid var(--autocomplete-border);
}
.ui-autocomplete {
    max-height: 350px;
    overflow-y: auto;
    overflow-x: hidden;
    border: 1px solid var(--autocomplete-border) !important;
}
```

### Items

```css
.ui-menu-item .ui-menu-item-wrapper {
    padding: var(--space-2) var(--space-3);
    border-radius: var(--radius-sm);   /* 4px, pill shape */
}
.ui-menu-item:hover .ui-menu-item-wrapper,
.ui-widget-content .ui-state-active {
    border-color: var(--ui-menu-item-state-focus-border) !important;
    background: var(--ui-menu-item-state-focus-background) !important;
}
```

### Variables de autocomplete

- `--autocomplete-border` = `var(--input-border)` (theme-aware)
- `--autocomplete-background` = `var(--input-background)`
- `--ui-menu-item-state-focus-background` = `var(--accent-soft)` (light), `var(--my-lightdark)` (dark)
- `--ui-menu-item-state-focus-border` = `transparent`

### Scrollbar

```css
.ui-autocomplete::-webkit-scrollbar {
    width: 8px;
    background: var(--autocomplete-scrollbar-background);
}
.ui-autocomplete::-webkit-scrollbar-thumb {
    background: var(--autocomplete-scrollbar-background-thumb);
    border-radius: var(--radius-full);
}
```

## Decisiones de diseño

1. **MDI en vez de SVG data URIs**: Se usaron inicialmente data URIs SVG con `mask-image` para los iconos de cerrar y resize. El usuario solicitó cambiar a MDI para consistencia con el resto de la UI. Ventajas: el icono hereda `currentColor` y se ve correctamente en dark mode (el SVG original tenía `stroke='black'` hardcodeado).

2. **`right: var(--space-2)` (8px)**: El botón de cerrar se acercó al borde derecho tras feedback visual del usuario — con `var(--space-4)` (16px) quedaba demasiado separado.

3. **`text-indent: 0` + `display: flex`**: Necesario para contrarrestar `text-indent:-9999px` que `jquery-ui.structure.css` aplica a `.ui-button-icon-only` para ocultar el texto del botón. Sin este reset, el icono MDI se desplaza 9999px a la izquierda y no se ve.

4. **`.empty().append()`**: jQuery UI genera el botón con un `<span class="ui-icon">` y un nodo de texto "Close". `.replaceWith()` del span dejaba el texto "Close" visible. `.empty()` elimina ambos.

5. **`jquery-ui.structure.css` se mantiene**: Contiene reglas funcionales (posicionamiento de overlay, handles de resize, layout de titlebar/menu/tooltip). `jquery-ui.js` sigue usándose para dialog, autocomplete, tooltip, resizable y sortable.

## Pendiente

- ApplicantTest: verificar que el test de recursos pasa correctamente.
- Un test que use diálogos.
- Ejecución de la suite de tests desde el IDE.
- Revisión visual manual en los 3 temas (Auto/Light/Dark) y modo phone.
