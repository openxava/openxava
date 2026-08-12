package org.openxava.web.render;

import java.util.*;

/**
 * Renders the reference search list (formerly referenceSearch.jsp).
 * <p>
 * Delegates to {@link ListRenderer} with {@code singleSelection=true}
 * and the {@code rowAction} parameter from the request.
 * </p>
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class ReferenceSearchRenderer {

	public static String render(ViewRenderContext ctx) throws Exception {
		String rowAction = ctx.getParameter("rowAction");
		Map<String, String> extra = new HashMap<>();
		if (rowAction != null) extra.put("rowAction", rowAction);
		extra.put("singleSelection", "true");
		return ListRenderer.render(ctx.withParameters(extra));
	}

}
