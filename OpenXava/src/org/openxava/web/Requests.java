package org.openxava.web;

import javax.servlet.http.*;
import org.openxava.controller.*;
import org.openxava.util.*;

/**
 * @since 5.9
 * @author Javier Paniza
 */
public class Requests {
	
	public static void init(HttpServletRequest request, String application, String module) {
		ModuleContext context = getContext(request);
		Users.setCurrent(request);
		ModuleManager manager = (ModuleManager) context.get(application, module, "manager");
		manager.setSession(request.getSession()); 
		manager.resetPersistence();
		SessionData.setCurrent(request);
		EmailNotifications.setModuleInfo(application, module, manager.getModuleURL()); 
	}

	private static ModuleContext getContext(HttpServletRequest request) {
		return (ModuleContext) request.getSession().getAttribute("context");
	}

}
