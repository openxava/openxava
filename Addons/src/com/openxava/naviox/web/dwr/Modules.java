package com.openxava.naviox.web.dwr;

import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.controller.*;
import org.openxava.util.*;
import org.openxava.web.servlets.*;

/**
 * 
 * @author Javier Paniza
 */
public class Modules {  
	
	private static Log log = LogFactory.getLog(Modules.class);
	
	public String displayModulesList(HttpServletRequest request, HttpServletResponse response) { 
		try { 
			return Servlets.getURIAsString(request, response, "/naviox/modulesList.jsp");
		}
		catch (Exception ex) {
			log.error(XavaResources.getString("display_modules_error"), ex); 
			return null; 
		}
		finally {
			ModuleManager.commit();
		}
	}
	
	public String displayAllModulesList(HttpServletRequest request, HttpServletResponse response, String searchWord) {   
		try { 
			return Servlets.getURIAsString(request, response, "/naviox/modulesList.jsp?modulesLimit=999&searchWord=" + searchWord); 
		}
		catch (Exception ex) {
			log.error(XavaResources.getString("display_modules_error"), ex); 
			return null; 
		}
		finally {
			ModuleManager.commit();
		}
	}

	
	public String filter(HttpServletRequest request, HttpServletResponse response, String searchWord) { 
		try { 
			if (request.getSession().getAttribute("context") == null) {
				throw new SecurityException("1928"); 
			}
			return Servlets.getURIAsString(request, response, "/naviox/selectModules.jsp?searchWord=" + searchWord);
		}
		catch (Exception ex) {
			log.error(XavaResources.getString("display_modules_error"), ex);
			return null; 
		}
		finally {
			ModuleManager.commit();
		}
	}

	public void bookmarkCurrentModule(HttpServletRequest request) { 
		try { 
			com.openxava.naviox.Modules modules = (com.openxava.naviox.Modules) request.getSession().getAttribute("modules");
			modules.bookmarkCurrentModule(request); 
		}
		catch (Exception ex) { 
			log.warn(XavaResources.getString("bookmark_module_problem"), ex);  
		}		
	}
	
	public void unbookmarkCurrentModule(HttpServletRequest request) { 
		try {  
			com.openxava.naviox.Modules modules = (com.openxava.naviox.Modules) request.getSession().getAttribute("modules");
			modules.unbookmarkCurrentModule(request); 
		}
		catch (Exception ex) { 
			log.warn(XavaResources.getString("unbookmark_module_problem"), ex);  
		}		
	}

}
