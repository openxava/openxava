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
		String nonce = (String) httpRequest.getSession().getAttribute("xava.nonce");
		if (nonce != null) {
			request.setAttribute("xava.nonce", nonce);
			return nonce;
		}
		nonce = (String) httpRequest.getAttribute("xava.nonce");
		if (nonce != null) return nonce;
		nonce = UUID.randomUUID().toString();
		httpRequest.getSession().setAttribute("xava.nonce", nonce);		
		return nonce;
	}

}
