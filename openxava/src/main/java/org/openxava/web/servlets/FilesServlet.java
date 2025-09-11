package org.openxava.web.servlets;

import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;
import org.openxava.web.editors.*;
import org.openxava.web.Schemas;
/**
 * 
 * @author Javier Paniza
 * @author Jeromy Altuna
 */
@WebServlet("/xava/xfile")
@SuppressWarnings("serial")
public class FilesServlet extends HttpServlet {
	
	private static final String MIME_UNKNOWN = "application/octet-stream";
	
	private static Log log = LogFactory.getLog(FilesServlet.class);
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// If we change this code verify that @File or @Files shows the preview for images 
		// in existing records in final distribution (no development) in Windows, Mac and Linux.
		try {
			String fileId = (String) request.getParameter("fileId");
			
			if (!Is.emptyString(fileId)) {
				Schemas.setDefaultSchema(request);
				AttachedFile file = FilePersistorFactory.getInstance().find(fileId);
				
				String mime = URLConnection.guessContentTypeFromName(file.getName());
				if (mime == null || MIME_UNKNOWN.equals(mime)) {
					mime = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(file.getData()));
				}				
				response.setContentType(mime);
				response.setHeader("Content-Disposition", "inline; filename=\""	+ file.getName() + "\"");
				response.getOutputStream().write(file.getData());
			}

		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new ServletException(XavaResources.getString("attached_file_error"));
		}
	}
}
