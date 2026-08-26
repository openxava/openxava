# Plan de modernización de la UI — OpenXava 8.0

Objetivo: apariencia moderna y reconocible como "versión 8", con HTML + JavaScript + CSS plano, sin framework JS ni framework CSS. Se conserva y potencia la arquitectura actual de variables CSS en `base.css` (`xava/style/`).

## Estrategia de trabajo

- Trabajo en **bloques** con rama propia cada uno; cada bloque se prueba, revisa en módulos reales (listas, diálogos, colecciones, modo phone) y se fusiona a master antes de empezar el siguiente.
- Orden estricto: **Bloque 1 → 2a → 2b → 2c → 2d → 3a → 3b → 3c**. Cada bloque asume los tokens del anterior. El Bloque 2 se divide en 4 sub-bloques y el Bloque 3 en 3 sub-bloques para reducir la carga de revisión y facilitar el merge.
- Antes del Bloque 1, definir el **sistema de diseño** (tokens) como primer entregable.
- Limpieza de CSS muerto (`.ie`, `cursor:hand`, `-webkit-gradient`, `scrollbar-face-color`) se hace dentro de cada bloque, en las zonas que se toquen.

## Sistema de diseño (entregable previo al Bloque 1)

Definir en `base.css` como tokens en `:root`:

- **Espaciado**: `--space-1..8` en múltiplos de 4px.
- **Radios**: `--radius-sm` / `--radius-md` / `--radius-lg` (hoy hay 2px, 10px, 16px, 24px mezclados).
- **Elevaciones**: `--elevation-1/2/3` (hoy las sombras son ad-hoc: `0px 1px 3px`, `4px 4px 18px`, etc.).
- **Tipografía**: escala modular (título de módulo, sección, label, dato) y `font-variant-numeric: tabular-nums` en celdas numéricas.
- **Transición estándar**: 150–200ms ease para hovers y aperturas.

## Temas

- Temas principales: **claro** (`light.css`, por defecto) y **oscuro** (`dark.css`, pulirlo al mismo nivel que el claro).
- **Eliminar `terra`**. Documentar en la guía de migración a 8.0 cómo recrearlo con variables en `custom.css`.
- `black-and-white`: eliminar o renombrar/mantener solo como tema de **alto contraste** si tiene propósito de accesibilidad.
- Las variaciones (p.ej. `blue`) se reestructuran como overrides mínimos sobre `light.css` (redefinir solo el acento), o mejor: eliminarlas y documentar la personalización de marca mediante una única variable `--accent-color` redefinible en `custom.css`.
- Soportar **`prefers-color-scheme`** para el oscuro automático.
- Theme chooser final: Claro / Oscuro.

## Bloque 1 — Fundamentos visuales (`ui-foundations`)

- [x] Tokens del sistema de diseño aplicados en `base.css` (`--space-*`, `--radius-*`, `--elevation-*`, `--font-size-*`, `--transition-*`, `--focus-ring-color`, `--accent-color`).
- [x] New typography (Inter variable font + escala modular + `tabular-nums`).
- [x] More layout spacing (espaciados de frames/secciones/listas migrados a `--space-*`).
- [x] New Light theme (`light.css` rehecho con acento índigo `#4f46e5`).
- [x] Dark theme pulido + `prefers-color-scheme` (nuevo `auto.css` por defecto + `dark-overrides.css` compartido).
- [x] New loading indicator (pill flotante con `--elevation-2` y animación de entrada).
- [x] Reorganización de temas (terra/blue/black-and-white eliminados; chooser Auto/Light/Dark; documentado en guía de migración).
- [x] Transiciones estándar en hover de botones, filas y menú.
- [x] Estados `:focus-visible` consistentes (anillo de foco propio).

Pendiente de revisión visual manual antes de fusionar: lista, detalle, colecciones, diálogos, calendario, modo phone, en los 3 temas.

## Bloque 2 — Formularios y menú

Dividido en 4 sub-bloques con rama propia cada uno. Cada sub-bloque se prueba, revisa y fusiona a master antes de empezar el siguiente. Orden estricto: **2a → 2b → 2c → 2d**.

### Bloque 2a — Paneles laterales (`ui-panels`)

- [x] Modernize left menu.
- [x] Modernize chat panel and chat style (it shares the show/hide mechanism and visual style with the left menu).
- [x] Module search input: visible border at rest, taller padding (~44px), subtle 2px focus ring at 20% opacity.
- [x] Thin rounded scrollbar for module menu (Chromium + Firefox).
- [x] Focus ring opacity reduced from 45% to 20% globally.
- [x] Changelog updated with 8 entries for Bloque 2a.

**Concluido.** Tests pasados, estética revisada en los 3 temas (Auto/Light/Dark) y modo phone.

### Bloque 2b — Campos (`ui-fields`)

- [x] Modernize textfield look&feel (1px border, radius-md, 38px de alto, hover, anillo de foco con acento, disabled suave, selects con chevron propio, tabular-nums en numéricos).
- [x] Mark with * required field (asterisco rojo vía `ox-required-label` en `PropertyEditorRenderer` y `ReferenceRenderer`; el borde distintivo se neutraliza a `--input-border`).
- [x] Switch editor for booleans (CSS puro sobre el checkbox dentro de `.xava_editor`, con tokens `--switch-*`).
- [x] Labels on top by default (`defaultLabelFormat` cambiado de `NORMAL` a `SMALL` en `XavaPreferences`; CSS de `.small-label` modernizado; `<br/>` eliminado en `PropertyEditorRenderer` y `ReferenceRenderer`; alineación de etiquetas SMALL con campos pequeños y mixtos resuelta; marcos alineados con campos).

