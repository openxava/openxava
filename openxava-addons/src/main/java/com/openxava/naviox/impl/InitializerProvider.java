package com.openxava.naviox.impl;

import javax.servlet.*;

import org.openxava.component.parse.*;

import com.openxava.naviox.model.*;


/**
 * tmr
 * @author Javier Paniza
 */
public class InitializerProvider implements IInitializerProvider {
	
	private static boolean initiated = false; 
	
	public void init(ServletRequest request) {
		if (initiated) return;
		AnnotatedClassParser.getManagedClassNames().add(SignIn.class.getName());	
		initiated = true;
	}

}
