# Bloque 7 — Editores avanzados (TinyMCE + FilePond): Plan de actuación

## Objetivo

Modernizar el aspecto visual de los dos editores basados en librerías JavaScript de terceros que aún muestran el look antiguo de OpenXava:

1. **Editor de texto rico (TinyMCE)** — usado por `@HtmlText` y `@Discussion`
2. **Editor de subida de archivos (FilePond)** — usado por `@File`, `@Files`, `@FileItemUpload`, `@Gallery` y estereotipos `FOTO`/`IMAGE`/`FILE`

El diseño debe ser moderno, al estilo de Attio, Notion y Linear, y coherente con el resto de la UI ya modernizada en los bloques 1–6.

---

## Estado actual

### TinyMCE (editor de texto rico)

- **Librería**: TinyMCE 7 (theme "silver", skin "oxide" / "oxide-dark"), servida localmente desde `/xava/editors/tinymce/`.
- **Inicialización**: `editors/js/htmlEditor.js` — dos configs:
  - `.ox-html-text` (toolbar completa, con menubar)
  - `.ox-simple-html-text` (toolbar simplificada, sin menubar; usada por `@HtmlText(simple=true)` y `@Discussion`)
- **JSP**: `editors/htmlEditor.jsp` (editable) → delega en `textAreaEditor.jsp`; modo read-only renderiza un `<div class="ox-read-only-html-text">`.
- **Discussion**: `editors/discussionEditor.jsp` renderiza los comentarios existentes + un `<textarea class="ox-simple-html-text xava-new-comment">` para nuevos comentarios. Los botones de post/cancel están en `div.ox-discussion-post-button`.
- **CSS de discussion**: `editors/style/discussionEditor.css` — **totalmente hardcodeado** con colores del tema antiguo (`#6F6F6F`, `#fafafa`, `lightgrey`, `#dcdcdc`, `box-shadow: 0 0 3px lightgrey`). No usa CSS vars.
- **CSS de TinyMCE en base.css**: Ya hay algunos overrides en `base.css` (~líneas 3493–3514):
  - `.xava_editor .tox-tinymce { border-style: none; border-radius: var(--radius-lg); }`
  - Hover de botones de toolbar con `--html-text-button-hover`
  - Pero la barra de herramientas, el área de edición y los menús desplegables siguen con el look por defecto de TinyMCE (skin "oxide"), que no se integra bien con el resto de la UI moderna.

### FilePond (subida de archivos)

- **Librería**: FilePond 4.30.4, servida localmente desde `editors/js/filepond.js` + plugins.
- **Inicialización**: `editors/js/uploadEditor.js` — registra plugins (image preview, file validate type/size), crea instancias FilePond para cada `.xava_upload`.
- **JSP**: `editors/uploadEditor.jsp` — genera un `<input type="file" class="xava_upload ...">` con data attributes; incluye `filePondTranslation.jsp` para i18n.
- **CSS de FilePond**: `editors/style/filepond.css` — es el CSS original de FilePond (27 KB), **no usa CSS vars**. Colores hardcodeados (`#4f4f4f`, `#292625`, etc.).
- **CSS de upload**: `editors/style/uploadEditor.css` — overrides mínimos (márgenes, download-all button). Algunos colores hardcodeados (`rgba(0,0,0,0.54)`).
- **CSS de FilePond en base.css** (~líneas 3795–3853): Ya hay overrides que usan CSS vars:
  - `.filepond--root { border-radius: var(--radius-lg); }`
  - `.filepond--file-action-button { background: var(--filepond--file-action-button-background); }`
  - `.filepond--image-preview-overlay-success/failure` con CSS vars
  - Pero la zona de drop, el panel, el label de "arrastra y suelta" y los items de archivo siguen con estilos por defecto de FilePond que no se adaptan al dark mode ni al diseño moderno.

### Dark mode

- Se gestiona con `dark-overrides.css` que redefine CSS vars (`--background`, `--color`, `--frame-background`, `--frame-border`, etc.).
- TinyMCE tiene skins `oxide` (light) y `oxide-dark` (dark) ya incluidas, pero la selección de skin no se hace dinámicamente — siempre usa `oxide` salvo que se configure explícitamente.
- FilePond no tiene soporte nativo de dark mode; depende de overrides CSS.

---

## Análisis de los pantallazos

