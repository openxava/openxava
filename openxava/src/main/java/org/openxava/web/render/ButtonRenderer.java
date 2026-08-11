package org.openxava.web.render;

import org.openxava.controller.meta.*;
import org.openxava.util.*;
import org.openxava.web.style.*;

/**
 * Renders a single button-bar button (formerly barButton.jsp).
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class ButtonRenderer {

	public static String render(ViewRenderContext ctx) {
		Style style = ctx.getStyle();
		boolean showImages = style.isShowImageInButtonBarButton();
		boolean showIcons = style.isUseIconsInsteadOfImages();
		boolean showLabels = !showImages ? true : XavaPreferences.getInstance().isShowLabelsForToolBarActions();
		String actionName = ctx.getParameter("action");
		boolean addSpace = ctx.getBooleanParameter("addSpaceWithoutImage", false);

		if (Is.emptyString(actionName)) return "";

		MetaAction action = MetaControllers.getMetaAction(actionName);
		String argv = ctx.getParameter("argv");
		String label = action.getLabel(ctx.getRequest());
		boolean alwaysAvailable = ctx.getBooleanParameter("alwaysAvailable", false);

		HtmlWriter w = new HtmlWriter();

		if (style.isUseStandardImageActionForOnlyImageActionOnButtonBar() && action.hasImage() && Is.emptyString(label)) {
			w.append(ActionHtml.image(ctx, action.getQualifiedName(), argv, style.getButtonBarImage(), alwaysAvailable));
		} else {
			w.append("<span class=\"ox-button-bar-button\">");
			String linkBody = renderButtonBody(style, showImages, showIcons, showLabels, action, label, addSpace, ctx);
			w.append(ActionHtml.link(ctx, action.getQualifiedName(), argv, null, alwaysAvailable, linkBody));
			w.append("</span>");
		}
		return w.toString();
	}

	private static String renderButtonBody(Style style, boolean showImages, boolean showIcons, boolean showLabels,
			MetaAction action, String label, boolean addSpace, ViewRenderContext ctx) {
		HtmlWriter w = new HtmlWriter();
		boolean showLabel = (showLabels || !action.hasImage()) && !Is.emptyString(label);
		boolean showImage = showImages && action.hasImage() || action.hasImage() && Is.emptyString(label);
		boolean showIcon = action.hasIcon() && (showImages && (showIcons || !action.hasImage()) || Is.emptyString(label) && (showIcons || !action.hasImage()));

		if (showIcon) {
			w.append("<i class=\"mdi mdi-").append(action.getIcon()).append("\"></i>");
		} else if (showImage) {
			w.append("<img src=\"").append(ctx.getContextPath()).append("/").append(style.getImagesFolder()).append("/").append(action.getImage()).append("\"/>");
		} else if (addSpace) {
			w.append("<i class=\"mdi mdi-square ox-icon-transparent\"></i>");
		}
		if (showLabel) {
			w.append("<span class=\"").append(style.getActionLabel()).append("\">").text(label).append("</span> ");
		}
		return w.toString();
	}

}
