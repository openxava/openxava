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

<<<<<<< HEAD
	private Map<String, String> map;

	@Override
	public void init() throws ServletException {
		map = new HashMap<>();
		map.put(".css", "text/css");
		map.put(".png", "image/png");
		map.put(".jpg", "image/jpeg");
		map.put(".jpeg", "image/jpeg");
		map.put(".map", "application/octet-stream");
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
=======
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
>>>>>>> refs/heads/master
		try {
<<<<<<< HEAD
			InputStream inputStream = getResourceAsStream(request.getPathInfo(), request.getServletPath());
=======
			InputStream inputStream = getCSSAsStream(request.getPathInfo(), request.getServletPath());
						
>>>>>>> refs/heads/master
			if (inputStream == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
<<<<<<< HEAD
			String contentType = getContentType(request.getPathInfo());
			response.setContentType(contentType); // // If you change this pass the ZAP test again
			StringWriter writer;
			String data = "";
			if (contentType == null) return;
			if (contentType.startsWith("image/")) {
				OutputStream outputStream = response.getOutputStream();
				IOUtils.copy(inputStream, outputStream);
				outputStream.close();
                inputStream.close();
                return;
			}
			
			writer = new StringWriter();
=======
			StringWriter writer = new StringWriter();
>>>>>>> refs/heads/master
			IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8);
			String data = writer.toString().replaceAll("@import (['\"].*)\\.css", "@import $1.css?ox=" + ModuleManager.getVersion());
			response.setContentType("text/css"); // If you change this pass the ZAP test again
			response.getWriter().append(data);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ServletException(XavaResources.getString("attachments_css_servlet_error", request.getServletPath() + request.getPathInfo()));
		}
	}
<<<<<<< HEAD

	private InputStream getResourceAsStream(String resourceName, String prefix) throws FileNotFoundException {
=======
	
	private InputStream getCSSAsStream(String resourceName, String prefix) throws FileNotFoundException {
>>>>>>> refs/heads/master
		if (resourceName == null) return null; // If you change this pass the ZAP test again
<<<<<<< HEAD
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
			stream = getClass().getClassLoader().getResourceAsStream("META-INF/resources" + prefix + resourceName);
			if (stream != null) {
				return stream;
			}
		}
		return null;
	}

	private String getContentType(String resourceName) {
		int dotIndex = resourceName.lastIndexOf(".");
		if (dotIndex != -1) {
			String extension = resourceName.substring(dotIndex);
			return map.get(extension);
		}
=======
		InputStream stream = getClass().getClassLoader().getResourceAsStream("META-INF/resources/" + prefix + resourceName);
		if (stream != null) return stream;
		stream = new FileInputStream(getServletContext().getRealPath("/") + prefix + resourceName);
		if (stream != null) return stream;
>>>>>>> refs/heads/master
		return null;
	}

}
