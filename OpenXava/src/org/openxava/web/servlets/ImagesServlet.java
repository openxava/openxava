package org.openxava.web.servlets;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.controller.*;
import org.openxava.util.*;
import org.openxava.view.*;
import org.openxava.web.*;


/**
 * @author Javier Paniza
 */

public class ImagesServlet extends HttpServlet {
	
	private static Log log = LogFactory.getLog(ImagesServlet.class);
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("[ImagesServlet.doGet] "); // tmp
		try {
			System.out.println("[ImagesServlet.doGet] request.getParameter('property')=" + request.getParameter("propertyKey")); // tmp
			// tmp String propertyKey = Ids.undecorate(request.getParameter( "property" ));
			String propertyKey = Ids.undecorate(request.getParameter( "propertyKey")); // tmp
			System.out.println("[ImagesServlet.doGet] propertyKey=" + propertyKey); // tmp
			View view = getCurrentView( request, propertyKey );
			byte [] image = (byte []) view.getValue(propertyKey); 
			if (image != null) {
				// tmp response.setContentType("image");
				response.setContentType("image/png"); // "images" without png does not work for FilePonde with Firefox, png works for any type of image // tmp 
				response.getOutputStream().write(image);
			}
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new ServletException(XavaResources.getString("image_error"));
		}		
	}
	
	private View getCurrentView( HttpServletRequest request, String propertyKey) {  		 
		ModuleContext context = (ModuleContext) request.getSession().getAttribute("context"); 
		return (View) context.get(request, "xava_view");
	}
		
}
