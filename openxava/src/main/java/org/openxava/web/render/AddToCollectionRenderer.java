package org.openxava.web.render;

import java.util.*;

/**
 * Renders the add-to-collection list.
 * <p>
 * Wraps {@link ListRenderer} in a table with {@code onlyOneActionPerRow=true}
 * and the {@code rowAction} parameter from the request.
 * </p>
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class AddToCollectionRenderer {

	public static String render(ViewRenderContext ctx) throws Exception {
		String rowAction = ctx.getParameter("rowAction");
		Map<String, String> extra = new HashMap<>();
		if (rowAction != null) extra.put("rowAction", rowAction);
		extra.put("onlyOneActionPerRow", "true");
		HtmlWriter w = new HtmlWriter();
		w.append("<table><tr><td>");
		w.append(ListRenderer.render(ctx.withParameters(extra)));
		w.append("</td></tr></table>");
		return w.toString();
	}

}
