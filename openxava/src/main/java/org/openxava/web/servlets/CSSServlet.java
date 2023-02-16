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

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (request.getRequestURI().endsWith(".css")) {
				String prefix = request.getServletPath();
				String file = request.getPathInfo();
				InputStream inputStream = getCSSAsStream(file, prefix);
				StringWriter writer = new StringWriter();
				IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8);
				String data = writer.toString().replaceAll("@import (['\"].*)\\.css", "@import $1.css?ox=" + ModuleManager.getVersion());
				response.getWriter().append(data);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ServletException(XavaResources.getString("attachments_css_servlet_error", request.getServletPath() + request.getPathInfo()));
		}
	}
	
	private InputStream getCSSAsStream(String resourceName, String prefix) { 
		InputStream stream = null;
		System.out.println(resourceName);
		System.out.println(prefix);
		try {
			if (getClass().getClassLoader().getResourceAsStream(prefix + resourceName) != null) {
				System.out.println("1");
				///
				return stream = getClass().getClassLoader().getResourceAsStream(prefix + resourceName);
			}
			if (getClass().getClassLoader().getResourceAsStream("META-INF/resources/" + prefix + resourceName) != null) {
				System.out.println("2");
				return stream = getClass().getClassLoader().getResourceAsStream("META-INF/resources/" + prefix + resourceName);
			}
		} catch (Exception e) {
			
		}
		return null;
	}

}