### Editor de texto rico (TinyMCE)

**Light mode**:
- La barra de herramientas de TinyMCE se ve grisácea, con bordes cuadrados y botones con iconos pequeños. El área de texto tiene un fondo blanco plano. El conjunto no encaja con el resto de la UI que ya usa bordes redondeados, tipografía Inter y espaciado generoso.
- En Discussion, los comentarios tienen fondo `#fafafa`, borde `#dcdcdc` y `box-shadow: 0 0 3px lightgrey` — look antiguo.

**Dark mode**:
- La barra de herramientas no se adapta al dark mode (mantiene fondo claro o no usa los tokens de color del tema oscuro). El área de edición tampoco se adapta bien.

**Objetivo visual**:
- Barra de herramientas: fondo transparente o `--frame-background`, sin bordes visibles, botones con hover sutil (`--html-text-button-hover` ya existe). Padding generoso. Iconos heredando `--color`.
- Área de edición: fondo transparente o `--background`, tipografía `--font-family`, `--font-size-md`, `--line-height`.
- Bordes redondeados `--radius-lg` (ya aplicado).
- En dark mode, usar skin `oxide-dark` o aplicar overrides CSS para que toolbar y área de edición usen los tokens del tema oscuro.
- Comentarios de Discussion: tarjetas con `--frame-background`, `--frame-border`, `border-radius: var(--radius-lg)`, sin box-shadow hardcodeado. Tipografía y color con CSS vars.

### Editor de subida de archivos (FilePond)

**Light mode**:
- La zona de drop tiene un borde discontinuo gris, fondo blanco, y el label "Arrastra y suelta…" con color `#4f4f4f`. Los items de archivo subidos tienen fondo gris claro. No encaja con el diseño moderno.

**Dark mode**:
- La zona de drop no se adapta al dark mode. Los items de archivo mantienen colores claros.

**Objetivo visual**:
- Zona de drop: borde discontinuo con `--frame-border`, fondo `--frame-background` o transparente, label con `--placeholder-color`. Border radius `--radius-lg`.
- Items de archivo: fondo `--frame-background`, borde `--frame-border`, border-radius `--radius-md`. Tipografía con `--color`.
- Botones de acción (remove, download): color `--action-color`, hover `--action-hover-background`.
- Preview de imágenes: border-radius `--radius-md`.
- En dark mode, todo debe adaptarse automáticamente vía CSS vars.

---

## Plan de actuación

### Fase 1: Editor de texto rico (TinyMCE)

#### 1.1 — Seleccionar skin dinámicamente según tema (light/dark)

**Problema**: TinyMCE se inicializa siempre con la skin `oxide` (light). En dark mode, la toolbar y el área de edición no se adaptan.

**Acción**: En `htmlEditor.js`, detectar el tema activo y configurar `skin: 'oxide-dark'` cuando corresponda. TinyMCE ya incluye ambas skins en `editors/tinymce/skins/ui/`.

**Detalles**:
- Detectar dark mode: comprobar `document.documentElement.classList.contains('dark')` o `window.matchMedia('(prefers-color-scheme: dark)')` o leer una CSS var (`getComputedStyle(document.documentElement).getPropertyValue('--color-scheme')`).
- Pasar `skin: isDark ? 'oxide-dark' : 'oxide'` en ambas llamadas `tinymce.init()`.
- Si el usuario cambia de tema en runtime, puede ser necesario re-inicializar TinyMCE. Evaluar si el cambio de tema ya recarga la página o si hay que escuchar un evento.

**Archivos**: `editors/js/htmlEditor.js`

#### 1.2 — Overrides CSS modernos para la toolbar y área de edición

**Problema**: La toolbar de TinyMCE tiene estilos por defecto de la skin "oxide" que no encajan con el diseño moderno (fondos grises, bordes cuadrados, espaciado reducido).

**Acción**: Añadir overrides CSS en `base.css` (sección existente de `.tox-*`) para modernizar la toolbar:

