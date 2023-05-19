package org.openxava.web.servlets;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

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
			InputStream inputStream = getCSSAsStream(request.getPathInfo(), request.getServletPath());
			if (inputStream == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			String contentType = getContentType(request.getPathInfo());
			response.setContentType(contentType);
			StringWriter writer;
			String data = "";
			if (contentType == null) return;
			if (contentType.startsWith("image/png")) {
				OutputStream outputStream = response.getOutputStream();
				IOUtils.copy(inputStream, outputStream);
				outputStream.close();
                inputStream.close();
                return;
			}
			
			writer = new StringWriter();
			IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8);
			data = writer.toString(); 
			
			if (contentType.startsWith("text/css")) {
				data = data.replaceAll("@import (['\"].*)\\.css", "@import $1.css?ox=" + ModuleManager.getVersion());
			}
			response.getWriter().append(data);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ServletException(XavaResources.getString("attachments_css_servlet_error",
					request.getServletPath() + request.getPathInfo()));
		}
	}

	private InputStream getCSSAsStream(String resourceName, String prefix) throws FileNotFoundException {
		if (resourceName == null) return null; // If you change this pass the ZAP test again
		boolean isEmpty = false;
		InputStream stream;
		try {
			stream = new FileInputStream(getServletContext().getRealPath("/") + prefix + resourceName);
			if (stream != null) {
				isEmpty = true;
				return stream;
			}
		} catch (FileNotFoundException e) {
		}
		if (!isEmpty) {
			System.out.println("META-INF/resources" + prefix + " --- " + resourceName);
			stream = getClass().getClassLoader().getResourceAsStream("META-INF/resources" + prefix + resourceName);
			if (stream != null) {
				return stream;
			}
		}
		return null;
	}

	private String getContentType(String resourceName) {
		Map<String, String> extensionToContentType = new HashMap<>();
		extensionToContentType.put(".css", "text/css");
		extensionToContentType.put(".png", "image/png");
		extensionToContentType.put(".jpg", "image/jpeg");
		extensionToContentType.put(".jpeg", "image/jpeg");
		extensionToContentType.put(".map", "application/octet-stream");
		int dotIndex = resourceName.lastIndexOf(".");
		if (dotIndex != -1) {
			String extension = resourceName.substring(dotIndex);
			return extensionToContentType.get(extension);
		}
		return null;
	}

}
