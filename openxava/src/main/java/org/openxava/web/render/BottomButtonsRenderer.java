package org.openxava.web.render;

import java.util.*;

import jakarta.servlet.http.*;

import org.openxava.controller.*;
import org.openxava.controller.meta.*;
import org.openxava.util.*;
import org.openxava.web.*;

/**
 * Renders the bottom action buttons (formerly bottomButtons.jsp): default action,
 * mode-appropriate actions, and hidden default action submit button.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class BottomButtonsRenderer {

	public static String render(ViewRenderContext ctx) {
		ModuleManager manager = ctx.getManager();
		HttpServletRequest request = ctx.getRequest();
		if (!manager.isBottomButtonsVisible()) return "";

		boolean buttonBar = !"false".equalsIgnoreCase(ctx.getParameter("buttonBar"));
		String mode = ctx.getParameter("xava_mode");
		if (mode == null) mode = manager.getModeName();

		HtmlWriter w = new HtmlWriter();

		String defaultAction = null;
		if (XavaPreferences.getInstance().isShowDefaultActionInBottom() && manager.isDetailMode()) {
			defaultAction = manager.getDefaultActionQualifiedName();
			w.append(ActionHtml.button(ctx, defaultAction, null));
		}

		Iterator it = manager.getMetaActions().iterator();
		while (it.hasNext()) {
			MetaAction action = (MetaAction) it.next();
			if (!manager.actionApplies(action)) continue;
			if (action.getQualifiedName().equals(defaultAction)) continue;
			if (action.appliesToMode(mode) && (!buttonBar || !(action.hasImage() || action.hasIcon()))) {
				if (action.hasIcon() && action.getLabel().isEmpty()) {
					w.append(ActionHtml.action(ctx, action.getQualifiedName(), null));
				} else {
					w.append(ActionHtml.button(ctx, action.getQualifiedName(), null));
				}
			}
		}

		MetaAction defaultMetaAction = manager.getDefaultMetaAction();
		if (defaultMetaAction != null) {
			w.append("<button class=\"xava_action\" name=\"xava.DEFAULT_ACTION\" type=\"submit\"");
			w.append(" data-application='").append(ctx.getParameter("application")).append("'");
			w.append(" data-module='").append(ctx.getParameter("module")).append("'");
			w.append(" data-confirm-message=\"").append(HtmlWriter.filterApostrophes(defaultMetaAction.getConfirmMessage(request))).append("\"");
			w.append(" data-takes-long=\"").append(defaultMetaAction.isTakesLong()).append("\"");
			w.append(" data-action=\"").append(manager.getDefaultActionQualifiedName()).append("\"");
			w.append(" data-in-new-window=\"").append(defaultMetaAction.inNewWindow()).append("\"");
			w.append("></button>");
		}
		return w.toString();
	}

}
