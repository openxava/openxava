package org.openxava.web.render;

import org.openxava.controller.*;
import org.openxava.util.*;
import org.openxava.view.*;
import org.openxava.web.*;
import org.openxava.web.style.*;

/**
 * Renders the core module shell: form, hidden fields,
 * button bar, errors, messages, view, and bottom buttons.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class CoreRenderer {

	public static String render(ViewRenderContext ctx) throws Exception {
		ModuleManager manager = ctx.getManager();
		View view = ctx.getView();
		Style style = ctx.getStyle();
		boolean buttonBar = !"false".equalsIgnoreCase(ctx.getParameter("buttonBar"));
		String focusPropertyId = manager.isListMode() ? Lists.FOCUS_PROPERTY_ID : view.getFocusPropertyId();

		HtmlWriter w = new HtmlWriter();
		String formId = ctx.decorateId("form");
		w.append("<form id='").append(formId).append("' name='").append(formId).append("'");
		w.append(" class=\"xava_form\" method='POST' ").append(manager.getEnctype()).append(" ");
		w.append(manager.getFormAction(ctx.getRequest())).append(">");

		w.append("<INPUT type=\"hidden\" name='").append(ctx.decorateId("xava_action")).append("' value=\"\"/>");
		w.append("<INPUT type=\"hidden\" name='").append(ctx.decorateId("xava_action_argv")).append("' value=\"\"/>");
		w.append("<INPUT type=\"hidden\" name='").append(ctx.decorateId("xava_action_range")).append("' value=\"\"/>");
		w.append("<INPUT type=\"hidden\" name='").append(ctx.decorateId("xava_action_already_processed")).append("' value=\"\"/>");
		w.append("<INPUT type=\"hidden\" name='").append(ctx.decorateId("xava_action_application")).append("' value=\"").append(ctx.getParameter("application")).append("\"/>");
		w.append("<INPUT type=\"hidden\" name='").append(ctx.decorateId("xava_action_module")).append("' value=\"").append(ctx.getParameter("module")).append("\"/>");
		w.append("<INPUT type=\"hidden\" name='").append(ctx.decorateId("xava_changed_property")).append("'/>");
		w.append("<INPUT type=\"hidden\" id='").append(ctx.decorateId("xava_current_focus")).append("' name='").append(ctx.decorateId("xava_current_focus")).append("'/>");
		w.append("<INPUT type=\"hidden\" id='").append(ctx.decorateId("xava_previous_focus")).append("' name='").append(ctx.decorateId("xava_previous_focus")).append("'/>");
		w.append("<INPUT type=\"hidden\" name='").append(ctx.decorateId("xava_focus_forward")).append("'/>");
		w.append("<INPUT type=\"hidden\" id='").append(ctx.decorateId("xava_focus_property_id")).append("' name='").append(ctx.decorateId("xava_focus_property_id")).append("' value=\"").append(focusPropertyId).append("\"/>");

		String listModeClass = manager.isListMode() ? "class='" + style.getListMode() + "'" : "";
		w.append("<div ").append(listModeClass).append(">");

		if (buttonBar) {
			w.append("<div id='").append(ctx.decorateId("button_bar")).append("' class=\"").append(style.getButtonBarContainer()).append("\">");
			w.append(Parts.render(ctx.getRequest(), ctx.getResponse(), "buttonBar"));
			w.append("</div>");
		}

		w.append("<div class=\"").append(style.getView()).append("\">");
		if (style.isShowModuleDescription() && !manager.isListMode()) {
			w.append("<div class=\"").append(style.getModuleDescription()).append("\">");
			w.append(manager.getModuleDescription());
			w.append("</div>");
		}

		w.append("<div id='").append(ctx.decorateId("errors")).append("' class=\"ox-display-inline\">");
		w.append(Parts.render(ctx.getRequest(), ctx.getResponse(), "errors"));
		w.append("</div>");

		w.append("<div id='").append(ctx.decorateId("messages")).append("' class=\"ox-display-inline\">");
		w.append(Parts.render(ctx.getRequest(), ctx.getResponse(), "messages"));
		w.append("</div>");

		w.append("<div id='").append(ctx.decorateId("view")).append("' ");
		if (!manager.isListMode()) {
			w.append("class='ox-detail");
			if (view.isSimple()) w.append(" ox-simple-layout");
			if (view.isFlowLayout()) w.append(" ox-flow-layout");
			w.append("'");
		}
		w.append(">");
		String viewURL = manager.getViewURL();
		if (Parts.isJavaRendered(viewURL, ctx.getRequest())) {
			w.append(Parts.render(ctx.getRequest(), ctx.getResponse(), viewURL));
		} else {
			w.append(JspFragment.render(ctx, viewURL));
		}
		w.append("</div>");

		w.append(JspFragment.render(ctx, "viewExt.jsp"));

		w.append("</div>");

		if (style.isSeparatorBeforeBottomButtons()) {
			w.append("<div class=\"ox-core-bottom-buttons-separator\"></div>");
		}

		w.append("<div id='").append(ctx.decorateId("bottom_buttons")).append("' class=\"").append(style.getBottomButtons()).append("\">");
		w.append(Parts.render(ctx.getRequest(), ctx.getResponse(), "bottomButtons"));
		w.append("</div>");

		w.append("</div>");
		w.append("</form>");
		return w.toString();
	}

}
