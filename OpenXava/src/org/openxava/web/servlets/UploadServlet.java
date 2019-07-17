package org.openxava.web.servlets;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.openxava.actions.*;
import org.openxava.controller.*;
import org.openxava.util.*;
import org.openxava.web.*;


/**
 * tmp
 * 
 * @author Javier Paniza
 */
@WebServlet("/xava/upload") // tmp Así o en web.xml / o "/xava/xupload"
public class UploadServlet extends HttpServlet { // tmp ¿Hacer una clase base para obtener manager y llamar acciones? 
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("[UploadServlet.doGet] "); // tmp
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("[UploadServlet.doPost] "); // tmp
		// tmp ¿Refectorizar con doDelete?
		try {
			ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");
			ModuleManager manager = (ModuleManager) context.get(request, "manager"); // TMP
			manager.parseMultipartRequest(request); // tmp ¿Cuando se libera xava.upload.fileitems?
			LoadImageAction action = new LoadImageAction();
			// tmp action.setNewImageProperty(request.getParameter("property")); 
			action.setNewImageProperty(Ids.undecorate(request.getParameter("propertyKey"))); // tmp
			Messages errors = (Messages) request.getAttribute("errors");
			Messages messages = (Messages) request.getAttribute("messages");
			manager.executeAction(action, errors, messages, request);
		}
		catch (Exception ex) { // tmp ¿Cómo hacemos esto?
			ex.printStackTrace();
			throw new ServletException("Error al subir archivo"); // tmp i18n ¿Lanzar esta excepción?
		}
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("[UploadServlet.doDelete] "); // tmp
		try {
			ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");
			ModuleManager manager = (ModuleManager) context.get(request, "manager");
			DeleteImageAction action = new DeleteImageAction();
			// tmp action.setNewImageProperty(request.getParameter("property"));
			action.setNewImageProperty(Ids.undecorate(request.getParameter("propertyKey")));
			Messages errors = (Messages) request.getAttribute("errors");
			Messages messages = (Messages) request.getAttribute("messages");
			manager.executeAction(action, errors, messages, request);
		}
		catch (Exception ex) { // tmp ¿Cómo hacemos esto?
			ex.printStackTrace();
			throw new ServletException("Error al subir archivo"); // tmp i18n ¿Lanzar esta excepción?
		}		
	}

}
