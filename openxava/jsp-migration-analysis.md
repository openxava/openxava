# JSP → Java migration analysis (OpenXava 8.0)

Working notes so progress is not lost between IntelliJ restarts.

## Scope decided with the user

- **Migrate to plain Java**: core UI rendering JSPs in `src/main/resources/META-INF/resources/xava` (`module.jsp`, `detail.jsp`, `list.jsp`, `core.jsp`, ...).
- **Keep as JSP (for now)**: `xava/editors/*` and all of `naviox/*` (`index.jsp`, `welcome.jsp`, `firstSteps.jsp`), because users customize them.
- **8.1 (later)**: support Thymeleaf (or similar) for editors and naviox customization, and rewrite the bundled editors/naviox with it.

## Already done (8.0)

| Item | Status |
|------|--------|
| `execute.jsp` → `org.openxava.web.ModuleExecutor` (+ `ParametersHttpServletRequest`) | Done, test suite 100% |
| `resetPreferences.jsp` → `org.openxava.web.servlets.ResetPreferencesServlet` (`/xava/resetPreferences`) | Done |
| Phase 0: `HtmlWriter`, `ViewRenderContext`, `JspFragment`, `PartRenderer`, `Parts` registry | Done, compiles |
| Phase 1: `ErrorsRenderer`, `MessagesRenderer`, `FrameActionsRenderer`, `ListConfigurationsRenderer`, `ThemeChooserRenderer`, `ButtonRenderer` (barButton), `SubButtonRenderer` | Done, compiles |
| Phase 2: `ButtonBarRenderer`, `BottomButtonsRenderer`, `CoreRenderer` | Done, compiles |
| Wiring: `HotwireServlet.getURIAsString` intercepts Java-rendered parts; `module.jsp` uses `CoreRenderer` + `ThemeChooserRenderer` | Done, compiles |
| Phase 3: `PropertyActionsRenderer`, `ReferenceActionsRenderer`, `ReferenceFrameHeaderRenderer`, `CollectionFrameHeaderRenderer`, `PropertyEditorRenderer`, `DetailViewRenderer`, `SectionsRenderer` | Done, compiles, app boots, basic detail view works |
| Phase 3 wiring: `CoreRenderer` uses `Parts.render` for `detail.jsp`; `Parts` registers `detail`, `sections`, `propertyActions`, `collectionFrameHeader` | Done |

Notes:
- `ModuleExecutor.execute(request, loadingModulePage)` called from `HotwireServlet.RequestProcessor.request()` and from `module.jsp`.
- Hotwire needs form values visible as request parameters (for `View.assignValuesToWebView()`); that is what `ParametersHttpServletRequest` + `buildModuleExecutionParameters()` reproduce (formerly the include query string).
- Careful: `org.openxava.web.Collections` shadows `java.util.Collections` in that package.
- JSP files for migrated parts are kept as safety net. `barButton.jsp`, `subButton.jsp`, `listConfigurations.jsp` are still included by non-migrated JSPs (`collectionEditor.jsp`, `collectionFromModel.jsp`, `listEditor.jsp`, `list.jsp`). Remove after full migration.
- `ActionHtml` centralizes action rendering (link, image, button) mirroring taglib output, used by all button renderers.

## Remaining JSPs in `xava` (non-editors)

Line counts approximate.

### Group A — Page / request entry points

| File | Lines | Role |
|------|-------|------|
| `module.jsp` | 320 | Full module page: html head, css/js resources, themes, nonce, calls `ModuleExecutor`, includes `core.jsp`, `themeChooser.jsp`. Entry point via `ModuleServlet` (`/xava/module.jsp?application=..&module=..`). |
| `unsubscribe.jsp` | 35 | Email unsubscribe landing page. Standalone; URL built in `EmailNotifications`. Almost logic + 3 `<xava:message>`. |

### Group B — Module shell (rendered as Hotwire "changed parts")

Keys used by `HotwireServlet.getChangedParts()`: `core`, `button_bar`, `bottom_buttons`, `errors`, `messages`, `view`, plus per-member keys.

| File | Lines | Role |
|------|-------|------|
| `core.jsp` | 85 | The `<form>`, hidden fields (`xava_action`, focus, etc.), button bar, view container, errors/messages containers, bottom buttons, `viewExt.jsp` hook. |
| `buttonBar.jsp` | 148 | Top bar: mode buttons, actions, list links, images. |
| `bottomButtons.jsp` | 59 | Bottom actions. |
| `barButton.jsp` | 50 | Single button/link rendering used by bars. |
| `subButton.jsp` | 75 | Submenu button (includes `barButton.jsp`). |
| `errors.jsp` | 25 | Error messages block. |
| `messages.jsp` | 50 | Info messages block. |

