# UI8: Ralentización de tests tras modernización

## Síntoma

La suite de `openxavatest` (1040 tests) pasó de completarse en **< 1 h** a superar las **2 h** a mitad de ejecución. El cambio no estaba en la migración a Jakarta EE ni en el reemplazo del motor JSP→Java, sino en la modernización de la UI.

## Causa

HtmlUnit ejecuta JavaScript, y `ModuleTestBase.execute()` invoca `client.waitForBackgroundJavaScriptStartingBefore(12000)` tras cada acción. La modernización UI8 introdujo **timers `setTimeout` en `openxava.js` que se activan en casi todas las peticiones AJAX**. Cada timer pendiente bloquea la espera de HtmlUnit, sumando decenas de segundos por test y miles de segundos en la suite completa.

### Timers afectados

| Función | Línea (aprox.) | Comportamiento | Impacto |
|---------|----------------|----------------|---------|
| `openxava.scheduleMessagesAutoClose` | ~370 | Programa `fadeOut` de mensajes tras **5000 ms** | **Culpable principal**: bloquea ~5 s cada vez que se muestra un mensaje. |
| `openxava.scheduleLoading` | ~1638 | Retrasa `show()` del indicador de carga 200 ms | Penalización por cada request. |
| `openxava.hideLoading` | ~1650 | Usa `setTimeout` anidados hasta ~550 ms para ocultar el indicador | Penalización por cada request. |

El propio `ui8-modernization-bloque4-thread.md` documentaba indirectamente el problema: el test `MessagesTest` tuvo que implementarse con Selenium precisamente porque `waitForBackgroundJavaScriptStartingBefore` consume el `setTimeout` de auto-cierre en HtmlUnit.

## Fix

Se aprovechó la bandera `openxava.browser.htmlUnit` ya existente (se establece en `ModulePageRenderer.java:282` a partir del user-agent). Se añadieron guardas en `openxava/src/main/resources/META-INF/resources/xava/js/openxava.js`:

- `scheduleMessagesAutoClose`: retorno inmediato bajo HtmlUnit (los mensajes se mantienen visibles, como esperan los tests).
- `scheduleLoading`: `$('#xava_loading').show()` sin timer.
- `hideLoading`: `$('#xava_loading').hide()` sin timers.

En navegadores reales el comportamiento visual no cambia; los timers y animaciones siguen aplicándose.

### Diffs aplicados

```diff
 openxava.scheduleMessagesAutoClose = function(messagesDiv) {
+    if (openxava.browser.htmlUnit) return; // Pending timers block HtmlUnit's waitForBackgroundJavaScript
     var $messages = $(messagesDiv);
     ...
 }

 openxava.scheduleLoading = function() {
     clearTimeout(openxava.loadingTimeout);
+    if (openxava.browser.htmlUnit) {
+        $('#xava_loading').show();
+        return;
+    }
     openxava.loadingTimeout = setTimeout(function() {
         ...
     }, openxava.loadingDelay);
 }

 openxava.hideLoading = function() {
     clearTimeout(openxava.loadingTimeout);
     openxava.loadingTimeout = null;
+    if (openxava.browser.htmlUnit) {
+        $('#xava_loading').hide();
+        return;
+    }
     if (!$('#xava_loading').is(':visible')) return;
     ...
 }
```

## Resultado

- `InvoiceTest`: **10' 32" → 5' 17"** (aproximadamente la mitad).
- Escala lineal: si la suite completa se ralentizaba principalmente por estos timers, el fix debería devolverla a tiempos similares a los de `master`.

## Si la suite sigue lenta

Si tras relanzar la suite completa el tiempo sigue siendo > 1 h, revisar:

1. **Otros `setTimeout` / `setInterval` añadidos en UI8**: buscar con `grep -rn "setTimeout\|setInterval" -- "*.js"` y añadir `if (openxava.browser.htmlUnit) return;` donde sea seguro.
2. **Puntos de entrada de los timers**:
   - `openxava.request()` llama `scheduleLoading()` y `markListsAsLoading()`.
   - `openxava.showMessages()` / `openxava.showNotification()` llaman `scheduleMessagesAutoClose()`.
   - `openxava.initMessages()` llama `scheduleMessagesAutoClose()` en contenedores visibles.
3. **No buscar en CSS**: HtmlUnit está configurado sin procesar CSS. La ralentización no viene de ahí.
4. **Comparación con master**: `git diff master...style8 -- openxava/src/main/resources/META-INF/resources/xava/js/openxava.js` resume los cambios de JS relevantes.

## Archivo modificado

- `openxava/src/main/resources/META-INF/resources/xava/js/openxava.js`
