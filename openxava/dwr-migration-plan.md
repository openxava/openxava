# Plan de Migración de DWR a Fetch + Servlet en OpenXava: Módulo Discussion

Este documento detalla el plan paso a paso para migrar el módulo de discusiones (`Discussion`) de OpenXava, actualmente implementado con Direct Web Remoting (DWR), hacia un enfoque moderno utilizando JavaScript `fetch` nativo en el cliente y un `HttpServlet` estándar en el servidor. 

Esta migración servirá como prototipo y base de aprendizaje para eliminar completamente el uso de DWR en el framework.

---

## 1. Análisis de la Implementación Actual

El flujo actual con DWR se compone de los siguientes elementos:

1. **Servidor (Clase DWR)**:
   - `@/E:/IdeaProjects/openxava/openxava/src/main/java/org/openxava/web/dwr/Discussion.java`: Define el método `postComment(...)` que gestiona la persistencia del comentario, auditoría de modificaciones mediante `AccessTracker` y control de transacciones. Extiende de `DWRBase` para obtener control de seguridad, inicialización del contexto y codificación de caracteres.
2. **Configuración de DWR**:
   - `@/E:/IdeaProjects/openxava/openxava/src/main/resources/META-INF/resources/WEB-INF/dwr.xml`: Define el creador para exponer el componente Java bajo el alias JavaScript `Discussion`.
3. **Cliente (Frontend)**:
   - `@/E:/IdeaProjects/openxava/openxava/src/main/resources/META-INF/resources/xava/editors/js/discussionEditor.js`: Carga dinámicamente el script de DWR `/dwr/interface/Discussion.js` e invoca directamente `Discussion.postComment(application, module, discussionId, commentContent)`.

---

## 2. Nueva Arquitectura Propuesta

La nueva arquitectura elimina por completo DWR para el componente `Discussion` sustituyéndolo por:

1. **Cliente**: Un envío HTTP POST asíncrono estándar mediante la API `fetch` nativa del navegador.
2. **Servidor**: Un servlet Java registrado mediante anotaciones estándar de Java Servlet (`@WebServlet`) mapeado en la ruta `/xava/discussion`.

---

## 3. Plan de Migración Paso a Paso

### Paso 1: Implementar el Servlet en Servidor (`DiscussionServlet.java`)

Se creará una nueva clase en el paquete de servlets de OpenXava para recibir las peticiones de discusión, procesar la petición, inicializar el contexto del módulo, realizar las validaciones de seguridad pertinentes (adaptadas de `DWRBase`) y persistir el comentario de la discusión.

- **Ruta del nuevo archivo**: `@/E:/IdeaProjects/openxava/openxava/src/main/java/org/openxava/web/servlets/DiscussionServlet.java`

#### Blueprint de `DiscussionServlet.java`

