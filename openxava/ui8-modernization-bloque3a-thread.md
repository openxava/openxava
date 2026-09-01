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

## Defecto 3: Totales editables sin hover ni anillo de foco

**Problema**: En las celdas de totales editables (p. ej. *Taxes rate* en `Quote`), al pasar el ratón no aparecía el fondo de hover y al pulsar/editar no se mostraba el borde azul de foco que sí se ve en las celdas de datos.

**Causa raíz**: Las celdas de totales se renderizan con `ox-total-cell`, no con `ox-editable-cell`. El estilo spreadsheet aplica hover y `:focus-within` únicamente a `td.ox-editable-cell` y, además, la regla de hover estaba limitada a filas `.ox-list-pair`/`.ox-list-odd`, no a `.ox-total-row`.

### Cambios

#### 1. `collectionTotals.jsp` — marcar totales editables

En el bucle que genera las celdas de totales, se consulta `subview.isCollectionTotalEditable(i, c)` y se añade `ox-editable-cell` a la clase del `<td>` cuando el total es editable:

```jsp
boolean totalEditable = subview.isCollectionTotalEditable(i, c);
%>
<td class="ox-total-cell <%=align%> <%=totalEditable ? "ox-editable-cell" : "">">
```

#### 2. `base.css` — extender hover a filas de totales

Se añade `.ox-total-row td.ox-editable-cell:hover` a la regla de fondo de hover:

```css
.ox-element-collection .ox-list .ox-list-pair td.ox-editable-cell:hover,
.ox-element-collection .ox-list .ox-list-odd td.ox-editable-cell:hover,
.ox-element-collection .ox-list .ox-total-row td.ox-editable-cell:hover {
    background-color: var(--element-collection-cell-hover-background);
}
```

La regla `:focus-within` ya cubría cualquier `td.ox-editable-cell`, por lo que con la nueva clase el anillo azul también aparece en los totales editables.

**Estado**: verificado manualmente; el input de totales ahora muestra hover y el borde de foco igual que el resto de celdas editables.

---

## Ajuste 4: Alineación de totales editables

**Problema**: En el total editable (p. ej. *Taxes rate* en `Quote`), el valor aparecía ligeramente desplazado a la izquierda respecto a los totales de solo lectura y a las celdas de datos de la misma columna.

**Causa**: Los valores read-only usan un `<nobr>valor&nbsp;</nobr>` que deja un pequeño margen a la derecha (~0.25em). El input editable de `ox-total-cell` heredaba `padding-right: var(--space-2)` (8 px) de `.ox-element-collection .editor`, dejando el texto demasiado a la izquierda.

**Solución**: En `base.css` se añade una regla específica para reducir el `padding-right` del `.editor` en celdas de total editables y compensar el borde transparente de 1 px:

```css
.ox-total-cell.ox-editable-cell.ox-text-align-right .editor {
    /* Match the right gap created by the &nbsp; in read-only totals */
    padding-right: calc(0.25em - 1px);
}
```

**Estado**: verificado manualmente; el total editable se alinea con el resto de totales y celdas de su columna.

---

## Ajuste 5: Hover en celdas no editables del diálogo de importar

**Problema**: En el diálogo de importar datos, las columnas de solo lectura (`headerInFile`, `sampleContent1` y `sampleContent2`) mostraban el mismo sombreado de hover que la celda editable `nameInApp`.

**Causa raíz**: `ImportColumn` usaba `@Stereotype("LABEL")` para esas propiedades. El estereotipo cambiaba el editor, pero no hacía que la propiedad fuera realmente no editable a nivel de `View`; por tanto `subview.isEditable()` devolvía `true` y las celdas se marcaban como `ox-editable-cell`, recibiendo el fondo de hover del estilo spreadsheet.

**Solución**: Reemplazar `@Stereotype("LABEL")` por `@ReadOnly` en las propiedades de solo lectura de `ImportColumn`:

```java
@ReadOnly
private String headerInFile;

@ReadOnly
private String sampleContent1;

@ReadOnly
private String sampleContent2;
```

