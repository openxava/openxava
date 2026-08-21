# UI8 Modernization - Bloque 2b: Label Styles

## Objetivo
Modernizar el estilo de las etiquetas en OpenXava 8.0, haciendo que el formato `SMALL` (etiquetas arriba del campo) sea el formato por defecto, con una apariencia moderna y profesional acorde a 2026.

## Cambios realizados y confirmados (ya en push)

### 1. Default label format cambiado a SMALL
- **Archivo**: `src/main/java/org/openxava/util/XavaPreferences.java` (líneas 344-345)
- **Cambio**: `defaultLabelFormat` cambiado de `"NORMAL"` a `"SMALL"`
- **También en**: `openxavatest/src/main/resources/xava.properties` (línea 61) — cambiado por el usuario

### 2. CSS de `.small-label` modernizado
- **Archivo**: `src/main/resources/META-INF/resources/xava/style/base.css` (líneas 2418-2427)
- **Cambio actual**:
  ```css
  .small-label {
      display: block;
      padding-left: 0;
      margin-top: var(--space-3);   /* 12px - separa del campo de arriba */
      margin-bottom: var(--space-1); /* 4px - pega al campo de abajo */
      font-size: var(--font-size-sm); /* 12px */
      font-weight: 500;
      color: var(--label-color);
      line-height: 1.3;
  }
  ```

### 3. Eliminado `<br/>` después del SMALL label
- **Archivos**: `PropertyEditorRenderer.java` (línea 70) y `ReferenceRenderer.java` (línea 86)
- **Cambio**: `w.append("</span><br/>")` → `w.append("</span>")`
- **Razón**: Con `display: block` en `.small-label`, el `<br/>` añadía una línea vacía extra entre la etiqueta y el campo, alejándolos.

### 4. Clase `ox-small-label-wrapper` en los renderers
- **Archivos**: `PropertyEditorRenderer.java` (líneas 54-56) y `ReferenceRenderer.java` (líneas 70-72)
- **Cambio**: Al construir `preEditor`, si el labelFormat es SMALL, se añade la clase `ox-small-label-wrapper`:
  ```java
  if (labelFormat == MetaPropertyView.SMALL_LABEL) {
      preEditor = preEditor.replace("ox-editor-wrapper'", "ox-editor-wrapper ox-small-label-wrapper'");
  }
  ```
- **Nota**: Esta clase ya existía en el código antes de esta sesión. Se usa para identificar los wrappers que contienen etiquetas SMALL.

### 5. `min-height` e `inline-flex` en `.xava_editor`
- **Archivo**: `src/main/resources/META-INF/resources/xava/style/base.css` (líneas 848-852)
- **Cambio actual**:
  ```css
  .xava_editor {
      min-height: 38px;
      display: inline-flex;
      align-items: center;
  }
  ```
- **Razón**: Solución genérica para que editores pequeños (switch booleano, radio buttons, etc.) ocupen el mismo espacio vertical que un input de texto (38px) y se centren verticalmente. Esto hace que las etiquetas SMALL de campos pequeños se alineen con las de campos normales.

### 6. `vertical-align: top` en `.ox-small-label-wrapper`
- **Archivo**: `src/main/resources/META-INF/resources/xava/style/base.css` (líneas 1016-1018)
- **Cambio actual**:
  ```css
  .ox-editor-wrapper.ox-small-label-wrapper {
      vertical-align: top;
  }
  ```
- **Estado**: NO funciona — la etiqueta del booleano sigue ligeramente desplazada.

## Problema pendiente: Alineación de la etiqueta del booleano

### Descripción
La etiqueta "Pagada" (campo booleano con switch) queda ligeramente desplazada (un par de píxeles) respecto a las demás etiquetas SMALL ("Año", "Número", "Fecha", "Cantidad líneas").

### Lo que se ha intentado
1. **`vertical-align: top` en todas las celdas del layout** (`layout.css`): Rompió la alineación de etiquetas NORMAL, acciones y campos NO_LABEL. Revertido.
2. **`:has(.ox-switch)` en `.ox-editor-wrapper`**: Funcionó para alinear la etiqueta pero el switch quedaba alineado arriba, no centrado. Revertido por solución a piñón fijo.
3. **`min-height: 38px` + `inline-flex` + `align-items: center` en `.xava_editor`**: Solución genérica. El switch se centra bien, pero la etiqueta queda ligeramente desplazada (un par de píxeles).
4. **`vertical-align: top` en `.ox-small-label-wrapper`**: Añadido encima del `min-height`, pero no resuelve el desfío. **Sigue sin funcionar.**
5. **Ajustar `margin-top` de `.small-label`**: Cambia todas las etiquetas a la vez, no solo la del booleano.
6. **Ajustar `padding-top` de `.ox-editor-wrapper`**: Igual, cambia todas a la vez.
7. **Ajustar `min-height` de `.xava_editor`**: Valores menores a 38px bajan la etiqueta (la alejan). Valores mayores a 38px no tienen efecto (los inputs de texto ya miden 38px).

