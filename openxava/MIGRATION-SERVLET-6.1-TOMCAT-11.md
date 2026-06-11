# Plan de migración: Servlet 4 / Tomcat 9 → Servlet 6.1 / Tomcat 11.0.22

Documento de planificación para migrar `openxava` y `openxavatest` desde la API
**`javax.servlet` (Servlet 4.0, Jakarta EE 8)** sobre **Tomcat 9** a la API
**`jakarta.servlet` (Servlet 6.1, Jakarta EE 11)** sobre **Tomcat 11.0.22**.

> Estado de partida relevante: el `pom.xml` de `openxava` ya está migrado a Jakarta
> en su capa de persistencia/validación (Hibernate ORM 7.2, Hibernate Validator 9.1,
> Expressly 6, `jakarta.json.bind`). **La única capa que sigue en `javax` es la web
> (Servlet + WebSocket + JSP/JSTL + Tomcat embebido + commons-fileupload).** Por eso
> esta migración es el paso natural que falta para cerrar el salto a Jakarta EE.

---

## 1. Contexto técnico

### 1.1. Correspondencia de versiones

| Componente | Actual (javax) | Objetivo (jakarta) |
|---|---|---|
| Servlet API | `javax.servlet:javax.servlet-api:4.0.1` | `jakarta.servlet:jakarta.servlet-api:6.1.0` |
| WebSocket API | `javax.websocket:javax.websocket-api:1.1` | `jakarta.websocket:jakarta.websocket-api:2.2.0` |
| JSP | JSP 2.3 | JSP 4.0 (incluido en Tomcat 11) |
| EL | EL 3.0 | EL 6.0 (`expressly:6.0.0` ya presente) |
| JSTL | `jstl:jstl:1.2` | `org.glassfish.web:jakarta.servlet.jsp.jstl:3.0.1` + `jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api:3.0.0` |
| Tomcat embebido | `tomcat-embed-*:9.0.118` | `tomcat-embed-*:11.0.22` |
| Tomcat DBCP | `tomcat-dbcp:9.0.118` | `tomcat-dbcp:11.0.22` |
| Commons FileUpload | `commons-fileupload:commons-fileupload:1.6.0` | `org.apache.commons:commons-fileupload2-jakarta-servlet6:2.0.0` |

**Tomcat 11.0.x** implementa **Jakarta Servlet 6.1, JSP 4.0, EL 6.0 y WebSocket 2.2**
(Jakarta EE 11). Requiere **Java 17 como mínimo** (ya estamos en `maven.compiler.source=17`).

### 1.2. Cambio fundamental de namespace

Todo el código y descriptores deben cambiar el prefijo de paquete:

```
javax.servlet.*    →  jakarta.servlet.*
javax.servlet.http.*  →  jakarta.servlet.http.*
javax.servlet.jsp.*   →  jakarta.servlet.jsp.*
javax.websocket.*  →  jakarta.websocket.*
```

---

## 2. Inventario del impacto (análisis del código actual)

### 2.1. Proyecto `openxava`

| Elemento | Cantidad / Ubicación | Acción |
|---|---|---|
| Clases Java con `import javax.servlet.*` | **137 ficheros** (`org.openxava.web.*`, `org.openxava.actions.*`, `com.openxava.naviox.*`, taglibs, filtros, listeners, servlets) | Reescribir imports a `jakarta.servlet.*` |
| Clases con `import javax.websocket.*` | **2 ficheros**: `org/openxava/chat/ChatEndpoint.java`, `org/openxava/chat/GetHttpSessionConfigurator.java` | Reescribir a `jakarta.websocket.*` |
| JSPs | **115 ficheros** en `src/main/resources/META-INF/resources/` | Revisar directivas `taglib`/`page import` |
| JSPs con URIs Sun antiguas | **2 ficheros** (`xava/imports.jsp`, `xava/jasperReport.jsp`) | Actualizar URIs JSTL |
| TLD | `META-INF/resources/WEB-INF/openxava.tld` | Migrar DTD/namespace a JSP 4.0 |
| `web-fragment.xml` | `META-INF/web-fragment.xml` (namespace `xmlns.jcp.org`, version 3.1) | Migrar a `jakarta.ee` namespace y version 6.x |
| Tomcat embebido | `org/openxava/util/AppServer.java` | Subir a 11.0.22 y validar API Catalina |
| Commons FileUpload | `ModuleManager`, `WithFileItem`, `WithExcelCSVFileItem`, `FileItemUpload`, `IProcessLoadedFileAction`, varias `*Action` | Migrar a `commons-fileupload2-jakarta-servlet6` (**API pública afectada**) |

