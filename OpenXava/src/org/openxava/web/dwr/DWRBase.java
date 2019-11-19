package org.openxava.web.dwr;

import javax.servlet.http.*;

import org.openxava.controller.*;
import org.openxava.util.*;
import org.openxava.web.*;
import org.openxava.web.servlets.*;
import org.openxava.web.style.*;

/**
 * Base class for creating DWR remote classes. <p>
 * 
 * @author Javier Paniza
 */
class DWRBase {
	
	protected static ModuleContext getContext(HttpServletRequest request) {
		return (ModuleContext) request.getSession().getAttribute("context");
	}

	/**
	 * @since 5.4
	 */
	protected void initRequest(HttpServletRequest request, HttpServletResponse response, String application, String module) {
		Servlets.setCharacterEncoding(request, response);
		ModuleContext context = getContext(request); // Must be here, before checkSecurity()
		if (context != null) context.setCurrentWindowId(request);
		checkSecurity(request, application, module);
		request.setAttribute("style", Style.getInstance(request)); 
		Locales.setCurrent(request); 
		Requests.partialInit(request, application, module); 
	}
	
	protected void cleanRequest() { 
		Requests.clean(); 
	}

	protected static void checkSecurity(HttpServletRequest request, String application, String module) {
		ModuleContext context = getContext(request);
		if (context == null) { // This user has not executed any module yet
			throw new SecurityException("6859"); 
		}
		if (!context.exists(application, module, "manager")) { // This user has not execute this module yet
			throw new SecurityException("9876");  
		}
		if (context.exists(application, module, "naviox_locked")) {
			Boolean locking = (Boolean) context.get(application, module, "naviox_locking"); 
			if (!locking) {
				Boolean locked = (Boolean) context.get(application, module, "naviox_locked");
				if (locked) throw new SecurityException("3923");  								
			}			
		}
	}	

}