- `.tox-tinymce` → fondo transparente, sin borde, `border-radius: var(--radius-lg)`.
- `.tox-toolbar__primary` → fondo transparente, sin borde inferior, padding `var(--space-2)`.
- `.tox-tbtn` → border-radius `var(--radius-sm)`, hover `--html-text-button-hover`, transición `var(--transition-fast)`.
- `.tox-tbtn--selected` → fondo `--accent-soft`, color `--accent-color`.
- `.tox .tox-toolbar-overlord` → sin borde.
- `.tox .tox-edit-area` → fondo transparente (hereda `--background`).
- `.tox .tox-edit-area iframe` → hereda `--font-family`, `--font-size-md`, `--color`, `--line-height`.
- `.tox .tox-mbtn` → hover con `--html-text-button-hover`, border-radius `var(--radius-sm)`.
- `.tox .tox-listbox` → sin borde, fondo transparente.
- `.tox .tox-color-input` / `.tox-color-button` → border-radius `var(--radius-sm)`.
- Menús desplegables (`.tox-menu`, `.tox-collection`): fondo `--frame-background`, borde `--frame-border`, border-radius `var(--radius-md)`, shadow `--elevation-2`.
- `.tox-collection__item` → hover `--action-hover-background`, color `--action-hover-color`.

**Archivos**: `src/main/resources/META-INF/resources/xava/style/base.css`

#### 1.3 — Content CSS del iframe de TinyMCE

**Problema**: El contenido dentro del iframe de TinyMCE usa la skin "default" de TinyMCE, que tiene su propia tipografía y colores. No hereda las CSS vars de la página.

**Acción**: Crear un content CSS personalizado (o modificar el existente en `editors/tinymce/skins/content/`) para que el contenido del iframe use la tipografía y colores de OpenXava:

- `body { font-family: var(--font-family); font-size: var(--font-size-md); line-height: var(--line-height); color: var(--color); background: transparent; }`
- Como el iframe no tiene acceso a las CSS vars de la página padre, hay dos opciones:
  - **Opción A (recomendada)**: Inyectar las CSS vars del `:root` de la página padre en el iframe via `init_instance_callback` (ya existe `htmlEditor.setInlineStyles` como referencia).
  - **Opción B**: Crear un content CSS con valores hardcodeados para light/dark y seleccionarlo dinámicamente.

**Archivos**: `editors/js/htmlEditor.js`, posiblemente nuevo `editors/style/htmlEditorContent.css`

#### 1.4 — Modernizar los comentarios de Discussion

**Problema**: `discussionEditor.css` tiene todos los colores hardcodeados del tema antiguo.

**Acción**: Reescribir `discussionEditor.css` usando CSS vars:

- `.ox-discussion-comment`:
  - `background: var(--frame-background)` (en lugar de `#fafafa !important`)
  - `border: 1px solid var(--frame-border)` (en lugar de `#dcdcdc`)
  - `border-radius: var(--radius-lg)` (en lugar de `12px`)
  - `box-shadow: none` o `var(--elevation-1)` (en lugar de `0 0 3px lightgrey`)
  - `color: var(--color)` (en lugar de `#6F6F6F`)
  - `font-family: var(--font-family)` (en lugar de `sans-serif, Arial, Verdana...`)
  - `font-size: var(--font-size-md)` (en lugar de `13px`)
  - `padding: var(--space-4)` (en lugar de `10px`)
  - `margin-bottom: var(--space-3)` (en lugar de `5px`)
- `.ox-discussion-comment-header`:
  - `color: var(--label-color)` o `color-mix(in srgb, var(--color) 60%, transparent)`
  - `font-size: var(--font-size-xs)` (en lugar de `120%`)
  - `font-weight: 600`
  - `text-transform: uppercase`
  - `letter-spacing: 0.06em`
  - Quitar `font-style: italic`
  - `margin-bottom: var(--space-2)`
- `.ox-discussion-comment-author`:
  - `font-weight: 600`
  - `color: var(--color)` (destacado sobre el header muted)
- `.ox-discussion-comment a`:
  - `color: var(--action-link-color)` (en lugar de `#0782C1`)
- `.ox-discussion-comment blockquote`:
  - `border-color: var(--frame-border)` (en lugar de `#ccc`)
- `.ox-discussion-comment hr`:
  - `border-top-color: var(--frame-border)`
- `.ox-discussion-comment figure`:
  - `border-color: var(--frame-border)`
  - `background: color-mix(in srgb, var(--color) 5%, transparent)`
- `.ox-discussion-post-button`:
  - Revisar posicionamiento. Los botones de post/cancel deben usar los estilos de botones modernos (`--default-action-button-background`, etc.) — ya referenciados en `base.css:1835`.