### Group C — View rendering (the hard core)

| File | Lines | Role |
|------|-------|------|
| `detail.jsp` | 344 | **The big one.** Iterates `view.getMetaMembers()` and renders properties, references, collections and groups, with frames/layout. Recursive (groups include `detail.jsp` again), plus `sections.jsp`. |
| `sections.jsp` | 79 | Section tabs; includes `detail.jsp` per section. |
| `reference.jsp` | 208 | Reference rendering: with/without frame, composite/non-composite editor, only-editor mode. |
| `editor.jsp` | 92 | Property editor wrapper: label, editor URL dispatch, `propertyActions.jsp`. |
| `editorWrapper.jsp` | 16 | Thin wrapper around `<xava:editor>`. |
| `htmlTagsEditor.jsp` | 20 | Computes `preLabel`/`postLabel`/`preEditor`/`postEditor` strings (layout decoration). No output. |
| `list.jsp` | 110 | List mode header (title, configurations, group-by combo, row count) then includes the tab editor (`listEditor.jsp` etc.). |
| `collection.jsp` | 27 | Resolves the collection editor and includes it. |
| `collectionList.jsp` | 22 | Prepares collection tab + styles, includes `../list.jsp`. |
| `collectionFromModel.jsp` | 191 | Collection rendering from model (ids, actions, links). |
| `listConfigurations.jsp` | 29 | List configurations dropdown. |

### Group D — Frame headers / action groups

| File | Lines | Role |
|------|-------|------|
| `frameActions.jsp` | 35 | Frame collapse/expand icons for a `frameId`. |
| `propertyActions.jsp` | 69 | Actions next to a property. |
| `referenceActions.jsp` | 45 | Actions for a reference. |
| `referenceFrameHeader.jsp` | 35 | Header content for reference frames (includes `propertyActions.jsp`). |
| `collectionFrameHeader.jsp` | 83 | Header content for collection frames (size, actions). |

### Group E — Dialog/search helpers

| File | Lines | Role |
|------|-------|------|
| `referenceSearch.jsp` | 12 | Include `list.jsp` with `singleSelection`. URL from `ReferenceSearchAction`. |
| `addToCollection.jsp` | 11 | Include list for adding elements. URL from `GoAddElementsToCollectionAction`. |
| `themeChooser.jsp` | 26 | Theme selector. |

### Group F — Do NOT migrate (user extension hooks, empty files)

`viewExt.jsp`, `propertyActionsExt.jsp`, `referenceActionsExt.jsp`, `referenceFrameHeaderExt.jsp`, `collectionFrameHeaderExt.jsp`
Also `imports.jsp` (only taglib declarations) disappears naturally when its includers become Java.

## Key couplings to respect

1. **`ModuleManager.getViewURL()`** returns a JSP path (`detail.jsp`, `list.jsp`, or a custom `webViewURL` from `application.xml`). Custom module views are a **public extension point** → after migration it must still be possible to point a module at a user JSP.
2. **Hotwire partial rendering** (`HotwireServlet.fillResult` / `getChangedParts`) maps a DOM id → a JSP URL string, then renders it with `Servlets.getURIAsString`. Migration must offer the equivalent: DOM id → Java renderer. Keys include `editor_<qname>`, `reference_editor_<qname>`, `property_actions_<qname>`, `frame_<qname>header`, `collection_<qname>.`, `collection_total_...`, `sections_<viewObject>`, `label_<...>` (already `html:`).
3. **Parameters as the calling convention**: JSPs communicate via request parameters (`viewObject`, `propertyKey`, `referenceKey`, `first`, `frame`, `composite`, `onlyEditor`, `collectionName`, `tabObject`, `frameId`, `closed`, ...) and request attributes (`request.setAttribute(propertyKey, metaProperty)`). In Java these become **method parameters / a context object**, which is the main readability win.
4. **Taglibs** used by these pages: `<xava:id>`, `<xava:action>`, `<xava:link>`, `<xava:image>`, `<xava:message>`, `<xava:editor>`, `<xava:button>`, `<xava:nonce>`, `<xava:label>`. The editors (staying JSP) also use them, so the tags must keep working; their logic should move to reusable Java helpers that both the tags and the new renderers call.
5. **Editors stay JSP** → the Java view renderer must still be able to include a JSP fragment and capture its HTML (`Servlets.getURIAsString`). This is the bridge that makes the split feasible.

