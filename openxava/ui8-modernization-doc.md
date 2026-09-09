# UI8 Modernization - Documentation Adaptation

## Objetivo

Adaptar la documentación de estilo visual de OpenXava (`custom-style_en.html` y `custom-style_es.html`) para reflejar la modernización de la UI en OpenXava 8.0, separando la documentación antigua (hasta v7.x) de la nueva (v8.0+).

## Estrategia

Siguiendo el patrón de `mysql_es.html` (documentación separada por versiones), se crearon documentos históricos y se reescribieron los actuales:

- **`custom-style-ox7_en.html`**: Copia del `custom-style_en.html` original con título "(until v7.x)" y nota enlazando a la nueva documentación.
- **`custom-style-ox7_es.html`**: Equivalente en español con título "(hasta v7.x)".
- **`custom-style_en.html`**: Reescrito completamente para OpenXava 8.0+.
- **`custom-style_es.html`**: Reescrito completamente para OpenXava 8.0+.

## Contenido nuevo (v8.0+)

Los documentos reescritos cubren:

1. **Choosing a theme**: `styleCSS` con `auto.css` (por defecto), `light.css`, `dark.css`.
2. **Defining your own style**: Crear CSS propio en `src/main/webapp/xava/style`.
3. **Extending an existing style**: Ejemplo `golden.css` extendiendo `light.css` con `--accent-color`.
4. **Your own color scheme**: Ejemplo `pink.css` extendiendo `base.css` con tokens de marca, menú, fondos, marcos y acciones.
5. **Customizing the brand color**: Redefinir `--accent-color`, `--accent-color-hover`, `--accent-soft` en `custom.css`.
6. **Design tokens**: Lista completa de tokens de `base.css` (brand, spacing, radius, elevations, typography, transitions, focus ring, surfaces, inputs, switch, buttons, sections, dialogs, list, calendar, messages).
7. **Refining other aspects of the UI**: Ejemplo de `.ox-frame` con `--radius-sm` y `--elevation-1`.
8. **Modify the application style with custom.css**: Ejemplos de `font-size`, altura de filas.
9. **A few CSS tricks**: ReadOnly fields, row-xxx en @Tab, quitar module_description, quitar checkboxes, hover en lista.
10. **Add elements to the page**: `indexExt.jsp` con ejemplo BETA.
11. **Theme chooser**: Propiedad `themes` con `auto.css, light.css, dark.css`.

## Cambios eliminados (obsoletos en v8.0+)

- Temas antiguos: `terra`, `blue`, `black-and-white`.
- Dependencia de jQuery UI (`jquery-ui.css`, `smoothness/`).
- Secciones "Defining your own style (until v5.9.1)" e "(until v4.9.1)".
- Marcas de versión "(new in v...)" en encabezados (no necesarias: el documento ya es para v8.0+).
- Referencia a "Desde la versión 4.5" en sección custom.css.

## Formato HTML

Los documentos nuevos usan HTML semántico limpio:

- `class="wiki" style="display: block;"` a nivel de `<body>`.
- Contenido estructurado con `h1`/`h2`/`h3`, `p`, `pre`, `ul`/`li`, `img`, `a`.
- `div` solo para el ToC (`<div id="toc">`).
- Sin `<div class="wiki">` envolventes ni `<br>` entre imágenes.

## Estrategia de imágenes

Los PNGs antiguos se renombraron con sufijo `-ox7` para no romper la documentación histórica. Los nuevos PNGs usan nombres sin sufijo:

| PNG | Descripción |
|---|---|
| `light-detail_en.png` | Vista detalle con tema Light de OpenXava 8.0 |
| `light-detail_es.png` | Igual en español |
| `dark-detail_en.png` | Vista detalle con tema Dark de OpenXava 8.0 |
| `dark-detail_es.png` | Igual en español |
| `golden-detail_en.png` | Vista detalle con golden.css (acento dorado) |
| `golden-detail_es.png` | Igual en español |
| `pink-detail_en.png` | Vista detalle con pink.css (ejemplo simplificado) |
| `pink-detail_es.png` | Igual en español |
| `frames-elevated-and-rounded_en.png` | Marcos con `--radius-sm` y `--elevation-1` |
| `frames-elevated-and-rounded_es.png` | Igual en español |
| `theme-chooser_en.png` | Selector de tema con Auto / Light / Dark |
| `theme-chooser_es.png` | Igual en español |

**12 PNGs nuevos a crear.** Los antiguos se renombraron a `-ox7` y la doc antigua los referencia correctamente. No hay screenshot de "Auto" (se explica en texto que sigue la preferencia del SO). `beta-corner_*.png` se mantiene sin cambios.

## Documentación de migración

`migration_en.html` ya contiene una sección "Redesigned user interface" que menciona:
- El nuevo sistema de design tokens.
- La eliminación de `terra.css`, `blue.css`, `black-and-white.css`.
- La introducción de `auto.css` como tema por defecto.
- Cómo actualizar la propiedad `themes` en `xava.properties`.
- Cómo personalizar el color de acento con `--accent-color` en `custom.css`.
- El cambio de etiquetas encima de campos por defecto (`SMALL`).

## Pendiente

- Crear los 12 PNGs listados arriba.
- Probar la documentación.
- Probar ejemplo de CSS de migración para simular terra.
- Revisar si hay otra documentación que necesite adaptación.
