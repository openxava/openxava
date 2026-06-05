# Plan de migración de `dwr.Module` a Fetch + Servlet

Este documento es el **plan específico** para migrar la última y más importante clase DWR
de OpenXava: `org.openxava.web.dwr.Module`. Es el corazón del framework: recibe la petición
de ejecutar una acción, la ejecuta, calcula qué fragmentos de HTML han cambiado y los
reenvía al navegador (HTML Over The Wire, antes de que el término existiera).

El plan genérico (`dwr-migration-plan.md`) cubre los casos sencillos (editores y utilidades
de UI). `Module` añade complejidad que aquel plan **no** contempla y que se detalla aquí.

> Estado de partida: todas las demás clases DWR ya están migradas. En `dwr.xml` solo queda
> el `<create>` de `Module` más los `<convert>` de `Result` y `StrokeAction`.

---

## 1. Inventario: qué expone `Module` y quién lo usa

### 1.1 Métodos remotos (invocados desde JavaScript vía DWR)

| Método | Llamado desde | Firma | Retorno |
|--------|---------------|-------|---------|
| `request(...)` | `openxava.ajaxRequest` (`openxava.js:36`) | `request, response, application, module, additionalParameters, values:Map, multipleValues:Map, selected:String[], deselected:String[], firstRequest:Boolean, baseFolder:String` + callback `openxava.refreshPage` | objeto `Result` (complejo) |
| `getStrokeActions(...)` | `openxava.initStrokeActions` (`openxava.js:415`) | `request, response, application, module` + callback `openxava.setStrokeActions` | `Map` de `StrokeAction` |

Estos son los **dos únicos** métodos remotos. Cada uno irá a su propio servlet
(decisión del usuario):

- `request()` -> **`HotwireServlet`** (`/xava/hotwire`).
- `getStrokeActions()` -> **`StrokeActionsServlet`** (`/xava/strokeActions`).

### 1.2 Métodos NO remotos (invocados desde Java/JSP, no desde el cliente)

| Miembro | Llamado desde | Tratamiento |
|---------|---------------|-------------|
| `requestMultipart(...)` | `module.jsp:78` (`new Module().requestMultipart(...)`) | Mover a clase de utilidad; reutiliza el pipeline de `request()`. |
| `static memorizeLastMessages(request, app, module)` | `UploadServlet:73` | Mover a clase de utilidad. |
| `static restoreLastMessages(request, app, module)` | `module.jsp:189` | Mover a clase de utilidad. |
| Resto de métodos privados (`fillResult`, `getChangedParts`, `fillChanged*`, `addEditor`, `getURI*`, `addValuesQueryString`, `filterKey/Value`, `setDialog*`, `isLinkageError`, `handleLinkageError`, ...) | Internos de `request()` | Migran dentro de `HotwireServlet` (ver §3). |

### 1.3 Comentarios en otras clases (no requieren cambio funcional)

- `View.java:3209` y `View.java:6794` referencian "dwr.Module" en comentarios. Conviene
  actualizar el texto del comentario a la nueva clase, pero no afecta a la lógica.

---

## 2. El gran cambio respecto al plan genérico: campos de instancia y thread-safety

**Punto crítico.** DWR crea **una instancia nueva de `Module` por cada llamada**, por eso la
clase actual guarda el estado de la petición en **campos de instancia**
(`request`, `response`, `application`, `module`, `manager`, `firstRequest`, `baseFolder`).

Un `HttpServlet` es **un singleton compartido por todos los hilos**. Si copiamos esos campos
tal cual a `HotwireServlet`, tendremos **corrupción de estado entre peticiones concurrentes**
(un bug intermitente y gravísimo en el corazón del framework).

### Solución (patrón ya usado en `CalendarServlet`)

`HotwireServlet.doPost` **no** guarda estado. Instancia, por cada petición, un objeto
procesador interno que contiene los antiguos campos de instancia y todos los métodos del
pipeline:

```java
@WebServlet(name = "hotwire", urlPatterns = "/xava/hotwire")
public class HotwireServlet extends BaseServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) ... {
        // parsear parámetros (ver §4) y delegar:
        Result result = new RequestProcessor(request, response, application, module, ...).request();
        // serializar result a JSON (ver §5)
    }

    // Inner class con estado POR PETICIÓN (thread-safe):
    static class RequestProcessor {
        private final HttpServletRequest request;
        private ModuleManager manager;
        private boolean firstRequest;
        private String baseFolder;
        // ... todos los métodos privados actuales de Module van aquí ...
        Result request() { ... }
    }
}
```