## Recommended strategy: global design, incremental landing

Do **not** migrate `detail.jsp` in isolation, and do not do a single big-bang either.

**Reason**: the readability win comes from a shared model (a render context + a writer + a member-renderer hierarchy). If `detail.jsp` is migrated alone, it will invent its own conventions and then be rewritten when the rest arrives.

### Phase 0 — Define the foundation (no behavior change)

Small package, e.g. `org.openxava.web.render` (name to decide):

- `HtmlWriter` / `Html` — append + escaping helpers, so no manual string concatenation.
- `ViewRenderContext` — carries `request`, `response`, `ModuleContext`, `ModuleManager`, `Style`, `Messages errors/messages`, `application`, `module`, id decoration (`Ids.decorate`).
- `JspFragment` (bridge) — renders a still-JSP fragment (editors, custom module views) into HTML via `Servlets.getURIAsString`.
- `PartRenderer` (interface) — `String render(ViewRenderContext)`; lets Hotwire map DOM id → renderer instead of DOM id → JSP URL.

### Phase 1 — Leaves (low risk, immediate proof)

`errors.jsp`, `messages.jsp`, `frameActions.jsp`, `listConfigurations.jsp`, `themeChooser.jsp`, `barButton.jsp`, `subButton.jsp`.

Suggested names: `ErrorsRenderer`, `MessagesRenderer`, `FrameActionsRenderer`, `ListConfigurationsRenderer`, `ThemeChooserRenderer`, `ButtonRenderer`.

### Phase 2 — Bars and shell

`buttonBar.jsp`, `bottomButtons.jsp`, `core.jsp` → `ButtonBarRenderer`, `BottomButtonsRenderer`, `ModuleFormRenderer` (or `CoreRenderer`).
At this point Hotwire's `changedParts` for `core`, `button_bar`, `bottom_buttons`, `errors`, `messages` stop being JSP URLs.

### Phase 3 — Member renderers (the design that makes `detail.jsp` clean)

Split `detail.jsp` by member type instead of one giant loop:

- `DetailViewRenderer` — iterates members, delegates; owns layout open/close (`openDiv`/`closeDiv`/frame wrapping) in something like `LayoutWriter` / `FrameWriter`.
- `PropertyMemberRenderer`
- `ReferenceMemberRenderer` (absorbs `reference.jsp`)
- `CollectionMemberRenderer` (absorbs `collection.jsp`, `collectionList.jsp`, `collectionFrameHeader.jsp`)
- `GroupMemberRenderer` (recursive into `DetailViewRenderer`)
- `SectionsRenderer` (absorbs `sections.jsp`)
- `PropertyEditorRenderer` (absorbs `editor.jsp`, `editorWrapper.jsp`, `htmlTagsEditor.jsp` decoration) — **delegates the actual editor to JSP** via `JspFragment`.
- `PropertyActionsRenderer`, `ReferenceActionsRenderer`, `ReferenceFrameHeaderRenderer`, `FrameHeaderRenderer`.

Naming principle requested by the user: names should say **what part of the UI they produce**, not "helper"/"util".

### Phase 4 — List mode

`list.jsp` (+ `collectionFromModel.jsp`) → `ListRenderer` / `ListHeaderRenderer`, still delegating to `listEditor.jsp` through `JspFragment`.

### Phase 5 — Page entry points

`module.jsp` → `ModulePageRenderer` + servlet (merge with `ModuleServlet`); `unsubscribe.jsp` → `UnsubscribeServlet`.
`referenceSearch.jsp` / `addToCollection.jsp` become tiny renderers or just parameters to the list renderer; the URLs returned by `ReferenceSearchAction` / `GoAddElementsToCollectionAction` must keep working (or be updated together).

## Compatibility rules for the whole migration

- **Same HTML output** (ids, CSS classes, data-attributes) so `openxava.js`, `listEditor.js` and the HtmlUnit/Selenium suite keep passing. The suite is the real regression net.
- **`webViewURL` custom views keep working** (user JSP for a module).
- **Editors keep being JSP**, resolved via `WebEditors` as today.
- **Ext hooks keep being includable JSPs** (`viewExt.jsp` etc.) — the Java renderers must include them at the same points.
- Migrate in vertical slices, run the suite after each slice.

## Test commands (user runs them from IntelliJ)

- openxava compile: `mvn compile` in `E:\IdeaProjects\openxava\openxava`
- Full suite: openxavatest module tests (HtmlUnit) + chattest.
