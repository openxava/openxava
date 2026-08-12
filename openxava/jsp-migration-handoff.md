# JSP → Java Renderer Migration — Handoff Notes

Context not already covered in `jsp-migration-analysis.md`. Read both files.

## Current state (Aug 2026)

Phases 0–4 complete and compiling. Wiring done. **Application boots, detail view and list mode work, collections render correctly. Tested by user.**

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

## Known runtime issues (post-Phase 3)

### 1. Switching to a tab with a collection fails (detail mode) — FIXED

Reproducible in `InvoiceTest.testNotLoseChangesMessageInListMode_paginationInCollections_notLoseChangesMessageWhenModifyCollectionElement()`.

**Cause:** `collectionEditor.jsp` did `<jsp:include page="<%=WebEditors.getUrl(...)%>">`. `WebEditors.getUrl` returns paths with the `editors/` prefix (e.g. `editors/validValuesEditor.jsp`). From `/xava/editors/collectionEditor.jsp`, a relative include resolved to `/xava/editors/editors/validValuesEditor.jsp` → 404.

**Why it surfaced now:** With Java `DetailViewRenderer` + `JspFragment` → `RequestDispatcher.include` of `collection.jsp`, nested relative includes from `collectionEditor.jsp` resolve against the current JSP directory (`/xava/editors/`). Previously the same relative path may have been less exercised or resolved differently depending on the include chain from `detail.jsp`.

**Fix:** Use an absolute path in `collectionEditor.jsp`:
`page='<%="/xava/" + WebEditors.getUrl(p, view.getViewName())%>'`
(same pattern as `EditorTag`, which always includes under `/xava/`).

**Status:** Fixed in `collectionEditor.jsp`. Re-run the InvoiceTest method above to confirm.

### 2. `frameActions.jsp` still included by non-migrated JSPs

`frameActions.jsp` is still present because `reference.jsp` and other non-migrated JSPs include it. It will be removed when those JSPs are migrated.

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

## Phase 3: Detail view member renderers (completed)

### New Java renderer files

- `PropertyActionsRenderer.java` — renders property actions (formerly `propertyActions.jsp`)
- `ReferenceActionsRenderer.java` — renders reference actions (formerly `referenceActions.jsp`)
- `ReferenceFrameHeaderRenderer.java` — renders reference frame header (formerly `referenceFrameHeader.jsp`)
- `CollectionFrameHeaderRenderer.java` — renders collection frame header (formerly `collectionFrameHeader.jsp`)
- `PropertyEditorRenderer.java` — renders property editor with label, layout, and actions (formerly `editor.jsp` + `htmlTagsEditor.jsp`). Delegates actual editor to `editorWrapper.jsp` via `JspFragment`.
- `DetailViewRenderer.java` — main detail view renderer iterating meta members (formerly `detail.jsp`). Dispatches to `PropertyEditorRenderer`, delegates `reference.jsp`/`collection.jsp` to `JspFragment`.
- `SectionsRenderer.java` — renders section tabs and active section content (formerly `sections.jsp`). Recursively calls `DetailViewRenderer` for the active section.

### Parts registry additions

- `detail` → `DetailViewRenderer::render` (alias: `detail.jsp`)
- `sections` → `SectionsRenderer::render` (alias: `sections.jsp`)
- `propertyActions` → `PropertyActionsRenderer::render` (alias: `propertyActions.jsp`)
- `collectionFrameHeader` → `CollectionFrameHeaderRenderer::render` (alias: `collectionFrameHeader.jsp`)

### CoreRenderer wiring

`CoreRenderer` calls `Parts.render(ctx.getRequest(), ctx.getResponse(), viewURL)` when `Parts.isJavaRendered(viewURL)` is true. Both `detail.jsp` (→ `DetailViewRenderer`) and `list.jsp` (→ `ListRenderer`) are now Java-rendered. For non-migrated views, it falls back to `JspFragment.render`.

### JSPs still used via JspFragment (not yet migrated)

- `reference.jsp` — reference rendering (descriptions lists, composite editors, etc.)
- `editorWrapper.jsp` — wraps `<xava:editor>` tag with `propertyStyle` wrapping
- `collectionEditor.jsp` — still JSP, but calls Java renderers for collectionFromModel and collectionList
- `collectionTotals.jsp` — still JSP, included from `CollectionFromModelRenderer` via `JspFragment`
- `propertyActionsExt.jsp`, `referenceFrameHeaderExt.jsp`, `collectionFrameHeaderExt.jsp`, `referenceActionsExt.jsp` — empty extension hooks