> Esta es la diferencia conceptual más importante con las migraciones anteriores
> (`Tab`, `Calendar`, etc.), donde los servlets eran prácticamente sin estado. Aquí hay
> que **encapsular el estado por petición**.

---

## 3. Arquitectura objetivo de la migración de Module

Se crean **3 piezas Java nuevas** y se eliminan las clases DWR al final.

### 3.1 `HotwireServlet` (`org.openxava.web.servlets`, `/xava/hotwire`)

- Extiende `BaseServlet`.
- `doPost`: parsea parámetros, ejecuta el pipeline, serializa `Result` a JSON.
- Contiene la inner class `RequestProcessor` con **todo** el pipeline de render
  (los ~25 métodos privados actuales de `Module`).
- Expone un punto de entrada reutilizable para `requestMultipart`:
  `public static Result executeRequest(request, response, application, module, additionalParameters, values, multipleValues, selected, deselected, firstRequest, baseFolder)`.
- Maneja `SecurityException` (reload/invalidate) y `Throwable` (incluido `LinkageError`
  con hotswap) **igual** que el `Module.request` actual. Esa lógica de error se mantiene
  intacta porque es delicada (ver `Module.java:105-137`).
- En el `finally`: `ModuleManager.commit()` y luego `cleanRequest()`, como ahora.

### 3.2 `StrokeActionsServlet` (`org.openxava.web.servlets`, `/xava/strokeActions`)

- Extiende `BaseServlet`. Patrón A (operación única).
- Reproduce `Module.getStrokeActions(request, response, application, module)`:
  recupera el `manager`, recorre `getAllMetaActionsIterator()` y construye el mapa
  `id -> {name, confirmMessage, takesLong}`.
- Devuelve el mapa serializado a **JSON** (ver §5.2).
- Conserva el manejo de error tolerante actual: si algo falla (p. ej. sesión
  invalidada) devuelve algo que el cliente interprete como "recarga la página"
  (hoy `getStrokeActions` devuelve `null` y `openxava.setStrokeActions` hace
  `window.location.reload()`). Hay que **preservar ese contrato** (ver §6.2).

> El cálculo de keystrokes (`KeyStroke`, `InputEvent`) y el `StrokeAction` se reutilizan; el
> método `getStrokeActions()` (privado, sin args) es compartido conceptualmente por el
> servlet y por el pipeline de `request()` (que también rellena `result.setStrokeActions`).
> Como ahora viven en servlets distintos, ese método se duplica o se extrae a un helper
> común reutilizable por ambos. **Recomendación**: extraer la construcción del mapa de
> stroke actions a un método estático compartido (p. ej. en una clase de utilidad, §3.3)
> que reciba el `ModuleManager`.

### 3.3 Clase de utilidad (`org.openxava.web.servlets`, p. ej. `ModuleRequests`)

Reúne lo no-remoto que hoy vive en `Module`:

- `static void requestMultipart(request, response, application, module)`: misma lógica que
  `Module.requestMultipart` (`Module.java:268`), pero invocando
  `HotwireServlet.executeRequest(...)` para el caso multipart, seguido de
  `memorizeLastMessages(...)` y `manager.setResetFormPostNeeded(true)`.
- `static void memorizeLastMessages(request, application, module)` (de `Module.java:684`).
- `static void restoreLastMessages(request, application, module)` (de `Module.java:696`).
- (Opcional) `static Map<String,StrokeAction> buildStrokeActions(ModuleManager manager)`
  compartido por `HotwireServlet` y `StrokeActionsServlet`.

Actualizar los **2 llamadores Java**:
- `UploadServlet:73` -> `ModuleRequests.memorizeLastMessages(...)`.
- `module.jsp:78,189` -> `ModuleRequests.requestMultipart(...)` y
  `ModuleRequests.restoreLastMessages(...)`; quitar `import ...dwr.Module`.

---

## 4. Cómo enviar los parámetros complejos de `request()`

`Module.request` recibía, gracias a la serialización automática de DWR:

- `values`: **Map** (nombre de campo -> valor) producido por `openxava.getFormValues(form)`.
- `multipleValues`: **Map** producido por `openxava.getMultipleValues(...)` (multi-selects).
- `selected` / `deselected`: **String[]**.
- escalares: `additionalParameters`, `firstRequest`, `baseFolder`.

