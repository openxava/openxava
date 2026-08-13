package org.openxava.web.render;

import java.util.*;

import jakarta.servlet.http.*;

import org.openxava.controller.*;
import org.openxava.controller.meta.*;
import org.openxava.util.*;
import org.openxava.web.*;
import org.openxava.web.style.*;

/**
 * Renders the top button bar: mode buttons, actions,
 * subcontrollers, list formats, email subscription, and help icon.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class ButtonBarRenderer {

	public static String render(ViewRenderContext ctx) {
		ModuleManager manager = ctx.getManager();
		Style style = ctx.getStyle();
		HttpServletRequest request = ctx.getRequest();

		if (!manager.isButtonBarVisible()) return "";

		String mode = ctx.getParameter("xava_mode");
		if (mode == null) mode = manager.isSplitMode() ? "detail" : manager.getModeName();
		boolean listFormats = !manager.isSplitMode() && mode.equals("list");

		HtmlWriter w = new HtmlWriter();
		w.append("<div class=\"ox-button-bar\">");
		w.append("<div id='").append(ctx.decorateId("controllerElement")).append("'>");
		w.append("<span>");

		Stack previousViews = (Stack) ctx.getModuleContext().get(request, "xava_previousViews");
		if (manager.isDetailMode() && !manager.isDetailModeOnly() && previousViews.isEmpty()) {
			Map<String, String> params = new HashMap<>();
			params.put("action", manager.getGoListAction());
			w.append(ButtonRenderer.render(new ViewRenderContext(request, ctx.getResponse(), params)));
		}

		Collection<MetaControllerElement> elements = manager.getMetaControllerElements();
		for (MetaControllerElement element : elements) {
			if (!element.appliesToMode(mode)) continue;
			if (element instanceof MetaAction) {
				MetaAction action = (MetaAction) element;
				if (!manager.actionApplies(action)) continue;
				if (action.hasImage() || action.hasIcon()) {
					Map<String, String> params = new HashMap<>();
					params.put("action", action.getQualifiedName());
					w.append(ButtonRenderer.render(new ViewRenderContext(request, ctx.getResponse(), params)));
				}
			} else if (element instanceof MetaSubcontroller) {
				MetaSubcontroller sub = (MetaSubcontroller) element;
				if (sub.hasActionsInThisMode(mode)) {
					Map<String, String> params = new HashMap<>();
					params.put("controller", sub.getControllerName());
					params.put("image", sub.getImage());
					params.put("icon", sub.getIcon());
					w.append(SubButtonRenderer.render(new ViewRenderContext(request, ctx.getResponse(), params)));
				}
			}
		}
		w.append("</span>");
		w.append("</div>");

		w.append("<div id='").append(ctx.decorateId("modes")).append("'>");
		w.append("<span>");
		w.append("<span class=\"ox-list-formats\">");
		if (listFormats) {
			String tabObject = ctx.getParameter("tabObject", "xava_tab");
			org.openxava.tab.Tab tab = (org.openxava.tab.Tab) ctx.getModuleContext().get(request, tabObject);
			Collection<String> editors = WebEditors.getEditors(tab.getMetaTab());
			if (editors.size() > 1) {
				for (String editor : editors) {
					String icon = WebEditors.getIcon(editor);
					if (icon == null) continue;
					String selected = editor.equals(tab.getEditor()) ? style.getSelectedListFormat() : "";
					if (Is.emptyString(editor)) editor = "__NONAME__";
					w.append(ActionHtml.link(ctx, "ListFormat.select", "editor=" + editor, selected, false,
							"<i class=\"mdi mdi-" + icon + "\" title=\"" + Labels.get(editor) + "\"></i>"));
				}
			}
		}
		w.append("</span>");

		if (EmailNotifications.isEnabled(manager.getModuleName())) {
			if (EmailNotifications.isSubscribedCurrentUserToModule(manager.getModuleName())) {
				w.append("<span class=\"" + style.getSubscribed() + "\">");
				w.append(ActionHtml.image(ctx, "EmailNotifications.unsubscribe", null, null, false));
				w.append("</span>");
			} else {
				w.append("<span class=\"" + style.getUnsubscribed() + "\">");
				w.append(ActionHtml.image(ctx, "EmailNotifications.subscribe", null, null, false));
				w.append("</span>");
			}
		}

		String helpURL = null;
		if (XavaPreferences.getInstance().isHelpAvailable() && style.isHelpAvailable()) {
			helpURL = Helps.urlForModule(request.getServletContext(), manager.getApplicationName(), manager.getModuleName());
		}
		if (helpURL != null) {
			String target = XavaPreferences.getInstance().isHelpInNewWindow() ? "_blank" : "";
			String helpImage = null;
			if (style.getHelpImage() != null) {
				helpImage = !style.getHelpImage().startsWith("/") ? request.getContextPath() + "/" + style.getHelpImage() : style.getHelpImage();
			}
			w.append("<span class=\"" + style.getHelp() + "\">");
			w.append("<a href=\"" + helpURL + "\" target=\"" + target + "\">");
			if (helpImage == null) {
				w.append("<i class=\"mdi mdi-help-circle\"></i>");
			} else {
				w.append("<img src=\"" + helpImage + "\"/>");
			}
			w.append("</a></span>");
		}
		w.append("&nbsp;");
		w.append("</span>");
		w.append("</div>");
		w.append("</div>");
		return w.toString();
	}

}
