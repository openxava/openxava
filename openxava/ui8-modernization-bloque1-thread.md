# UI8 Modernization — Bloque 1: Thread Summary

## Objetivo

Modernizar los fundamentos visuales de OpenXava 8 (tipografía, espaciados, colores, temas) para que la UI tenga un aspecto propio de una aplicación de 2026.

## Implementación (Bloque 1 — ui-foundations)

### Sistema de design tokens en `base.css`
- Espaciados: `--space-1..8` (escala 4px: 4, 8, 12, 16, 20, 24, 28, 32)
- Radios: `--radius-sm/md/lg/xl/full` (4, 8, 12, 24, 9999px)
- Elevaciones: `--elevation-1/2/3` (sombras multi-capa suaves)
- Tipografía: `--font-size-xs..display` + `--font-family` (Inter variable font, woff2)
- Transiciones: `--transition-fast` (150ms) / `--transition-normal` (200ms)
- Focus ring: `--focus-ring-color` con `color-mix(in srgb, ...)`
- Marca: `--accent-color` (#4f46e5 índigo) + hover/contrast/soft

### Tipografía
- Fuente Inter (variable font) reemplaza a Roboto. woff2 en `xava/fonts/`.
- Base 14px, `tabular-nums` en celdas numéricas de listas.
- Escala modular: 11/12/14/16/20/25/32/64px.

### Temas reorganizados
- **Eliminados:** terra.css, blue.css, black-and-white.css.
- **Nuevos:**
  - `light.css` — tema claro con paleta slate.
  - `dark-overrides.css` — overrides para tema oscuro con paleta zinc.
  - `dark.css` — wrapper que importa base.css + dark-overrides.css.
  - `auto.css` (default) — importa light.css + dark-overrides.css condicionalmente con `prefers-color-scheme: dark`.
- Theme chooser final: Auto / Light / Dark.
- `XavaPreferences.getStyleCSS()` default cambiado a `auto.css`.
- OJO: Los CSS de temas no deben tener BOM (rompe tests que leen la primera línea).

### Paletas de color (versión final tras revisión visual)

#### Light (slate)
- Fondo: `#f8fafc` (slate-50)
- Texto: `#0f172a` (slate-900)
- Texto secundario: `#64748b` (slate-500)
- Frames: fondo `#f1f5f9` (slate-100), borde `#e2e8f0` (slate-200)
- Input border: `#cbd5e1` (slate-300)
- Acento: `#4f46e5` (indigo-600)

#### Dark (zinc)
- Fondo principal: `#18181b` (zinc-900, negro real)
- Fondo profundo: `#09090b` (zinc-950)
- Gris medio: `#27272a` (zinc-800)
- Texto: `#e4e4e7` (zinc-200, más legible que el anterior #bbbbbb)
- Input border: `#3f3f46` (zinc-700)
- Acento: `#818cf8` (indigo-400, más claro en oscuro)
- Frame border: `#27272a` (zinc-800)

### Espaciados aumentados (cambio perceptible)
- Frames: padding de `12px 12px 4px 12px` → `16px 16px 8px 16px`
- Detail: margin de `8px 12px 0 12px` → `12px 16px 0 16px`
- Module header: padding `13px 0 10px 10px` → `12px 0 8px 16px`
- Button bar: padding `8px 12px 12px 12px` → `12px 16px 16px 16px`
- Sections: margin/padding aumentados a `--space-2`/`--space-4`

### Otros cambios
- Loading indicator: floating pill con `--accent-color`, elevación y animación de entrada.
- Transiciones 150-200ms en hover de botones, filas, menú, secciones, tarjetas.
- `:focus-visible` consistente con `color-mix` del acento.
- Limpieza CSS muerto: reglas `.ie`, `scrollbar-face-color`, `-webkit-gradient`, vendor prefixes.
- `XavaStyle.getBodyClass()` ya no devuelve clase `ie` (IE/Edge legacy fuera de soporte).
- `openxava.browser.ie` (flag JS) sigue existiendo, usado por `uploadEditor.js`.
- Tokens hardcodeados migrados: `lightgray` → `#cbd5e1`, `#ebebe4` → `#f1f5f9`, `#F5F5F5` → `#f1f5f9`.

### Tests actualizados (openxavatest)
- `DealTest.themeChooser`: terra/blue → auto/dark.
- `ApplicantTest.assertCSSWellUploaded`: pink.css → dark.css, replace "terra" → "auto", primera línea esperada ahora regex `base|light`.
- `xava.properties`: `styleCSS=auto.css`, `themes=auto.css, light.css, dark.css`.

### Documentación
- `migration_en.html` / `migration_es.html`: sección sobre nuevos temas, `--accent-color` para marca en custom.css, recreación de terra con variables.
- `changelog.txt`: entradas detalladas del Bloque 1.

## Revisiones visuales y correcciones

### Primera revisión: valores demasiado conservadores
- **Problema:** Los valores de `light.css` y `dark-overrides.css` eran copias de los antiguos. El cambio visual era imperceptible.
- **Solución:** Rehacer paletas con slate (light) y zinc (dark). Aumentar espaciados de layout. Añadir `--frame-border` para definir frames visualmente.

### Segunda revisión: pestañas separadas de la barra de botones
- **Problema:** El `border-bottom` añadido al `#module_header` creaba una separación visual entre las pestañas de módulos y la barra de botones.
- **Solución:** Eliminar `border-bottom` del module header y reducir padding inferior. Eliminar `--module-header-border` de ambos temas.

### Observación: bordes de campos finos en Chrome, gruesos en Firefox
- **Causa:** Ilusión óptica por el cambio de color (`lightgray` → `#cbd5e1` slate-300). El `border-width` sigue siendo 2px en ambos. Chrome suaviza más los bordes subpixel, Firefox los renderiza más "cuadrados".
- **Acción:** No cambiar. El rediseño de textfields (border-width 1px, padding, etc.) es del Bloque 2.

## Veredicto final

Tras las correcciones, **sí tiene aspecto de 2026**:
- **Light:** slate, limpio, con bordes que estructuran — estilo shadcn/ui / Tailwind.
- **Dark:** zinc, negro real, texto claro, jerarquía por elevación sutil — estilo Linear / Vercel.

Lo que queda para el salto visual definitivo es el **Bloque 2** (textfields de 1px, botones rediseñados, selects modernos).

## Pendiente
- Revisión visual manual en módulos reales (list, detail, collections, dialogs, calendar, phone mode) en los 3 temas.
- Suite de tests (ejecutar manualmente desde el IDE).
- Documentación: rehacer `custom-style_en/es.html`.
- Pedir cambios en invoicedemo, chattest y openxava-archetype.
- Bloque 2 y Bloque 3.
- Borrar MDs temporales.
