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
import java.util.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.openxava.web.*;

import com.openxava.naviox.*;
import com.openxava.naviox.impl.*;

/**
 * 
 * @author Javier Paniza
 */

@WebServlet(name = "naviox", urlPatterns = { "/modules/*", "/m/*" })
public class NaviOXServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("[NaviOXServlet.doGet] request.getRequestURI()=" + request.getRequestURI()); // tmr
		String [] uri = request.getRequestURI().split("/");
		System.out.println("[NaviOXServlet.doGet] uri=" + Arrays.toString(uri)); // tmr
		System.out.println("[NaviOXServlet.doGet] uri.length=" + uri.length); // tmr
		/*
		if (uri.length < 3) {
			response.getWriter().print(XavaResources.getString(request, "module_name_missing"));
			return;
		}
		*/
		// tmr ini
		if (uri.length < 4) {
			// TMR ME QUEDÉ POR AQUÍ: INTENTANDO QUE /modules VAYA AL A ÚLTIMO MÓDULO CON LA URL, PARA LLAMARLO DESDE welcome.jsp
			// TMR  INTENTANDO QUE SSO FUNCIONE BIEN CUANDO SE ENTRA EN RÁIZ. YA FUNCIONA BIEN CUANDO SE ENTRA DIRECTAMENTE EN MÓDULO
			RequestDispatcher dispatcher = request.getRequestDispatcher("/m/" + getModules(request).getCurrent(request));
			dispatcher.forward(request, response);
			return;
		}		
		// tmr fin
		String applicationName = MetaModuleFactory.getApplication(); 
		String moduleName = uri[uri.length - 1];
		System.out.println("[NaviOXServlet.doGet] moduleName=" + moduleName); // tmr
		String url = Browsers.isMobile(request)?"/p/" + moduleName:"/naviox/index.jsp?application=" + applicationName + "&module=" + moduleName;
		RequestDispatcher dispatcher = request.getRequestDispatcher(url);		
		
		dispatcher.forward(request, response);		
	}
	
	
	private Modules getModules(HttpServletRequest request) { // tmr
		Modules modules = (Modules) request.getSession().getAttribute("modules");
		if (modules == null) {
			modules = new Modules();
			request.getSession().setAttribute("modules", modules);
		}
		return modules;
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	

}
