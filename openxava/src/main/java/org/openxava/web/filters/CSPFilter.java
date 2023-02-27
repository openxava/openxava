package org.openxava.web.filters;

import java.io.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebFilter("/*")
public class CSPFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
        String policy = "default-src 'self'; script-src 'self' 'unsafe-inline'";
        filterConfig.getServletContext().setAttribute("csp", policy);
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    	System.out.println("[CSPFilter.doFilter] v2"); // tmr
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("Content-Security-Policy", (String) request.getServletContext().getAttribute("csp"));
        chain.doFilter(request, response);
    }

    public void destroy() {
        // No se requiere ninguna limpieza.
    }
}
