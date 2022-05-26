package com.openxava.naviox.impl;

import javax.servlet.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;

import com.openxava.naviox.util.*;


/**
 * 
 * @author Javier Paniza
 */
public class Initializer {
	
	private static Log log = LogFactory.getLog(Initializer.class);
	// tmr private static boolean initiated = false; 
	
	public static void init(ServletRequest request) {
		/* tmr
		if (initiated) return;
		AnnotatedClassParser.getManagedClassNames().add(SignIn.class.getName());	
		initiated = true;
		*/
		/*
		try {
			IInitializerProvider provider = (IInitializerProvider) Class.forName(NaviOXPreferences.getInstance().getInitializerProviderClass()).newInstance();
			provider.init(request);
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("access_tracker_provider_creation_error", trackerClass), ex);
		}
		*/
		;
		try {
			IInitializerProvider provider = (IInitializerProvider) Class.forName(NaviOXPreferences.getInstance().getInitializerProviderClass()).newInstance();
			provider.init(request);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
			log.warn(XavaResources.getString("initializer_provider_creation_error", ex));
			throw new XavaException("initializer_provider_creation_error");
		}
		
	}

}
