package org.openxava.web.servlets;

import java.io.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.openxava.web.filters.*;

/**
 * tmr
 * 
 * @since 7.1
 * @author Javier Paniza
 */
@WebServlet({"/m/.htaccess", "/dwr/call/plaincall/.htaccess"})
public class NotFoundServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Content-Security-Policy", ContentSecurityPolicyFilter.CSP_HEADER); 
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

}
