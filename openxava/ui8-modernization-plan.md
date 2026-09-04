# Plan de modernización de la UI — OpenXava 8.0

Objetivo: apariencia moderna y reconocible como "versión 8", con HTML + JavaScript + CSS plano, sin framework JS ni framework CSS. Se conserva y potencia la arquitectura actual de variables CSS en `base.css` (`xava/style/`).

Por "apariencia moderna" entendemos una estética comparable a las aplicaciones punteras de 2026 —p. ej. Attio, Linear, Notion, Vercel—: interfaces limpias, espaciados generosos, bordes y sombras sutiles, tipografía legible, estados de foco y hover coherentes, y componentes con ritmo visual propio.

Aunque en ocasiones se mencione Material Design como referencia, **no es obligatorio seguirlo al pie de la letra**. En cada bloque es más importante que el resultado sea coherente con lo construido hasta entonces (tokens, radios, colores, acentos, anillos de foco y animaciones) que forzar un sistema concreto.

Las aplicaciones generadas por OpenXava son **aplicaciones empresariales de uso interno** (facturación, contabilidad, inventarios, nóminas, etc.) para usuarios que trabajan varias horas al día con ellas. El diseño debe priorizar la productividad, la legibilidad prolongada y una buena UX general. No siempre será posible copiar literalmente los patrones de las soluciones SaaS más vanguardistas, sino adaptarlos a un contexto de datos densos, flujos repetitivos y operaciones de alto volumen.

## Estrategia de trabajo

- Trabajo en **bloques** con rama propia cada uno; cada bloque se prueba, revisa en módulos reales (listas, diálogos, colecciones, modo phone) y se fusiona a master antes de empezar el siguiente.
- Orden estricto: **Bloque 1 → 2a → 2b → 2c → 2d → 3a → 3b → 3c → 4 → 5 → 6 → 7**. Cada bloque asume los tokens del anterior. El Bloque 2 se divide en 4 sub-bloques y el Bloque 3 en 3 sub-bloques para reducir la carga de revisión y facilitar el merge.
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

- [x] Spreadsheet visual style para `@ElementCollection`:
  - Celdas borderless/transparentes, hairlines entre filas (`--frame-border`), cabecera compacta muted (`--font-size-sm`, 500, `--label-color`), hover solo en celdas editables (`ox-editable-cell` + `--element-collection-cell-hover-background`), anillo de celda activa con acento (`:focus-within`, inset 2px) y acciones de fila (papelera, handle de orden, lupa de búsqueda) reveladas en hover/focus de la fila.
  - Propiedades de solo lectura dentro de colecciones editables se renderizan como texto formateado en lugar de inputs deshabilitados: nuevo atributo `readOnlyAsLabel` en `xava:editor` (propagado como parámetro al editor) + soporte del parámetro `readOnlyAsLabel` en los editores estándar (text, date, time, datetime, boolean yes/no, valid values). Las celdas se marcan con `ox-editable-cell`/`ox-readonly-cell`.
  - `@Calculation` también actualiza valores renderizados como texto (`openxava.calculate` con fallback a `.text()`; las labels llevan `span` con el id de la propiedad).
  - La lupa de búsqueda de referencias solo se muestra si la celda es editable.
  - Footer de totales: hairline superior en la primera fila de totales, etiquetas muted alineadas a la derecha y total final enfatizado (600).
  - `--element-collection-row-hover-background` pasa de blanco (invisible) a tinte translúcido 4% del color de texto.

**Concluido.** Tests pasados, estética revisada en los 3 temas (Auto/Light/Dark) y modo phone. Detalles del trabajo en `ui8-modernization-bloque3a-thread.md`.

### Bloque 3b — Calendario (`ui-calendar`)

- [x] Improve style of events in calendar, y el calendario completo:
  - Todo por tokens: variables `--fc-*` de FullCalendar v6 acotadas a `#xava_calendar` + tokens `--calendar-*` propios; eliminado el `eventColor` inline del JS.
  - Toolbar: título grande (600, `--font-size-xl`), botones como pills segmentadas (`--calendar-view-switcher-*`), focus ring.
  - Cabeceras de día uppercase muted; hoy = tinte sutil + chip circular de acento en el número; hover de día sutil; "+" de nuevo evento solo visible al hover.
  - Eventos: chips suaves de acento en mes (`--calendar-event-background`), barra izquierda de acento en semana/día; hora en negrita muted, título 500.
  - Popover "+n más" con radius-lg + elevation-3; tooltip de evento con tokens de tooltip y elevation-2.
  - Eliminada `--calendar-event-time-background`; documentado en migration_en/es.

**Concluido.** Tests pasados, estética revisada en los 3 temas (Auto/Light/Dark) y modo phone. Detalles del trabajo en `ui8-modernization-bloque3b-thread.md`.

### Bloque 3c — Listas (`ui-lists`)

**Consultar `ui8-modernization-bloque3a-thread.md` antes de empezar.** Las element collections son grids editables y las listas no, pero hay muchos elementos de diseño que deben ser coherentes o compartidos entre ambos. Revisar las decisiones de diseño del Bloque 3a y reutilizar lo que aplique.

Elementos a valorar para compartir o hacer coherentes con element collections:

