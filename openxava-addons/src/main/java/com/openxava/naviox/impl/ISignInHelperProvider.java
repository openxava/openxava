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
	String init(HttpServletRequest request, View view);
	
	void initRequest(HttpServletRequest request, View view);
	
	String refineForwardURI(HttpServletRequest request, String forwardURI);
	
	void signIn(HttpServletRequest request, String userName);
	
	boolean isAuthorized(ServletRequest request, String userName, String password, Messages errors, String unauthorizedMessage);

	
	
}
