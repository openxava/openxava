package org.openxava.web.servlets;

import java.io.*;
import java.nio.charset.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.apache.commons.io.*;
import org.apache.commons.logging.*;
import org.openxava.controller.*;
import org.openxava.util.*;

/**
 * @author Chungyen Tsai
 */

@WebServlet("/xava/style/*")
public class CSSServlet extends HttpServlet {

	private static Log log = LogFactory.getLog(CSSServlet.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			if (!((HttpServletRequest) request).getRequestURI().endsWith(".css")) {
				System.out.println("notcss " + ((HttpServletRequest) request).getRequestURI());
				return;
			}
			InputStream inputStream = getCSSAsStream(request.getPathInfo(), request.getServletPath());
			if (inputStream == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			StringWriter writer = new StringWriter();
			IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8);
			String data = writer.toString().replaceAll("@import (['\"].*)\\.css",
					"@import $1.css?ox=" + ModuleManager.getVersion());
			response.setContentType("text/css"); // If you change this pass the ZAP test again
			response.getWriter().append(data);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ServletException(XavaResources.getString("attachments_css_servlet_error",
					request.getServletPath() + request.getPathInfo()));
		}
	}

	private InputStream getCSSAsStream(String resourceName, String prefix) throws FileNotFoundException {
		if (resourceName == null)
			return null; // If you change this pass the ZAP test again
		boolean isEmpty = false;
		InputStream stream;
		try {
			stream = new FileInputStream(getServletContext().getRealPath("/") + prefix + resourceName);
			if (stream != null) {
				System.out.println(getServletContext().getRealPath("/") + prefix + resourceName);
				isEmpty = true;
				return stream;
			}
		} catch (FileNotFoundException e) {
		}
		if (!isEmpty) {
			stream = getClass().getClassLoader().getResourceAsStream("META-INF/resources" + prefix + resourceName);
			if (stream != null) {
				return stream;
			}
		}
		return null;
	}

}
