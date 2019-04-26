package com.openxava.naviox.web;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

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
		Modules.init(cfg.getServletContext().getContextPath().substring(1)); 
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
					if (SignInHelper.isAuthorized(autologinUser, NaviOXPreferences.getInstance().getAutologinPassword())) {
						SignInHelper.signIn(session, autologinUser);
					}					
				}
			}
			session.setAttribute("xava.user", session.getAttribute("naviox.user")); // We use naviox.user instead of working only
						// with xava.user in order to prevent some security hole using UrlParameters.setUser
			
			HttpServletRequest secureRequest = new SecureRequest(request);
			
			Users.setCurrent(secureRequest);
			SessionData.setCurrent(secureRequest); 
		
			if (modules.isModuleAuthorized(secureRequest)) {
				chain.doFilter(secureRequest, response);
			}
			else {
				char base = secureRequest.getRequestURI().split("/")[2].charAt(0)=='p'?'p':'m'; 
				String originalURI = secureRequest.getRequestURI();
				String organization = Organizations.getCurrent(request);
				if (organization != null) originalURI = originalURI.replace("/modules/", "/o/" + organization + "/m/");
				String userAccessModule = modules.getUserAccessModule(request);
				RequestDispatcher dispatcher = request.getRequestDispatcher("/" + base + "/" + userAccessModule + "?originalURI=" + originalURI);
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
