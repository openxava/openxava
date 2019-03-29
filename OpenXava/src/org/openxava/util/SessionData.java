package org.openxava.util;

import javax.servlet.http.*;

/**
 * To access session data without having access to HttpServletRequest or HttpSession. <p>
 * 
 * It has to be used from inside an OpenXava request, though you can use it in any part
 * of your application: JSPs, actions, calculators, validators, entities, etc. <br>
 * 
 * If you want use it inside your own servlet you have to call first to SessionData.setCurrent(request),
 * and SessionData.clean() at end. <br>
 * 
 * An advantage of using this class over direct request.getSession() is that it abstracts your code
 * from web technology, so that code could work in other contexts, as Swing for example. <br>
 *
 * @since 5.7
 * @author Javier Paniza
 */

public class SessionData {
	
	final private static ThreadLocal<HttpSession> current = new ThreadLocal<HttpSession>(); 
	
	/**
	 * To use SessionData in your own servlet first call to this method.
	 */	
	public static void setCurrent(HttpServletRequest request) {	
		current.set(request.getSession());
	}
	
	/**
	 * If you use SessionData in your own servlet call to this method at end.
	 */
	public static void clean() {
		current.set(null);
	}
		
	/**
	 * If not session available returns null.
	 */
	public static Object get(String key) { 
		HttpSession session = current.get();
		if (session == null) return null; 
		return session.getAttribute(key);
	}
	
	/**
	 * @exception IllegalStateException  If not session available.  
	 */	
	public static void put(String key, Object value) { 
		HttpSession session = current.get();
		if (session == null) throw new IllegalStateException(XavaResources.getString("session_not_set")); 
		session.setAttribute(key, value);		
	}
	
	/**
	 * @exception IllegalStateException  If not session available.  
	 */
	public static void remove(String key) { 
		HttpSession session = current.get();
		if (session == null) throw new IllegalStateException(XavaResources.getString("session_not_set")); 
		session.removeAttribute(key);		
	}

}
