# JSP → Java Renderer Migration — Handoff Notes

Context not already covered in `jsp-migration-analysis.md`. Read both files.

## Current state (Aug 2026)

**All phases (0–5) complete, compiling, and tested by user.** Application boots, module pages render, detail view and list mode work, collections render correctly, reference search and add-to-collection dialogs work, email unsubscription works via servlet.

## Deleted JSP files

These were fully replaced by Java renderers/servlets and deleted from `src/main/resources/META-INF/resources/xava/`:

- `core.jsp` → `CoreRenderer`
- `buttonBar.jsp` → `ButtonBarRenderer`
- `bottomButtons.jsp` → `BottomButtonsRenderer`
- `errors.jsp` → `ErrorsRenderer`
- `messages.jsp` → `MessagesRenderer`
- `themeChooser.jsp` → `ThemeChooserRenderer`
- `unsubscribe.jsp` → `UnsubscribeServlet` (`/xava/unsubscribe`)
- `referenceSearch.jsp` → `ReferenceSearchRenderer`
- `addToCollection.jsp` → `AddToCollectionRenderer`
- `collectionList.jsp` → `CollectionListRenderer` (called directly from `collectionEditor.jsp`)
- `collectionFromModel.jsp` → `CollectionFromModelRenderer`

## JSP files kept (safety net)

Still present because non-migrated JSPs include them:

- `barButton.jsp` — included by `collectionEditor.jsp`, `listEditor.jsp`, `list.jsp`
- `subButton.jsp` — included by `collectionEditor.jsp`
- `listConfigurations.jsp` — included by `list.jsp`
- `frameActions.jsp` — included by `detail.jsp`, `reference.jsp`, `collectionFrameHeader.jsp`
- `list.jsp` — kept as fallback, but `ListRenderer` is the primary renderer via `Parts`.
- `collection.jsp` — kept as fallback, but `CollectionRenderer` is the primary renderer via `Parts`. Still included by `detail.jsp` (which is itself a fallback for `DetailViewRenderer`).
- `detail.jsp` — kept as fallback for `DetailViewRenderer` via `JspFragment`.
- `sections.jsp` — kept as fallback for `SectionsRenderer` via `JspFragment`.
- `propertyActions.jsp` — kept as fallback for `PropertyActionsRenderer` via `JspFragment`.
- `collectionFrameHeader.jsp` — kept as fallback for `CollectionFrameHeaderRenderer` via `JspFragment`.
- `module.jsp` — thin wrapper calling `ModulePageRenderer.render()`. Kept as JSP because `naviox/index.jsp` and `signIn.jsp` include it via `<jsp:include>`.

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

- `list.jsp` — kept as fallback, but `ListRenderer` is the primary renderer via `Parts`.
- `collection.jsp` — kept as fallback, but `CollectionRenderer` is the primary renderer via `Parts`.
- `collectionEditor.jsp` — still JSP, orchestrates collection rendering but delegates to Java renderers for the actual list/from-model content.
- `collectionTotals.jsp` — still JSP, included from `CollectionFromModelRenderer` via `JspFragment`.
- `listEditor.jsp` — still JSP, included from `ListRenderer` via `JspFragment`.

### JSP files deleted (Phase 4)

- `collectionFromModel.jsp` — replaced by `CollectionFromModelRenderer` (via `Parts` and direct call from `collectionEditor.jsp`).
- `collectionList.jsp` — replaced by `CollectionListRenderer` (called directly from `collectionEditor.jsp`).

## What to do next

Phases 0–5 are complete and tested. Phases 6–8 are pending (see `jsp-migration-analysis.md`):

1. **Phase 6**: ✅ Done — deleted 9 dead fallback JSPs; `detail.jsp` restored as thin wrapper (still included by `editors/referenceEditor.jsp` and `editors/chartsEditor.jsp`).
2. **Phase 7**: Rewire `collectionEditor.jsp` / `listEditor.jsp` to call `ButtonRenderer` / `SubButtonRenderer` directly, then delete `barButton.jsp` and `subButton.jsp`.
3. **Phase 8**: Migrate `reference.jsp` → `ReferenceRenderer`, then delete `reference.jsp`, `htmlTagsEditor.jsp`, `referenceActions.jsp`.

### Future work (8.1+)

- Migrate `collectionEditor.jsp` fully to Java (currently delegates to Java renderers but is still JSP itself).
- Migrate `listEditor.jsp` and `collectionTotals.jsp` to Java.
- Support Thymeleaf (or similar) for editors and `naviox/*` customization, and rewrite the bundled editors/naviox with it.

## Phase 5: Page entry points and dialog helpers (completed)

### New Java renderer files

- `ModulePageRenderer.java` — renders the full module page (formerly `module.jsp`): HTML head with CSS/JS resources, body with core content, and the initialization JavaScript. Handles both full-page mode (`htmlHead=true`) and embedded mode (`htmlHead=false` for `naviox/index.jsp` includes).
- `ReferenceSearchRenderer.java` — renders the reference search list (formerly `referenceSearch.jsp`). Delegates to `ListRenderer` with `singleSelection=true` and the `rowAction` parameter.
- `AddToCollectionRenderer.java` — renders the add-to-collection list (formerly `addToCollection.jsp`). Wraps `ListRenderer` in a table with `onlyOneActionPerRow=true` and the `rowAction` parameter.

### New servlet files

- `UnsubscribeServlet.java` (`@WebServlet("/xava/unsubscribe")`) — handles email unsubscription requests (formerly `unsubscribe.jsp`). `EmailNotifications.java` updated to use `/xava/unsubscribe` instead of `/xava/unsubscribe.jsp`.

### Parts registry additions

- `referenceSearch` → `ReferenceSearchRenderer::render` (alias: `referenceSearch.jsp`)
- `addToCollection` → `AddToCollectionRenderer::render` (alias: `addToCollection.jsp`)

### module.jsp — thin wrapper

`module.jsp` is now a 5-line JSP that calls `ModulePageRenderer.render(request, response)`. Kept as a JSP because `naviox/index.jsp` and `signIn.jsp` include it via `<jsp:include>`.

### ModuleServlet — updated

`ModuleServlet` now calls `ModulePageRenderer.render()` directly instead of forwarding to `module.jsp`. Note: `ModuleServlet` has no `@WebServlet` annotation and is not registered — the main flow goes through `NaviOXServlet` (`/m/*`) → `naviox/index.jsp` → `module.jsp` (thin wrapper) → `ModulePageRenderer`.

### Action URL updates

- `ReferenceSearchAction.getCustomView()` — still returns `xava/referenceSearch.jsp?rowAction=...` (kept `.jsp` because `getViewURL()` appends `.jsp` if not present; `Parts.partName()` strips it so the Java renderer is dispatched).
- `GoAddElementsToCollectionAction.getCustomView()` — still returns `xava/addToCollection.jsp?rowAction=...` (same reason).

### JSP files kept (Phase 5)

- `module.jsp` — thin wrapper calling `ModulePageRenderer.render()`. Kept as JSP because `naviox/index.jsp` and `signIn.jsp` include it via `<jsp:include>`.

### JSP files deleted (Phase 5)

- `unsubscribe.jsp` — replaced by `UnsubscribeServlet` at `/xava/unsubscribe`.
- `referenceSearch.jsp` — replaced by `ReferenceSearchRenderer` via `Parts`.
- `addToCollection.jsp` — replaced by `AddToCollectionRenderer` via `Parts`.