Con `fetch` + `application/x-www-form-urlencoded` (helper `openxava.post`) hay que
serializar estructuras. **Enfoque recomendado**: enviar `values`, `multipleValues`,
`selected` y `deselected` como **cadenas JSON** dentro de `URLSearchParams`, y parsearlas en
el servlet con `org.json` (ya disponible, lo usa `CalendarServlet`):

```javascript
// cliente (openxava.ajaxRequest)
var params = new URLSearchParams();
params.append("application", application);
params.append("module", module);
params.append("additionalParameters", document.additionalParameters || "");
params.append("values", JSON.stringify(openxava.getFormValues(form)));
params.append("multipleValues", JSON.stringify(openxava.getMultipleValues(application, module)));
params.append("selected", JSON.stringify(openxava.getSelectedValues(application, module)));
params.append("deselected", JSON.stringify(openxava.deselected));
params.append("firstRequest", firstRequest ? "true" : "false");
params.append("baseFolder", openxava.baseFolder || "");
openxava.post("/xava/hotwire", params, function(text) {
    if (text && text.indexOf("ERROR:") === 0) { openxava.showError(openxava.postErrorMessage); return; }
    openxava.refreshPage(JSON.parse(text));
});
```

```java
// servlet
Map<String,Object> values = JsonUtil.toMap(request.getParameter("values"));         // o null
Map<String,Object> multipleValues = JsonUtil.toMap(request.getParameter("multipleValues"));
String[] selected = JsonUtil.toStringArray(request.getParameter("selected"));
String[] deselected = JsonUtil.toStringArray(request.getParameter("deselected"));
```

> Cuidado con `null` vs vacío: el pipeline distingue `values == null` (recarga todo el core,
> ver `getChangedParts`, `Module.java:430`) de un mapa vacío. La primera petición
> (`firstRequest=true`) y `requestMultipart` pasan `values = null`. El parseo debe respetar
> esa distinción (cadena ausente/"null" -> `null`; "{}" -> mapa vacío).

> Las claves de `values` son los `id` decorados de los inputs (con comas, `:` y `::` que el
> pipeline interpreta en `filterKey`, `addMultipleValuesQueryString`). JSON las transporta sin
> problema; no se pierde nada respecto a DWR.

---

## 5. Convención de respuesta: serializar `Result` y el mapa de strokes a JSON

DWR serializaba automáticamente el bean `Result` (y `StrokeAction`). Con `fetch`
serializamos a JSON con `org.json` y lo parseamos con `JSON.parse` en
`openxava.refreshPage`.

### 5.1 `Result` -> JSON

`refreshPage` (`openxava.js:49`) consume estas propiedades, que deben aparecer en el JSON:

`error, reload, forwardURL, forwardInNewWindow, forwardURLs, nextModule, application,
module, showDialog, hideDialog, dialogLevel, dialogTitle, resizeDialog, focusPropertyId,
viewMember, selectedRows (Map), urlParam, viewSimple, dataChanged, hasPostJS,
editorsWithError (String[]), editorsWithoutError (String[]),
propertiesUsedInCalculations (String[]), strokeActions (Map), changedParts (Map<String,String>)`.

**Opciones de implementación** (elegir una):

- **(a)** Mantener el bean `Result` y escribir un único método
  `String toJson()` (o un helper `ResultJson.write(result)`) que vuelque todos los campos a
  un `JSONObject`. Mínimo impacto, fácil de revisar contra `refreshPage`.
- **(b)** Eliminar `Result` y construir el `JSONObject` directamente en el pipeline.

**Recomendación**: opción (a) durante la migración (menor riesgo, se compara campo a campo
con lo que lee `refreshPage`); el borrado de `Result` se valora en la limpieza final (§8).

Notas:
- `changedParts` es `Map<String,String>` con HTML. Hoy el HTML pasa por `filterHTML`
  (sustituye `,` por `_#C#_`, `Module.java:291`) y el cliente revierte con
  `.replace(/_#C#_/g, ",")` (`openxava.js:129`). Con JSON las comas **ya no son
  problemáticas**, así que este escape/desescape es innecesario. **Recomendación**:
  eliminar `filterHTML` y el `replace` del cliente a la vez. Si se prefiere minimizar
  cambios en una primera pasada, se pueden mantener ambos (siguen funcionando).