### 2.2. Proyecto `openxavatest`

| Elemento | Cantidad / Ubicación | Acción |
|---|---|---|
| Clases Java con `javax.servlet` | **10 ficheros** (`servlets/TestServlet`, `filters/YearParameterFilter`, formatters, actions) | Reescribir imports a `jakarta.servlet.*` |
| JSPs | **23 ficheros** | Revisar directivas |
| `web.xml` | `src/main/webapp/WEB-INF/web.xml` (DTD 2.3) | Migrar a `web-app` Servlet 6.1 |
| Dependencias de test | Selenium 4.13, GSON | Verificar compatibilidad (sin cambios de namespace) |

### 2.3. Puntos calientes (mayor riesgo)

1. **`ModuleManager.java`** (44 referencias a tipos servlet + uso de `ServletFileUpload`/`DiskFileItemFactory`). Es el corazón del framework web.
2. **`commons-fileupload` → `commons-fileupload2`**: cambio de API **no trivial**:
   - `org.apache.commons.fileupload.FileItem` → `org.apache.commons.fileupload2.core.FileItem`
   - `DiskFileItemFactory` ahora usa patrón *builder*.
   - `ServletFileUpload` → `org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload`.
   - **`FileItem` está expuesto en la API pública de OpenXava** (`WithFileItem`, anotación `@FileItemUpload`, `IProcessLoadedFileAction.setFileItems(List)`), por lo que el cambio impacta a las aplicaciones de usuario → requiere nota en el `changelog` y posiblemente documentación de migración.
3. **`AppServer.java`** (Tomcat embebido): los paquetes `org.apache.catalina.*` se mantienen en Tomcat 11, pero hay que validar `addWebapp`, `Rfc6265CookieProcessor`, `StandardRoot`/`DirResourceSet` y `StandardJarScanFilter` contra la nueva versión.
4. **WebSocket del chat**: `request.getHttpSession()` y el `Configurator` cambian a `jakarta.websocket`. La dependencia `javax.websocket-api:1.1` debe sustituirse.
5. **JSTL**: el `jstl:jstl:1.2` actual es incompatible con Jakarta; las URIs de los taglibs cambian (`http://java.sun.com/jsp/jstl/core` → `jakarta.tags.core`).

---

## 3. Estrategia de migración

Migración **"big bang" por capas** dentro de una rama dedicada (p.ej. `feature/servlet-6.1`),
ya que `javax.servlet` y `jakarta.servlet` **no pueden coexistir** en el mismo classpath de
ejecución para el mismo contenedor. El orden recomendado:

1. Dependencias (`pom.xml`) → 2. Código Java (imports) → 3. Descriptores y TLD →
4. JSPs/JSTL → 5. commons-fileupload2 → 6. Tomcat embebido → 7. `openxavatest` →
8. Compilar, arrancar y pasar tests.

> **Recomendación de herramienta**: usar el **Eclipse Transformer** o el
> **Apache Tomcat Migration Tool for Jakarta EE** para automatizar el grueso del
> renombrado `javax.* → jakarta.*` en las 137+10 clases y descriptores, y revisar
> a mano los puntos calientes (fileupload, AppServer, websocket).

---

## 4. Plan detallado por fases

### Fase 0 — Preparación
- [ ] Crear rama `feature/servlet-6.1`.
- [ ] Confirmar JDK 17+ en el entorno de build y en Tomcat 11.
- [ ] Descargar/instalar Tomcat 11.0.22 para pruebas de despliegue WAR (además del embebido).
- [ ] Hacer copia de seguridad / commit limpio antes de los renombrados masivos.

### Fase 1 — Dependencias `openxava/pom.xml`
- [ ] Sustituir `javax.servlet:javax.servlet-api:4.0.1` →
      `jakarta.servlet:jakarta.servlet-api:6.1.0` (scope `provided`).
- [ ] Sustituir `javax.websocket:javax.websocket-api:1.1` →
      `jakarta.websocket:jakarta.websocket-api:2.2.0` (scope `provided`).
