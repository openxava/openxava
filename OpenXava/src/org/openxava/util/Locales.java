package org.openxava.util;

import java.util.*;

import javax.servlet.http.HttpServletRequest;




/**
 * Access to the locale associated to the current thread. <p> 
 * 
 * @author Javier Paniza
 */

public class Locales {
	
	final private static ThreadLocal current = new ThreadLocal();
	
	
	
	/**
	 * The Locale associated to the current thread. <p>
	 * 
	 * @return Never null. If no locale associated, return default Locale.
	 */
	public static Locale getCurrent() {
		Locale r = (Locale) current.get();
		if (r == null) return Locale.getDefault();
		return r;
	}
	
	/**
	 * Associated a Locale to the current thread. <p>
	 */	
	public static void setCurrent(Locale locale) {
		current.set(locale);
	}
	
	/**
	 * Associated the Locale of the request to the current thread. <p> 
	 */
	public static void setCurrent(HttpServletRequest request) {
		Locale locale = (Locale) request.getSession().getAttribute("xava.portal.locale");
		if (locale == null) current.set(request.getLocale());
		else current.set(locale);
	}

} 
