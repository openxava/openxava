# Bloque 8 — Página de login, welcome y first steps: Resumen del hilo

## Objetivo

Modernizar la pantalla de acceso completa de OpenXava —la página de bienvenida (`welcome.jsp`), el formulario de autenticación (`naviox/signIn.jsp` / módulo `SignIn`) y la página de primeros pasos (`firstSteps.jsp`)— con una estética moderna de 2026 (Attio, Linear, Notion), coherente con los tokens y componentes definidos en los bloques anteriores.

Restricciones acordadas con el usuario:

- No editar los `Messages_*.properties` directamente.
- No ejecutar `mvn test` automáticamente; la verificación visual y la suite de tests las hace el usuario.
- Compilar con `mvn compile` para verificar.

---

## Cambios implementados

### Página de bienvenida (`#welcome`)

- Contenido envuelto en `.ox-welcome-card`: tarjeta centrada con `--radius-lg`, `--elevation-2`, fondo `--welcome-card-background` y animación de entrada `ox-welcome-in`.
- Eliminado el patrón SVG inline de 2004 del fondo; `--welcome-background` pasa a ser un degradado sutil con tinte de acento.
- Tipografía con la escala del sistema (`--font-size-display`, `--font-size-2xl`, `--font-size-lg`) y color muted `--welcome-muted-color` para textos secundarios.
- Botón "Sign in" como acción primaria del Bloque 5, centrado.
- `#signin_tip` ("Sign in with user: admin, password: admin") como chip: `display: inline-block`, fondo `color-mix(in srgb, var(--color) 5%, transparent)`, `--radius-md`, `max-width: 100%` con `box-sizing: border-box`.

### Formulario de login (`#sign_in_box`)

- Tarjeta centrada: `max-width: 360px`, `width: calc(100% - var(--space-8))`, padding `--space-6`, `--radius-lg`, `--elevation-2`, borde `--sign-in-border`, animación de entrada.
- Eliminados anchos fijos (`220px`, `230px`, `246px`) y márgenes en píxeles; layout con `gap` de `--space-*`.
- Campos con el look&feel del Bloque 2b: `height: 38px`, `width: 100%`, `box-sizing: border-box`, `--radius-md`, anillo de foco con acento.
- `#sign_in_box .ox-layout-detail` pasa de `display: table` + `white-space: nowrap` a `display: block; width: 100%; white-space: normal` para que los campos ocupen todo el ancho de la tarjeta.
- Botón "Sign in" primario a ancho completo; botón Azure AD/SSO como secundario ghost con tokens `--azure-signin-button-*`.
- Mensajes de error integrados en la tarjeta.

### Cabecera en login

- `#module_header` se oculta en el módulo `SignIn` (`index.jsp`): la página de login ya no muestra el chrome de la aplicación.
- Nueva línea de marca `.ox-sign-in-brand` en `signIn.jsp`: muestra `organización - aplicación` centrada sobre la tarjeta, con `--font-size-xl`, peso 600, `letter-spacing: -0.01em`, margen superior `clamp(var(--space-8), 12vh, 120px)` y animación de entrada. Patrón habitual en Attio/Linear/Notion.

### Página de primeros pasos (`#first_steps`)

- Layout flex horizontal: icono a la izquierda, texto a la derecha, `gap: var(--space-5)`, centrado verticalmente.
- Icono `mdi-arrow-left` (antes `mdi-reply`) dentro de un badge de 96×96px: fondo `--accent-soft`, borde `color-mix(in srgb, var(--accent-color) 20%, transparent)`, `--radius-xl`, icono a 48px, animación `first-steps-arrow` (1.4s ease-in-out infinita).
- Texto `#first_steps p`: `--font-size-xl`, peso 500, `max-width: 420px`, `text-wrap: balance`.

### Tokens nuevos

Definidos en `base.css` con overrides en `light.css` y `dark-overrides.css`:

- `--welcome-background`, `--welcome-color`, `--welcome-card-background`, `--welcome-muted-color`
- `--sign-in-background`, `--sign-in-text`, `--sign-in-border`, `--sign-in-input-border`, `--sign-in-input-focus-outline-color`
- `--azure-signin-button-background`, `--azure-signin-button-border`, `--azure-signin-button-color`, `--azure-signin-button-hover-background`
- `--first-steps-icon-color`, `--accent-soft`

---

## Defectos visuales corregidos durante la revisión

| Defecto | Causa | Fix |
|---|---|---|
| `signin_tip`: "admin" huérfano en línea propia (ES) | Padding horizontal excesivo | `--space-4` → `--space-3`, `max-width: 100%` + `box-sizing: border-box` |
| Campos de login muy bajos (~20px) | `box-sizing: border-box` reducía la altura efectiva | `height: 38px` explícita, igual que los campos de la aplicación |
| Campos de login no ocupaban todo el ancho | `.ox-layout-detail` era `display: table` con shrink-wrap | `display: block; width: 100%; white-space: normal` |
| Cabecera de aplicación fuera de lugar en login | `#module_header` se renderizaba siempre | Ocultar en `index.jsp` cuando `module == "SignIn"` + `.ox-sign-in-brand` centrada |
| Icono de FirstSteps "desnudo" | `mdi-reply` sin contenedor | Badge 96×96 con `--accent-soft`, borde de acento y `--radius-xl` |
| Icono semánticamente incorrecto | `mdi-reply` apuntaba mal con el texto a la derecha | `mdi-arrow-left` |
| "menu" huérfano en inglés en FirstSteps | `max-width: 340px` insuficiente | `max-width: 420px` + `text-wrap: balance` |

---

## Archivos modificados

| Archivo | Cambio |
|---|---|
| `xava/style/base.css` | Bloques CSS reescritos para `#welcome`, `#sign_in_box`, `#first_steps`; tokens nuevos; fixes de wrapping y alturas |
| `xava/style/light.css` | Overrides de los nuevos tokens en tema claro |
| `xava/style/dark-overrides.css` | Overrides de los nuevos tokens en tema oscuro |
| `naviox/welcome.jsp` | Contenido envuelto en `.ox-welcome-card` |
| `naviox/signIn.jsp` | Línea de marca `.ox-sign-in-brand` sobre la tarjeta |
| `naviox/index.jsp` | `#module_header` oculto en el módulo `SignIn` |
| `naviox/firstSteps.jsp` | Icono `mdi-reply` → `mdi-arrow-left` |

---

## Verificación

- `mvn compile` correcto.
- Revisión visual manual por el usuario en español e inglés: welcome, login y first steps confirmados.
- Suite de tests HtmlUnit y revisión en los 3 temas (Auto/Light/Dark) y modo phone: a cargo del usuario.

**Bloque 8 concluido.**
