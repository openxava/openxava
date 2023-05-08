package org.openxava.web.filters;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.openxava.web.*;

/**
 * 
 * @since 7.1
 * @author Javier Paniza
 */

// tmr @WebFilter("/*") // If you change this pass the ZAP test again
public class ContentSecurityPolicyFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    	// If you change this pass the ZAP test again
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("Content-Security-Policy", "default-src 'self'; script-src 'self' 'nonce-" + Nonces.get(request) +"' 'unsafe-eval'; style-src 'self' 'nonce-" + Nonces.get(request) +"'; img-src 'self' data: blob:; worker-src 'self' blob:; frame-ancestors 'self'; form-action 'self'; font-src 'self' data:");
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        chain.doFilter(request, response);
    }

}
