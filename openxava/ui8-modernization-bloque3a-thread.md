# Bloque 3a — ElementCollection: Hilo de trabajo

## Objetivo

Modernizar el estilo visual de las `@ElementCollection` a un estilo hoja de cálculo (spreadsheet), y corregir dos defectos visuales detectados tras la implementación inicial.

---

## Decisiones de diseño (estilo spreadsheet)

### Filosofía visual

- **Celdas transparentes sin bordes**: Se eliminaron los bordes de celda y el fondo de la tabla. Las celdas son transparentes para que el contenido respire.
- **Hairlines horizontales**: Separadores de fila con `border-bottom: 1px solid` usando un color sutil (`--element-collection-row-hover-background` con `color-mix`), en lugar de bordes completos o zebra.
- **Cabecera compacta y muted**: Column headers con `font-size: var(--font-size-xs)`, `font-weight: 600`, `text-transform: uppercase`, `letter-spacing: 0.06em`, color muted (`--label-color`). Padding reducido.
- **Hover solo en celdas editables**: El feedback de hover se aplica únicamente a `td.ox-editable-cell:hover`, no a toda la fila ni a celdas read-only. Fondo sutil con `--element-collection-cell-hover-background` (color-mix de accent al 7%).
- **Accent ring en celda activa**: `:focus-within` en `td.ox-editable-cell` aplica un `box-shadow` inset con `--focus-ring-color` (color-mix de accent al 20%), simulando un ring de selección tipo spreadsheet.
- **Acciones de fila reveladas en hover/focus**: Los botones de acción de fila (eliminar, etc.) permanecen ocultos y solo aparecen al hacer hover o focus sobre la fila.
- **Totals footer**: Borde superior hairline, tipografía con `font-weight: 600`, fondo transparente.

### Read-only como texto plano (`readOnlyAsLabel`)

- **Motivación**: En colecciones editables, las propiedades calculadas o read-only se mostraban como `<input disabled>`, lo que rompía la estética spreadsheet (inputs grises con bordes) y causaba problemas de alineación vertical.
- **Decisión**: Introducir el parámetro `readOnlyAsLabel` para que los editores rendericen un `<span>` con el valor formateado en lugar de un `<input disabled>`.
- **Compatibilidad con `@Calculation`**: El span incluye el `id` del property para que `openxava.calculate` pueda localizarlo y actualizarlo en cliente cuando cambien las dependencias.
- **Compatibilidad con tests HtmlUnit**: Se mantiene el `<input type="hidden">` para las propiedades read-only usadas en cálculos, de modo que los tests que verifican valores sigan funcionando.

### Tokens CSS introducidos

- `--element-collection-row-hover-background`: Fondo de hover de fila completa (color-mix de `--color` al 4%).
- `--element-collection-cell-hover-background`: Fondo de hover de celda editable (color-mix de `--accent-color` al 7%).

### Arquitectura del cambio `readOnlyAsLabel`

- **Flujo de renderizado inicial**: `elementCollectionEditor.jsp` → `<xava:editor readOnlyAsLabel="true">` → `EditorTag.java` pasa el parámetro al JSP del editor → el editor renderiza `<span>` en lugar de `<input disabled>`.
- **Flujo de re-renderizado AJAX**: `HotwireServlet.fillChangedPropertiesActionsAndReferencesWithNotCompositeEditor` detecta si el editor está dentro de una element collection (`isInsideElementCollection`) y añade `&readOnlyAsLabel=true` a la URL de `editorWrapper.jsp` → `editorWrapper.jsp` lee el parámetro y lo pasa al `<xava:editor>` tag.
- **Detección de element collection en AJAX**: Se usa `qualifiedName.contains(":")` como indicador de que la propiedad está dentro de una element collection (el separador `:` es propio de los qualified names de element collections).

---

## Defecto 1: Hairline de cabecera cortada por la celda de la lupa de búsqueda

**Problema**: La línea separadora de la cabecera aparecía cortada a la altura de la columna de la lupa de búsqueda.

**Causa**: El `<th>` vacío de la columna de búsqueda no tenía la clase `ox-list-header`, por lo que no recibía el `border-bottom` hairline.

**Solución**: Añadir `class="ox-list-header"` al `<th>` vacío en `elementCollectionEditor.jsp:116`.

---

## Defecto 2: Valores read-only desalineados verticalmente en líneas nuevas

**Problema**: En las líneas añadidas vía AJAX, los valores de solo lectura aparecían desalineados respecto a las líneas existentes. Además, tras un primer intento de fix con CSS (`display: flex`), los valores numéricos de "Importe" en filas existentes pasaron a alinearse a la izquierda.

**Causa raíz**: Las nuevas líneas añadidas vía AJAX no renderizaban los campos read-only como etiquetas de texto (spans), sino como inputs deshabilitados. El `readOnlyAsLabel` no se propagaba en las peticiones AJAX de `HotwireServlet`.

### Cambios realizados

