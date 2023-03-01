package org.openxava.web.filters;

import java.io.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

/**
 * tmr
 * 
 * @since 7.1
 * @author Javier Paniza
 */

@WebFilter("/*")
public class ContentSecurityPolicyFilter implements Filter {
	
	public final static String CSP_HEADER = "default-src 'self'; frame-ancestors 'self'; form-action 'self'"; 


    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    	System.out.println("[ContentSecurityPolicyFilter.doFilter] "); // tmr
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("Content-Security-Policy", CSP_HEADER);
        chain.doFilter(request, response);
    }

}
