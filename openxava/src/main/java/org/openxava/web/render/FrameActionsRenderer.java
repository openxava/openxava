package org.openxava.web.render;

import org.openxava.web.style.*;

/**
 * Renders the frame collapse/expand actions.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class FrameActionsRenderer {

	public static String render(ViewRenderContext ctx) {
		String frameId = ctx.getParameter("frameId");
		boolean closed = ctx.getBooleanParameter("closed", false);
		Style style = ctx.getStyle();

		String frameContentId = frameId + "content";
		String frameShowId = frameId + "show";
		String frameHideId = frameId + "hide";
		String hideClass = closed ? "class='ox-display-none'" : "";
		String showClass = closed ? "" : "class='ox-display-none'";

		String minimizeImage = resolveImage(style.getMinimizeImage(), ctx.getContextPath());
		String restoreImage = resolveImage(style.getRestoreImage(), ctx.getContextPath());

		HtmlWriter w = new HtmlWriter();

		w.append("<span id='").append(frameHideId).append("' ").append(hideClass).append(">");
		w.append("<a class=\"xava_hide_frame\" data-frame=\"").append(frameId).append("\">");
		if (minimizeImage == null) {
			w.append("<i class=\"mdi mdi-menu-down\"></i>");
		} else {
			w.append("<img src=\"").append(minimizeImage).append("\" border=0 align=\"absmiddle\"/>");
		}
		w.append("</a></span> ");

		w.append("<span id='").append(frameShowId).append("' ").append(showClass).append(">");
		w.append("<a class=\"xava_show_frame\" data-frame=\"").append(frameId).append("\">");
		if (restoreImage == null) {
			w.append("<i class=\"mdi mdi-menu-right\"></i>");
		} else {
			w.append("<img src=\"").append(restoreImage).append("\" border=0 align=\"absmiddle\"/>");
		}
		w.append("</a></span>");
		return w.toString();
	}

	private static String resolveImage(String image, String contextPath) {
		if (image == null) return null;
		return image.startsWith("/") ? image : contextPath + "/" + image;
	}

}
