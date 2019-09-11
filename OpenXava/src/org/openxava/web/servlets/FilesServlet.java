package org.openxava.web.servlets;

import static org.apache.commons.lang.SystemUtils.*;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.controller.*;
import org.openxava.jpa.*;
import org.openxava.util.*;
import org.openxava.web.editors.*;

import eu.medsea.mimeutil.*;
/**
 * 
 * @author Jeromy Altuna
 */
@SuppressWarnings("serial")
public class FilesServlet extends HttpServlet {
	
	private static final String MIME_UNKNOWN = "application/octet-stream";
	
	private static Log log = LogFactory.getLog(FilesServlet.class);
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String fileId = (String) request.getParameter("fileId");
			
			if (!Is.emptyString(fileId)) {
				setDefaultSchema(request);
				AttachedFile file = FilePersistorFactory.getInstance().find(fileId);
				
				registerMimeDetector();
				
				MimeType mimeType = MimeUtil.getMostSpecificMimeType(MimeUtil.getMimeTypes(file.getName()));
				String mime = mimeType.getMediaType() + "/" + mimeType.getSubType();
								
				if(MIME_UNKNOWN.equals(mime)) {
					mimeType = MimeUtil.getMostSpecificMimeType(MimeUtil.getMimeTypes(file.getData()));
					mime = mimeType.getMediaType() + "/" + mimeType.getSubType();
				}
				response.setContentType(mime);
				response.setHeader("Content-Disposition", "inline; filename=\""	+ file.getName() + "\"");
				response.getOutputStream().write(file.getData());
				
				unregisterMimeDetector();
			}

		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new ServletException(XavaResources.getString("attached_file_error"));
		}
	}

	private static void registerMimeDetector() {
		if(IS_OS_UNIX)
			MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.OpendesktopMimeDetector");
		else if (IS_OS_WINDOWS)
			MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.WindowsRegistryMimeDetector");
		else 
			MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.ExtensionMimeDetector");
	}
	
	private static void unregisterMimeDetector() {
		if(IS_OS_UNIX)
			MimeUtil.unregisterMimeDetector("eu.medsea.mimeutil.detector.OpendesktopMimeDetector");
		else if (IS_OS_WINDOWS)
			MimeUtil.unregisterMimeDetector("eu.medsea.mimeutil.detector.WindowsRegistryMimeDetector");
		else 
			MimeUtil.unregisterMimeDetector("eu.medsea.mimeutil.detector.ExtensionMimeDetector");
	}
	
	private void setDefaultSchema(HttpServletRequest request) {
		String organization = (String) request.getSession().getAttribute("naviox.organization");
		if (!Is.emptyString(organization)) { 
			XPersistence.setDefaultSchema(organization);
			return;
		}
		ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");
		String defaultSchema = (String) context.get(request, "xava_defaultSchema");
		if (!Is.emptyString(defaultSchema)) XPersistence.setDefaultSchema(defaultSchema);
	}
}