```java
package org.openxava.web.servlets;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import org.openxava.controller.*;
import org.openxava.formatters.*;
import org.openxava.jpa.*;
import org.openxava.util.*;
import org.openxava.view.*;
import org.openxava.web.*;
import org.openxava.web.editors.*;

/**
 * Servlet para gestionar los comentarios de discusión sin DWR.
 * 
 * @author Javier Paniza / Migración Fetch
 * @since 8.0
 */
@WebServlet(name = "discussion", urlPatterns = "/xava/discussion")
public class DiscussionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String application = request.getParameter("application");
        String module = request.getParameter("module");
        String discussionId = request.getParameter("discussionId");
        String commentContent = request.getParameter("commentContent");

        try {
            initRequest(request, response, application, module);
            
            DiscussionComment comment = new DiscussionComment();
            comment.setDiscussionId(discussionId);
            comment.setUserName(Users.getCurrent());
            comment.setComment(commentContent);
            XPersistence.getManager().persist(comment);
            
            trackModification(request, application, module, discussionId, commentContent);
            
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            try {
                XPersistence.commit();
            } finally {
                cleanRequest();
            }
        }
    }

    private void initRequest(HttpServletRequest request, HttpServletResponse response, String application, String module) {
        Servlets.setCharacterEncoding(request, response);
        ModuleContext context = getContext(request);
        if (context != null) context.setCurrentWindowId(request);
        checkSecurity(request, application, module);
        Requests.partialInit(request, application, module);
    }

    private void cleanRequest() {
        Requests.clean();
    }

    private ModuleContext getContext(HttpServletRequest request) {
        return (ModuleContext) request.getSession().getAttribute("context");
    }

    private void checkSecurity(HttpServletRequest request, String application, String module) {
        ModuleContext context = getContext(request);
        if (context == null) {
            throw new SecurityException("6859");
        }
        if (!context.exists(application, module, "manager")) {
            throw new SecurityException("9876");
        }
        if (context.exists(application, module, "naviox_locked")) {
            Boolean locking = (Boolean) context.get(application, module, "naviox_locking");
            if (!locking) {
                Boolean locked = (Boolean) context.get(application, module, "naviox_locked");
                if (locked) throw new SecurityException("3923");
            }
        }
    }

    private void trackModification(HttpServletRequest request, String application, String module, String discussionId, String commentContent) {
        View view = (View) getContext(request).get(application, module, "xava_view");
        String property = getDiscussionProperty(view.getValues(), discussionId);
        Map<String, Object> oldChangedValues = new HashMap<>();
        oldChangedValues.put(property, XavaResources.getString("discussion_new_comment"));
        Map<String, Object> newChangedValues = new HashMap<>();
        String formattedContent = formatContent(request, commentContent);
        newChangedValues.put(property, formattedContent);
        AccessTracker.modified(view.getModelName(), view.getKeyValues(), oldChangedValues, newChangedValues);
    }

    private String formatContent(HttpServletRequest request, String commentContent) {
        try {
            return new HtmlTextListFormatter().format(request, commentContent);
        } catch (Exception e) {
            return commentContent;
        }
    }

    private String getDiscussionProperty(Map<?, ?> values, String discussionId) {
        return (String) Maps.getKeyFromValue(values, discussionId, "DISCUSSION");
    }
}
```

---

### Paso 2: Adaptar el Frontend (`discussionEditor.js`)

Se modificará el inicializador del editor de discusiones para eliminar la carga del archivo autogenerado de DWR y se reescribirán los métodos que envían el comentario para realizar un `fetch` POST HTTP estándar hacia el nuevo servlet.

- **Ruta del archivo a modificar**: `@/E:/IdeaProjects/openxava/openxava/src/main/resources/META-INF/resources/xava/editors/js/discussionEditor.js`

#### Cambios requeridos en `discussionEditor.js`

1. **Eliminar la línea de carga de DWR**:
   ```javascript
   // Eliminar esta línea
   openxava.getScript(openxava.contextPath + "/dwr/interface/Discussion.js"); 
   ```

2. **Reescribir `discussionEditor.postMessage` y `discussionEditor.postMessageHtmlUnit` para usar Fetch**:

