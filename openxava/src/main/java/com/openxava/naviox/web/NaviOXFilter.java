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
			System.out.println("[NaviOXFilter.doFilter] url=" + ((HttpServletRequest) request).getRequestURL()); // tmr
			System.out.println("[NaviOXFilter.doFilter] queryString=" + ((HttpServletRequest) request).getQueryString()); // tmr
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
				// tmr ini
				else {
					SignInHelper.printUser((HttpServletRequest) request, (HttpServletResponse) response);
				}
				// tmr fin
			}
			session.setAttribute("xava.user", session.getAttribute("naviox.user")); // We use naviox.user instead of working only
						// with xava.user in order to prevent some security hole using UrlParameters.setUser
			
			HttpServletRequest secureRequest = new SecureRequest(request);
			
			Users.setCurrent(secureRequest);
			
			if (modules.isModuleAuthorized(secureRequest)) {
				System.out.println("[NaviOXFilter.doFilter] MODULE AUTHORIZED"); // tmr
				chain.doFilter(secureRequest, response);
			}
			else {
				System.out.println("[NaviOXFilter.doFilter] MODULE NO AUTHORIZED"); // tmr
				char base = secureRequest.getRequestURI().split("/")[Is.emptyString(request.getServletContext().getContextPath())?1:2].charAt(0)=='p'?'p':'m';
				String originalURI = secureRequest.getRequestURI();
				String organization = OrganizationsCurrent.get(request);
				System.out.println("[NaviOXFilter.doFilter] organization=" + organization); // tmr
				System.out.println("[NaviOXFilter.doFilter] originalURI> " + originalURI); // tmr
				if (organization != null) originalURI = originalURI.replace("/modules/", "/o/" + organization + "/m/");
				System.out.println("[NaviOXFilter.doFilter] originalURI< " + originalURI); // tmr
				String originalParameters = secureRequest.getQueryString();
				String parametersQuery = "";
				if (!Is.emptyString(originalParameters)) {
					if (organization != null) originalParameters = originalParameters.replace("organization=" + organization + "&", "");
					originalParameters = originalParameters.replace("&", "__AMP__");
					parametersQuery = "?originalParameters=" + originalParameters;
				}
				String userAccessModule = modules.getUserAccessModule(request);
				/* tmr
				RequestDispatcher dispatcher = request.getRequestDispatcher(
					"/" + base + "/" + userAccessModule + 
					"?originalURI=" + originalURI +
					parametersQuery);
				*/
				// tmr ini
				String dispatcherURL = "/" + base + "/" + userAccessModule + "?originalURI=" + originalURI + parametersQuery;
				System.out.println("[NaviOXFilter.doFilter] Users.getCurrent()=" + Users.getCurrent()); // tmr
				System.out.println("[NaviOXFilter.doFilter] session.getAttribute(naviox.originalURL)=" + session.getAttribute("naviox.originalURL")); // tmr
				if (Users.getCurrent() == null && session.getAttribute("naviox.originalURL") == null) {
					System.out.println("[NaviOXFilter.doFilter] NO USER"); // tmr
					session.setAttribute("naviox.userAccessURL", dispatcherURL); // tmr ¿La utilizamos? ¿Este nombre de atributo? ¿naviox como prefijo? ¿URL o URI? ¿La utilizamos?
					System.out.println("[NaviOXFilter.doFilter] naviox.userAccessURL=" + dispatcherURL); // tmr
					session.setAttribute("naviox.originalURL", originalURI + parametersQuery); // tmr ¿Este nombre de atributo? ¿naviox como prefijo? ¿URL o URI?
					System.out.println("[NaviOXFilter.doFilter] naviox.originalURL=" + session.getAttribute("naviox.originalURL")); // tmr
					dispatcherURL = "/azure/signIn"; // tmr Obviamente no se puede quedar así									
				}
				else {
					System.out.println("[NaviOXFilter.doFilter] USER IN SESSION"); // tmr
				}
				System.out.println("[NaviOXFilter.doFilter] dispatcherURL=" + dispatcherURL); // tmr
				RequestDispatcher dispatcher = request.getRequestDispatcher(dispatcherURL); 				
				// tmr fin
				dispatcher.forward(secureRequest, response); 
			}
		} 
		finally {
			System.out.println("[NaviOXFilter.doFilter] OrganizationsCurrent.get(request)=" + OrganizationsCurrent.get(request)); // tmr
			XPersistence.commit();
		}
	}
		
	public void destroy() {
	}

}
