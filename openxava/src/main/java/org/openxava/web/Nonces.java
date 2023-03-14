package org.openxava.web;

import java.util.*;

import javax.servlet.*;

/**
 * tmr
 * 
 * @since 7.1
 * @author Javier Paniza
 */
public class Nonces {
	
	public static String get(ServletRequest request) {
		String nonce = (String) request.getAttribute("xava.nonce");
		if (nonce == null) {
			nonce = UUID.randomUUID().toString();
			request.setAttribute("xava.nonce", nonce);
		}
		return nonce;
	}

}
