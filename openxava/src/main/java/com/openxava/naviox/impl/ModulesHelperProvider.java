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

package com.openxava.naviox.impl;

import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.openxava.application.meta.*;
import org.openxava.util.*;

import com.openxava.naviox.*;

/**
 * @since 7.0
 * @author Javier Paniza
 */

public class ModulesHelperProvider implements IModulesHelperProvider { 
	
	private static List<MetaModule> all;
	
	public void init(String applicationName) {
	}
	
	public String getCurrent(HttpServletRequest request) {
		return null;
	}
	
	/** @since 5.6 */
	public String getUserAccessModule(ServletRequest request) { 
		return "SignIn";
	}
	
	public List<MetaModule> getAll(HttpServletRequest request) { 
		if (Users.getCurrent() == null) return Collections.EMPTY_LIST;
		if (all == null) all = createAll();
		return all;
	}
	
	/** @since 6.5 */
	public List<MetaModule> getNotInMenu() { 
		return Collections.EMPTY_LIST;
	}

	private List<MetaModule> createAll() {
		List<MetaModule> result = new ArrayList<MetaModule>();
		for (MetaModule module: MetaModuleFactory.createAll()) {
			if (module.getName().equals("SignIn")) continue;
			if (module.getName().equals("DiscussionComment")) continue;  
			result.add(module);
		}
		return result;
	}
	
	public boolean isPublic(HttpServletRequest request, String moduleName) { 
		return "FirstSteps".equals(moduleName); 
	}
	
	public boolean showsIndexLink() { 
		return false;
	}	

	public boolean showsSearchModules(HttpServletRequest request) { 
		Modules modules = (Modules) request.getSession().getAttribute("modules");
		return modules.getAll(request).size() > 30;   
	}

	public Collection<MetaModule> getInMenu(HttpServletRequest request, Modules modules) {
		if ("true".equals(request.getParameter("fixedModules"))) {
			return modules.getFixedModules(request); 
		}
		if ("true".equals(request.getParameter("bookmarkModules"))) {
			return modules.getBookmarkModules(request); 
		}
		return modules.getRegularModules(request); 
	}	

}
