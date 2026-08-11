# JSP → Java Renderer Migration — Handoff Notes

Context not already covered in `jsp-migration-analysis.md`. Read both files.

## Current state (Aug 2026)

Phases 0–2 complete and compiling. Wiring done. **First runtime test revealed a bug** (see below). No runtime success yet.

## Deleted JSP files

These were fully replaced by Java renderers and deleted from `src/main/resources/META-INF/resources/xava/`:

- `core.jsp` → `CoreRenderer`
- `buttonBar.jsp` → `ButtonBarRenderer`
- `bottomButtons.jsp` → `BottomButtonsRenderer`
- `errors.jsp` → `ErrorsRenderer`
- `messages.jsp` → `MessagesRenderer`
- `themeChooser.jsp` → `ThemeChooserRenderer`

## JSP files kept (safety net)

Still present because non-migrated JSPs include them:

- `barButton.jsp` — included by `collectionEditor.jsp`, `collectionFromModel.jsp`, `listEditor.jsp`, `list.jsp`
- `subButton.jsp` — included by `collectionEditor.jsp`, `collectionFromModel.jsp`
- `listConfigurations.jsp` — included by `list.jsp`
- `frameActions.jsp` — included by `detail.jsp`, `reference.jsp`, `collectionFrameHeader.jsp`

Remove these only after Phase 3/4 migrates their consumers.

## Wiring architecture

### HotwireServlet (`getURIAsString`, line ~751)

When `Parts.isJavaRendered(jspFile)` returns true, instead of `Servlets.getURIAsString` (which does `RequestDispatcher.include`), it:
1. Builds the URI with `getURI()` (same as before — appends `application`, `module`, form values as query string).
2. Parses the query string into `Map<String, String[]>` via `parseQueryString(uri)`.
3. Wraps the **original** request in `ParametersHttpServletRequest(request, params)` — merges original request params with parsed query string params (extra params override).
4. Calls `Parts.render(wrappedRequest, response, jspFile)`.

`Parts.render` creates a `ViewRenderContext.forPart(request, response, partDescriptor)` and dispatches to the registered renderer.

### module.jsp (line ~206, ~215)

Direct calls to `CoreRenderer.render(new ViewRenderContext(request, response))` and `ThemeChooserRenderer.render(...)` replace the former `<jsp:include>` for `core.jsp` and `themeChooser.jsp`.

### CoreRenderer → JspFragment bridge

`CoreRenderer` calls `JspFragment.render(ctx, manager.getViewURL())` to render the view (still `detail.jsp` or `list.jsp`). `JspFragment.render` calls `Servlets.getURIAsString(request, response, uri)` which does `RequestDispatcher.include(request, fakeResponse)`.

## Known bug: `NumberFormatException` in `comparatorsCombo.jsp`

### Symptom

`Integer.parseInt(request.getParameter("index"))` throws because `index` is null.

### Chain of calls

1. HotwireServlet renders `core.jsp` → `CoreRenderer.render(ctx)`.
2. `CoreRenderer` calls `JspFragment.render(ctx, manager.getViewURL())`.
3. In list mode, `getViewURL()` returns `list.jsp`.
4. `JspFragment.render` calls `Servlets.getURIAsString(ctx.getRequest(), ctx.getResponse(), "/xava/list.jsp")`.
5. `Servlets.getURIAsString` does `request.getRequestDispatcher("/xava/list.jsp").include(request, fakeResponse)`.
6. `list.jsp` includes `listEditor.jsp` (via `WebEditors.getUrl`).
7. `listEditor.jsp` builds `urlComparatorsCombo` with `index` as a query-string param and includes `comparatorsCombo.jsp`.

### Root cause analysis (not yet fixed)

The `index` parameter is passed by `listEditor.jsp` as a query string in the `<jsp:include page="<%=urlComparatorsCombo%>"/>` call (line 290–297). This should work regardless of the Java renderer migration because `listEditor.jsp` is still a JSP.

