package com.openxava.naviox.web;

import java.io.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;

import com.openxava.naviox.impl.*;
import com.openxava.naviox.util.*;

/**
 * 
 * @author Javier Paniza
 */

@WebServlet("/o/*")
public class OrganizationServlet extends HttpServlet {
	
	private static Log log = LogFactory.getLog(OrganizationServlet.class);
	private static IServletProvider provider;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		getProvider().doGet(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private static IServletProvider getProvider() {
		if (provider == null) {
			try {
				provider = (IServletProvider) Class.forName(NaviOXPreferences.getInstance().getOrganizationServletProviderClass()).newInstance();
			} 
			catch (Exception ex) {
				log.warn(XavaResources.getString("provider_creation_error", "OrganizationServlet"), ex);
				throw new XavaException("provider_creation_error", "OrganizationServlet");
			}
		}
		return provider;
	}

}
