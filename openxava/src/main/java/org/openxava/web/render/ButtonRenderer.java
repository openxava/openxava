package org.openxava.web.render;

import org.openxava.controller.meta.*;
import org.openxava.util.*;

/**
 * Renders a single button-bar button.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class ButtonRenderer {

	public static String render(ViewRenderContext ctx) {
		boolean showImages = true;
		boolean showIcons = XavaPreferences.getInstance().isUseIconsInsteadOfImages();
		boolean showLabels = XavaPreferences.getInstance().isShowLabelsForToolBarActions();
		String actionName = ctx.getParameter("action");
		boolean addSpace = ctx.getBooleanParameter("addSpaceWithoutImage", false);

		if (Is.emptyString(actionName)) return "";

		MetaAction action = MetaControllers.getMetaAction(actionName);
		String argv = ctx.getParameter("argv");
		String label = action.getLabel(ctx.getRequest());
		boolean alwaysAvailable = ctx.getBooleanParameter("alwaysAvailable", false);

		HtmlWriter w = new HtmlWriter();

		w.append("<span class=\"ox-button-bar-button\">");
		String linkBody = renderButtonBody(showImages, showIcons, showLabels, action, label, addSpace, ctx);
		w.append(ActionHtml.link(ctx, action.getQualifiedName(), argv, null, alwaysAvailable, linkBody));
		w.append("</span>");
		return w.toString();
	}

	private static String renderButtonBody(boolean showImages, boolean showIcons, boolean showLabels,
			MetaAction action, String label, boolean addSpace, ViewRenderContext ctx) {
		HtmlWriter w = new HtmlWriter();
		boolean showLabel = (showLabels || !action.hasImage()) && !Is.emptyString(label);
		boolean showImage = showImages && action.hasImage() || action.hasImage() && Is.emptyString(label);
		boolean showIcon = action.hasIcon() && (showImages && (showIcons || !action.hasImage()) || Is.emptyString(label) && (showIcons || !action.hasImage()));
		boolean wrapIcon = ctx.getBooleanParameter("wrapIcon", false);

		if (showIcon || showImage || addSpace) {
			if (wrapIcon) w.append("<span class=\"ox-button-bar-button-icon\">");
			if (showIcon) {
				w.append("<i class=\"mdi mdi-").append(action.getIcon()).append("\"></i>");
			} else if (showImage) {
				w.append("<img src=\"").append(ctx.getContextPath()).append("/xava/images/").append(action.getImage()).append("\"/>");
			} else if (addSpace) {
				w.append("<i class=\"mdi mdi-square ox-icon-transparent\"></i>");
			}
			if (wrapIcon) w.append("</span>");
		}
		if (showLabel) {
			w.append("<span class=\"ox-action-label\">").text(label).append("</span> ");
		}
		return w.toString();
	}

}
