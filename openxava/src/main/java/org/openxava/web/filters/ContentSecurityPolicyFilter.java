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
	
	public final static String CSP_HEADER = "default-src 'self'; script-src 'self' 'unsafe-inline'; frame-ancestors 'self'; form-action 'self'"; 


    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    	System.out.println("[ContentSecurityPolicyFilter.doFilter] v5"); // tmr
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        // TMR httpResponse.setHeader("Content-Security-Policy", CSP_HEADER);
        // TMR nonce-tmr1 tiene que ser generado
        // TMR ME QUEDÉ POR AQUÍ: YA FUNCIONA EL WELCOLME, AHORA TIENES QUE FUNCIONAR EL SIGNIN
        httpResponse.setHeader("Content-Security-Policy", "default-src 'self'; script-src 'self' 'nonce-tmr1' 'unsafe-eval'; style-src 'self' 'unsafe-inline'; frame-ancestors 'self'; form-action 'self'");
        chain.doFilter(request, response);
    }

}
