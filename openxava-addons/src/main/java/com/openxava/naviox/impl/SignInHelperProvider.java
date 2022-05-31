package com.openxava.naviox.impl;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;
import org.openxava.view.*;

import com.openxava.naviox.*;

/**
 * tmr
 * 
 * @since 7.0 
 * @author Javier Paniza
 */

public class SignInHelperProvider implements ISignInHelperProvider {
	
	private final static String PROPERTIES_FILE = "naviox-users.properties";
	private static Log log = LogFactory.getLog(SignInHelperProvider.class);
	private Properties users;
	
	public void init(HttpServletRequest request, View view) {
	}
	
	public String refineForwardURI(HttpServletRequest request, String forwardURI) {
		return forwardURI;
	}	
	
	public void signIn(HttpServletRequest request, String userName) {
		HttpSession session = request.getSession();
		session.setAttribute("naviox.user", userName);
		Modules modules = (Modules) session.getAttribute("modules");
		Users.setCurrent(userName); // In order startInLastVisitedModule=false works
		modules.reset();		
	}
	
	private boolean isAuthorized(ServletRequest request, String user, String password) {
		String storedPassword = getUsers().getProperty(user, null);
		return password.equals(storedPassword);
	}	
	
	public boolean isAuthorized(ServletRequest request, String userName, String password, Messages errors, String unauthorizedMessage) {  
		boolean authorized = isAuthorized(request, userName, password);
		if (!authorized) errors.add(unauthorizedMessage);
		return authorized;
	}	
	
	private Properties getUsers() {		
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

}
