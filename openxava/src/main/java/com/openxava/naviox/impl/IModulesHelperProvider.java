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

import com.openxava.naviox.*;

/**
 * @version 7.0
 * @author Javier Paniza
 */
public interface IModulesHelperProvider {
	
	void init(String applicationName);
	
	String getCurrent(HttpServletRequest request); 
	
	String getUserAccessModule(ServletRequest request);
	
	List<MetaModule> getAll(HttpServletRequest request);
	
	List<MetaModule> getNotInMenu();
	
	Collection<MetaModule> getInMenu(HttpServletRequest request, Modules modules);

	boolean isPublic(HttpServletRequest request, String moduleName);
	
	boolean showsIndexLink();

	boolean showsSearchModules(HttpServletRequest request);

}
