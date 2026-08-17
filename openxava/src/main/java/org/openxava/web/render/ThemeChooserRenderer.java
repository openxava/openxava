package org.openxava.web.render;

import org.openxava.util.*;
import org.openxava.web.style.*;

/**
 * Renders the theme chooser.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class ThemeChooserRenderer {

	public static String render(ViewRenderContext ctx) {
		HtmlWriter w = new HtmlWriter();
		w.append("<div id=\"theme_chooser\">");
		w.append(org.openxava.util.Labels.get("themes", XavaResources.getLocale(ctx.getRequest()))).append(": ");
		String nexus = "";
		String current = Themes.getCSS(ctx.getRequest());
		for (String theme : Themes.getAll()) {
			String label = Themes.cssToLabel(theme);
			if (theme.equals(current)) {
				w.append(nexus).append("<b>").append(label).append("</b> ");
			} else {
				w.append(nexus).append("<a href=\"?theme=").append(theme).append("\">").append(label).append("</a> ");
			}
			nexus = " - ";
		}
		w.append("</div>");
		return w.toString();
	}

}