The issue is likely that `ctx.getRequest()` (the `ParametersHttpServletRequest` wrapper) interferes with `RequestDispatcher.include`'s parameter merging. Per servlet spec, `RequestDispatcher.include` should merge the included URL's query string params with the request's existing params. But when the request is already a `HttpServletRequestWrapper` that overrides `getParameter`/`getParameterMap`, Tomcat's `ApplicationDispatcher` may use the wrapper's `getParameter` instead of merging the include query string.

**Key insight**: `ParametersHttpServletRequest` overrides `getParameter`, `getParameterMap`, `getParameterNames`, and `getParameterValues` to return a fixed merged map. When `RequestDispatcher.include` is called with this wrapped request, Tomcat's `ApplicationRequest` (which wraps the request during include) may delegate `getParameter` to the underlying request (our wrapper) instead of merging the include query string. This means `<jsp:include page="comparatorsCombo.jsp?index=0&...">` does NOT add `index` to the parameters visible inside `comparatorsCombo.jsp`.

### Possible fixes (not yet attempted)

1. **Pass the original (unwrapped) request to `JspFragment`/`Servlets.getURIAsString`** instead of the `ParametersHttpServletRequest` wrapper. The wrapper is only needed for the Java renderer's own parameter access, not for JSP includes. The original request still has all the HTTP request parameters.
2. **In `CoreRenderer`, use `ctx.getRequest()` for parameter access but pass the original request to `JspFragment.render`**. This requires either storing the original request in `ViewRenderContext` or unwrapping it.
3. **Make `ParametersHttpServletRequest` not override `getParameter` etc. when the extra params are empty** — but this doesn't help because the extra params aren't empty.

**Recommended approach**: Option 1 or 2. The cleanest is to have `ViewRenderContext` store both the wrapped and original request, or to unwrap in `JspFragment.render` before calling `Servlets.getURIAsString`.

## Conventions and decisions

### HtmlWriter: `append()` vs `text()`

- `HtmlWriter.text()` escapes HTML special characters (`<`, `>`, `&`, `"`, `'`).
- `HtmlWriter.append()` writes raw strings.
- JSPs use `<%= ... %>` which outputs raw — so renderers must use `append()` for content that was previously raw JSP output (message labels, descriptions, configuration names, etc.).
- Use `text()` only for user-provided content that needs escaping (rare in these shell renderers).

### `org.openxava.web.Collections` shadows `java.util.Collections`

In the `org.openxava.web` package, `Collections` refers to `org.openxava.web.Collections`, not `java.util.Collections`. Always qualify `java.util.Collections` explicitly.

### `ViewRenderContext.forPart` vs constructor

- `forPart(request, response, partDescriptor)` parses query params from the part descriptor (e.g. `core.jsp?buttonBar=false`) and creates a context with those as `parameters`.
- `new ViewRenderContext(request, response)` creates a context with empty parameters — used for direct calls from `module.jsp`.
- `getParameter(name)` checks `parameters` map first, then falls back to `request.getParameter(name)`.

### `Parts` registry

- `register(name, renderer)` adds to `REGISTRY` map.
- `JSP_ALIASES` maps JSP file names to part names (e.g. `core.jsp` → `core`).
- `isJavaRendered(partDescriptor)` strips query string and path, then checks `REGISTRY`.
- `render(request, response, partDescriptor)` creates `ViewRenderContext.forPart` and dispatches.

### `ActionHtml` class

Centralizes action rendering (link, image, button) mirroring `<xava:action>` / `<xava:link>` / `<xava:image>` taglib output. Used by `ButtonBarRenderer`, `BottomButtonsRenderer`, `ButtonRenderer`, `SubButtonRenderer`.

## What to do next

1. **Fix the `comparatorsCombo.jsp` parameter bug** — this is blocking the first runtime test.
2. Run the full test suite manually from IntelliJ to validate the migration.
3. Proceed to Phase 3 (member renderers for `detail.jsp`).
