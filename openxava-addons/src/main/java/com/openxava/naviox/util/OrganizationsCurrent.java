package com.openxava.naviox.util;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;

import com.openxava.naviox.impl.*;

/**
 * tmr
 * @since 7.0
 * @author Javier Paniza
 */

public class OrganizationsCurrent {
	
	private static Log log = LogFactory.getLog(OrganizationsCurrent.class); // tmr
	private static IOrganizationsCurrentProvider provider; // tmr
	
	public static String get(ServletRequest request) {
		// tmr return null;
		return getProvider().get(request); // tmr
	}
	

	public static String getName(HttpServletRequest request) {
		// tmr return null;
		return getProvider().getName(request); // tmr
	}
	
	// tmr ini	
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
	// tmr fin
	
}
