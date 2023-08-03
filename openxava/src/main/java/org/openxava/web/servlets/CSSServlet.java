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

	private Map<String, String> contentTypes;

	@Override
	public void init() throws ServletException {
		contentTypes = new HashMap<>();
	    contentTypes.put(".jpg", "image/jpeg");
	    contentTypes.put(".jpeg", "image/jpeg");
	    contentTypes.put(".png", "image/png");
	    contentTypes.put(".gif", "image/gif");
	    contentTypes.put(".bmp", "image/bmp");
	    contentTypes.put(".webp", "image/webp");
	    contentTypes.put(".svg", "image/svg+xml");

	    contentTypes.put(".mp3", "audio/mpeg");
	    contentTypes.put(".wav", "audio/wav");
	    contentTypes.put(".ogg", "audio/ogg");
	    contentTypes.put(".mid", "audio/midi");

	    contentTypes.put(".mp4", "video/mp4");
	    contentTypes.put(".avi", "video/x-msvideo");
	    contentTypes.put(".mov", "video/quicktime");
	    contentTypes.put(".mkv", "video/x-matroska");
	    contentTypes.put(".wmv", "video/x-ms-wmv");

	    contentTypes.put(".txt", "text/plain");
	    contentTypes.put(".pdf", "application/pdf");
	    contentTypes.put(".doc", "application/msword");
	    contentTypes.put(".xls", "application/vnd.ms-excel");
	    contentTypes.put(".ppt", "application/vnd.ms-powerpoint");
	    contentTypes.put(".map", "application/octet-stream");
	    contentTypes.put(".css", "text/css");
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
			response.setHeader("Cache-Control", "public, max-age=1209600"); 
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
			return contentTypes.get(extension);
		}
		return null;
	}

}
