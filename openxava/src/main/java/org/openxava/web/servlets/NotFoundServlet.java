package org.openxava.web.servlets;

import java.io.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

/**
 * 
 * 
 * @since 7.1
 * @author Javier Paniza
 */
@WebServlet({"/m/.htaccess", "/dwr/call/plaincall/.htaccess"}) // If you change this pass the ZAP test again
public class NotFoundServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Content-Security-Policy", "default-src 'self'; frame-ancestors 'self'; form-action 'self'"); 
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

}
