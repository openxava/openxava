# Plan: Migrar la generación dinámica de reportes de listado a la API Java de JasperReports (compatibilidad con JasperReports 7)

## Contexto / causa raíz

JasperReports 7 sustituyó el parser basado en Apache Commons Digester por Jackson XML para leer `*.jrxml`. Cualquier JRXML "fuente" creado o compatible con JasperReports 6.x o anterior ya no puede cargarse con la librería 7.x sola (`net.sf.jasperreports.engine.xml.JRXmlLoader` lanza `JRException: Unable to load report`, sin causa envuelta legible). No existe flag de compatibilidad ni conversor programático en la librería open source; solo Jaspersoft Studio 7 (GUI) puede convertir archivos `.jrxml` estáticos.

Esto afecta a dos escenarios distintos en OpenXava:

1. **Reportes `.jrxml` estáticos** creados por el usuario final (p. ej. `openxavatest/src/main/resources/informes/Invoice.jrxml`, usados vía `JasperReportBaseAction`). Estos los debe convertir el propio usuario con Jaspersoft Studio 7 (fuera del alcance de este plan, es responsabilidad de cada proyecto).
2. **Reportes de listado automáticos generados por OpenXava** (acción `GenerateReportAction` → `.pdf`/`.csv`/`.xls`), que dependen de `@/E:/IdeaProjects/openxava/openxava/src/main/resources/META-INF/resources/xava/jasperReport.jsp`, el cual genera un JRXML como texto en tiempo de ejecución. Esto es responsabilidad de OpenXava y **debe arreglarse en el core** (objeto de este plan).

## Objetivo

Eliminar la generación de JRXML como texto en `jasperReport.jsp` y sustituirla por la construcción programática del reporte usando la API de diseño de JasperReports (`net.sf.jasperreports.engine.design.*`), que es estable entre versiones y es el mecanismo recomendado por la propia documentación de `JRXmlLoader` para reportes generados dinámicamente en tiempo de ejecución.

## Archivos involucrados

- `@/E:/IdeaProjects/openxava/openxava/src/main/resources/META-INF/resources/xava/jasperReport.jsp` (a eliminar/deprecar tras la migración)
- `@/E:/IdeaProjects/openxava/openxava/src/main/java/org/openxava/web/servlets/GenerateReportServlet.java` (método `getReport()`, líneas ~213 y ~323-336)
- Nueva clase: `org.openxava.util.jasper.DynamicListReportBuilder` (o similar, paquete a decidir, p. ej. `org.openxava.web.report`)

## Pasos

1. **Crear `DynamicListReportBuilder`** (nueva clase Java) que replique exactamente la lógica actual del JSP:
   - Métodos de cálculo de anchos: `parseWidths`, `adjustWithsToLabels`, `calculateRowsInHeader`, `expandWidths`, `tightenWidths`, `getAlign`, `getMetaProperties` (portar tal cual, son puro cálculo, sin XML).
   - Construcción del reporte con `JRDesignReport` (o clase equivalente `JasperDesign` en JR7; confirmar nombre exacto de la clase raíz en 7.0.7, puede haber cambiado de paquete por el split de artefactos Jakarta) en lugar de emitir tags XML:
     - `setName`, `setColumnCount`, `setPrintOrder`, `setOrientation`, `setPageWidth/Height`, `setColumnWidth`, márgenes, `setWhenNoDataType`, etc. — un `set*` por cada atributo que hoy se pone como atributo XML en `<jasperReport>`.
     - Estilos (`Arial_Normal`, `Arial_Bold`, `Arial_Italic`) vía `JRDesignStyle`.
     - Parámetros (`Title`, `Organization`, `Date`, `<property>__TOTAL__`) vía `JRDesignParameter`.
     - Campos (`field`) vía `JRDesignField`.
     - Variable `Variable_1` vía `JRDesignVariable` + `JRDesignExpression`.
     - Bandas (`background`, `title`, `pageHeader`, `columnHeader`, `detail`, `pageFooter`, `summary`) vía `JRDesignBand`, añadiendo elementos:
       - `JRDesignStaticText` (etiquetas de cabecera de columna, textos fijos).
       - `JRDesignTextField` + `JRDesignExpression` (fecha, título, número de página, valores de fila, totales).
       - `JRDesignLine` (líneas separadoras).
       - `JRDesignRectangle` (fondo de cabecera de columna).
       - `JRDesignImage` + `JRDesignExpression` (columnas tipo `byte[]`).
     - Reproducir exactamente las mismas coordenadas/tamaños/condiciones (`orientation`, `letterWidth`, `letterSize`, `lineHeight`, `pageWidth/Height`, `columnWidth`) que hoy calculan las ramas `if/else` según `totalWidth`.
   - Compilar con `JasperCompileManager.compileReport(JasperDesign)` y devolver el `JasperReport` (compilado), no un `InputStream`.

