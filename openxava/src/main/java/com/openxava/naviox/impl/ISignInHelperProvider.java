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

import org.openxava.util.*;
import org.openxava.view.*;

/**
 * 
 * @since 7.0
 * @author Javier Paniza
 */

public interface ISignInHelperProvider {
	
	/** @return Qualified name of the action to add, or null if none. */
	// tmr String init(HttpServletRequest request, View view);
	String [] init(HttpServletRequest request, View view); // tmr
	
	void initRequest(HttpServletRequest request, View view);
	
	String refineForwardURI(HttpServletRequest request, String forwardURI);
	
	void signIn(HttpServletRequest request, String userName);
	
	boolean isAuthorized(ServletRequest request, String userName, String password, Messages errors, String unauthorizedMessage);

	void printUser(HttpServletRequest request, HttpServletResponse response); // tmr

	/**
	 * @since 7.1 
	 */
	String getSignInURL(); // tmr ¿En migration? 
}
