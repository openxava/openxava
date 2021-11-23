package com.openxava.naviox.impl;

import javax.servlet.*;
import org.openxava.component.parse.*;
import com.openxava.naviox.model.*;


/**
 * 
 * @author Javier Paniza
 */
public class Initializer {
	
	private static boolean initiated = false; 
	
	public static void init(ServletRequest request) {
		if (initiated) return;
		AnnotatedClassParser.getManagedClassNames().add(SignIn.class.getName());	
		initiated = true;
	}

}
