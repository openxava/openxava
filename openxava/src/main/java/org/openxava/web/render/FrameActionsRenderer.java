package org.openxava.web.render;

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

		String frameContentId = frameId + "content";
		String frameShowId = frameId + "show";
		String frameHideId = frameId + "hide";
		String hideClass = closed ? "class='ox-display-none'" : "";
		String showClass = closed ? "" : "class='ox-display-none'";

		HtmlWriter w = new HtmlWriter();

		w.append("<span id='").append(frameHideId).append("' ").append(hideClass).append(">");
		w.append("<a class=\"xava_hide_frame\" data-frame=\"").append(frameId).append("\">");
		w.append("<i class=\"mdi mdi-menu-down\"></i>");
		w.append("</a></span> ");

		w.append("<span id='").append(frameShowId).append("' ").append(showClass).append(">");
		w.append("<a class=\"xava_show_frame\" data-frame=\"").append(frameId).append("\">");
		w.append("<i class=\"mdi mdi-menu-right\"></i>");
		w.append("</a></span>");
		return w.toString();
	}

}