**Concluido.** Tests pasados, estética revisada en los 3 temas (Auto/Light/Dark) y modo phone.

### Bloque 2c — Popups (`ui-popups`)

- [x] Adapt popup calendar (flatpickr) to the new design system. Overrides con tokens en `base.css`, sin tocar el CSS vendor ni cambiar la interacción:
  - Día seleccionado con `--accent-color` (verificar también en dark, hoy apunta a `--my-blue`).
  - Día de hoy con indicador de acento (anillo o punto).
  - Hover de días con fondo suave y transición estándar 150–200ms.
  - Popup con `--radius-md`/`--radius-lg` y `--elevation-2`/`--elevation-3`.
  - Inter heredada, `tabular-nums` en números de día, tamaños `--font-size-*`.
  - Flechas de navegación tipo chevron coherentes con los selects del Bloque 2b.
  - Cabecera mes/año estilizada como los selects de 2b (o texto clickable).
  - Fila de hora con inputs/spinners coherentes con los textfields nuevos.
  - `:focus-visible` con `--focus-ring-color` en navegación por teclado.
  - Verificación en dark theme y modo phone.
- [x] Mensajes de éxito, advertencia e información como snackbar/toast modernos con auto-cierre; errores siguen persistentes.

**Concluido.** Tests pasados, estética revisada en los 3 temas (Auto/Light/Dark) y modo phone.

### Bloque 2d — Diálogos (`ui-dialogs`)

- [x] Diálogos: eliminar `jquery-ui.css` + `smoothness/` y estilizar con CSS propio:
  - Solo se carga `jquery-ui.structure.css` (reglas funcionales). Eliminados `jquery-ui.css` y `smoothness/` (~45KB + 11 imágenes de tema).
  - Diálogo: `--radius-lg`, borde hairline `--dialog-border`, `--elevation-3`, backdrop translúcido con blur (`--dialog-backdrop-background`) y animaciones de entrada (diálogo fade+scale, overlay fade; desactivadas con `prefers-reduced-motion`).
  - Botón cerrar circular propio con icono MDI `mdi-close` (reemplaza el `span.ui-icon` de jQuery UI en openxava.js) y grip de resize con `mdi-resize-bottom-right` vía `::after`.
  - Titlebar: `--font-size-lg`, peso 600, padding generoso, `cursor: move`.
  - Autocomplete independiente del tema: borde propio, `--radius-md`, padding 4px, items pill con `--accent-soft` en hover/activo.
  - `ApplicantTest.assertResorcesWellReaded` usa `favicon.ico` en lugar de una imagen de smoothness.
  - Documentado en guía de migración (EN/ES) y changelog.

**Concluido.** Tests pasados, estética revisada en los 3 temas (Auto/Light/Dark) y modo phone.

## Bloque 3 — Componentes de datos (`ui-data`)

Dividido en 3 sub-bloques con rama propia cada uno. Cada sub-bloque se prueba, revisa y fusiona a master antes de empezar el siguiente. Orden estricto: **3a → 3b → 3c**.

### Bloque 3a — ElementCollection (`ui-elementcollection`)

- [ ] Spreadsheet visual style para `@ElementCollection`.

### Bloque 3b — Calendario (`ui-calendar`)

- [ ] Improve style of events in calendar.

### Bloque 3c — Listas (`ui-lists`)

- [ ] Listas: quitar zebra por columnas (`nth-child(even)` con fondo gris), dejar hairlines horizontales + hover (estilo Material 3).
- [ ] Cabecera de lista sticky; altura de fila 35px → 40–44px.
- [ ] Skeleton loading (filas que pulsan) al recargar listas, en lugar de solo spinner.

## Limpieza y rendimiento

- [ ] Eliminar reglas muertas en `base.css` (`.ie`, `cursor:hand`, `-webkit-gradient`, `scrollbar-face-color`, `layer-background-color`).
- [ ] Valorar eliminación de `default.css` (estilos de portal de 2004) si ya no se usa.

## Mejoras opcionales posteriores (decidir tras los bloques)

- Densidad compacta/confortable como preferencia de usuario.
- Menú de usuario con avatar/iniciales arriba a la derecha, absorbiendo "Sign out".
- Empty states con icono y texto guía en listas y colecciones vacías.

## Pantallazos de referencia

Disponibles en `ui8-screenshots/`:

- **list-mode.png** — Lista de un módulo (con filas, filtros y botonera).
- **detail-mode.png** — Detalle con secciones y frames (un módulo con varias secciones y una referencia).
- **detail-with-elementcollection.png** — Una `@ElementCollection` con algunas filas.
- **dialog.png** — Un diálogo abierto (p.ej. añadir a colección o un diálogo de acción).
- **calendar-list-format.png** — El calendario (vista mensual con eventos).
- **date-calendar-popup.png** — El popup calendar de un campo fecha.
- **modules-menu-and-chat-panels.png** — Menú izquierdo + panel de chat abierto a la vez.
- **signin.png** — Sign in.

## Verificación

- Pasar los tests de UI (HtmlUnit) tras cada bloque.
- Revisión visual manual en módulos reales: lista, detalle, colecciones, diálogos, calendario, modo phone.
- Al implementar componentes concretos, usar los pantallazos de `ui8-screenshots/` como referencia.