#### 1. Propagar `readOnlyAsLabel` en AJAX (`HotwireServlet.java`)

En `fillChangedPropertiesActionsAndReferencesWithNotCompositeEditor`, se añade `&readOnlyAsLabel=true` a la URL de `editorWrapper.jsp` cuando el editor está dentro de una element collection:

```java
put(result, "editor_" + qualifiedName,
    "editorWrapper.jsp?propertyName=" + name +
    "&editable=" + containerView.isEditable(name) +
    "&throwPropertyChanged=" + containerView.throwsPropertyChanged(name) +
    "&viewObject=" + containerView.getViewObject() +
    "&propertyPrefix=" + containerView.getPropertyPrefix() +
    (isInsideElementCollection ? "&readOnlyAsLabel=true" : ""));
```

#### 2. Leer `readOnlyAsLabel` en `editorWrapper.jsp`

```jsp
boolean readOnlyAsLabel = "true".equals(request.getParameter("readOnlyAsLabel"));
%>
<xava:editor
    property='<%=propertyName%>'
    editable='<%=...%>'
    throwPropertyChanged='<%=...%>'
    readOnlyAsLabel='<%=readOnlyAsLabel%>'/>
```

#### 3. Añadir atributo `readOnlyAsLabel` a `EditorTag.java`

- Nuevo atributo `readOnlyAsLabel` con getter/setter.
- Se propaga como parámetro de request al JSP del editor.

#### 4. Registrar atributo en `openxava.tld`

Nuevo atributo `readOnlyAsLabel` en el tag `editor`.

#### 5. Honorear `readOnlyAsLabel` en los editores JSP

Cada editor JSP comprueba el parámetro y, cuando es `true` y el campo no es editable, renderiza un `<span>` con el valor formateado en lugar de un `<input disabled>`:

- `textEditor.jsp` — span con `id` del property para que `@Calculation` pueda actualizarlo.
- `dateCalendarEditor.jsp`
- `timeCalendarEditor.jsp`
- `dateTimeSeparatedCalendarEditor.jsp`
- `dateTimeCombinedCalendarEditor.jsp`
- `booleanYesNoEditor.jsp`
- `editableValidValuesEditor.jsp`
- `dynamicValidValuesEditor.jsp`
- `validValueEditorCommon.jsp`

#### 6. `elementCollectionEditor.jsp` — renderizado de celdas

- Las celdas se marcan como `ox-editable-cell` u `ox-readonly-cell` según si son editables.
- Las propiedades read-only se renderizan como texto formateado vía `readOnlyAsLabel="true"`.
- Se oculta la acción de búsqueda para referencias read-only.

#### 7. `openxava.js` — `@Calculation` actualiza spans

La función `openxava.calculate` ahora actualiza también elementos `<span>` (no solo `<input>`), para que los valores read-only mostrados como texto se recalculen en cliente.

#### 8. `base.css` — estilo spreadsheet y fixes de alineación

- Implementado estilo visual spreadsheet: celdas transparentes sin bordes, hairlines horizontales, cabecera compacta y muted, hover solo en celdas editables, accent ring en celda activa (`:focus-within`), acciones de fila reveladas en hover/focus.
- Totals footer con borde superior hairline y tipografía destacada.
- Se revertió el cambio de `display: flex` en `td.ox-readonly-cell > div` que rompía la alineación de valores numéricos.
- La regla `width: 100%` para `.editor` se acotó a `.ox-list-data-cell .editor` para no afectar a otros contextos.

---

## Archivos modificados

| Archivo | Cambio |
|---|---|
| `EditorTag.java` | Atributo `readOnlyAsLabel` + propagación |
| `openxava.tld` | Registro del atributo |
| `elementCollectionEditor.jsp` | Render read-only como texto, clases de celda, fix hairline |
| `textEditor.jsp` | `readOnlyAsLabel` + span con id |
| `dateCalendarEditor.jsp` | `readOnlyAsLabel` + span con id |
| `timeCalendarEditor.jsp` | `readOnlyAsLabel` + span con id |
| `dateTimeSeparatedCalendarEditor.jsp` | `readOnlyAsLabel` + span con id |
| `dateTimeCombinedCalendarEditor.jsp` | `readOnlyAsLabel` + span con id |
| `booleanYesNoEditor.jsp` | `readOnlyAsLabel` |
| `editableValidValuesEditor.jsp` | `readOnlyAsLabel` |
| `dynamicValidValuesEditor.jsp` | `readOnlyAsLabel` |
| `validValueEditorCommon.jsp` | `readOnlyAsLabel` |
| `openxava.js` | `calculate` actualiza spans |
| `HotwireServlet.java` | `readOnlyAsLabel=true` en AJAX de element collections |
| `editorWrapper.jsp` | Lee y pasa `readOnlyAsLabel` |
| `base.css` | Estilo spreadsheet, fixes de alineación, tokens CSS |
| `changelog.txt` | Entradas de changelog |
