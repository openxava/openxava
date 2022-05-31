package com.openxava.naviox.impl;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;
import org.openxava.view.*;

import com.openxava.naviox.util.*;

/**
 * 
 * @author Javier Paniza
 */

public class SignInHelper {
	
	// tmr private final static String PROPERTIES_FILE = "naviox-users.properties";
	private static Log log = LogFactory.getLog(SignInHelper.class);
	// tmr private static Properties users;
	private static ISignInHelperProvider provider; // tmr
	
	public static void init(HttpServletRequest request, View view) {
		getProvider().init(request, view); // tmr
	}
	
	public static String refineForwardURI(HttpServletRequest request, String forwardURI) {
		// tmr return forwardURI;
		return getProvider().refineForwardURI(request, forwardURI); // tmr
	}	
	
	public static void signIn(HttpServletRequest request, String userName) {
		/* tmr
		HttpSession session = request.getSession();
		session.setAttribute("naviox.user", userName);
		Modules modules = (Modules) session.getAttribute("modules");
		Users.setCurrent(userName); // In order startInLastVisitedModule=false works
		modules.reset();
		*/
		getProvider().signIn(request, userName); // tmr
	}

	/* tmr
	public static boolean isAuthorized(ServletRequest request, String user, String password) {
		String storedPassword = getUsers().getProperty(user, null);
		return password.equals(storedPassword);
	}	
	
	public static boolean isAuthorized(ServletRequest request, String userName, String password, Messages errors) {
		boolean authorized = isAuthorized(request, userName, password);
		if (!authorized) errors.add("unauthorized_user");
		return authorized;
	}
	*/
	
	// tmr ini
	
	public static boolean isAuthorized(ServletRequest request, String userName, String password) { 
		return isAuthorized(request, userName, password, new Messages()); 
	}

	/**
	 * @since 5.4 
	 */
	public static boolean isAuthorized(ServletRequest request, String userName, String password, Messages errors) {    
		return isAuthorized(request, userName, password, errors, "unauthorized_user"); 
	}	
		
	/**
	 * @since 5.4 
	 */	
	public static boolean isAuthorized(ServletRequest request, String userName, String password, Messages errors, String unauthorizedMessage) { 
		return getProvider().isAuthorized(request, userName, password, errors, unauthorizedMessage);
	}
	// tmr fin
	
	/* tmr
	private static Properties getUsers() {		
		if (users == null) {
			PropertiesReader reader = new PropertiesReader(
					Users.class, PROPERTIES_FILE);
			try {
				users = reader.get();
			} catch (IOException ex) {
				log.error(XavaResources.getString("properties_file_error",
						PROPERTIES_FILE), ex);
				users = new Properties();
			}
		}
		return users;		
	}
	*/
	
	private static ISignInHelperProvider getProvider() {
		if (provider == null) {
			try {
				provider = (ISignInHelperProvider) Class.forName(NaviOXPreferences.getInstance().getSignInHelperProviderClass()).newInstance();
			} 
			catch (Exception ex) {
				log.warn(XavaResources.getString("provider_creation_error", "SignInHelper"), ex);
				throw new XavaException("provider_creation_error", "SignInHelper");
			}
		}
		return provider;
	}

}
