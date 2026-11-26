# Retirada gradual de `Style`: traspaso

## Contexto y decisión

`org.openxava.web.style.Style` nació para que los portlets de OpenXava adoptaran
el aspecto del portal anfitrión (Liferay, WebSphere, etc.). Permitía cambiar
clases CSS y estructura HTML. OpenXava retiró el soporte de portales en 7.0 y
marcó `Style` como obsoleta en 7.1. La clase mezcla CSS, HTML, imágenes,
JavaScript y opciones de comportamiento.

**8.1 es una versión menor: debe ser compatible con 8.0 y no exigir pasos de
migración.** No modificar ni eliminar `Style.java`. Reservar la retirada de la
clase y de su contrato de personalización para 9.0. Que esté obsoleta no
significa que nadie la use: se enseñó en la documentación y muchos editores
personalizados se basan en los JSP estándar.

## Criterio para 8.1

- Quitar usos internos solo cuando no se pierda comportamiento de 8.0. Usar
  clases CSS literales en el marcado cuando el valor sea realmente fijo.
- Conservar una llamada explícita a `Style` si sustituirla impediría que una
  subclase personalice el resultado. Por ejemplo, `getEditor()` devuelve
  `editor` por defecto, pero una subclase configurada con `styleClass` puede
  cambiarlo. No sustituirlo sin más por `class="editor"` en un editor estándar.
- No crear una fachada que solo cambie el nombre de la llamada. Se probó
  `EditorClasses`, que devolvía los valores por defecto y delegaba en `Style`
  para subclases. El JSP seguía sin mostrar la clase CSS y la dependencia
  quedaba oculta. **Se retiró `EditorClasses` y se revirtieron sus cambios en
  los 23 JSP. No retomar ese enfoque.**
- Mantener el atributo `style` de la petición para JSP antiguos. Revisar
  también las inclusiones estáticas: pueden depender de la variable JSP
  `style` que declara el editor incluido, no solo del atributo de petición.
- Actualizar los ejemplos de personalización para que el código nuevo no
  use `Style`, sin dar a entender que actualizar editores existentes sea
  obligatorio en 8.1. No confundir `Style` con `Themes`, `IThemeProvider` o
  `styleCSS`: la selección de temas es una funcionalidad distinta.

## Estado al cerrar esta conversación

- `Style.java` sigue intacto salvo una nota en el Javadoc: se retirará en v9.0.
  `XavaStyle.java` sin cambios.
- **Todos los usos internos de `Style` eliminados**: los renderers Java, los
  ~37 JSP de `xava/editors`, los tags y los formatters ahora usan los valores
  fijos que resolvía `XavaStyle` (clases `ox-*`, `xava/images`, ajustes 17/-40,
  flags `isUseIconsInsteadOfImages` vía `XavaPreferences`, etc.). Si `Style`
  desapareciera, OpenXava seguiría renderizando igual, ignorando `styleClass`.
- **Único uso conservado** (contrato para editores JSP de clientes):
  `request.setAttribute("style", Style.getInstance(...))` en `BaseServlet`,
  `HotwireServlet` y `ModulePageRenderer`. Los `jsp:useBean id="style"` de
  aplicaciones cliente siguen funcionando con el estilo configurado.
- `ModuleManager`: `getDefaultModeController` por el literal `"Mode"`.
- `TreeViewParser.createMetaTreeView` sin el parámetro `Style`
  (`treeViewListEditor.jsp` actualizado).
- `ViewRenderContext.getStyle()` eliminado (sin llamadores).
- `LayoutCells.preLabel/preEditor` sin el parámetro `Style`.
- Documentación: ejemplos de `customizing_en/es.html` con `class="editor"`.
- `openxavatest/.../importantEditor.jsp` sigue usando `Style` (regresión del
  contrato del atributo; lo ejercita `DebtTest`).
- `mvn -o -B compile` pasó. Los JSP no se compilan con Maven: revisar
  renderizado en IDE.

## Por dónde seguir

Las rutas son relativas a la raíz del repositorio:

1. Inventariar usos con `git grep -n -E 'Style\.getInstance|getStyle\(\)|jsp:useBean id="style"|XavaStyle' -- openxava/src/main`.
   Clasificar antes de tocar: valor fijo, inicialización del bean para JSP,
   personalización por subclase o comportamiento de renderizado. Una
   declaración JSP sin llamadas visibles no es necesariamente código muerto:
   revisar inclusiones y etiquetas que leen el atributo.
2. Revisar `openxava/src/main/java/org/openxava/web/style/Style.java` y los
   consumidores en `org.openxava.web.render`, `BaseServlet`, `HotwireServlet`
   y `openxava/src/main/resources/META-INF/resources/xava/editors/`.
   `styleClass` selecciona el estilo de escritorio. Para móvil, revisar
   `Style.getInstance(request)`, `xava.renderOverride` y `phoneStyleClass`.
   `com.openxava.phone.web.PhoneStyle` no está en este checkout; comprobar
   XavaPro antes de cambiar esa ruta. Para 9.0, `Parts.registerOverride` es
   un punto de partida si móvil necesita HTML distinto, no solo otro tema.
3. Actualizar `openxava-doc/web/docs/customizing_en.html` y su equivalente
   en español. Los ejemplos antiguos declaran `jsp:useBean id="style"` y
   llaman a `style.getEditor()`. Un editor estándar que los usuarios pudieron
   copiar es `openxava/src/main/resources/META-INF/resources/xava/editors/textEditor.jsp`.
4. Verificar desde el IDE, como pide `openxavatest/AGENTS.md`.
   `openxavatest/src/main/webapp/xava/editors/importantEditor.jsp` sigue
   usando `Style` y `DebtTest.testCheckBoxEditorWithNotBooleanValue` lo
   ejercita junto a un editor estándar. `customWebURLEditor.jsp` es un ejemplo
   de inclusión estática de `textEditor.jsp`. Antes de dar por compatible un
   cambio de renderizado, probar un editor antiguo, un `styleClass` que cambie
   algo visible en un editor estándar, y la UI móvil de XavaPro. Revisar
   visualmente estilo y disposición y ejecutar la suite desde el IDE.

Para 9.0, preparar la nota de migración con ejemplos de CSS directo y cómo
adaptar los JSP copiados o que declaran `Style`. Eliminar el contrato antiguo
solo después de verificar las alternativas para los puntos de extensión que
realmente se usan.