### Análisis del problema
- El layout usa `display: table` con celdas `table-cell` o `inline-block`.
- Los wrappers se alinean por `vertical-align: baseline` por defecto.
- Con `inline-flex` en `.xava_editor`, la baseline del flex container cambia respecto a la de un input de texto normal.
- El switch (24px) centrado en un `min-height: 38px` produce una baseline diferente a la de un input de texto (38px reales).
- Ese desfío de baseline arrastra la etiqueta SMALL del booleano un par de píxeles.

### HTML de referencia
El wrapper del booleano tiene esta estructura:
```html
<div class="ox-layout-not-aligned-cell ox-editor-wrapper ox-small-label-wrapper">
  <span class="small-label">Pagada</span>
  <span class="xava_editor">
    <input type="checkbox" class="editor ox-switch" ...>
  </span>
  <span id="..._property_actions_paid"></span>
</div>
```

### Posibles siguientes pasos a explorar
- **`vertical-align: top` en `.ox-small-label-wrapper` combinado con quitar `inline-flex` de `.xava_editor`**: Quizás el problema es que `inline-flex` cambia la baseline. Probar con `display: inline-block` + `line-height: 38px` en `.xava_editor` en lugar de `inline-flex`.
- **Alinear el switch con `margin-top` negativo dentro del flex**: Ajustar fino el `margin-top` del `input.ox-switch` dentro del flex container.
- **Usar `vertical-align: top` en `.ox-small-label-wrapper` sin `inline-flex`**: Si `.xava_editor` vuelve a ser `inline` normal, el `vertical-align: top` del wrapper podría alinear todo desde arriba correctamente, y el `min-height` alone podría ser suficiente.
- **Inspeccionar en el navegador**: Comparar las baselines exactas del wrapper del booleano vs el de un input de texto para entender el desfío en píxeles.

## Resolución

El problema de alineación se resolvió aplicando la misma baseline de 38px a **todos** los `.xava_editor` dentro de `.ox-editor-wrapper`, no solo a los de `.ox-small-label-wrapper`, y ajustando el `::before` para que la línea base quede en el centro del texto del input (25px) en lugar del borde inferior del contenido (29px).

### CSS final (base.css)

```css
.ox-editor-wrapper .xava_editor {
    display: inline-flex;
    align-items: center;
    min-height: 38px;
}

.ox-editor-wrapper .xava_editor::before {
    content: "";
    width: 0;
    height: calc(20px + (var(--space-2) / 2) + 1px); /* 25px */
    align-self: baseline;
}
```

### Resultado
- La etiqueta del booleano "Pagada" queda perfectamente alineada.
- Acciones como "Cambiar nombre de etiqueta" y "Prefix street" se centran verticalmente con su campo.
- Campos NO_LABEL como "Población" se alinean con campos SMALL como "Estado".
- Campos NORMAL como "Vía pública" y "Código postal" se alinean con SMALL en la misma fila.
- No se usa `.ox-switch` en la regla; es una solución genérica para editores pequeños (boolean, radio, etc.).
- Verificado manualmente por el usuario: todo en su sitio.

## Archivos modificados en esta sesión

| Archivo | Cambios |
|---------|---------|
| `src/main/java/org/openxava/util/XavaPreferences.java` | defaultLabelFormat: NORMAL → SMALL |
| `src/main/resources/META-INF/resources/xava/style/base.css` | `.small-label` modernizado, `<br/>` eliminado (via Java), `.xava_editor` con min-height+inline-flex, `.ox-small-label-wrapper` con vertical-align:top |
| `src/main/java/org/openxava/web/render/PropertyEditorRenderer.java` | `<br/>` eliminado, `ox-small-label-wrapper` ya existía |
| `src/main/java/org/openxava/web/render/ReferenceRenderer.java` | `<br/>` eliminado, `ox-small-label-wrapper` ya existía |
| `src/main/resources/META-INF/resources/xava/style/layout.css` | Sin cambios (revertido) |
| `openxavatest/src/main/resources/xava.properties` | defaultLabelFormat=SMALL (cambiado por el usuario) |

## Notas
- La versión de OpenXava es 8.0.
- El usuario prefiere testing manual desde el IDE.
- El usuario hizo un push con los cambios confirmados (puntos 1-4) antes de seguir con el problema del booleano.
- Los puntos 5 y 6 (min-height + vertical-align:top) están en el código pero NO resuelven completamente el problema del booleano.
- Hay que decidir si mantener los puntos 5 y 6 o revertirlos si no se encuentra solución.
