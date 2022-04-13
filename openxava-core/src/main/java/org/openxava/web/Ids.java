package org.openxava.web;

import javax.servlet.http.*;
import org.openxava.controller.*;

/**
 * To decorate ids used for HTML elements.
 * 
 * @author Javier Paniza
 */

public class Ids {

	public static String decorate(String application, String module, String name) {
		if (name == null) return null;
		name = name.replaceAll("__PARENT__\\.", ""); // __PARENT__ is used in View class for internal purposes
		name = name.replaceAll("\\.", "___"); 
		if (name.startsWith("ox_")) return name;
		return "ox_" + application + "_" + module + "__" + name;
	}
	
	public static String decorate(HttpServletRequest request, String name) { 
		ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");
		String module = context.getCurrentModule(request);  
		return decorate(				
			request.getParameter("application"), 
			module, 
			name);
	}
	
	public static String undecorate(String name) {
		if (name == null) return null;
		name = name.replaceAll("___", ".");
		if (!name.startsWith("ox_")) return name;
		return name.substring(name.indexOf("__") + 2);		
	}

}
