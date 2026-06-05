# Plan genérico de migración de DWR a Fetch + Servlet en OpenXava

Este documento es la **guía de referencia** para eliminar progresivamente el uso de
Direct Web Remoting (DWR) en OpenXava, sustituyéndolo por un enfoque moderno:
JavaScript `fetch` nativo en el cliente y `HttpServlet` estándar en el servidor.

Ya hay **dos migraciones completas y funcionando** que sirven de plantilla:

- **`Discussion`** -> patrón de **operación única** (un solo método).
- **`Tab`** -> patrón de **multi-operación** (varias acciones bajo un mismo servlet).

Las clases DWR pendientes están en
`@/E:/IdeaProjects/openxava/openxava/src/main/java/org/openxava/web/dwr`.

---

## 1. Arquitectura objetivo

El reemplazo de DWR se apoya en tres piezas reutilizables ya creadas:

### 1.1 Servidor: `BaseServlet`

Toda la lógica común de DWR (`DWRBase`) se ha extraído a una clase base de la que
heredan todos los servlets de migración:

- **Ruta**: `@/E:/IdeaProjects/openxava/openxava/src/main/java/org/openxava/web/servlets/BaseServlet.java`

Proporciona:

- **`initRequest(request, response, application, module)`**: fija el encoding, el
  `windowId` actual, ejecuta `checkSecurity(...)`, pone el `style` en el request e
  invoca `Requests.partialInit(...)`. Es el equivalente exacto a `DWRBase.initRequest`.
- **`checkSecurity(request, application, module)`**: lanza `SecurityException` con los
  códigos `6859` (sin sesión), `9876` (módulo no ejecutado) o `3923` (módulo bloqueado).
- **`cleanRequest()`**: llama a `Requests.clean()` (invocar siempre en el `finally`).
- **`getContext(request)`**: recupera el `ModuleContext` de la sesión.
- **`sendError(response, statusCode, message)`**: responde con texto plano, evitando la
  página de error de Tomcat (que revela la versión del servidor).

> Cualquier servlet nuevo **debe** extender `BaseServlet` y **no** reimplementar esta lógica.

### 1.2 Cliente: `openxava.post`

Helper único que centraliza todas las llamadas `fetch`:

- **Ruta**: `@/E:/IdeaProjects/openxava/openxava/src/main/resources/META-INF/resources/xava/js/openxava.js`

```javascript
openxava.post = function(url, params, callback) {
	var fullUrl = url.indexOf("://") >= 0 ? url : openxava.contextPath + url;
	var fetchOptions = {
		method: "POST",
		credentials: "same-origin",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded",
			"xavawindowid": $("#xava_window_id").val()
		},
		body: params
	};
	fetch(fullUrl, fetchOptions)
		.then(function(response) {
			if (!response.ok) {
				return response.text().then(function(text) {
					throw new Error("HTTP Status " + response.status + " - " + text);
				});
			}
			if (callback) return response.text();
		})
		.then(function(text) { if (callback) callback(text); })
		.catch(function(error) {
			console.error("Error in openxava.post:", error);
			openxava.showError(openxava.postErrorMessage);
			if (callback) callback("ERROR: " + error.message);
		});
};
```

Puntos clave que ya resuelve por nosotros:

- **`contextPath`** se antepone automáticamente (pasar siempre rutas tipo `/xava/...`).
- **`xavawindowid`** se envía en la cabecera, imprescindible para que el servlet
  recupere el `ModuleContext`/ventana correctos (lo usa `context.setCurrentWindowId(request)`).
- **`credentials: same-origin`** envía las cookies de sesión.
- **`callback(text)`**: si se pasa, recibe el cuerpo de la respuesta como texto; en caso
  de error recibe una cadena que empieza por `"ERROR:"`.

### 1.3 Cliente: funciones de espacio de nombres

Cada interfaz DWR se sustituye por funciones JS con la **misma firma** que los métodos de
la clase Java original, de modo que el código que las invoca no necesita cambiar (o cambia
mínimamente). Hay dos ubicaciones según el caso:

- **Global de framework** (p. ej. `Tab`): se definen como funciones planas a nivel de
  `openxava` dentro de `openxava.js`, siguiendo el estilo del resto del fichero (todas las
  funciones cuelgan directamente de `openxava`, sin objetos anidados). Para conservar el
  agrupamiento temático y evitar colisiones de nombres con funciones de UI ya existentes
  (p. ej. `openxava.setFilterVisible` o `openxava.filterColumns`), se prefijan con el tema:
  `openxava.tabSetFilterVisible`, `openxava.tabFilterColumns`, etc.
- **De editor** (p. ej. `Discussion`): se define en el `*.js` del editor
  (`discussionEditor.js`).

---

## 2. Los dos patrones de servlet

### 2.1 Patrón A - Operación única (`DiscussionServlet`)

Cuando la clase DWR expone esencialmente **un método**, el servlet implementa
directamente `doPost` con parámetros fijos.

