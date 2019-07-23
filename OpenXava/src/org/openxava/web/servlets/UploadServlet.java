package org.openxava.web.servlets;

import java.io.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.actions.*;
import org.openxava.controller.*;
import org.openxava.util.*;
import org.openxava.web.*;


/**
 * 
 * @author Javier Paniza
 */
@WebServlet("/xava/upload")
public class UploadServlet extends HttpServlet {
	
	private static Log log = LogFactory.getLog(UploadServlet.class); 
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		executeAction(request, response, new LoadImageAction(), true);
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		executeAction(request, response, new DeleteImageAction(), false);
	}
	
	private void executeAction(HttpServletRequest request, HttpServletResponse response, IAction action, boolean parseMultipart) throws ServletException {
		try {
			ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");
			ModuleManager manager = (ModuleManager) context.get(request, "manager"); 
			if (parseMultipart) manager.parseMultipartRequest(request); 
			String property = Ids.undecorate(request.getParameter("propertyKey"));
			PropertiesManager pm = new PropertiesManager(action);
			pm.executeSet("newImageProperty", property);
			Messages errors = (Messages) request.getAttribute("errors");
			Messages messages = (Messages) request.getAttribute("messages");
			manager.executeAction(action, errors, messages, request);
		}
		catch (Exception ex) { 
			log.error(XavaResources.getString("no_execute_action", action.getClass(), ex.getMessage()), ex);
			throw new ServletException(XavaResources.getString("upload_error"));  
		}		
	}

}
