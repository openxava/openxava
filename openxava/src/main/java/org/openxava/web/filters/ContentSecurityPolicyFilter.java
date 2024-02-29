package org.openxava.web.filters;

import java.io.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;
import org.openxava.web.*;

/**
 * 
 * @since 7.1
 * @author Javier Paniza
 */

@WebFilter("/*") // If you change this pass the ZAP test again
public class ContentSecurityPolicyFilter implements Filter {
	
	private static Log log = LogFactory.getLog(ContentSecurityPolicyFilter.class); 
    
    private boolean turnOffWebSecurity = false; 
	private String trustedHostsForImages;
	private String trustedHostsForScripts;
	private String trustedHostsForStyles;
	private String trustedHostsForFrames;
	private String mapsTileProviderURL;
	private String unsafeEvalInScripts; // tmr

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    	// If you change this pass the ZAP test again
		
		if (turnOffWebSecurity) {
			chain.doFilter(request, response);
			return;
		}
		
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String policy = "default-src 'self'; script-src 'self' 'nonce-" + 
        	Nonces.get(request) + 
        	// tmr "' 'unsafe-eval' " + 
        	"' " +  unsafeEvalInScripts + // tmr
        	trustedHostsForScripts + 
        	"; style-src 'self' 'nonce-" + 
        	Nonces.get(request) +
        	"' " + 
        	trustedHostsForStyles + 
        	"; img-src 'self' data: blob: " + 
        	mapsTileProviderURL + trustedHostsForImages +
        	"; worker-src 'self' blob:; frame-src 'self' " +
        	trustedHostsForFrames +
        	"; frame-ancestors 'self'; form-action 'self'; font-src 'self' data:";        
        httpResponse.setHeader("Content-Security-Policy", policy);
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        chain.doFilter(request, response);
    }
	
	public void init(FilterConfig cfg) throws ServletException { // In order to work with Tomcat 8.x
		turnOffWebSecurity = XavaPreferences.getInstance().isTurnOffWebSecurity();
		if (turnOffWebSecurity) {
			log.warn(XavaResources.getString("security_disabled")); 
		}
		else {
			trustedHostsForImages = XavaPreferences.getInstance().getTrustedHostsForImages().replace(",", " ");
			trustedHostsForScripts = XavaPreferences.getInstance().getTrustedHostsForScripts().replace(",", " ");
			trustedHostsForStyles = XavaPreferences.getInstance().getTrustedHostsForStyles().replace(",", " ");
			trustedHostsForFrames = XavaPreferences.getInstance().getTrustedHostsForFrames().replace(",", " ");
			mapsTileProviderURL = getBaseURL(XavaPreferences.getInstance().getMapsTileProvider()) + " ";
			// tmr ini
			if (XavaPreferences.getInstance().isUnsafeEvalInScripts()) {
				System.out.println("[ContentSecurityPolicyFilter.init] A"); // tmr
				log.warn(XavaResources.getString("unsafe_eval_enabled")); // tmr i18n
				unsafeEvalInScripts = "'unsafe-eval' ";
			}
			else {
				System.out.println("[ContentSecurityPolicyFilter.init] B"); // tmr
				unsafeEvalInScripts = "";
			}
			// tmr fin
		}
	}
	
	public void destroy() { // In order to work with Tomcat 8.x
	}
    
    private String getBaseURL(String url) { 
        String[] tokens = url.split("/");
        return tokens[0] + "//" + tokens[2] + "/" ;
    }

}
