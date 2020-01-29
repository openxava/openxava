package com.openxava.naviox.web;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.openxava.util.*;
import org.openxava.web.*;
import org.openxava.web.style.*;

import com.openxava.naviox.util.*;
import com.openxava.phone.web.*;

/**
 * 
 * @author Javier Paniza
 */
public class NaviOXServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String [] uri = request.getRequestURI().split("/");
		/**
		 * Default the application name with url
		 */		
		if (uri.length < 3) {
			response.getWriter().print(XavaResources.getString(request, "module_name_missing"));
			return;
		}
		String applicationName = NaviOXPreferences.getInstance().getapplicationName();
		String url = Browsers.isMobile(request)?"/p/" + uri[2]:"/naviox/index.jsp?application=" + applicationName + "&module=" + uri[2];
		RequestDispatcher dispatcher = request.getRequestDispatcher(url);		
		
		Style.setPotalInstance(XavaStyle.getInstance()); // We manage style in NaviOX as in the portal case, to override the style defined in xava.properties and by device 
		dispatcher.forward(request, response);		
	}
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	

}