De este modo, `subview.isEditable()` devuelve `false` para esas celdas, se renderizan como `ox-readonly-cell` y `readOnlyAsLabel` muestra su valor como texto plano sin recibir el hover de celdas editables.

**Archivo principal tocado**: `ImportColumn.java`. No fue necesario modificar `elementCollectionEditor.jsp` ni `base.css`.

---

## Archivos modificados

| Archivo | Cambio |
|---|---|
| `ImportColumn.java` | Propiedades del diálogo de importar como `@ReadOnly` (fix hover en celdas no editables) |
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
| `collectionTotals.jsp` | Marca `ox-editable-cell` en celdas de totales editables |
| `base.css` | Estilo spreadsheet, fixes de alineación (incl. total editable `Taxes rate`), tokens CSS, hover/focus de totales editables |
| `changelog.txt` | Entradas de changelog |

---

## Resumen de ajustes visuales finales (conversación de revisión)

Durante la revisión visual del grid de `@ElementCollection` se aplicaron los siguientes retoques en `base.css` para pulir la apariencia spreadsheet:

- **Líneas de fila a ancho completo**: El borde de `2px` de `.ox-element-collection .ox-list` ocultaba la última fracción de las líneas horizontales. Se eliminó con `border: none`.
- **Alineación derecha de cabeceras numéricas**: Las cabeceras de columnas numéricas (`th.ox-list-header`) se alinean a la derecha (`ox-text-align-right`) y el `padding-right` se compensa para columnas editables (`ox-editable-column`) de modo que el borde derecho del texto de la cabecera coincida exactamente con el de los valores de las celdas.
- **Espaciado del total en esquina inferior derecha**: Se añadió `padding-right: var(--space-2)` a la última columna (th, td y totales) y `padding-bottom: var(--space-2)` a la última fila de totales para equilibrar el resumen sin desalinear los valores.
- **Símbolo de moneda flotante**: El `<b>` con el símbolo `$` del `MoneyEditor` se oculta dentro de `.ox-element-collection` para evitar ruido visual.
- **Iconos de sumatorio (Σ)**: Los enlaces de `.ox-total-capable-cell a` pasan a `opacity: 0` por defecto y se revelan con `tr:hover` o `tr:focus-within`, manteniendo la funcionalidad.
- **Handles de resize (línea vertical)**: El antiguo icono PNG se reemplaza por una línea vertical CSS de 2 px centrada en el borde derecho del `th`; el asidero interactivo es invisible y su hover/focus resalta la línea con el color de acento. El cursor se alinea exactamente con la línea y se corrige el efecto "huidizo" en columnas numéricas alineadas a la derecha.

### Notas y próximos pasos pendientes de la conversación

- Pendiente de verificar en iPad/táctil cómo se comportan los efectos de hover.
- Actualizar las capturas PNG con el estado final.

**Archivo principal tocado**: `base.css`.

---

## Ajuste 6: Booleano read-only como icono check

**Problema**: Los booleanos read-only en element collections se mostraban como un checkbox deshabilitado (switch), lo que resultaba estéticamente tosco dentro del grid spreadsheet.

**Decisión**: En formularios verticales el booleano read-only sigue siendo un switch deshabilitado (coherente con campos editables). Pero en element collections (cuando `readOnlyAsLabel` está activo), se renderiza `true` como un icono `mdi-check` con color de acento y `false` como espacio en blanco (`&nbsp;`).

### Cambios

#### 1. `booleanEditor.jsp`

Se añadió lógica para detectar `readOnlyAsLabel` (además del global `XavaPreferences.isReadOnlyAsLabel()`). Cuando `label` es `true` y el campo no es editable, se renderiza un `<span>` con clase `ox-boolean-check` (para `true`) o `ox-boolean-false` (para `false`) en lugar del `<input type="checkbox">`:

```jsp
boolean label = org.openxava.util.XavaPreferences.getInstance().isReadOnlyAsLabel() || "true".equalsIgnoreCase(request.getParameter("readOnlyAsLabel"));
```