- Serializar solo lo no nulo o serializar nulos como ausentes: `refreshPage` comprueba
  `!= null`, así que basta con no incluir las claves nulas (o incluirlas como `null`).
- `selectedRows` es `Map<String,int[]>`; `JSONObject` lo serializa bien.

### 5.2 Mapa de strokeActions -> JSON

`StrokeActionsServlet` devuelve `{ "keyCode,ctrl,alt,shift": {name, confirmMessage,
takesLong}, ... }`. El cliente (`openxava.setStrokeActions`) lo asigna a
`openxava.strokeActions` y lo consulta en `processKey` (`openxava.js:935`).

---

## 6. Cambios en el cliente (`openxava.js`, `module.jsp`)

### 6.1 `openxava.ajaxRequest` (`openxava.js:23-47`)

Sustituir la llamada `Module.request(...)` (líneas 36-44) por `openxava.post("/xava/hotwire",
params, cb)` con el `cb` que hace `JSON.parse` y llama a `openxava.refreshPage` (§4).
`refreshPage` queda **casi idéntico**: solo cambia que recibe un objeto ya parseado.

### 6.2 `openxava.initStrokeActions` (`openxava.js:414-416`)

Sustituir `Module.getStrokeActions(application, module, openxava.setStrokeActions)` por
`openxava.post("/xava/strokeActions", params, function(text){ openxava.setStrokeActions(text ?
JSON.parse(text) : null); })`. Conservar el contrato: respuesta vacía/`null` -> recarga
(`setStrokeActions` ya lo hace, `openxava.js:447`).

### 6.3 Eliminar dependencias de la librería DWR en JS

`Module` es la **última** clase DWR; al migrarla podemos retirar DWR del cliente por
completo. Pero hay 4 usos de la API `dwr.*` en `openxava.js` que hay que **reemplazar por
código nativo** antes de quitar `dwr-engine.js`/`dwr/util.js`:

| Uso actual | Ubicación | Reemplazo |
|------------|-----------|-----------|
| `dwr.engine.setHeaders({xavawindowid})` | `initWindowId`, `openxava.js:427` | Eliminar: `openxava.post` ya envía la cabecera `xavawindowid`. |
| `dwr.util.getValue` / `dwr.util.toDescriptiveString` | `getMultipleValues`, `openxava.js:964` | Implementar equivalente nativo (leer valor del control; el formato "descriptive string" para selects múltiples). |
| `dwr.util._isHTMLElement(ele, "select"/"input")` | `getFormValue`, `openxava.js:1098,1120` | Reemplazar por comprobación nativa de `ele.tagName` / `ele.nodeName`. |

> Estos reemplazos son pequeños pero **imprescindibles**: si se quita `dwr/util.js` sin
> sustituirlos, se rompe el envío de formularios con multi-valores y la lectura de campos.

### 6.4 `module.jsp`

- Quitar el bloque que carga DWR (`xava/module.jsp:126-135`): el `dwr.engine` inline,
  `dwr-engine.js`, `dwr/util.js` y `dwr/interface/Module.js`.
- Quitar `<%@page import="org.openxava.web.dwr.Module"%>` (línea 14).
- `new Module().requestMultipart(...)` (línea 78) -> `ModuleRequests.requestMultipart(...)`.
- `Module.restoreLastMessages(...)` (línea 189) -> `ModuleRequests.restoreLastMessages(...)`.
- `naviox/index.jsp:76-81` carga `dwr-engine.js` para naviox; revisar si tras la migración
  sigue haciendo falta (probablemente no) y retirarlo en la limpieza final.

---

## 7. Orden de ejecución recomendado

1. **Crear `ModuleRequests`** (utilidad) con `memorizeLastMessages`, `restoreLastMessages`
   y (placeholder de) `requestMultipart`. Actualizar `UploadServlet`. Compila sin tocar nada
   del flujo todavía (los métodos pueden delegar temporalmente en `Module`).
2. **Crear `HotwireServlet`** con la inner `RequestProcessor` (copiar el pipeline de
   `Module.request` y sus privados, eliminando los campos de instancia del servlet). Añadir
   `toJson` del `Result`. Exponer `executeRequest(...)` estático.
3. **Completar `ModuleRequests.requestMultipart`** para usar `HotwireServlet.executeRequest`.
4. **Crear `StrokeActionsServlet`** (+ helper `buildStrokeActions`).
5. **Cliente**: cambiar `ajaxRequest`, `initStrokeActions`; reemplazar usos de `dwr.*`
   (§6.3); ajustar `refreshPage` para `JSON.parse`.
