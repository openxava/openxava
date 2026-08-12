package org.openxava.web.render;

import java.util.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import org.openxava.web.*;
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

	/**
	 * Renders a JSP applying {@link ViewRenderContext} overlay parameters (e.g. {@code viewObject})
	 * as query-string parameters on the include URI.
	 * <p>
	 * Context parameters must not rely only on {@link ParametersHttpServletRequest}: that wrapper
	 * prefers its extras over nested {@code RequestDispatcher.include} query params when JSPs call
	 * {@code request.getParameter} on the wrapper itself, and stacking wrappers deepens
	 * {@code getSession()} chains until {@link StackOverflowError}. Appending to the URI lets
	 * Tomcat's include merge put the correct {@code viewObject} first.
	 */
	public static String render(ViewRenderContext ctx, String uri) throws Exception {
		if (uri == null) return "";
		if (!uri.startsWith("/")) uri = "/xava/" + uri;
		uri = appendContextParameters(uri, ctx.getParametersAsRequestMap());
		return Servlets.getURIAsString(unwrapParameters(ctx.getRequest()), ctx.getResponse(), uri);
	}

	private static String appendContextParameters(String uri, Map<String, String[]> contextParams) {
		if (contextParams == null || contextParams.isEmpty()) return uri;
		StringBuilder sb = new StringBuilder(uri);
		char sep = uri.indexOf('?') >= 0 ? '&' : '?';
		for (Map.Entry<String, String[]> entry : contextParams.entrySet()) {
			String name = entry.getKey();
			if (name == null || parameterPresent(uri, name)) continue;
			String[] values = entry.getValue();
			String value = values == null || values.length == 0 || values[0] == null ? "" : values[0];
			sb.append(sep).append(name).append('=').append(value);
			sep = '&';
		}
		return sb.toString();
	}

	private static boolean parameterPresent(String uri, String name) {
		int q = uri.indexOf('?');
		if (q < 0) return false;
		String query = uri.substring(q + 1);
		for (String pair : query.split("&")) {
			int eq = pair.indexOf('=');
			String key = eq < 0 ? pair : pair.substring(0, eq);
			if (name.equals(key)) return true;
		}
		return false;
	}

	/**
	 * Avoid stacking {@link ParametersHttpServletRequest} on every fragment include (deep
	 * {@code getSession()} wrapper chains). Hotwire already applied form/query extras on the
	 * outer request; include query strings carry per-fragment overrides.
	 */
	private static HttpServletRequest unwrapParameters(HttpServletRequest request) {
		HttpServletRequest current = request;
		while (current instanceof ParametersHttpServletRequest) {
			ServletRequest wrapped = ((ParametersHttpServletRequest) current).getRequest();
			if (!(wrapped instanceof HttpServletRequest)) break;
			current = (HttpServletRequest) wrapped;
		}
		return current;
	}

}
