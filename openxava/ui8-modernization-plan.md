# Plan de modernización de la UI — OpenXava 8.0

Objetivo: apariencia moderna y reconocible como "versión 8", con HTML + JavaScript + CSS plano, sin framework JS ni framework CSS. Se conserva y potencia la arquitectura actual de variables CSS en `base.css` (`xava/style/`).

## Estrategia de trabajo

- Trabajo en **3 bloques** con rama propia cada uno; cada bloque se prueba, revisa en módulos reales (listas, diálogos, colecciones, modo phone) y se fusiona a master antes de empezar el siguiente.
- Orden estricto: **Bloque 1 → 2 → 3**. Cada bloque asume los tokens del anterior.
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

- [ ] Tokens del sistema de diseño aplicados en `base.css`.
- [ ] New typography (fuente nueva + escala modular + `tabular-nums`).
- [ ] More layout spacing (migrar espaciados a `--space-*`).
- [ ] New Light theme (paleta clara nueva; rehacer `light.css`).
- [ ] Dark theme pulido + `prefers-color-scheme`.
- [ ] New loading indicator.
- [ ] Reorganización de temas (terra fuera, variantes como overrides o eliminadas).
- [ ] Transiciones estándar en hover de botones, filas y menú.
- [ ] Estados `:focus-visible` consistentes (anillo de foco propio).

## Bloque 2 — Formularios y menú (`ui-forms`)

- [ ] Modernize left menu.
- [ ] Modernize chat panel and chat style (it shares the show/hide mechanism and visual style with the left menu).
- [ ] Modernize textfield look&feel.
- [ ] Mark with * required field.
- [ ] Switch editor for booleans.
- [ ] Adapt popup calendar to Material Design 3.
- [ ] Mensajes de éxito como snackbar/toast con auto-cierre (errores siguen persistentes).
- [ ] Diálogos: eliminar `jquery-ui.css` + `smoothness/` y estilizar con CSS propio (radios, `--elevation-3`, backdrop con blur, animación de entrada). Quita ~47KB.

## Bloque 3 — Componentes de datos (`ui-data`)

- [ ] Spreadsheet visual style para `@ElementCollection`.
- [ ] Improve style of events in calendar.
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

## Verificación

- Pasar los tests de UI (HtmlUnit) tras cada bloque.
- Revisión visual manual en módulos reales: lista, detalle, colecciones, diálogos, calendario, modo phone.
- Al implementar componentes concretos, usar pantallazos de la app en ejecución como referencia.