- Quitar todos los estilos de elementos heredados de CKEditor (`.marker`, `span[lang]`, `img.right`, `img.left`, etc.) que ya no son relevantes con TinyMCE.

**Archivos**: `editors/style/discussionEditor.css`

#### 1.5 — Botones de Discussion (post/cancel)

**Problema**: Los botones de "Añadir comentario" y "Cancelar" de Discussion se muestran con `fadeIn`/`fadeOut` y posicionamiento absoluto. Su estilo puede no ser coherente con los botones modernos del resto de la UI.

**Acción**: Verificar que los botones heredan los estilos de `base.css` (`.ox-discussion-post-button input` ya referenciado en `base.css:1835`). Si es necesario, añadir overrides para que usen `--default-action-button-*` vars consistentemente.

**Archivos**: `editors/style/discussionEditor.css` (ajustes menores)

---

### Fase 2: Editor de subida de archivos (FilePond)

#### 2.1 — Overrides CSS modernos para la zona de drop

**Problema**: FilePond usa colores hardcodeados en `filepond.css` (27 KB de CSS original). La zona de drop, el label, el panel y los items no se adaptan al tema.

**Acción**: Añadir overrides CSS en `base.css` (extender la sección existente ~líneas 3795–3853):

- `.filepond--root`:
  - `background: var(--frame-background)` o transparent
  - `border: 1px dashed var(--frame-border)` cuando está vacío
  - `border-radius: var(--radius-lg)` (ya aplicado)
  - `font-family: var(--font-family)` (ya aplicado vía `base.css:384`)
- `.filepond--drop-label`:
  - `color: var(--placeholder-color)` (en lugar de `#4f4f4f`)
  - `font-size: var(--font-size-sm)`
- `.filepond--panel-root`:
  - `background: var(--frame-background)` o `color-mix(in srgb, var(--color) 3%, transparent)`
  - `border-radius: var(--radius-md)` (ya aplicado)
  - `border: 1px dashed var(--frame-border)` (si está vacío)
- `.filepond--item`:
  - `border-radius: var(--radius-md)`
  - Ya tiene `width: 190px` (imágenes) / `300px` (archivos)
- `.filepond--file`:
  - `background: var(--frame-background)`
  - `border: 1px solid var(--frame-border)`
  - `border-radius: var(--radius-md)`
  - `box-shadow: var(--elevation-1)` (en lugar del shadow por defecto)
  - `color: var(--color)`
- `.filepond--file-info`:
  - `color: var(--color)`
  - `font-size: var(--font-size-sm)`
- `.filepond--file-info-sub`:
  - `color: var(--placeholder-color)`
- `.filepond--drip-blob`:
  - `background: var(--accent-color)` (en lugar de `#292625`)
- `.filepond--label-action`:
  - `color: var(--accent-color)`
  - `text-decoration: none` o underline con `--accent-color`
- `.filepond--file-action-button`:
  - Ya usa `--filepond--file-action-button-background`
  - Añadir `border-radius: var(--radius-sm)`, hover con `--action-hover-background`
- Estado hover/drag-over de la zona de drop:
  - `border-color: var(--accent-color)` o `--input-focus-border`
  - `background: var(--accent-soft)` (sutil)

**Archivos**: `src/main/resources/META-INF/resources/xava/style/base.css`

#### 2.2 — Dark mode para FilePond

**Problema**: FilePond no se adapta al dark mode porque sus colores base están hardcodeados en `filepond.css`.

**Acción**: Los overrides del paso 2.1 ya usarán CSS vars, por lo que el dark mode se gestionará automáticamente vía `dark-overrides.css`. Verificar que:

- `--frame-background` en dark es `var(--my-transparent-lightdark)` → la zona de drop tendrá fondo semi-transparente oscuro.
- `--frame-border` en dark es `#35353c` → borde visible.
- `--placeholder-color` en dark es `#71717a` → label legible.
- `--color` en dark es `var(--my-silver)` → texto de archivos legible.

Si es necesario, añadir overrides adicionales en `dark-overrides.css` para casos especiales de FilePond.

**Archivos**: `src/main/resources/META-INF/resources/xava/style/dark-overrides.css` (solo si es necesario)

#### 2.3 — Modernizar `uploadEditor.css`