- [ ] Sustituir los 4 artefactos Tomcat `9.0.118` → `11.0.22`
      (`tomcat-embed-core`, `tomcat-embed-jasper`, `tomcat-embed-websocket`, `tomcat-dbcp`).
- [ ] Sustituir `jstl:jstl:1.2` por:
      - `org.glassfish.web:jakarta.servlet.jsp.jstl:3.0.1`
      - `jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api:3.0.0`
- [ ] Sustituir `commons-fileupload:commons-fileupload:1.6.0` →
      `org.apache.commons:commons-fileupload2-jakarta-servlet6:2.0.0`.
- [ ] Revisar `javax.inject` / `javax.ejb` / `javax.mail`: **no forman parte de esta
      migración Servlet**. Se pueden dejar como están (anotación pendiente para una
      migración futura a `jakarta.inject` / `jakarta.mail`), salvo que provoquen
      conflicto de classpath.

### Fase 2 — Código Java (`openxava`)
- [ ] Renombrar imports en las **137 clases**: `javax.servlet` → `jakarta.servlet`
      (preferiblemente con el Tomcat Migration Tool, luego revisión manual).
- [ ] Renombrar `javax.websocket` → `jakarta.websocket` en `ChatEndpoint.java` y
      `GetHttpSessionConfigurator.java`.
- [ ] Revisar APIs eliminadas/cambiadas en Servlet 6.1 respecto a 4.0:
      - `HttpServletRequest#isRequestedSessionIdFromUrl` (eliminado).
      - `HttpServletResponse#encodeUrl`/`encodeRedirectUrl` (eliminados; usar `encodeURL`).
      - `SingleThreadModel`, `HttpSessionContext`, `HttpUtils` (eliminados).
      - Verificar `getRealPath`, codificación de caracteres por defecto (UTF-8 en EE 11).

### Fase 3 — Descriptores y TLD (`openxava`)
- [ ] `META-INF/web-fragment.xml`: cambiar namespace
      `http://xmlns.jcp.org/xml/ns/javaee` → `https://jakarta.ee/xml/ns/jakartaee`,
      `schemaLocation` a `web-fragment_6_0.xsd` y `version="6.0"`.
- [ ] `META-INF/resources/WEB-INF/openxava.tld`: migrar del DOCTYPE/DTD Sun JSP 1.1 a
      el formato `<taglib>` JSP 4.0 con namespace
      `https://jakarta.ee/xml/ns/jakartaee` y `web-jsptaglibrary_3_1.xsd` (`version="3.1"`).
      Renombrar tags `<tagclass>`→`<tag-class>`, `<bodycontent>`→`<body-content>`,
      `<tlibversion>`→`<tlib-version>`, etc.

### Fase 4 — JSPs y JSTL (`openxava`)
- [ ] Actualizar URIs JSTL en `xava/imports.jsp`:
      - `http://java.sun.com/jstl/core` → `jakarta.tags.core`
      - `http://java.sun.com/jstl/fmt` → `jakarta.tags.fmt`
- [ ] Revisar `xava/jasperReport.jsp` y cualquier JSP con `<%@ page import="javax.servlet..."%>`.
- [ ] Barrer las 115 JSPs en busca de URIs `java.sun.com` y tipos `javax.servlet` en scriptlets.

### Fase 5 — Commons FileUpload 2 (punto crítico, `openxava`)
- [ ] Migrar `ModuleManager.processMultipartRequest` (líneas ~1144-1152):
      `DiskFileItemFactory` (builder) + `JakartaServletFileUpload`.
- [ ] Cambiar el tipo público `FileItem` en:
      `WithFileItem`, `WithExcelCSVFileItem`, `@FileItemUpload`,
      `IProcessLoadedFileAction`, `LoadFileItemAction`, `LoadAttachedFileAction`,
      `LoadImageAction`, `LoadImageIntoGalleryAction`, `LoadIntoAttachedFilesAction`.
- [ ] Verificar métodos usados de `FileItem` (`getName`, `getInputStream`, `getSize`,
      `isFormField`, `get`, `getString`) frente a la nueva interfaz `FileItem<T>`.
- [ ] **Documentar el cambio de API** para usuarios en `changelog.txt`.

### Fase 6 — Tomcat embebido (`AppServer.java`)
- [ ] Verificar que `addWebapp`, `addContext`, `Tomcat.addServlet`,
      `Rfc6265CookieProcessor.setSameSiteCookies`, `StandardRoot`, `DirResourceSet`,
      `ErrorPage` y `addServletMappingDecoded` siguen disponibles en 11.0.22.
