package org.openxava.web.servlets;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;

/**
 * 
 * @author Laurent Wibaux 
 */
public class GenerateSimpleHTMLReportServlet extends HttpServlet {

	public static final String SESSION_REPORT = "org.openxava.report.simpleHtml";
	
	private static final long serialVersionUID = 7971363697569814431L;

	private static Log log = LogFactory.getLog(GenerateSimpleHTMLReportServlet.class);
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String simpleHtml = (String)request.getSession().getAttribute(SESSION_REPORT);
		request.getSession().removeAttribute(SESSION_REPORT); 
		try {
			response.setContentType("text/html");
			response.getWriter().println(simpleHtml);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new ServletException(XavaResources.getString("report_error"));
		}		
	}

}