**Problema**: `uploadEditor.css` tiene colores hardcodeados (`rgba(0,0,0,0.54)`, `rgba(0,0,0,0.06)`, `rgba(0,0,0,0.87)`) en el botón de download-all.

**Acción**: Reescribir usando CSS vars:

- `.ox-download-all-link`:
  - `color: var(--action-color)` (en lugar de `rgba(0,0,0,0.54)`)
  - `border-radius: var(--radius-sm)` (en lugar de `4px`)
- `.ox-download-all-link:hover`:
  - `background: var(--action-hover-background)` (en lugar de `rgba(0,0,0,0.06)`)
  - `color: var(--action-hover-color)` (en lugar de `rgba(0,0,0,0.87)`)

**Archivos**: `editors/style/uploadEditor.css`

#### 2.4 — Evaluar label de "arrastra y suelta"

**Problema**: El label de FilePond muestra "Arrastra y suelta tus archivos o explora" (traducido via `filePondTranslation.jsp`). El estilo visual del label no es moderno.

**Acción**: El override CSS del paso 2.1 ya moderniza el color y tipografía. Verificar que el icono de upload (si existe) o el texto del botón "explora" usa `--accent-color`. Si FilePond no incluye un icono, considerar añadir un icono SVG inline en el label via `labelIdle` con un estilo coherente.

**Archivos**: Posiblemente `editors/filePondTranslation.jsp` (para añadir icono al label)

---

### Fase 3: Verificación y tests

#### 3.1 — Tests Selenium existentes

- **`HtmlTextTest.java`**: Verifica que el botón "Insert/edit link" de TinyMCE funciona y que el color del texto se aplica correctamente. Los selectores CSS usados (`[title='Insert/edit link']`, `#tinymce p span`, `iframe` por id) deben seguir funcionando tras los cambios CSS.
- **`DiscussionTest.java`**: Verifica que los botones de comment se muestran/ocultan al hacer focus en el área de comentario. Los selectores (`tox-edit-area`, `ox-discussion-cancel-button`) deben seguir funcionando.

**Acción**: Ejecutar ambos tests tras los cambios. No se esperan fallos porque los cambios son puramente CSS (no se modifica la estructura HTML ni los selectores de TinyMCE). Si se añade el content CSS del iframe (fase 1.3), verificar que `#tinymce p span` sigue siendo accesible.

#### 3.2 — Verificación visual manual

**Casos a verificar**:

- **TinyMCE light**:
  - `Incident` → campo `description` (`@HtmlText(simple=true)`) → toolbar simplificada
  - `Doc` → campo `content` (`@HtmlText`) → toolbar completa con menubar
  - `Incident` → campo `discussion` → comentarios + área de nuevo comentario
- **TinyMCE dark**:
  - Mismos casos en dark mode → toolbar, área de edición y comentarios adaptados
- **FilePond light**:
  - `Incident` → campo `photos` (si existe en algún módulo) o cualquier entidad con `@File`/`@Files`/`@Gallery`/`@FileItemUpload`
  - Zona de drop vacía → borde discontinuo, label centrado
  - Archivo subido → item con preview, nombre, botón remove
  - Multiple files → grid de items
- **FilePond dark**:
  - Mismos casos en dark mode
- **Read-only**:
  - `@HtmlText` en modo no editable → `div.ox-read-only-html-text` → ya tiene estilos en `base.css:3881`
  - `@File`/`@Gallery` en modo no editable → `ox-filepond-read-only` → oculta drop label y remove button

#### 3.3 — Verificación de compatibilidad

- **HtmlUnit**: Los tests HtmlUnit saltan la inicialización de TinyMCE (`if (openxava.browser.htmlUnit) return;`). Los cambios CSS no afectan a HtmlUnit.
- **Navegadores legacy**: Verificar que los overrides CSS no rompen IE11/Edge (usar `color-mix` con fallback si es necesario — aunque `color-mix` ya se usa ampliamente en `base.css`).
- **CSP**: No se añade CSS inline ni JS inline. Los content CSS del iframe se inyectan via API de TinyMCE, no via inline styles.

---

## Resumen de archivos a modificar