- [ ] Revisar el filtro `tomcat.util.scan.StandardJarScanFilter.jarsToSkip` (sigue válido).
- [ ] Probar arranque vía `AppServer.run(...)` (usado por `exec-maven-plugin` en los tests).

### Fase 7 — `openxavatest`
- [ ] `pom.xml`: hereda Servlet vía dependencia `openxava`; verificar que no declara
      `javax.servlet` propio. Mantener `openxava.version`.
- [ ] Renombrar imports `javax.servlet` → `jakarta.servlet` en las **10 clases**.
- [ ] Migrar `src/main/webapp/WEB-INF/web.xml` del DTD 2.3 a `web-app` Servlet 6.1
      (`https://jakarta.ee/xml/ns/jakartaee`, `web-app_6_1.xsd`, `version="6.1"`).
      La declaración del `JspServlet` (`org.apache.jasper.servlet.JspServlet`) sigue
      válida en Tomcat 11.
- [ ] Revisar las 23 JSPs de test.

### Fase 8 — Otros módulos dependientes (fuera de alcance directo, a coordinar)
- [ ] `openxava-7.7-chat-jdk17`: implementación del chat; revisar si usa servlet/websocket.
- [ ] `chattest`, `invoicedemo` y los `*-archetype*`: tienen `web.xml` propios con DTD
      antigua; deberán migrarse cuando se libere la nueva versión (no requerido para
      compilar `openxava`/`openxavatest`, pero sí para que las apps de usuario arranquen).

### Fase 9 — Verificación
- [ ] `mvn compile` en `openxava` y luego en `openxavatest` (regla del proyecto:
      compilar antes de asumir que funciona).
- [ ] Arranque con Tomcat 11 embebido (`AppServer`) y despliegue del WAR en Tomcat 11.0.22 externo.
- [ ] Ejecutar la suite de tests de `openxavatest` **manualmente desde el IDE**
      (preferencia del usuario; no lanzar `mvn test` automáticamente).
- [ ] Pruebas funcionales: subida de ficheros (fileupload2), chat (websocket),
      generación de informes, navegación NaviOX, cookies SameSite.
- [ ] Repetir el test de ZAP si se toca el bloque de cookies/error pages de `AppServer`.

---

## 5. Riesgos y mitigaciones

| Riesgo | Impacto | Mitigación |
|---|---|---|
| Cambio de API pública `FileItem` (commons-fileupload2) | Rompe apps de usuario que usan `WithFileItem`/`@FileItemUpload` | Documentar en changelog; valorar capa de adaptación temporal |
| Coexistencia `javax`/`jakarta` en classpath | Errores de arranque difíciles de diagnosticar | Migración completa por capas; no mezclar |
| JSPs con URIs JSTL antiguas no detectadas | Errores 500 en tiempo de ejecución, no en compilación | Barrido exhaustivo de las 138 JSPs |
| Cambios de API Catalina en Tomcat 11 embebido | Fallo de arranque de `AppServer` | Probar arranque temprano (Fase 6) |
| Tests Selenium/HtmlUnit sensibles a cambios de salida HTML | Falsos negativos | Ejecutar suite completa y comparar |
| Dependencias transitivas que arrastran `javax.servlet` | Conflicto de namespace | `mvn dependency:tree` y exclusiones |

---

## 6. Checklist de entrega

- [ ] `openxava` compila con Servlet 6.1 / Tomcat 11.0.22.
- [ ] `openxavatest` compila y arranca sobre Tomcat 11.
- [ ] Suite de tests verde (ejecución manual).
- [ ] Subida de ficheros funcional con commons-fileupload2.
- [ ] Chat WebSocket funcional con `jakarta.websocket`.
- [ ] `changelog.txt` y `xava-version.properties` actualizados.
- [ ] Nota de migración para usuarios (API `FileItem`, requisito Tomcat 11 / Java 17).

---

> **Nota de alcance**: este plan cubre `openxava` y `openxavatest` según lo solicitado.
> Los proyectos `chattest`, `invoicedemo` y los `archetype` comparten el mismo patrón
> de descriptores `javax`/DTD antiguos y deberán migrarse en una pasada posterior para
> que las aplicaciones de ejemplo y las plantillas generadas funcionen sobre Tomcat 11.
