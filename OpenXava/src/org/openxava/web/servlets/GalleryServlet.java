package org.openxava.web.servlets;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.controller.*;
import org.openxava.session.*;
import org.openxava.util.*;
import org.openxava.view.*;
import org.openxava.web.*;


/**
 * @author Javier Paniza
 */

public class GalleryServlet extends HttpServlet {

	private static Log log = LogFactory.getLog(GalleryServlet.class);
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		try {			
			/* tmp
			ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");
			Gallery gallery = (Gallery) context.get(request, "xava_gallery");
			String oid=request.getParameter("oid");
			if (oid == null) {
				throw new Exception(XavaResources.getString("image_oid_required"));
			}
			*/
			// tmp byte [] image = gallery.getImage(oid);		
			// tmp ini
			String oid=request.getParameter("fileId");
			if (oid == null) {
				throw new Exception(XavaResources.getString("image_oid_required")); // tmp ¿Cambiar el mensaje?
			}			
			String property = Ids.undecorate(request.getParameter("propertyKey"));
			String galleryOid = getCurrentView(request).getValueString(property);
			GalleryImage galleryImage = GalleryImage.find(oid);
			if (Is.equal(galleryOid, galleryImage.getGalleryOid())) {
				byte [] image = galleryImage.getImage(); // tmp
				// tmp fin
				if (image != null) {					
					response.setContentType("image/png"); // "images" without png does not work for FilePonde with Firefox, png works for any type of image // tmp
					response.getOutputStream().write(image);
				}
			} // tmp
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new ServletException(XavaResources.getString("image_error"));
		}		
	}
	
	private View getCurrentView( HttpServletRequest request) { // tmp  		 
		ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");
		return (View) context.get(request, "xava_view");
	}


}
