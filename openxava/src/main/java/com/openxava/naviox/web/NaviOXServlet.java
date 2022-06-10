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
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.openxava.util.*;
import org.openxava.web.*;
import org.openxava.web.style.*;

import com.openxava.naviox.impl.*;

/**
 * 
 * @author Javier Paniza
 */

@WebServlet(name = "naviox", urlPatterns = { "/modules/*", "/m/*" })
public class NaviOXServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String [] uri = request.getRequestURI().split("/");
		if (uri.length < 3) {
			response.getWriter().print(XavaResources.getString(request, "module_name_missing"));
			return;
		}
		String applicationName = MetaModuleFactory.getApplication(); 
		String moduleName = uri[uri.length - 1]; 
		String url = Browsers.isMobile(request)?"/p/" + moduleName:"/naviox/index.jsp?application=" + applicationName + "&module=" + moduleName;
		RequestDispatcher dispatcher = request.getRequestDispatcher(url);		
		
		Style.setPotalInstance(XavaStyle.getInstance()); // We manage style in NaviOX as in the portal case, to override the style defined in xava.properties and by device 
		dispatcher.forward(request, response);		
	}
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	

}