| Archivo | Cambio |
|---|---|
| `editors/js/htmlEditor.js` | Selección dinámica de skin `oxide`/`oxide-dark` según tema; inyección de CSS vars en iframe |
| `editors/style/discussionEditor.css` | Reescritura completa con CSS vars (colores, tipografía, espaciado, bordes) |
| `editors/style/uploadEditor.css` | Reemplazar colores hardcodeados por CSS vars en botón download-all |
| `style/base.css` | Extender overrides `.tox-*` (toolbar, menús, área de edición) y `.filepond-*` (drop zone, items, panel) |
| `style/dark-overrides.css` | Solo si es necesario añadir overrides adicionales para TinyMCE/FilePond en dark |

**No se modifica**:
- `editors/js/uploadEditor.js` — la lógica de FilePond no necesita cambios
- `editors/js/discussionEditor.js` — la lógica de Discussion no necesita cambios
- `editors/uploadEditor.jsp` — la estructura HTML no necesita cambios
- `editors/discussionEditor.jsp` — la estructura HTML no necesita cambios
- `editors/htmlEditor.jsp` — la estructura HTML no necesita cambios
- `editors/style/filepond.css` — se mantiene el CSS original de FilePond; los overrides van en `base.css`
- `editors/tinymce/` — las skins y themes de TinyMCE se mantienen; los overrides van en `base.css`

---

## Orden de ejecución recomendado

1. **Fase 1.1** — Skin dinámico TinyMCE (JS) → impacto inmediato en dark mode
2. **Fase 1.2** — Overrides CSS toolbar/área de edición en `base.css`
3. **Fase 1.4** — Modernizar `discussionEditor.css`
4. **Fase 2.1** — Overrides CSS FilePond en `base.css`
5. **Fase 2.3** — Modernizar `uploadEditor.css`
6. **Fase 1.3** — Content CSS del iframe (si es necesario tras evaluar 1.1+1.2)
7. **Fase 3** — Verificación visual y tests

---

## Riesgos y consideraciones

- **TinyMCE iframe**: El contenido del iframe no hereda CSS vars de la página padre. La fase 1.3 puede requerir inyectar las vars manualmente. Si resulta complejo, como fallback se pueden usar valores hardcodeados light/dark en el content CSS.
- **Especificidad CSS**: FilePond y TinyMCE usan selectores muy específicos (`.filepond--drop-label.filepond--drop-label label`). Los overrides en `base.css` pueden necesitar `!important` o selectores igual de específicos.
- **Cambio de tema en runtime**: Si el usuario cambia de light a dark sin recargar la página, TinyMCE necesitará re-inicializarse con la skin opuesta. Verificar si el cambio de tema ya recarga la página (probable) o si hay que manejar el evento.
- **FilePond CSS original (27 KB)**: No se modifica directamente. Los overrides van en `base.css` al final del archivo, extendiendo la sección existente. Esto mantiene la actualización de FilePond sencilla en el futuro.

---

## Resumen de la sesión — corrección de TinyMCE en dark mode

**Problema observado**

Tras el primer intento de modernizar TinyMCE, en dark mode:
- La toolbar mostraba fondo blanco/azulado con botones invisibles.
- El combo “Párrafo” era blanco y no se leía su contenido.
- El área de escritura seguía siendo fondo blanco con texto negro.

**Causas identificadas**

1. `htmlEditor.isDark()` usaba únicamente `prefers-color-scheme`, no la variable `color-scheme` de OpenXava.
2. El skin `oxide-dark` aportaba un tinte azul que desentonaba, y el skin `oxide` fija `background-color: #fff` en `.tox-edit-area__iframe`.
3. Los overrides CSS eran poco específicos y no cubrían iconos SVG ni combos/listbox.
4. Las variables CSS inyectadas en el iframe del editor podían ser anidadas (`var(--my-dark)`), por lo que el `<body>` del iframe las resolvía como `initial`.

**Solución aplicada**

`editors/js/htmlEditor.js`:
- `htmlEditor.isDark()` lee `color-scheme` del `<html>` antes de recurrir a `matchMedia`.
- TinyMCE siempre usa `skin: 'oxide'`; todo el tema se controla por CSS.
- Nuevo `htmlEditor.resolveVar()` para resolver recursivamente variables anidadas.
- `htmlEditor.injectContentStyles()` añade reglas `!important` para `body/.mce-content-body` y, como refuerzo, asigna `backgroundColor` y `color` directamente al `body` del iframe.

