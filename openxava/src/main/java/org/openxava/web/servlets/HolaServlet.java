package org.openxava.web.servlets;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * tmr Quitar
 * 
 * @author javi
 */

public class HolaServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><head><title>Hola</title></head><body>");
        out.println("<h1>Hola</h1>");
        out.println("</body></html>");
    }
}