- [x] **Cabeceras**: estilo compacto y muted (uppercase, letter-spacing, `--font-size-xs`, `--label-color`), alineación de cabeceras numéricas a la derecha, padding de primera/última columna.
- [x] **Hairlines horizontales**: separadores de fila con borde sutil en lugar de zebra por columnas (`nth-child(even)` con fondo gris). Usar el mismo enfoque y tokens que en element collections (`--frame-border` / color-mix).
- [x] **Hover de fila**: fondo sutil translúcido (como `--element-collection-row-hover-background`). En listas el hover es de fila completa (no de celda) porque no hay edición.
- [x] **Espaciados**: padding de celdas y altura de fila coherentes. Cabecera de lista sticky; altura de fila 35px → 42px.
- [x] **Totales**: si la lista tiene totales, aplicar el mismo estilo de footer (hairline superior, etiquetas muted alineadas a la derecha, total final enfatizado 600).
- [x] **Handles de resize de columnas**: línea vertical CSS de 2px en el borde del `th`, como se hizo en element collections.
- [x] **Skeleton loading** (filas que pulsan) al recargar listas, en lugar de solo spinner.

Diferencias intencionadas a marcar visualmente:

- [x] **Listas no son editables**: el hover debe ser de fila completa (feedback de selección), no de celda individual. No hay `ox-editable-cell` ni anillo de foco en celdas.
- [x] **Columnas editables en listas**: las listas pueden tener alguna columna editable. En ese caso, esa columna trata el hover y el foco como en element collections (`ox-editable-cell` con `--element-collection-cell-hover-background` y anillo de foco `:focus-within`). El indicador visual de editabilidad es un lápiz on-hover de fila (MDI `mdi-pencil` en `::after` de la celda, `opacity: 0.5` en hover de fila, `opacity: 0` en hover de celda para no pisar el chrome del editor). El `$` de moneda y el icono del combo se ocultan en celdas editables y solo aparecen on hover de celda. Esto unifica visualmente `@OneToMany` y `@ElementCollection`: el usuario solo ve listas de datos, y el lápiz le dice cuáles puede editar en línea.
- [x] **Acciones de fila**: en listas las acciones son de selección/edición/eliminación de la fila completa, no por celda. Revelar en hover/focus como en element collections, pero adaptadas al contexto de lista.

**Concluido.** Tests pasados, estética revisada en los 3 temas (Auto/Light/Dark) y modo phone. Detalles del trabajo en `ui8-modernization-bloque3c-thread.md`.

## Bloque 4 — Loading moderno (`ui-loading`)

- [x] Revisar el indicador de carga actual (pill flotante con `--elevation-2` y animación de entrada, heredado del Bloque 1). El indicador actual es similar al GMail original.
- [x] Valorar e implementar un loading más moderno: barra fina de 3px pegada al borde superior con shimmer indeterminado (tipo Linear/YouTube), con retardo de 200ms antes de mostrarse y mínimo de 400ms visible para evitar parpadeos. Skeleton loading descartado: con Hotwire no se sabe de antemano qué parte de la pantalla cambiará.
- [x] Eliminado el código muerto de `#xava_loading2` (`isFixedPositionSupported()` devuelve siempre `true`).
- [x] Verificación en los 3 temas (Auto/Light/Dark) y modo phone.

**Concluido.** Tests pasados, estética revisada en los 3 temas (Auto/Light/Dark) y modo phone. Detalles del trabajo en `ui8-modernization-bloque4-thread.md`.

## Bloque 5 — Botones y navegación (`ui-buttons`)

- [x] **Barra de botones**: barra superior modernizada — flexbox sin floats, hairline inferior en lugar de sombra, botones ghost pill (`--radius-md`, peso 500), hover/focus ring estándar, "Nuevo" como acción primaria con acento, hover rojo en "Borrar", iconos derecha (ayuda/suscripción) como botones cuadrados uniformes y dropdown de subcontrolador con estilo de menú moderno. **Pendiente revisión visual.**
- [ ] **Botones de abajo**: revisar y modernizar los botones inferiores (acciones de detalle/formulario). Mismos criterios que la barra superior.
- [ ] **Pestañas de módulos**: modernizar las pestañas de navegación entre módulos. Estilo coherente con los tabs y pills ya definidos.
- [x] **Botones para cambio de formato de lista**: formatos de lista como segmented control con tokens `--segmented-*` compartidos con el view-switcher del calendario (Bloque 3b). **Pendiente revisión visual.**
- [ ] Verificación en los 3 temas (Auto/Light/Dark) y modo phone.

## Bloque 6 — Secciones, marcos y acciones en vista (`ui-sections`)

- [ ] **Secciones**: revisar y modernizar el estilo de las secciones (cabeceras de sección, separadores, espaciados). Coherente con tokens de tipografía y espaciado ya definidos.
- [ ] **Marcos**: revisar y modernizar los marcos (frames) que agrupan campos y referencias. Bordes, radios, elevaciones y espaciados coherentes con el sistema de diseño.
- [ ] **Acciones en vista**: revisar y modernizar las acciones mostradas dentro de la vista de detalle (botones inline, enlaces de acción). Estilo coherente con los botones del Bloque 5.
- [ ] Verificación en los 3 temas (Auto/Light/Dark) y modo phone.

## Bloque 7 — Editores especiales (`ui-editors`)

- [ ] **Editor de carga de archivos**: revisar y modernizar el editor de carga de archivos. Al menos el color, alineándolo con `--accent-color` y los tokens del sistema de diseño. Valorar drag & drop, progress bar y estados de hover/focus.
- [ ] **Editor de texto rico**: revisar y modernizar el editor de texto rico (toolbar, área de edición, estados). Coherente con los tokens de tipografía, bordes, radios y colores ya definidos.
- [ ] Verificación en los 3 temas (Auto/Light/Dark) y modo phone.

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