```jsp
<% if (editable || !label) { %>
<INPUT ... class="... ox-switch" ... />
<% } else { %>
<span id="<%=propertyKey%>" class="ox-label-editor <%=Boolean.TRUE.equals(value)?"ox-boolean-check":"ox-boolean-false"%>"><% if (Boolean.TRUE.equals(value)) { %><i class="mdi mdi-check"></i><% } else { %>&nbsp;<% } %></span>
<% } %>
```

#### 2. `base.css`

```css
.ox-boolean-check .mdi-check {
    color: var(--accent-color);
    font-size: 1.1em;
    vertical-align: -2px;
}

.ox-boolean-false {
    color: var(--label-color);
    opacity: 0.4;
}
```

**Estado**: verificado visualmente; el check en color de acento y el false vacío quedan coherentes con el estilo spreadsheet.

---

## Ajuste 7: Padding izquierdo de la primera columna

**Problema**: En element collections donde la primera columna no es numérica (p. ej. `ReallocationDetailsReadOnly` con `place` como primera columna), la etiqueta y el valor aparecían demasiado cerca del borde izquierdo.

**Solución**: Se añadió `padding-left` a la primera columna (cabecera y datos) equiparable al `padding-right` de la última columna:

```css
.ox-element-collection th:first-child, .ox-element-collection td:first-child {
    padding-left: var(--space-3) !important;
}

.ox-element-collection th:last-child, .ox-element-collection td:last-child {
    padding-right: var(--space-2) !important;
}
```

Se probó primero con `--space-2` (8px) y finalmente se asentó en `--space-3` (12px) para la izquierda, dando más respiración visual.

**Archivo tocado**: `base.css`.

---

## Bug fix 8: Formatter de `@Editor` no se aplicaba en element collections read-only

**Problema**: `ReallocationDetailsReadOnlyTest` fallaba esperando `"VALENCIA OFFICE"` pero obteniendo `"Valencia Office"` tras el cambio a `<xava:editor readOnlyAsLabel="true">`.

**Causa raíz**: El código anterior de `elementCollectionEditor.jsp` calculaba `fvalue` manualmente con `view.getViewName()` (el nombre de la vista padre), mientras que `EditorTag` usa `inElementCollection ? "" : view.getViewName()` (cadena vacía para element collections). Ese `viewName` diferente hacía que `WebEditors.getMetaEditorFor()` no resolviera el editor `PlaceName` (registrado con `@Editor("PlaceName")` en `ReallocationDetail.place`), sino que cayera al `TextField` genérico, que no tiene formatter — devolviendo el valor en bruto de la BD (`"VALENCIA OFFICE"`).

Con el cambio a `<xava:editor>`, el `EditorTag` resuelve correctamente el editor `PlaceName` y aplica el `NameFormatter` (con `firstLetterInUpperCase=true`), produciendo `"Valencia Office"` — exactamente igual que en modo lista.

**Conclusión**: El comportamiento anterior era un bug. Las element collections read-only ahora formatean igual que las listas, que es lo correcto.

**Solución**: Se actualizó el test `ReallocationDetailsReadOnlyTest.java` para esperar los valores formateados correctamente:
- `"VALENCIA OFFICE"` → `"Valencia Office"`
- `"CASA EN MICHIGAN"` → `"Casa En Michigan"`

No fue necesario añadir el atributo `fromList` al `<xava:editor>` ni modificar `EditorTag.java`, ya que el formatter regular (no el list-formatter) es el que debe aplicarse en este caso, y `EditorTag` ya lo hace correctamente con `fromList=false` por defecto.

**Archivos tocados**: `ReallocationDetailsReadOnlyTest.java` (test actualizado), `changelog.txt` (entrada de bug fix).

---

## Archivos modificados (actualización)

| Archivo | Cambio |
|---|---|
| `booleanEditor.jsp` | `readOnlyAsLabel` + renderizado como icono check / vacío |
| `base.css` | Estilos `ox-boolean-check` / `ox-boolean-false`, padding primera columna |
| `ReallocationDetailsReadOnlyTest.java` | Valores esperados actualizados a formato `NameFormatter` |
