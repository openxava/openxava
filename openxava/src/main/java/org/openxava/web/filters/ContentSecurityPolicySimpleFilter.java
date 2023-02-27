package org.openxava.web.filters;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

//@WebFilter("/*")
public class ContentSecurityPolicySimpleFilter implements Filter {
    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                         FilterChain chain) throws IOException, ServletException {
    	
    	System.out.println("[ContentSecurityPolicySimpleFilter.doFilter] "); // tmr

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("Content-Security-Policy", "default-src 'self'; frame-ancestors 'self'");

        chain.doFilter(request, response);  /* Let request continue chain filter */
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
    }
}