### Key design decisions

- `DetailViewRenderer` handles frame layout (open/close divs) and member iteration, delegating leaf rendering to `PropertyEditorRenderer` for properties and `JspFragment` for references/collections.
- `FrameLayout` class encapsulates `openDiv`/`closeDiv`/`openDivForFrame`/`closeDivForFrame` logic from `detail.jsp`.
- `LayoutCells` class encapsulates `preLabel`/`postLabel`/`preEditor`/`postEditor` from `htmlTagsEditor.jsp`.
- `ViewRenderContext.withParameters(Map)` creates a derived context with merged parameters for nested renderer calls.
- `SectionsRenderer` uses `ActionHtml.link` with body content for section tab links (matching `<xava:link>` body content).

## Phase 4: List mode and collection renderers (completed)

### New Java renderer files

- `ListRenderer.java` — renders list mode header (title, configurations, group-by dropdown, row count) and delegates the tab editor to JSP via `JspFragment` (formerly `list.jsp`).
- `CollectionListRenderer.java` — prepares a collection tab (styles, context) and delegates to `ListRenderer` (formerly `collectionList.jsp`). Takes explicit parameters (idCollection, subview, lineAction, viewName, view) from `collectionEditor.jsp`.
- `CollectionFromModelRenderer.java` — renders a collection-from-model inline table with headers, row actions, checkboxes, and formatted values (formerly `collectionFromModel.jsp`). Self-contained from `ViewRenderContext`, computing all variables (view, subview, idCollection, propertyPrefix, lineAction, viewName) from context parameters. Delegates totals to `collectionTotals.jsp` via `JspFragment`.
- `CollectionRenderer.java` — resolves the collection editor via `WebEditors.getMetaEditorFor` and delegates to it via `JspFragment` (formerly `collection.jsp`).

### Parts registry additions

- `list` → `ListRenderer::render` (alias: `list.jsp`)
- `collection` → `CollectionRenderer::render` (alias: `collection.jsp`)
- `collectionFromModel` → `CollectionFromModelRenderer::render` (alias: `collectionFromModel.jsp`)

### DetailViewRenderer wiring

`DetailViewRenderer` now checks `Parts.isJavaRendered(collectionUrl)` for `collection.jsp` and uses `Parts.render` when true, falling back to `JspFragment` otherwise.

### collectionEditor.jsp wiring

`collectionEditor.jsp` now calls Java renderers directly instead of static `<%@include%>`:
- `CollectionFromModelRenderer.render(new ViewRenderContext(request, response))` for `collectionFromModel` collections.
- `CollectionListRenderer.render(new ViewRenderContext(request, response), idCollection, subview, lineAction, viewName, view)` for list-based collections.
- `listEditor` includes (custom list editors) remain as JSP includes.

### JSP files kept (Phase 4)

- `list.jsp` — kept as fallback, but `ListRenderer` is now the primary renderer via `Parts`.
- `collection.jsp` — kept as fallback, but `CollectionRenderer` is now the primary renderer via `Parts`.
- `collectionFromModel.jsp` — kept as fallback, but `CollectionFromModelRenderer` is now the primary renderer via `Parts` and direct call from `collectionEditor.jsp`.
- `collectionList.jsp` — kept as fallback, but `CollectionListRenderer` is called directly from `collectionEditor.jsp`.
- `collectionEditor.jsp` — still JSP, orchestrates collection rendering but delegates to Java renderers for the actual list/from-model content.
- `collectionTotals.jsp` — still JSP, included from `CollectionFromModelRenderer` via `JspFragment`.
- `listEditor.jsp` — still JSP, included from `ListRenderer` via `JspFragment`.

## What to do next

1. ~~Run the full test suite manually from IntelliJ~~ — user tested, works.
2. ~~Confirm collection-tab switching fix~~ — confirmed working.
3. Migrate `reference.jsp` to a Java renderer (Phase 5 candidate).
4. Migrate `collectionEditor.jsp` fully to Java (Phase 5 candidate — currently calls Java renderers but is still JSP itself).
5. Migrate `listEditor.jsp` and `collectionTotals.jsp` (Phase 5 candidate).
6. Migrate `module.jsp` page entry point (Phase 5 candidate).
