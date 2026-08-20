# UI8 Modernization — Bloque 2a: Thread Summary

## Objetivo

Modernizar los paneles laterales de OpenXava 8 —menú de módulos izquierdo y panel de chat derecho— para que tengan un aspecto coherente con una aplicación de 2026, manteniendo toda la funcionalidad existente (filtro de módulos, cargar más, navegación por carpetas, bookmarks, WebSocket del chat, persistencia de estado).

## Implementación (Bloque 2a — ui-panels)

### Menú de módulos (`base.css` + `naviox/leftMenu.jsp`)

- **Ancho**: 180px → 260px (`#modules_list`, `#modules_list_outbox`, `#modules_list_core`).
- **Separación del panel**: sombra `elevation-1` sustituida por `border-right: 1px solid var(--frame-border)` (hairline estilo Linear/Notion).
- **Filas como pills**: `.module-row`, `.folder-row`, `#organizations_index` con `margin: var(--space-1) var(--space-2)`, `padding: var(--space-2) var(--space-3)`, `border-radius: var(--radius-md)`, `font-weight: 500` (seleccionado 600). Hover sutil con `color-mix(in srgb, var(--modules-list-color) 10%, transparent)`.
- **Seleccionado**: fondo `--accent-soft` + texto `--modules-list-selected-color` (= `--accent-color` en light y dark). Eliminado el `border-right: 4px solid` heredado.
- **Buscador**: icono `mdi-magnify` dentro de `#search_modules` (añadido en `leftMenu.jsp`). Input pill con fondo `--search-modules-input-background`, `border-radius: var(--radius-full)`, y focus ring (`border-color: var(--accent-color)` + `box-shadow: 0 0 0 3px var(--focus-ring-color)`). `search.png` ya no se usa en `search_modules_text` (sigue en `#xava_search_columns_text`).
- **Cabeceras de sección** (`#modules_list_header`, `#modules_list_search_header`): tipografía small-caps (`font-size: var(--font-size-xs)`, `text-transform: uppercase`, `letter-spacing: 0.06em`), borde inferior hairline `var(--frame-border)`.
- **Cargar más** (`#more_modules`): fila sutil centrada, `font-weight: 500`, color acento. Spinner y lógica AJAX intactos.
- **Botones ocultar/mostrar**:
  - `#modules_list_hide`: círculo 28px en `left: 246px` (a caballo del borde del panel, `z-index: 21`), `border-radius: var(--radius-full)`.
  - `#modules_list_show`: pestaña vertical 22×56px en `left: 0` (`z-index: 1`, queda tapada por el panel al abrirse; jQuery nunca la oculta), `border-radius: 0 var(--radius-md) var(--radius-md) 0`.
  - Iconos chevron 18px. Hover con acento (`--module-list-hide-show-hover-color`) y elevación.
  - **Nota técnica**: no usar `display: flex` en estos botones — jQuery `show()`/`fadeIn()` pone `display: inline` en línea, pero `position: fixed` lo computa como `block`; centrar con `text-align` + `line-height`.
- **Título de aplicación** (`#application_title`, `#application_name`): `font-weight: 600`, `letter-spacing: -0.01em`, padding con tokens `--space-4`.
- **Organizaciones** (`#organizations_index`): pill con icono 20px, hover en acento.

### Chat (`chat.css` + `chat.js`)

- **Panel**: 330px → 360px, `border-left: 1px solid var(--frame-border)` (sin sombra difusa).
- **Burbujas de usuario**: fondo `--accent-color`, texto `--accent-contrast-color`, `border-radius: var(--radius-xl) var(--radius-xl) var(--radius-sm) var(--radius-xl)` (esquina inferior derecha como "cola"). Asistente plano sin fondo (estilo ChatGPT/Copilot).
- **Input**: `border-radius: var(--radius-xl)`, `focus-within` con borde accent + focus ring.
- **Botones toggle**: `#chat_panel_hide` (círculo `right: 346px`) / `#chat_panel_show` (pestaña `right: 0`) idénticos a los del menú.
- **Sincronización JS↔CSS**: `chat.PANEL_WIDTH = 360` en `chat.js` sustituye los 4 `330px` hardcodeados (`initPanel`, `restorePanelState`, `showPanel` y el margin dinámico de `.module-wrapper`).

### Variables CSS

- **Nuevas defaults en `base.css :root`**: `--background`, `--color`, `--frame-border` (para que temas custom que solo importan `base.css` rendericen correctamente).
- **Eliminadas** (sin referencias restantes en ningún workspace):
  - `--modules-list-background-darker`
  - `--modules-list-shadow`
  - `--modules-list-selected-border-right`
  - `--search-modules-input-border`
  - `--chat-panel-shadow`
- **Nuevas**:
  - `--search-modules-input-background`
  - `--module-list-hide-show-hover-color`
- **Temas actualizados**:
  - `light.css`: `--modules-list-selected-color: var(--accent-color)`.
  - `dark-overrides.css`: `--modules-list-selected-color: var(--accent-color)`, `--modules-list-selected-background: var(--accent-soft)`. Eliminado `--chat-panel-shadow`.

## Archivos modificados

| Archivo | Cambio |
|---|---|
| `xava/style/base.css` | Variables, contenedor menú, filas, buscador, cabeceras, seleccionado, cargar más, botones toggle, título app, organizaciones |
| `naviox/leftMenu.jsp` | Icono `mdi-magnify` dentro de `#search_modules` |
| `xava/style/light.css` | `--modules-list-selected-color` → acento |
| `xava/style/dark-overrides.css` | Selección con acento; eliminado `--chat-panel-shadow` |
| `chat/chat.css` | Panel 360px, hairline, burbujas accent, focus ring, botones toggle |
| `chat/chat.js` | `chat.PANEL_WIDTH = 360` sustituye 330px hardcodeados |
| `ui8-modernization-plan.md` | Bloque 2a marcado como completado |

## Tests

No requieren cambios. Se conservan todos los IDs y clases usados por los tests existentes:

- **`ModulesMenuTest`**: `modules_list`, `modules_list_hide`, `modules_list_show`, `module_header_menu_button`, `module_extended_title`, `module-header-tab`, `close-icon`.
- **`ApplicantTest`**: `search_modules_text`, `modules_list`, `module-name`.
- **`FirstStepsTest`**: `modules_list`, `module_header_menu_button`, `module_extended_title`, `modules_list_hide`, `modules_list_show`, `ox-display-block-important`.

`mvn compile` verificado OK.

## Pendiente

- Revisión visual manual en los 3 temas (Auto/Light/Dark) y modo phone:
  - Menú: filtro, cargar más, navegación por carpetas, bookmarks.
  - Chat: burbujas, input, ocultar/mostrar, persistencia de estado.
- Ejecutar la suite de tests desde el IDE (`ModulesMenuTest`, `ApplicantTest`, `FirstStepsTest`).
