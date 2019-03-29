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
	
}
