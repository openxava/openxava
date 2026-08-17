package org.openxava.web.servlets;

import java.io.*;
import java.util.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import org.openxava.util.*;
import org.openxava.web.*;
import org.openxava.web.render.*;

/**
 * Entry point for module pages via friendly URL ({@code /m/<module>}).
 * <p>
 * Calls {@link ModulePageRenderer} directly instead of forwarding to {@code module.jsp}.
 * </p>
 *
 * @author Javier Paniza
 * @since 8.0
 */
public class ModuleServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String [] uri = request.getRequestURI().split("/");
		if (uri.length < 4) {
			response.getWriter().print(XavaResources.getString(request, "module_name_missing"));
			return;
		}
		String application = uri[1];
		String module = uri[3];
		String queryString = "application=" + application + "&module=" + module + "&friendlyURL=true";
		String existingQs = request.getQueryString();
		if (existingQs != null && !existingQs.isEmpty()) {
			queryString = queryString + "&" + existingQs;
		}
		Map<String, String[]> params = parseQueryString(queryString);
		request = new ParametersHttpServletRequest(request, params);

		Servlets.setCharacterEncoding(request, response);
		response.setContentType("text/html; charset=UTF-8");
		try {
			String html = ModulePageRenderer.render(request, response);
			response.getWriter().print(html);
		}
		catch (Exception ex) {
			throw new ServletException(ex);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private static Map<String, String[]> parseQueryString(String qs) {
		Map<String, String[]> result = new LinkedHashMap<>();
		if (qs == null || qs.isEmpty()) return result;
		for (String pair : qs.split("&")) {
			if (pair.isEmpty()) continue;
			int eq = pair.indexOf('=');
			String name = eq < 0 ? pair : pair.substring(0, eq);
			String value = eq < 0 ? "" : pair.substring(eq + 1);
			result.put(name, new String[] { value });
		}
		return result;
	}

}
