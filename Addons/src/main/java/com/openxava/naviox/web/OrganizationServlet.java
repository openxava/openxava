package com.openxava.naviox.web;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class OrganizationServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().print("<html><head></head><body style='font-size: 500%;'>"); 
		response.getWriter().print(XavaResources.getString(request.getLocale(), "multitenancy_xavapro", "<a href='http://www.openxava.org/xavapro'>XavaPro</a>"));  
		response.getWriter().print("</body>");
	}

}
