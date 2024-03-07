/*
 * NaviOX. Navigation and security for OpenXava applications.
 * Copyright 2014 Javier Paniza Lucas
 *
 * License: Apache License, Version 2.0.	
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */

package com.openxava.naviox.web.dwr;

import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.controller.*;
import org.openxava.util.*;
import org.openxava.web.servlets.*;

import com.openxava.naviox.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class Modules {  
	
	private static Log log = LogFactory.getLog(Modules.class);
	
	public String displayModulesList(HttpServletRequest request, HttpServletResponse response) { 
		try { 
			return Servlets.getURIAsString(request, response, 
				"/naviox/" + NaviOXPreferences.getInstance().getModulesListJSP());
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
			return Servlets.getURIAsString(request, response, 
				"/naviox/" + NaviOXPreferences.getInstance().getModulesListJSP() + 
				"?modulesLimit=999&searchWord=" + searchWord); 
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
	/*
	public void closeModule(HttpServletRequest request, HttpServletResponse response, String application, String module, int i) {
		try {
			initRequest(request, response, application, module);
			HttpSession session = ((HttpServletRequest) request).getSession();
			com.openxava.naviox.Modules modules = (com.openxava.naviox.Modules) session.getAttribute("modules");
			modules.removeModule(i);
		} finally {
			XPersistence.commit();
			cleanRequest();
			//ModuleManager.commit();
		}
	}
*/
}
