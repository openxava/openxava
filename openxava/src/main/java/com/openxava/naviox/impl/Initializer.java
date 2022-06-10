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

import org.apache.commons.logging.*;
import org.openxava.util.*;

import com.openxava.naviox.util.*;


/**
 * 
 * @author Javier Paniza
 */
public class Initializer {
	
	private static Log log = LogFactory.getLog(Initializer.class);
	private static IInitializerProvider provider; 
	
	public static void init(ServletRequest request) {
		getProvider().init(request);  		
	}
	
	private static IInitializerProvider getProvider() {
		if (provider == null) {
			try {
				provider = (IInitializerProvider) Class.forName(NaviOXPreferences.getInstance().getInitializerProviderClass()).newInstance();
			} 
			catch (Exception ex) {
				log.warn(XavaResources.getString("provider_creation_error", "Initializer"), ex);
				throw new XavaException("provider_creation_error", "Initializer");
			}
		}
		return provider;
	}

}
