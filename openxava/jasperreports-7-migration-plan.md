# Plan para actualizar OpenXava a JasperReports 7

## Situación actual

- **Versión actual**: JasperReports `6.21.5` (un único JAR monolítico), declarado en `openxava/pom.xml` (líneas 126-152).
- **Última versión**: `7.0.7` (rama 7.x, reorganizada en múltiples módulos Maven).

Uso de JasperReports en OpenXava:

- **`org/openxava/actions/JasperReportBaseAction.java`** y variantes (`JasperMultipleReportBaseAction`, `JasperConcatReportBaseAction`): compilan y rellenan informes con `JasperCompileManager` / `JasperFillManager`.
- **`org/openxava/web/servlets/GenerateReportServlet.java`**: informe automático del modo lista (PDF/CSV/XLS).
- **`org/openxava/web/servlets/GenerateCustomReportServlet.java`** y **`GenerateConcatReportServlet.java`**: exportan con la **API antigua de exportadores** (`JRExporter`, `JRExporterParameter`, `JRPdfExporterParameter`, `JRXlsExporter`, `JRRtfExporter`, `JROdtExporter`).
- **`org/openxava/web/servlets/JasperReportServlet.java`** + **`src/main/resources/META-INF/resources/xava/jasperReport.jsp`**: generan un **JRXML dinámicamente** en el modo lista.

## Cambios que rompen compatibilidad en JR 7 (según changelog oficial)

1. **Código deprecado eliminado**: la API antigua de exportadores (`JRExporterParameter`, `JRPdfExporterParameter`, `JRExporter.setParameter(...)`) **ya no existe**. Hay que migrar a la API basada en `setExporterInput()` / `setExporterOutput()` con `SimpleExporterInput`, `SimpleOutputStreamExporterOutput`, `SimplePdfReportConfiguration`, etc.
2. **Modularización**: el JAR monolítico se dividió. Funcionalidades como el exportador PDF, POI (Excel), Open Document (ODT/ODS) y el compilador JDT están ahora en artefactos separados.
3. **Cambios de paquetes**: al extraer módulos, algunos nombres de paquete cambiaron (p.ej. el exportador PDF).
4. **Incompatibilidad del formato JRXML/`.jasper`**: se sustituyó el parser basado en Apache Commons Digester por Jackson XML. **Los `.jrxml` creados con la versión 6 o anterior no se pueden cargar con la 7**; requieren conversión con Jaspersoft Studio 7.
5. **JFreeChart 1.5.4**: sin soporte 3D (gráficos Pie/Bar 3D se renderizan en 2D).

## Riesgo principal (crítico)

**El JRXML generado en `jasperReport.jsp` usa el formato DTD legacy** (`<reportFont>`, `orientation`/`printOrder` como atributos, `<graphicElement>`, etc.). Ese formato antiguo casi con seguridad **no será parseable por JR 7** (nuevo parser Jackson). Este es el mayor foco de trabajo: hay que **reescribir `jasperReport.jsp`** al esquema JRXML moderno.

Además, los `.jrxml` de ejemplo en `openxavatest` (`Customer.jrxml`, `Invoice.jrxml`, etc.) también apuntan al esquema antiguo (`http://jasperreports.sourceforge.net/xsd/jasperreport.xsd`) y probablemente necesiten reconversión.

## Plan propuesto

### Fase 1 — Dependencias (`pom.xml`)
- Subir `jasperreports` y `jasperreports-fonts` a `7.0.7`.
- Añadir los módulos ahora separados que OpenXava usa: `jasperreports-pdf` (PDF), `jasperreports-poi` (Excel), `jasperreports-open-document` (ODT), y `jasperreports-jdt` (compilación con ECJ). *(Los IDs exactos se confirman contra el POM de 7.0.7 al implementar.)*
- Revisar/actualizar las exclusiones de Jackson (JR 7 depende de Jackson XML) y las de `commons-io`/`poi` para evitar conflictos de versión.

### Fase 2 — Migración de la API de exportadores
- Reescribir `GenerateCustomReportServlet` y `GenerateConcatReportServlet` con la nueva API (`JRPdfExporter`, `JRXlsExporter`, `JRRtfExporter`, `JROdtExporter` + `SimpleExporterInput` / `SimpleOutputStreamExporterOutput` y clases de configuración). Sustituir `JRPdfExporterParameter.JASPER_PRINT_LIST` por `SimpleExporterInput` con lista de `JasperPrint`.
- Ajustar imports de paquetes cambiados.

### Fase 3 — JRXML dinámico (lo más delicado)
- Reescribir `jasperReport.jsp` al esquema JRXML 7: reemplazar `<reportFont>` por `<style>`, mover `orientation`/`printOrder` a `<property>`/atributos válidos, actualizar fuentes/`pdfEncoding`, y validar la compilación con `JasperCompileManager`.
- Revisar `JasperReportServlet` y las referencias a `jasper.reports.compile.class.path` / `/WEB-INF/lib/jasperreports.jar` (ese JAR ya no existe con ese nombre en 7.x).

### Fase 4 — Reconversión de `.jrxml` de prueba
- Convertir los `.jrxml` de `openxavatest` (`reports/`, `informes/`) al formato 7 con Jaspersoft Studio 7 (manual, no automatizable).

### Fase 5 — Verificación
- `mvn compile` en `openxava` para validar compilación.
- Pruebas manuales de generación de informes (los tests de `openxavatest/manual-tests/PrettyPrintingTest.txt` cubren el JSP; también `ChartsTest.txt`, etc.), ejecutadas desde el IDE.
- Documentar en `changelog.txt` y en la guía de migración de `openxava-doc`.

## Preguntas abiertas antes de implementar

- Confirmar los IDs exactos de los módulos Maven de JR 7.0.7 (PDF, POI, Open Document, JDT).
- Decidir si se reescribe `jasperReport.jsp` al nuevo esquema o se genera el diseño programáticamente (`JasperDesign`) en lugar de JSP.
- Verificar si los gráficos 3D se usan en algún informe (JFreeChart 1.5.4 los renderiza en 2D).