```javascript
if (discussionEditor == null) var discussionEditor = {};

openxava.addEditorInitFunction(function() {
	$('.ox-discussion-add-button').off('click').click(function() {
		discussionEditor.postMessage(openxava.lastApplication, openxava.lastModule, $(this).parent().data("discussion-id"))
	});
	$('.ox-discussion-cancel-button').off('click').click(function() {
		discussionEditor.cancel($(this).parent().data("discussion-id"));
	});	
});

discussionEditor.postMessage = function(application, module, discussionId) {
	var newComment = tinymce.get('xava_new_comment_' + discussionId); 
	var comments = $('#xava_comments_' + discussionId);
	var lastComment = comments.children().last(); 
	var template = lastComment.clone();
	var commentContent = newComment.getContent(); 
	lastComment.find(".ox-discussion-comment-content").html(commentContent);
	lastComment.slideDown(); 
	newComment.resetContent(""); 

	// Reemplazo de DWR.postComment por fetch API nativo
	discussionEditor.sendComment(application, module, discussionId, commentContent);

	comments.append(template);
	discussionEditor.clear(discussionId); 
}

discussionEditor.postMessageHtmlUnit = function(application, module, discussionId, commentContent) {
	var comments = $('#xava_comments_' + discussionId);
	var lastComment = comments.children().last(); 
	var template = lastComment.clone();
	lastComment.find(".ox-discussion-comment-content").html(commentContent);
	lastComment.show(); 

	// Reemplazo de DWR.postComment por fetch API nativo
	discussionEditor.sendComment(application, module, discussionId, commentContent);

	comments.append(template);
}

// Nueva función auxiliar para encapsular la llamada HTTP fetch
discussionEditor.sendComment = function(application, module, discussionId, commentContent) {
	var url = openxava.contextPath + "/xava/discussion";
	var params = new URLSearchParams();
	params.append("application", application);
	params.append("module", module);
	params.append("discussionId", discussionId);
	params.append("commentContent", commentContent);

	fetch(url, {
		method: "POST",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded"
		},
		body: params
	})
	.then(function(response) {
		if (!response.ok) {
			throw new Error("HTTP status " + response.status);
		}
	})
	.catch(function(error) {
		console.error("Error posting discussion comment:", error);
		alert("Error: Discussion comment not added");
	});
}

discussionEditor.cancel = function(discussionId) {
	discussionEditor.clear(discussionId);
}

discussionEditor.clear = function(discussionId) {
	$("#xava_new_comment_" + discussionId + "_buttons input").fadeOut();
	$('.ox-button-bar-button').fadeIn();
	$('.ox-bottom-buttons').css("visibility", "visible");  
	$('.ox-bottom-buttons').children().fadeIn(); 
	tinymce.get('xava_new_comment_' + discussionId).resetContent(); 
}
```

---

### Paso 3: Limpieza de la configuración de DWR

Una vez verificado que el nuevo enfoque funciona correctamente, se eliminarán las referencias a la clase `Discussion` de DWR:

1. **Editar `dwr.xml`**:
   - Eliminar el bloque de creación del bean de discusión en `@/E:/IdeaProjects/openxava/openxava/src/main/resources/META-INF/resources/WEB-INF/dwr.xml`:
     ```xml
     <!-- ELIMINAR ESTE BLOQUE -->
     <create creator="new" javascript="Discussion">
       <param name="class" value="org.openxava.web.dwr.Discussion"/>
     </create>
     ```

2. **Eliminar la clase DWR heredada**:
   - Borrar el archivo `@/E:/IdeaProjects/openxava/openxava/src/main/java/org/openxava/web/dwr/Discussion.java`.

---

## 4. Estrategia de Migración para el Resto de DWR (Futuro)

Tras consolidar con éxito la migración de `Discussion`, se abordarán las restantes clases de DWR en `org.openxava.web.dwr` (siendo la más compleja `Module`):

1. **Identificación de clases a migrar**:
   - `Module`, `Tab`, `View`, `Calendar`, `Tree`, `Descriptions`.
2. **Centralización vs Servlets Múltiples**:
   - Para no saturar con decenas de servlets específicos, se propone crear un servlet general de despacho (ej. `org.openxava.web.servlets.XavaAjaxServlet`) que acepte un parámetro como `action` o `method` y enrute a la clase de servicio correspondiente, actuando de forma similar a un controlador frontal AJAX.
3. **Extracción de la Lógica Común de Seguridad**:
   - Las validaciones de seguridad de `DWRBase` deberán ser extraídas a una clase de utilidad (por ejemplo, en `Servlets.java` o `Requests.java`) para que cualquier nuevo servlet o manejador AJAX pueda verificar que el usuario actual tenga acceso legítimo a la aplicación y al módulo.
