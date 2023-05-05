/*
 * NaviOX. Navigation and security for OpenXava applications.
 * Copyright 2014 Javier Paniza Lucas
 *
 * License: Apache License, Version 2.0.	
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */

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
	
	private static Log log = LogFactory.getLog(SignInHelper.class);
	private static ISignInHelperProvider provider; 
	
	/** @return Qualified names of the actions to add, or null if none. */
	public static String [] init(HttpServletRequest request, View view) {
		return getProvider().init(request, view); 
	}
	
	public static void initRequest(HttpServletRequest request, View view) {
		getProvider().initRequest(request, view); 
	}
	
	public static String refineForwardURI(HttpServletRequest request, String forwardURI) {
		return getProvider().refineForwardURI(request, forwardURI); 
	}	
	
	public static void signIn(HttpServletRequest request, String userName) {
		getProvider().signIn(request, userName); 
	}
	
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
	
	/**
	 * @since 7.1 
	 */
	public static String getSignInURL() { 
		return getProvider().getSignInURL();
	}
	
	
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
