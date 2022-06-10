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

package com.openxava.naviox.util;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;

import com.openxava.naviox.impl.*;

/**
 * @since 7.0
 * @author Javier Paniza
 */

public class OrganizationsCurrent {
	
	private static Log log = LogFactory.getLog(OrganizationsCurrent.class); 
	private static IOrganizationsCurrentProvider provider; 
	
	public static String get(ServletRequest request) {
		return getProvider().get(request); 
	}
	

	public static String getName(HttpServletRequest request) {
		return getProvider().getName(request); 
	}
	
	private static IOrganizationsCurrentProvider getProvider() {
		if (provider == null) {
			try {
				provider = (IOrganizationsCurrentProvider) Class.forName(NaviOXPreferences.getInstance().getOrganizationsCurrentProviderClass()).newInstance();
			} 
			catch (Exception ex) {
				log.warn(XavaResources.getString("provider_creation_error", "Organizations"), ex);
				throw new XavaException("provider_creation_error", "Organizations");
			}
		}
		return provider;
	}
	
}
