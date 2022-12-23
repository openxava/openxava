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

package com.openxava.naviox.web;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.openxava.application.meta.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

import com.openxava.naviox.*;
import com.openxava.naviox.impl.*;
import com.openxava.naviox.util.*;

/**
 * 
 * @author Javier Paniza
 */

public class NaviOXFilter implements Filter {
	
	public void init(FilterConfig cfg) throws ServletException {
		String contextPath = cfg.getServletContext().getContextPath();
		String applicationName = null;
		if (Is.emptyString(contextPath)) {
			if (MetaApplications.getMetaApplications().size() > 1) {
				throw new XavaException("root_context_only_one_app"); 
			}
			applicationName = MetaApplications.getMainMetaApplication().getName(); 
		}
		else {
			applicationName = contextPath.substring(1);
			MetaApplications.setMainApplicationName(applicationName);
		}
		
		Modules.init(applicationName); 
	}



	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			XPersistence.reset();
			Initializer.init(request);
			HttpSession session = ((HttpServletRequest) request).getSession();
			Modules modules = (Modules) session.getAttribute("modules");
			if (modules == null) {
				modules = new Modules();
				session.setAttribute("modules", modules);
			}
			if (Is.empty(session.getAttribute("xava.user"))) {
				String autologinUser = NaviOXPreferences.getInstance().getAutologinUser();
				if (!Is.emptyString(autologinUser)) {
					if (SignInHelper.isAuthorized(request, autologinUser, NaviOXPreferences.getInstance().getAutologinPassword())) { 
						SignInHelper.signIn((HttpServletRequest) request, autologinUser); 
					}					
				}
			}
			session.setAttribute("xava.user", session.getAttribute("naviox.user")); // We use naviox.user instead of working only
						// with xava.user in order to prevent some security hole using UrlParameters.setUser
			
			HttpServletRequest secureRequest = new SecureRequest(request);
			
			Users.setCurrent(secureRequest); 
		
			if (modules.isModuleAuthorized(secureRequest)) {
				chain.doFilter(secureRequest, response);
			}
			else {
				char base = secureRequest.getRequestURI().split("/")[Is.emptyString(request.getServletContext().getContextPath())?1:2].charAt(0)=='p'?'p':'m';
				String originalURI = secureRequest.getRequestURI();
				String originalParameters = secureRequest.getQueryString(); 
				String parametersQuery = "";
				if (!Is.emptyString(originalParameters)) {
					originalParameters = originalParameters.replace("&", "__AMP__");
					parametersQuery = "&originalParameters=" + originalParameters;
				}
				String organization = OrganizationsCurrent.get(request);
				if (organization != null) originalURI = originalURI.replace("/modules/", "/o/" + organization + "/m/");
				String userAccessModule = modules.getUserAccessModule(request);
				RequestDispatcher dispatcher = request.getRequestDispatcher(
					"/" + base + "/" + userAccessModule + 
					"?originalURI=" + originalURI +
					parametersQuery);
				dispatcher.forward(secureRequest, response); 
			}
		} 
		finally {
			XPersistence.commit();
		}
	}
		
	public void destroy() {
	}

}