- **Ruta**: `@/E:/IdeaProjects/openxava/openxava/src/main/java/org/openxava/web/servlets/DiscussionServlet.java`

```java
@WebServlet(name = "discussion", urlPatterns = "/xava/discussion")
public class DiscussionServlet extends BaseServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String application = request.getParameter("application");
        String module = request.getParameter("module");
        String discussionId = request.getParameter("discussionId");
        String commentContent = request.getParameter("commentContent");
        try {
            initRequest(request, response, application, module);
            // ... lógica de negocio ...
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SecurityException e) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            try { XPersistence.commit(); } finally { cleanRequest(); }
        }
    }
}
```

Cliente correspondiente (`discussionEditor.js`):

```javascript
discussionEditor.sendComment = function(application, module, discussionId, commentContent) {
	var params = new URLSearchParams();
	params.append("application", application);
	params.append("module", module);
	params.append("discussionId", discussionId);
	params.append("commentContent", commentContent);
	openxava.post("/xava/discussion", params);
}
```

### 2.2 Patrón B - Multi-operación (`TabServlet`)

Cuando la clase DWR expone **varios métodos**, se usa un único servlet con un parámetro
`operation` y un `switch` que despacha a un método privado por operación. Así se evita
crear decenas de servlets.

- **Ruta**: `@/E:/IdeaProjects/openxava/openxava/src/main/java/org/openxava/web/servlets/TabServlet.java`

```java
@WebServlet(name = "tab", urlPatterns = "/xava/tab")
public class TabServlet extends BaseServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String operation = request.getParameter("operation");
        if (operation == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Missing operation parameter");
            return;
        }
        try {
            String application = request.getParameter("application");
            String module = request.getParameter("module");
            initRequest(request, response, application, module);
            switch (operation) {
                case "setFilterVisible" -> handleSetFilterVisible(request, response, application, module);
                case "updateValue"      -> handleUpdateValue(request, response, application, module);
                case "removeProperty"   -> handleRemoveProperty(request, response, application, module);
                case "moveProperty"     -> handleMoveProperty(request, response, application, module);
                case "setColumnWidth"   -> handleSetColumnWidth(request, response, application, module);
                case "filterColumns"    -> handleFilterColumns(request, response, application, module);
                default -> sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Unknown operation: " + operation);
            }
        } catch (SecurityException e) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            log.error("Error processing tab operation: " + operation, e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            cleanRequest();
        }
    }
}
```

Cliente correspondiente: funciones planas `openxava.tab*` en `openxava.js` (una por
operación) y, donde aplica, helpers en el editor (`listEditor.updateValue`). Cada función
construye un `URLSearchParams` con `operation` + parámetros y llama a
`openxava.post("/xava/tab", params[, callback])`.

```javascript
openxava.tabSetFilterVisible = function(application, module, filterVisible, tabObject) {
	var params = new URLSearchParams();
	params.append("operation", "setFilterVisible");
	params.append("application", application);
	params.append("module", module);
	params.append("filterVisible", filterVisible);
	params.append("tabObject", tabObject);
	openxava.post("/xava/tab", params);
}

// ... resto de operaciones ...

openxava.tabFilterColumns = function(application, module, searchWord, callback) {
	var params = new URLSearchParams();
	params.append("operation", "filterColumns");
	params.append("application", application);
	params.append("module", module);
	params.append("searchWord", searchWord);
	openxava.post("/xava/tab", params, callback); // operación con retorno
}
```

### 2.3 ¿Cuándo usar cada patrón?

- **1 método** o métodos sin relación con otros editores -> **Patrón A**.
- **2+ métodos** de un mismo componente de framework -> **Patrón B**.

---

## 3. Convenciones de respuesta

DWR devolvía objetos Java serializados; con `fetch` trabajamos con **texto plano** y
**códigos de estado HTTP**:

- **Sin retorno** (acción "fire-and-forget"): `response.setStatus(SC_OK)`. El cliente
  llama a `openxava.post(url, params)` sin callback.
- **Con retorno**: escribir el resultado como `text/plain; charset=UTF-8` y consumirlo en
  el `callback(text)`. Ejemplo en `TabServlet.handleUpdateValue` /
  `handleFilterColumns`.
- **Errores de negocio dentro de una operación con retorno**: devolver una cadena que
  empiece por `"ERROR: "` (convención que ya interpreta el cliente, p. ej. en
  `listEditor.updateValue`).
- **Errores de seguridad / fallo de servidor**: usar `sendError(...)` con
  `SC_FORBIDDEN` / `SC_INTERNAL_SERVER_ERROR`; `openxava.post` los detecta vía
  `response.ok` y dispara `openxava.showError`.

> Si una operación DWR devolvía un objeto complejo, serializarlo a JSON
> (`response.setContentType("application/json")`) y parsearlo en el callback con
> `JSON.parse`.

