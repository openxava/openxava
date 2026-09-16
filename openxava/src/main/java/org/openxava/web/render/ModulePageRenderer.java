package org.openxava.web.render;

import java.io.*;
import java.util.*;

import jakarta.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.controller.*;
import org.openxava.util.*;
import org.openxava.web.*;
import org.openxava.web.style.*;
import org.openxava.web.servlets.*;

/**
 * Renders the full module page: HTML head with CSS/JS resources,
 * body with core content, and the initialization JavaScript.
 * <p>
 * When {@code htmlHead} is {@code false} only the body content is produced (for embedding
 * inside {@code naviox/index.jsp}).
 * </p>
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class ModulePageRenderer {

	private static Log log = LogFactory.getLog("ModulePageRenderer");

	public static String render(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Servlets.setCharacterEncoding(request, response);

		Style style = Style.getInstance(request);
		request.setAttribute("style", style);

		Messages errors = new Messages();
		request.setAttribute("errors", errors);
		Messages messages = new Messages();
		request.setAttribute("messages", messages);

		ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");
		if (context == null) {
			context = new ModuleContext();
			request.getSession().setAttribute("context", context);
		}
		String windowId = context.getWindowId(request);
		context.setCurrentWindowId(windowId);
		request.getSession().setAttribute("xava.user", request.getRemoteUser());

		String app = request.getParameter("application");
		String module = context.getCurrentModule(request);
		String contextPath = (String) request.getAttribute("xava.contextPath");
		if (contextPath == null) contextPath = request.getContextPath();

		ModuleManager managerHome = (ModuleManager) context.get(request, "manager", "org.openxava.controller.ModuleManager");
		ModuleManager manager = (ModuleManager) context.get(app, module, "manager", "org.openxava.controller.ModuleManager");

		manager.setSession(request.getSession());
		managerHome.setSession(request.getSession());
		manager.setApplicationName(request.getParameter("application"));
		manager.setModuleName(module);

		boolean restoreLastMessage = false;
		if (manager.isFormUpload()) {
			Multiparts.request(request, response, app, module);
		}
		else {
			restoreLastMessage = true;
		}

		boolean htmlHead = !Is.equalAsStringIgnoreCase(request.getParameter("htmlHead"), "false");
		String version = ModuleManager.getVersion();
		String realPath = request.getSession().getServletContext().getRealPath("/");
		Requests.init(request, app, module);
		manager.log(request, "MODULE:" + module);
		manager.setModuleURL(request);
		ModuleExecutor.execute(request, true);

		HtmlWriter w = new HtmlWriter();

		if (htmlHead) {
			w.append("<!DOCTYPE html>\n\n<head>\n");
			w.append("\t<title>").append(managerHome.getModuleDescription()).append("</title>\n");
			w.append("\t<link rel=\"icon\" href=\"").append(contextPath).append("/xava/images/favicon.ico\">\n");
			w.append("\t").append(style.getMetaTags()).append("\n");
		}

		if (style.getCssFile() != null) {
			w.append("\t<link href=\"").append(contextPath).append("/xava/style/")
				.append(style.getCssFile()).append("?ox=").append(version)
				.append("\" rel=\"stylesheet\" type=\"text/css\">\n");
		}
		w.append("\t<link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"")
			.append(contextPath).append("/xava/style/custom.css?ox=").append(version).append("\"/>\n");

		for (String styleEditor : EditorsResources.listCSSFiles(realPath, request.getSession().getServletContext())) {
			w.append("\t<link href=\"").append(contextPath).append("/xava/editors/")
				.append(styleEditor).append("?ox=").append(version)
				.append("\" rel=\"stylesheet\" type=\"text/css\">\n");
		}

		w.append("\t<script type='text/javascript' src='").append(contextPath)
			.append("/xava/js/openxava.js?ox=").append(version).append("'></script>\n");
		w.append("\t<script type='text/javascript' nonce='").append(Nonces.get(request)).append("'> \n");
		w.append("\t\topenxava.lastApplication='").append(app).append("'; \n");
		w.append("\t\topenxava.lastModule='").append(module).append("'; \n");
		w.append("\t\topenxava.language='").append(Locales.getCurrent().getLanguage()).append("'; \n");
		w.append("\t\topenxava.contextPath = '").append(contextPath).append("';\n");
		w.append("\t</script>\n");

		if (new File(realPath + "/xava/js/custom-editors.js").exists()) {
			w.append("\t<script type='text/javascript' src='").append(contextPath)
				.append("/xava/js/custom-editors.js?ox=").append(version).append("'></script>\n");
			log.warn(XavaResources.getString("custom_editors_deprecated"));
		}

		w.append("\t<script type=\"text/javascript\" src=\"").append(contextPath)
			.append("/xava/js/jquery.js?ox=").append(version).append("\"></script>\n");
		w.append("\t<script type=\"text/javascript\" src=\"").append(contextPath)
			.append("/xava/js/jquery-ui.js?ox=").append(version).append("\"></script>\n");
		w.append("\t<script type=\"text/javascript\" src=\"").append(contextPath)
			.append("/xava/js/jquery.sorttable.js?ox=").append(version).append("\"></script>\n");
		w.append("\t<script type=\"text/javascript\" src=\"").append(contextPath)
			.append("/xava/js/jquery.ui.touch-punch.js?ox=").append(version).append("\"></script>\n");
		w.append("\t<script type='text/javascript' src='").append(contextPath)
			.append("/xava/js/typewatch.js?ox=").append(version).append("'></script>\n");

		String browser = request.getHeader("user-agent");
		boolean browserIsHtmlUnit = browser != null && browser.contains("HtmlUnit");
		for (String editorJS : EditorsResources.listJSFiles(realPath, request.getSession().getServletContext())) {
			if (browserIsHtmlUnit && editorJS.equals("js/tinymce.js")) continue;
			String encoding = editorJS.toLowerCase().endsWith("-utf8.js") ? "UTF-8" : "ISO-8859-1";
			w.append("\t<script type=\"text/javascript\" charset=\"").append(encoding)
				.append("\" src=\"").append(contextPath).append("/xava/editors/")
				.append(editorJS).append("?ox=").append(version).append("\"></script>\n");
		}

		String[] jsFiles = request.getParameterValues("jsFiles");
		if (jsFiles != null) {
			for (int i = 0; i < jsFiles.length; i++) {
				if (jsFiles[i].endsWith(".js")) {
					w.append("\t<script type=\"text/javascript\" src=\"").append(contextPath)
						.append("/").append(jsFiles[i]).append("?ox=").append(version).append("\"></script>\n");
				}
			}
		}

		if (htmlHead) {
			w.append("</head> \n<body bgcolor=\"#ffffff\">\n");
		}

		boolean coreViaHotwire = manager.isCoreViaHotwire(request);
		if (!coreViaHotwire && restoreLastMessage) {
			LastMessages.restore(request, app, module);
		}

		if (manager.isResetFormPostNeeded()) {
			w.append("\t<form id=\"xava_reset_form\">\n");
			if (!"true".equals(request.getParameter("friendlyURL"))) {
				w.append("\t\t<input name=\"application\" type=\"hidden\" value=\"")
					.append(request.getParameter("application")).append("\"/>\n");
				w.append("\t\t<input name=\"module\" type=\"hidden\" value=\"")
					.append(request.getParameter("module")).append("\"/>\n");
			}
			w.append("\t</form>\n");
		}
		else {
			if (!coreViaHotwire) manager.executeBeforeLoadPage(request, errors, messages);
			w.append("\t<input id=\"xava_last_module_change\" type=\"hidden\" value=\"\"/>\n");
			w.append("\t<input id=\"xava_window_id\" type=\"hidden\" value=\"").append(windowId).append("\"/>\n");
			w.append("\t<input id=\"").append(Ids.decorate(request, "loading"))
				.append("\" type=\"hidden\" value=\"").append(coreViaHotwire).append("\"/>\n");
			w.append("\t<input id=\"").append(Ids.decorate(request, "loaded_parts"))
				.append("\" type=\"hidden\" value=\"\"/>\n");
			w.append("\t<input id=\"").append(Ids.decorate(request, "view_member"))
				.append("\" type=\"hidden\" value=\"\"/>\n");

			w.append("\t<div id='xava_processing_layer'>\n");
			w.append("\t\t<div>").append(XavaResources.getString(request, "processing")).append("</div>\n");
			w.append("\t\t<i class=\"mdi mdi-cog spin\"></i>\n");
			w.append("\t</div>\n");
			w.append(style.getCoreStartDecoration());
			w.append("\t<div id=\"").append(Ids.decorate(request, "core")).append("\" class=\"ox-module\">\n");
			if (!coreViaHotwire) {
				w.append(CoreRenderer.render(new ViewRenderContext(request, response)));
			}
			w.append("\t</div>\n");
			w.append(style.getCoreEndDecoration());
		}

		if (Themes.isChooserEnabled(request)) {
			w.append(ThemeChooserRenderer.render(new ViewRenderContext(request, response)));
		}

		w.append("\t<div id=\"xava_console\" >\n\t</div>\n");
		w.append("\t<div id=\"xava_loading\" role=\"progressbar\" aria-label=\"")
			.append(XavaResources.getString(request, "loading")).append("\">\n");
		w.append("\t\t<div class=\"ox-loading-progress\"></div>\n");
		w.append("\t</div>\n");

		if (htmlHead) {
			w.append("</body>\n</html>\n");
		}

		if (manager.isResetFormPostNeeded()) {
			manager.setResetFormPostNeeded(false);
			w.append("\t<script type=\"text/javascript\" nonce='").append(Nonces.get(request)).append("'>\n");
			w.append("\t$(\"#xava_reset_form\").submit();\n");
			w.append("\t</script>\n");
		}
		else {
			w.append("\n<span id='").append(Ids.decorate(request, "postjs")).append("'>\n</span>\n\n");

			w.append(renderInitScript(request, manager, style, app, module, browser, browserIsHtmlUnit, coreViaHotwire));
		}

		try {
			manager.commit();
		}
		finally {
			context.cleanCurrentWindowId();
			SessionData.clean();
		}

		return w.toString();
	}

	private static String renderInitScript(HttpServletRequest request, ModuleManager manager,
			Style style, String app, String module, String browser, boolean browserIsHtmlUnit,
			boolean coreViaHotwire) {
		HtmlWriter w = new HtmlWriter();
		String nonce = Nonces.get(request);
		w.append("<script type=\"text/javascript\" nonce='").append(nonce).append("'> \n");

		String prefix = Strings.change(manager.getApplicationName(), "-", "_")
			+ "_" + Strings.change(manager.getModuleName(), "-", "_");
		String onLoadFunction = prefix + "_openxavaOnLoad";
		String initiated = prefix + "_initiated";

		w.append(onLoadFunction).append(" = function() {\n");
		w.append("\tdocument.additionalParameters=\"").append(getAdditionalParameters(request)).append("\"; \n");
		w.append("\tif (openxava != null && openxava.").append(initiated).append(" == null) {\n");
		w.append("\t\topenxava.browser.ie = ").append(Browsers.isIE(request)).append(";\n");
		w.append("\t\topenxava.browser.ff = ").append(Browsers.isFF(request)).append(";\n");
		w.append("\t\topenxava.browser.edge = ").append(Browsers.isEdge(request)).append("; \n");
		w.append("\t\topenxava.showFiltersMessage = '").append(XavaResources.getString(request, "show_filters")).append("';\n");
		w.append("\t\topenxava.hideFiltersMessage = '").append(XavaResources.getString(request, "hide_filters")).append("';\n");
		w.append("\t\topenxava.confirmLoseChangesMessage = '").append(XavaResources.getString(request, "confirm_lose_changes")).append("';\n");
		w.append("\t\topenxava.confirmRemoveFileMessage = '").append(XavaResources.getString(request, "confirm_remove_file")).append("';\n");
		w.append("\t\topenxava.postErrorMessage = '").append(XavaResources.getString(request, "action_not_completed")).append("';\n");
		w.append("\t\topenxava.selectedRowClass = '").append(style.getSelectedRow()).append("';\n");
		w.append("\t\topenxava.currentRowClass = '").append(style.getCurrentRow()).append("';\n");
		w.append("\t\topenxava.currentRowCellClass = '").append(style.getCurrentRowCell()).append("';\n");
		w.append("\t\topenxava.selectedListFormatClass = '").append(style.getSelectedListFormat()).append("'; \n");
		w.append("\t\topenxava.customizeControlsClass = '").append(style.getCustomizeControls()).append("';\n");
		w.append("\t\topenxava.errorEditorClass = '").append(style.getErrorEditor()).append("';\n");
		w.append("\t\topenxava.editorClass = '").append(style.getEditor()).append("'; \n");
		w.append("\t\topenxava.listAdjustment = ").append(String.valueOf(style.getListAdjustment())).append(";\n");
		w.append("\t\topenxava.collectionAdjustment = ").append(String.valueOf(style.getCollectionAdjustment())).append(";\n");
		w.append("\t\topenxava.closeDialogOnEscape = ")
			.append(browser != null && browser.indexOf("Firefox") >= 0 ? "false" : "true").append("; \n");
		w.append("\t\topenxava.calendarAlign = '")
			.append(browser != null && browser.indexOf("MSIE 6") >= 0 ? "tr" : "Br").append("';\n");
		w.append("\t\topenxava.subcontrollerSelectedClass = '").append(style.getSubcontrollerSelected()).append("';\n");
		w.append("\t\topenxava.mapsTileProvider = '").append(XavaPreferences.getInstance().getMapsTileProvider()).append("';\n");
		w.append("\t\topenxava.mapsAttribution = \"")
			.append(XavaPreferences.getInstance().getMapsAttribution().replace("\"", "'")).append("\";\n");
		w.append("\t\topenxava.mapsTileSize = ").append(XavaPreferences.getInstance().getMapsTileSize()).append(";\n");
		w.append("\t\topenxava.mapsZoomOffset = ").append(XavaPreferences.getInstance().getMapsZoomOffset()).append(";\n");
		w.append("\t\topenxava.filterOnChange = ").append(XavaPreferences.getInstance().isFilterOnChange()).append(";\n");

		java.text.DecimalFormatSymbols symbols = java.text.DecimalFormatSymbols.getInstance(Locales.getCurrent());
		w.append("\t\topenxava.decimalSeparator = \"").append(symbols.getDecimalSeparator()).append("\";\n");
		w.append("\t\topenxava.groupingSeparator = \"").append(symbols.getGroupingSeparator()).append("\";\n");
		w.append("\t\topenxava.setHtml = ").append(style.getSetHtmlFunction()).append(";\n");

		if (XavaPreferences.getInstance().isEnterMovesToNextField()) {
			w.append("\t\topenxava.initFocusKey = openxava.setEnterAsFocusKey;\n");
		}
		if (browserIsHtmlUnit) {
			w.append("\t\topenxava.fadeIn = openxava.show;\n");
			w.append("\t\topenxava.browser.htmlUnit = true; \n");
		}

		String initThemeScript = style.getInitThemeScript();
		if (initThemeScript != null) {
			w.append("\t\topenxava.initTheme = function () { ").append(initThemeScript).append(" }; \n");
		}

		if (coreViaHotwire) {
			w.append("\t\topenxava.init(\"").append(manager.getApplicationName()).append("\", \"")
				.append(manager.getModuleName()).append("\", false);\n");
			w.append("\t\topenxava.request(\"").append(manager.getApplicationName()).append("\", \"")
				.append(manager.getModuleName()).append("\", true);\n");
		}
		else {
			w.append("\t\topenxava.init(\"").append(manager.getApplicationName()).append("\", \"")
				.append(manager.getModuleName()).append("\", true);\n");
			if (!Is.equalAsStringIgnoreCase(request.getParameter("noFocus"), "true")) {
				w.append("\t\topenxava.setFocus(\"").append(manager.getApplicationName()).append("\", \"")
					.append(manager.getModuleName()).append("\"); \n");
			}
		}
		w.append("\t\topenxava.").append(initiated).append(" = true;\n");
		w.append("\t}\n");
		w.append("}\n");
		w.append(onLoadFunction).append("();\n");
		w.append("</script>\n");
		return w.toString();
	}

	private static String getAdditionalParameters(HttpServletRequest request) {
		StringBuffer result = new StringBuffer();
		for (Enumeration<String> en = request.getParameterNames(); en.hasMoreElements(); ) {
			String name = en.nextElement();
			if ("application".equals(name) || "module".equals(name)) continue;
			if (!Strings.isJavaIdentifier(name)) continue;
			String value = request.getParameter(name);
			if (!Is.emptyString(value) && !(value.contains("<") || value.contains("\""))) {
				result.append('&');
				result.append(name);
				result.append('=');
				result.append(value);
			}
		}
		return result.toString();
	}

}
