package org.openxava.web.servlets;

import java.io.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.controller.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.view.*;
import org.openxava.web.*;
import org.openxava.web.meta.*;


/**
 * 
 * @author Javier Paniza
 */
@WebServlet("/xava/upload")
public class UploadServlet extends HttpServlet {
	
	private static Log log = LogFactory.getLog(UploadServlet.class);
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String property = Ids.undecorate(request.getParameter("propertyKey"));
		String url = getEditorProperty(request, property, "getURL");
		System.out.println("[UploadServlet.doGet] url=" + url); // tmp
		System.out.println("[UploadServlet.doGet] fileId=" + request.getParameter("fileId")); // tmp
		getServletContext().getRequestDispatcher(url).forward(request, response);			  		
	}
	
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// tmp executeAction(request, response, new LoadImageAction(), true);
		executeAction(request, response, "post", true); // tmp
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// tmp executeAction(request, response, new DeleteImageAction(), false);
		// tmp ini
		/*
		RemoveImageFromGalleryAction action = new RemoveImageFromGalleryAction();
		String fileId = request.getParameter("fileId");
		action.setOid(fileId);
		executeAction(request, response, action, false);
		*/
		executeAction(request, response, "delete", false);
		// tmp fin
	}
	
	private void executeAction(HttpServletRequest request, HttpServletResponse response, String method, boolean parseMultipart) throws ServletException {
		String action = "Unknown"; // tmp ¿i18n?
		try {
			ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");
			ModuleManager manager = (ModuleManager) context.get(request, "manager"); 
			if (parseMultipart) manager.parseMultipartRequest(request);
			/* tmp
			String property = Ids.undecorate(request.getParameter("propertyKey"));
			PropertiesManager pm = new PropertiesManager(action);
			pm.executeSet("newImageProperty", property);
			Messages errors = (Messages) request.getAttribute("errors");
			Messages messages = (Messages) request.getAttribute("messages");			
			*/
			// tmp ini
			String property = Ids.undecorate(request.getParameter("propertyKey"));
			action = getEditorProperty(request, property, method + "Action");
			Messages errors = new Messages(); 
			request.setAttribute("errors", errors);
			Messages messages = new Messages(); 			
			request.setAttribute("messages", messages);
			String fileId = request.getParameter("fileId");
			String propertyValues = "property=" + property; // tmp ¿Usar una interfaz? Plantear para cuando haga FILE/FILES y acción genérica
			if (fileId != null) propertyValues = propertyValues + ",fileId=" + fileId; // tmp ¿Usar una interfaz? Plantear para cuando haga FILE/FILES y acción genérica
			manager.executeAction(action, errors, messages, propertyValues, request);
			// tmp fin
			// tmp manager.executeAction(action, errors, messages, request);
		}
		catch (Exception ex) { 
			// tmp log.error(XavaResources.getString("no_execute_action", action.getClass(), ex.getMessage()), ex);
			log.error(XavaResources.getString("no_execute_action", action, ex.getMessage()), ex); // tmp
			throw new ServletException(XavaResources.getString("upload_error"));  
		}
		// tmp ini
		finally {
			ModuleManager.commit();
		}
		// tmp fin
	}


	private String getEditorProperty(HttpServletRequest request, String property, String editorProperty) {
		View view = getCurrentView(request);
		MetaProperty metaProperty = view.getMetaProperty(property);
		MetaEditor metaEditor = WebEditors.getMetaEditorFor(metaProperty, view.getViewName());
		return metaEditor.getProperty(editorProperty);
	}
	
	private View getCurrentView( HttpServletRequest request) { // tmp  		 
		ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");
		return (View) context.get(request, "xava_view");
	}


}
