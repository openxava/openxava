package org.openxava.web.servlets;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.controller.*;
import org.openxava.session.*;
import org.openxava.util.*;


/**
 * @author Javier Paniza
 */

public class GalleryServlet extends HttpServlet {

	private static Log log = LogFactory.getLog(GalleryServlet.class);
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		try {			
			ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");
			Gallery gallery = (Gallery) context.get(request, "xava_gallery");
			String oid=request.getParameter("oid");
			if (oid == null) {
				throw new Exception(XavaResources.getString("image_oid_required"));
			}
			byte [] image = gallery.getImage(oid);			 
			if (image != null) {					
				response.getOutputStream().write(image);
			}
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new ServletException(XavaResources.getString("image_error"));
		}		
	}

}
