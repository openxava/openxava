package org.openxava.web;

import javax.servlet.http.*;

/**
 *  
 * @since 6.0
 * @author Javier Paniza
 */

public class Browsers {
	
	public static boolean isMobile(HttpServletRequest request) {
		String browser = request.getHeader("user-agent");
		return browser != null && ((browser.contains("Android") && browser.contains("Mobile")) || browser.contains("iPhone"));
	}
	
	public static String getCSSClass(HttpServletRequest request) {
		return isIPhone(request)?"mobile iphone":"mobile";
	}
	
	private static boolean isIPhone(HttpServletRequest request) {
		String browser = request.getHeader("user-agent");
		return browser != null && browser.contains("iPhone");
	}
	
	/**
	 * @since 6.2
	 */
	public static boolean isIE(HttpServletRequest request) { 
		String browser = request.getHeader("user-agent");
		return browser == null?false:browser.contains("Trident") || browser.contains("MSIE");
	}
	
	/**
	 * @since 6.2
	 */
	public static boolean isFF(HttpServletRequest request) {
		return isBrowser(request, "Firefox"); 
	}

	/**
	 * @since 6.3.2
	 */	
	public static boolean isEdge(HttpServletRequest request) { 
		return isBrowser(request, "Edge");
	}
	
	private static boolean isBrowser(HttpServletRequest request, String string) {   
		String browser = request.getHeader("user-agent");
		return browser == null?false:browser.contains(string);
	}

	
}