---

## 4. Receta paso a paso para migrar una clase DWR

Para cada clase de `org.openxava.web.dwr`:

1. **Inventariar** los métodos públicos invocados desde el cliente y sus parámetros /
   tipos de retorno (buscar `NombreClase.metodo(` en los `*.js`).
2. **Elegir patrón** (A o B, sección 2.3) y crear el servlet en
   `org.openxava.web.servlets` extendiendo `BaseServlet`, mapeado en `/xava/<nombre>`.
3. **Trasladar la lógica** del método DWR al servlet:
   - Sustituir el preámbulo de `DWRBase` por `initRequest(...)` / `cleanRequest()`.
   - Recuperar objetos de sesión con `getContext(request).get(application, module, "...")`
     (p. ej. `xava_view`, `xava_tab`).
   - Mantener el control de transacciones donde la clase original lo hiciera
     (p. ej. `XPersistence.commit()` en el `finally`).
4. **Crear/actualizar el objeto JS** con la misma firma:
   - Framework global -> `openxava.<nombre>` en `openxava.js`.
   - Editor concreto -> en su `*.js`.
   - Cada método construye `URLSearchParams` y delega en `openxava.post`.
5. **Eliminar la carga del script DWR** autogenerado en el cliente:
   ```javascript
   openxava.getScript(openxava.contextPath + "/dwr/interface/<Nombre>.js"); // ELIMINAR
   ```
   y/o el `<script src=".../dwr/interface/<Nombre>.js">` en JSPs
   (`module.jsp`, `naviox/index.jsp`, etc.).
6. **Probar** la funcionalidad (incluida la ruta HtmlUnit si existe, como
   `discussionEditor.postMessageHtmlUnit`).
7. **Limpiar DWR** (sección 5).

---

## 5. Limpieza de DWR tras verificar

1. **`dwr.xml`** - eliminar el `<create>` de la clase migrada:
   `@/E:/IdeaProjects/openxava/openxava/src/main/resources/META-INF/resources/WEB-INF/dwr.xml`
   ```xml
   <!-- ELIMINAR -->
   <create creator="new" javascript="Nombre">
     <param name="class" value="org.openxava.web.dwr.Nombre"/>
   </create>
   ```
   Si la clase usaba `<convert>` (p. ej. `Result`, `StrokeAction`) y deja de ser
   necesaria, eliminar también esa entrada.
2. **Borrar la clase DWR** en `org.openxava.web.dwr` (y clases auxiliares que queden
   huérfanas).
3. Cuando **todas** las clases estén migradas: eliminar la dependencia de DWR del
   `pom.xml`, el servlet de DWR de los `web.xml` y este patrón quedará completamente
   retirado.

> Nota: `TableId` se hizo `public` para poder reutilizarse desde `org.openxava.web.servlets`.
> Cualquier clase auxiliar de `dwr` que un servlet necesite debe promoverse a `public`
> (o moverse al paquete `servlets`).

---

## 6. Inventario de clases DWR pendientes

Estado actual de `@/E:/IdeaProjects/openxava/openxava/src/main/resources/META-INF/resources/WEB-INF/dwr.xml`
y del paquete `org.openxava.web.dwr`:

| Clase DWR | Estado | Patrón sugerido | Notas |
|-----------|--------|-----------------|-------|
| `Discussion` | **Migrado** | A | `DiscussionServlet` (`/xava/discussion`). Plantilla de operación única. |
| `Tab` | **Migrado** | B | `TabServlet` (`/xava/tab`). Plantilla de multi-operación. |
| `Module` | Pendiente | B | **La más compleja** (~35 KB). Núcleo del ciclo de petición/render. Migrar al final. |
| `View` | **Migrado** | B | `ViewServlet` (`/xava/view`). |
| `Calendar` | Pendiente | B | ~18 KB. Usa `CalendarEvent` as bean de transferencia -> serializar a JSON. |
| `Tree` | Pendiente | B | ~10 KB. |
| `Descriptions` | **Migrado** | B | `DescriptionsServlet` (`/xava/descriptions`). Listas de descripciones / combos dependientes. |
| `Modules` (naviox) | Pendiente | - | En `com.openxava.naviox.web.dwr`, fuera de este repo base. |
| `Folders` (naviox) | Pendiente | - | Idem naviox. |
| `PhoneList` (phone) | Pendiente | - | En `com.openxava.phone.web.dwr`. |

Clases auxiliares (no exponen métodos al cliente, se migran/eliminan con su clase):
`DWRBase` (sustituida por `BaseServlet`), `ViewBase`, `Result`, `StrokeAction`,
`CalendarEvent`, `TableId` (ya `public`).

### Orden recomendado

1. `View` (sencilla, consolida el patrón B fuera de Tab).
2. `Descriptions`, `Tree`, `Calendar`.
3. Clases de naviox/phone.
4. `Module` (la última, por su tamaño y por ser el corazón del framework).
