# UI8 Modernization — Resumen del hilo de arranque

## Contexto

OpenXava 8.0 (próximo mes). Parte 1 terminada: modernización interna (Jakarta EE 11, Spring Boot 4.1, eliminación de DWR y JSP). Parte 2: nueva apariencia. Objetivo: apariencia moderna y atractiva que se note como versión nueva, con HTML + JavaScript + CSS plano, sin framework JS ni framework CSS.

## Tareas de UI planificadas (lista original)

1. New typography
2. More layout spacing
3. New Light theme
4. New loading indicator
5. Modernize left menu
6. Modernize textfield look&feel
7. Mark with * required field
8. Adapt popup calendar to Material Design 3
9. Spreadsheet visual style for @ElementCollection
10. Improve style of events in calendar
11. Switch editor for booleans

## Estrategia acordada: 3 bloques en ramas, orden estricto

No punto a punto (las tareas se influyen mutuamente) ni todo a la vez (imposible de revisar). Tres bloques coherentes, cada uno en su rama, fusionado a master antes del siguiente:

- **Bloque 1 — Fundamentos visuales (`ui-foundations`)**: tokens de diseño, tipografía, espaciado, tema claro, tema oscuro, loading indicator, reorganización de temas, transiciones, focus-visible.
- **Bloque 2 — Formularios y menú (`ui-forms`)**: menú izquierdo, panel de chat, textfields, required con *, switch para booleanos, popup calendar MD3, snackbar para mensajes, diálogos sin jquery-ui.css.
- **Bloque 3 — Componentes de datos (`ui-data`)**: spreadsheet style @ElementCollection, eventos de calendario, listas sin zebra por columnas, cabecera sticky, skeleton loading.

Antes del Bloque 1: definir el sistema de diseño (tokens) como primer entregable.

## Sistema de diseño (tokens en base.css :root)

- Espaciado: `--space-1..8` en múltiplos de 4px.
- Radios: `--radius-sm/md/lg` (hoy hay 2px, 10px, 16px, 24px mezclados).
- Elevaciones: `--elevation-1/2/3` (sombras ad-hoc hoy).
- Tipografía: escala modular + `font-variant-numeric: tabular-nums` en celdas numéricas.
- Transición estándar: 150–200ms ease.

## Decisiones sobre temas

- Temas principales: **claro** (por defecto) y **oscuro** (pulir al mismo nivel).
- **Eliminar `terra`**. Documentar cómo recrearlo con variables en `custom.css`.
- `black-and-white`: eliminar o mantener solo como tema de alto contraste si tiene propósito de accesibilidad.
- Variantes (`blue`, etc.): reestructurar como overrides mínimos sobre `light.css` o eliminar y documentar `--accent-color` redefinible en `custom.css`.
- Soportar `prefers-color-scheme` para oscuro automático.
- Theme chooser final: Claro / Oscuro.

## Mejoras adicionales propuestas (más allá de la lista original)

- Transiciones de 150–200ms en hover de botones, filas, menú y apertura de diálogos (lo que más "viejo" hace parecer una UI).
- Estados `:focus-visible` consistentes con anillo de foco propio.
- Listas: quitar zebra por columnas (`nth-child(even)` con fondo gris), dejar hairlines horizontales + hover (estilo Material 3).
- Cabecera de lista sticky; altura de fila 35px → 40–44px.
- Skeleton loading en listas.
- Diálogos: eliminar `jquery-ui.css` + `smoothness/` (~47KB), estilizar con CSS propio (radios, elevation, backdrop con blur, animación).
- Snackbar/toast para mensajes de éxito con auto-cierre; errores siguen persistentes.
- Modernizar panel de chat y su estilo (comparte mecanismo show/hide y estética con el menú izquierdo).
- Soporte de `prefers-color-scheme` para tema oscuro automático.

## Limpieza

- Eliminar reglas muertas en `base.css`: `.ie`, `cursor:hand`, `-webkit-gradient`, `scrollbar-face-color`, `layer-background-color`.
- Valorar eliminación de `default.css` (estilos de portal de 2004) si ya no se usa.
- MDI: NO subconjunto — todos los iconos deben estar disponibles para que el desarrollador los use en sus acciones.

## Framework CSS (Tailwind): descartado

Tailwind no encaja en OpenXava porque:
- Necesita build step que escanea markup; el HTML se genera en servidor desde renderers Java con clases como strings.
- Su tematificación es compile-time, la de OpenXava es runtime (variables CSS, theme chooser, custom.css).
- Los desarrolladores de aplicaciones quedarían fuera (necesitarían Node + Tailwind para personalizar).
- El diseño viviría en strings Java ensuciando los renderers.
Conclusión: seguid con CSS plano + variables + tokens.

## Mejoras opcionales posteriores (decidir tras los bloques)

- Densidad compacta/confortable como preferencia de usuario.
- Menú de usuario con avatar/iniciales arriba a la derecha, absorbiendo "Sign out".
- Empty states con icono y texto guía en listas y colecciones vacías.

## Estado actual

- Plan completo en `ui8-modernization-plan.md`.
- Pantallazos pendientes en `ui8-screenshots/` (ver `pending.txt`).
- Próximo paso: crear rama `ui-foundations` y empezar por los tokens en `base.css`.

## Notas de trabajo

- El usuario trabaja en 3 ordenadores distintos; las conversaciones de Windsurf son locales. Este archivo sirve para retomar el contexto en otra máquina.
- El usuario prefiere ejecutar los tests manualmente desde su IDE.
- Al implementar componentes concretos, usar pantallazos de la app en ejecución como referencia (bajo demanda, no masivos).
