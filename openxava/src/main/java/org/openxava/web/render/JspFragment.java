package org.openxava.web.render;

import jakarta.servlet.http.*;

import org.openxava.web.servlets.*;

/**
 * Renders a JSP fragment and returns its HTML (bridge for editors and custom views).
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class JspFragment {

	public static String render(HttpServletRequest request, HttpServletResponse response, String uri) throws Exception {
		if (uri == null) return "";
		if (!uri.startsWith("/")) uri = "/xava/" + uri;
		return Servlets.getURIAsString(request, response, uri);
	}

	public static String render(ViewRenderContext ctx, String uri) throws Exception {
		return render(ctx.getRequest(), ctx.getResponse(), uri);
	}

}
