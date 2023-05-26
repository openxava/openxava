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

	private Map<String, String> map;

	@Override
	public void init() throws ServletException {
		map = new HashMap<>();
	    map.put(".jpg", "image/jpeg");
	    map.put(".jpeg", "image/jpeg");
	    map.put(".png", "image/png");
	    map.put(".gif", "image/gif");
	    map.put(".bmp", "image/bmp");
	    map.put(".webp", "image/webp");
	    map.put(".svg", "image/svg+xml");

	    map.put(".mp3", "audio/mpeg");
	    map.put(".wav", "audio/wav");
	    map.put(".ogg", "audio/ogg");
	    map.put(".mid", "audio/midi");

	    map.put(".mp4", "video/mp4");
	    map.put(".avi", "video/x-msvideo");
	    map.put(".mov", "video/quicktime");
	    map.put(".mkv", "video/x-matroska");
	    map.put(".wmv", "video/x-ms-wmv");

	    map.put(".txt", "text/plain");
	    map.put(".pdf", "application/pdf");
	    map.put(".doc", "application/msword");
	    map.put(".xls", "application/vnd.ms-excel");
	    map.put(".ppt", "application/vnd.ms-powerpoint");
	    map.put(".map", "application/octet-stream");
	    map.put(".css", "text/css");
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			InputStream inputStream = getResourceAsStream(request.getPathInfo(), request.getServletPath());
			if (inputStream == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			String contentType = getContentType(request.getPathInfo());
			response.setContentType(contentType); // // If you change this pass the ZAP test again
			StringWriter writer;
			String data = "";
			if (contentType == null) return;
			if (!contentType.startsWith("text/css")) {
				OutputStream outputStream = response.getOutputStream();
				IOUtils.copy(inputStream, outputStream);
				outputStream.close();
                inputStream.close();
                return;
			}
			
			writer = new StringWriter();
			IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8);
			data = writer.toString(); 
			data = data.replaceAll("@import (['\"].*)\\.css", "@import $1.css?ox=" + ModuleManager.getVersion());
			response.getWriter().append(data);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ServletException(XavaResources.getString("attachments_css_servlet_error",
					request.getServletPath() + request.getPathInfo()));
		}
	}

	private InputStream getResourceAsStream(String resourceName, String prefix) throws FileNotFoundException {
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
		return null;
	}

}