`style/base.css`:
- Selectores con `body .tox` en lugar de `.xava_editor .tox` para garantizar que se apliquen independientemente del wrapper del editor.
- `!important` en fondos, colores y bordes del contenedor, toolbar, botones, área de edición, menús desplegables, diálogos y campos.
- Overrides para iconos SVG (`fill: var(--color)` y estados `selected` con `fill: var(--accent-color)`).
- Fondo del combo/listbox y del diálogo de enlace con tokens del tema.

**Verificación**

El usuario confirmó visualmente que la toolbar, los botones, el combo y el área de escritura ya se adaptan correctamente al tema oscuro.

**Pendiente**

- FilePond (fase 2) y modernización de `discussionEditor.css` / `uploadEditor.css` aún no abordados.

---

## Resumen de la sesión — corrección de FilePond dark mode (zona de drop)

**Problema observado**

- La zona de drop de FilePond tenía un efecto de "doble borde" con border-radius inconsistentes.
- La zona de drop era indistinguible del frame en dark mode.
- Se quería un color de fondo diferente (`--accent-soft`) para la zona de drop, visible incluso con archivos cargados.

**Descubrimiento clave: el CSS no se aplicaba**

1. **Orden de carga**: `filepond.css` (CSS original de FilePond) se carga **después** de `base.css` porque los CSS de editores se incluyen después del CSS principal (ver `ModulePageRenderer.java` líneas 96-100 y `EditorsResources.listCSSFiles`). Esto significa que las reglas de `filepond.css` con `!important` (como `.filepond--panel[data-scalable='true'] { background-color: transparent !important; }`) pisaban los overrides de `base.css`.
2. **Intento con `uploadEditor.css`**: Se movieron los overrides a `uploadEditor.css` (que se carga después de `filepond.css`), pero tampoco funcionó.
3. **Prueba con `<style>` inline en JSP**: Se añadió un `<style>` directamente en `uploadEditor.jsp`. Tampoco funcionó — el navegador no recompilaba el JSP o cacheaba el resultado.
4. **Prueba con JS**: Se añadió `setTimeout` con `el.style.setProperty('background', 'red', 'important')` en `uploadEditor.js`. **Esto sí funcionó** — el rojo apareció.

**Conclusión**: FilePond JS manipula los estilos dinámicamente tras la inicialización, pisando cualquier CSS (incluso con `!important`). La única forma fiable de aplicar el fondo es vía JS después de que FilePond termine su inicialización.

**Solución aplicada**

`editors/js/uploadEditor.js` (líneas 126-142):
- `setTimeout(100ms)` después de la inicialización de FilePond.
- Lee `--accent-soft`, `--radius-md` de `getComputedStyle(document.documentElement)`.
- Aplica `background: --accent-soft`, `border-radius`, `overflow: hidden` al `.filepond--root`.
- Hace transparentes todos los paneles internos (`.filepond--panel`, `.filepond--panel-root`, `.filepond--panel-top`, `.filepond--panel-bottom`, `.filepond--panel-center`) para que el fondo del root se vea.

`style/base.css`:
- Eliminadas las reglas CSS de fondo de `.filepond--root` y paneles transparentes (no funcionaban).
- Se mantiene un comentario indicando que el fondo se aplica vía JS.

`editors/style/uploadEditor.css`:
- Eliminadas las reglas CSS de fondo que no funcionaban.

`editors/uploadEditor.jsp`:
- Eliminado el `<style>` de diagnóstico.

**Estado actual**

- El fondo de la zona de drop ahora se aplica vía JS y usa `--accent-soft`.
- El usuario confirmó que ahora se ven colores diferentes, aunque "bastante feos" — **falta ajustar el color/estilo para que sea visualmente agradable**.
- Falta verificar el comportamiento en dark mode específicamente.
- Falta ajustar el estado drag-over.
- Los paneles de items (`.filepond--item-panel`) no se hacen transparentes (solo los del root), por lo que los items conservan su fondo original.

**Pendiente**

- Ajustar el color de fondo para que sea visualmente agradable (el usuario dijo "bastante feos").
- Verificar y ajustar dark mode.
- Implementar estado drag-over con color distinto.
- Modernizar `discussionEditor.css` y `uploadEditor.css` (fase 2.3 del plan).
- Considerar si los paneles de items también necesitan ajustes.
