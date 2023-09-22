package org.openxava.web;

import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Utility class to generate nonce numbers for using in inline JavaScript.
 * 
 * @since 7.1
 * @author Javier Paniza
 */
public class Nonces {
	
	public static String get(ServletRequest request) {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		// tmr String nonce = (String) request.getAttribute("xava.nonce");
		String nonce = (String) httpRequest.getSession().getAttribute("xava.nonce"); // tmr
		if (nonce == null) {
			nonce = UUID.randomUUID().toString();
			// tmr request.setAttribute("xava.nonce", nonce);
			httpRequest.getSession().setAttribute("xava.nonce", nonce); // tmr
		}
		return nonce;
	}

}