2. **Verificar nombres de clases/paquetes en JasperReports 7.0.7**: dado el split de artefactos (`jasperreports`, `jasperreports-pdf`, `jasperreports-fonts`, `jasperreports-excel-poi`, `jasperreports-jdt`, ya presentes en `@/E:/IdeaProjects/openxava/openxava/pom.xml:135-168`), confirmar que `net.sf.jasperreports.engine.design.*` sigue en el artefacto core `jasperreports` (debería, ya que es la API de diseño, no un writer/parser de XML). Comprobar también si `JRDesignReport` sigue existiendo o fue renombrado/movido en 7.x (revisar javadoc 7.0.7).

3. **Modificar `GenerateReportServlet`**:
   - Sustituir `getReport(request, response, tab, tableModel, columnCountLimit)` (que hoy pide `/xava/jasperReport` como URI vía `Servlets.getURIAsStream`) por una llamada directa a `DynamicListReportBuilder.build(...)`, devolviendo un `JasperReport` ya compilado.
   - Cambiar `JasperFillManager.fillReport(is, parameters, ds)` por `JasperFillManager.fillReport(jasperReport, parameters, ds)` (overload que acepta `JasperReport` en lugar de `InputStream`).
   - Eliminar el import/uso de `Servlets.getURIAsStream` en este flujo si ya no se necesita.

4. **Retirar `jasperReport.jsp`** una vez verificado que nada más lo referencia (buscar otras referencias a `/xava/jasperReport` o `xava_reportTab` antes de eliminar).

5. **Pruebas de regresión**:
   - Revisar/crear test en `openxavatest` que genere un PDF de listado (`GenerateReportAction` con `type=pdf`) para un modelo con columnas anchas (para ejercitar la rama `Landscape`/`tightenWidths`) y uno estrecho (`Portrait`), y para un modelo con columna `byte[]` (imagen).
   - Comparar visualmente/estructuralmente el PDF generado antes (JasperReports 6.x, si se puede probar en rama aparte) y después del cambio.
   - Ejecutar manualmente `PrettyPrintingTest.txt` (mencionado en el comentario del propio `jasperReport.jsp`) si existe en `openxavatest/manual-tests/`.

## Fuera de alcance (responsabilidad del usuario final)

- Convertir los `.jrxml` estáticos de proyectos (`Invoice.jrxml`, `InvoiceNoVAT.jrxml`, `Customer.jrxml`, `Film.jrxml`, `Images.jrxml`, `Products.jrxml` en `openxavatest`, y cualquier otro en proyectos de usuario) con Jaspersoft Studio 7 (clic derecho sobre carpeta → "Update JasperReports files"). `JasperReportBaseAction` no requiere cambios de código; simplemente necesita que el `.jrxml` en disco esté en formato v7.

## Riesgos

- La API de diseño de JasperReports 7 podría haber renombrado algunas clases (`net.sf.jasperreports.engine.design.*`) al dividir artefactos para Jakarta; se debe confirmar en el javadoc 7.0.7 antes de codificar.
- Reproducir con exactitud el layout pixel-a-pixel del JSP actual requiere cuidado, especialmente en `columnHeader`/`detail` (posiciones `x` acumuladas por columna) y en los casos límite de `tightenWidths` (que elimina columnas si no caben).
- Compatibilidad con estilos de fuente (`DejaVu Sans`, `Identity-H`) usados para PDF con `pdfEmbedded="true"`: confirmar que se configuran igual vía `JRDesignStyle` (setters `setFontName`, `setPdfFontName`, `setPdfEncoding`, `setPdfEmbedded`).
