package org.openxava.web.servlets;

import java.io.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.controller.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.validators.*;
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
		/* tmp
		Requests.init(request, request.getParameter("application"), request.getParameter("module"));
		getManager(request).executeBeforeEachRequestActions(request, new Messages(), new Messages());  
		try {
			String property = Ids.undecorate(request.getParameter("propertyKey"));
			String url = getEditorProperty(request, property, "getURL");
			getServletContext().getRequestDispatcher(url).forward(request, response);
		}
		finally {
			try {
				ModuleManager.commit();
			}
			finally { 
				Requests.clean();
			}
		}
		*/
		// tmp ini
		response.sendError(406, "NO es NO");
		// tmp fin

	}	
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// tmp executeAction(request, response, "load", true);
		// tmp ini
		response.sendError(500, "De plano NO");
		// tmp fin
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		executeAction(request, response, "remove", false);
	}
	
	private void executeAction(HttpServletRequest request, HttpServletResponse response, String actionPrefix, boolean parseMultipart) throws ServletException {
		String action = "UNKNOWN"; 
		try {
			Requests.init(request, request.getParameter("application"), request.getParameter("module"));
			ModuleManager manager = getManager(request);
			manager.executeBeforeEachRequestActions(request, new Messages(), new Messages());  
			if (parseMultipart) manager.parseMultipartRequest(request);
			String property = Ids.undecorate(request.getParameter("propertyKey"));
			action = getEditorProperty(request, property, actionPrefix + "Action");
			Messages errors = new Messages(); 
			request.setAttribute("errors", errors);
			Messages messages = new Messages(); 			
			request.setAttribute("messages", messages);
			String fileId = request.getParameter("fileId");
			String propertyValues = "property=" + property; 
			if (fileId != null) propertyValues = propertyValues + ",fileId=" + fileId; 
			manager.executeAction(action, errors, messages, propertyValues, request);
			// tmp ini
			if (errors.contains()) {
				response.sendError(406, "Lo que pides es inaceptable");
				response.addHeader("message", "El mensaje");
				throw new ValidationException(errors);
			}
			// tmp fin
		}
		// tmp ini
		catch (ValidationException ex) { 
			System.out.println("[UploadServlet.executeAction] ValidationException atrapada v2"); // tmp 
			// tmp throw new ServletException("Moko roto");
			
		}
		// tmp fin
		catch (Exception ex) { 
			System.out.println("[UploadServlet.executeAction] Excepción atrapada"); // tmp
			log.error(XavaResources.getString("no_execute_action", action, ex.getMessage()), ex); 
			throw new ServletException(XavaResources.getString("upload_error"));  
		}
		finally {
			try {
				ModuleManager.commit();
			}
			finally { 
				Requests.clean();
			}
		}
	}

	private ModuleManager getManager(HttpServletRequest request) {
		ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");
		return (ModuleManager) context.get(request, "manager");
	}

	private String getEditorProperty(HttpServletRequest request, String property, String editorProperty) {
		View view = getCurrentView(request);
		MetaProperty metaProperty = view.getMetaProperty(property);
		MetaEditor metaEditor = WebEditors.getMetaEditorFor(metaProperty, view.getViewName());
		return metaEditor.getProperty(editorProperty);
	}
	
	private View getCurrentView( HttpServletRequest request) {  		 
		ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");
		return (View) context.get(request, "xava_view");
	}
	
}
