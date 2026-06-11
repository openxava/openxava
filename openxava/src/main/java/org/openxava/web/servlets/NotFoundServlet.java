package org.openxava.web.servlets;

import java.io.*;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

/**
 * 
 * 
 * @since 7.1
 * @author Javier Paniza
 */
@WebServlet("/m/.htaccess") // If you change this pass the ZAP test again
public class NotFoundServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Content-Security-Policy", "default-src 'self'; frame-ancestors 'self'; form-action 'self'"); 
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

}
