package org.openxava.web.render;

import java.util.*;

import org.openxava.util.*;

/**
 * Renders the errors block.
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class ErrorsRenderer {

	public static String render(ViewRenderContext ctx) {
		Messages errors = ctx.getErrors();
		if (!errors.contains()) return "";
		HtmlWriter w = new HtmlWriter();
		w.append("<div class='ox-errors-wrapper'>");
		w.append("<table id='").append(ctx.decorateId("errors_table")).append("'>");
		for (Iterator<String> it = errors.getStrings(ctx.getRequest()).iterator(); it.hasNext(); ) {
			w.append("<tr><td class='ox-errors'><div class='ox-message-box'>");
			w.append("<i class=\"mdi mdi-close\"></i>");
			w.append(it.next());
			w.append("</div></td></tr>");
		}
		w.append("</table></div>");
		return w.toString();
	}

}
