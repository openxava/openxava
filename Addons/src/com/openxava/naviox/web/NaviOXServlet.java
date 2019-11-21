package com.openxava.naviox.web;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.openxava.util.*;
import org.openxava.web.*;
import org.openxava.web.style.*;

import com.openxava.phone.web.*;

/**
 * 
 * @author Javier Paniza
 */
public class NaviOXServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String [] uri = request.getRequestURI().split("/");
		if (uri.length < 4) {
			response.getWriter().print(XavaResources.getString(request, "module_name_missing"));
			return;
		}
		String url = Browsers.isMobile(request)?"/p/" + uri[3]:"/naviox/index.jsp?application=" + uri[1] + "&module=" + uri[3];
		RequestDispatcher dispatcher = request.getRequestDispatcher(url);		
		
		Style.setPotalInstance(XavaStyle.getInstance()); // We manage style in NaviOX as in the portal case, to override the style defined in xava.properties and by device 
		dispatcher.forward(request, response);		
	}
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	

}