6. **`module.jsp`**: cambiar llamadas a `ModuleRequests`, quitar scripts DWR e import.
7. **Probar a fondo** (§9). Es el flujo central: probar muchos módulos, diálogos, listas,
   colecciones, forward, cambio de módulo, multipart/upload, keystrokes, permalinks.
8. **Limpieza DWR** (§8) solo cuando todo verifique.

---

## 8. Limpieza final de DWR (Module es la última clase)

Tras verificar, al ser `Module` la última pieza DWR, se puede **retirar DWR por completo**:

1. **`dwr.xml`**: eliminar el `<create>` de `Module` y los `<convert>` de `Result`/
   `StrokeAction`. El fichero queda vacío -> borrarlo (y su referencia si la hubiera).
2. **Borrar el paquete `org.openxava.web.dwr`**: `Module.java`, `DWRBase.java` (ya sustituida
   por `BaseServlet`), `Result.java` y `StrokeAction.java` (si se optó por construir JSON
   directamente; si se conservó `Result` como bean, moverlo a `org.openxava.web.servlets`).
   `TableId` ya es `public` y se usa desde servlets: dejarla donde la necesiten.
3. **`pom.xml`**: eliminar la dependencia `dwr` (`pom.xml:169-173`) y el comentario asociado.
4. **Cliente**: borrar `xava/js/dwr-engine.js`; quitar la carga de DWR en `naviox/index.jsp`.
5. **`web.xml` / web-fragment**: el servlet de DWR lo aporta el propio jar (web-fragment);
   al quitar la dependencia desaparece. Verificar que no queden mapeos `/dwr/*` manuales.
6. Buscar referencias residuales a `dwr` en JSP/JS/Java y limpiarlas (incluidos comentarios
   en `View.java`, `descriptionsEditor.jsp`).

---

## 9. Pruebas (crítico: es el motor del framework)

Cubrir, como mínimo, los caminos que toca `request()`/`refreshPage`:

- **Render parcial**: cambiar una propiedad y ver que solo se actualiza su editor.
- **Listas/colecciones**: filtros, orden, paginación, totales, secciones, etiquetas.
- **Diálogos**: `showDialog`, `hideDialog`, `resizeDialog`, varios niveles anidados.
- **Forward**: `forwardURL` (misma/nueva ventana, proxy con `x-forwarded-*`) y `forwardURLs`.
- **Cambio de módulo**: `nextModule`, módulo previo, reentrancia prohibida.
- **Multipart/upload**: `requestMultipart` + `setResetFormPostNeeded` (el form se reenvía).
- **Keystrokes**: `StrokeActionsServlet` y `processKey`.
- **Permalinks**: `getUrlParam` (test Selenium de Permalink mencionado en el código).
- **Errores**: `SecurityException` (sesión invalidada -> reload), error de negocio,
  `LinkageError` con hotswap activo.
- **Concurrencia**: dos peticiones simultáneas del mismo usuario en ventanas distintas
  (`xavawindowid`) — valida que el refactor a `RequestProcessor` por petición es correcto.
- **HtmlUnit**: los tests existentes que ejercitan el módulo.

---

## 10. Resumen de artefactos

**Nuevos:**
- `org.openxava.web.servlets.HotwireServlet` (`/xava/hotwire`) + inner `RequestProcessor`.
- `org.openxava.web.servlets.StrokeActionsServlet` (`/xava/strokeActions`).
- `org.openxava.web.servlets.ModuleRequests` (utilidad: requestMultipart + memorize/restore
  lastMessages [+ buildStrokeActions]).

**Modificados:**
- `xava/js/openxava.js` (ajaxRequest, refreshPage, initStrokeActions, getMultipleValues,
  getFormValue, initWindowId).
- `xava/module.jsp` (quitar DWR, usar `ModuleRequests`).
- `naviox/index.jsp` (quitar carga de DWR).
- `UploadServlet.java` (usar `ModuleRequests`).
- `View.java` (comentarios).

**Eliminados (limpieza final):**
- `org.openxava.web.dwr.*` (Module, DWRBase, Result?, StrokeAction?).
- `dwr.xml`, dependencia `dwr` en `pom.xml`, `xava/js/dwr-engine.js`.
</CodeContent>
<parameter name="EmptyFile">false
