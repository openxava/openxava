package com.openxava.naviox.impl;

import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.application.meta.*;
import org.openxava.util.*;

import com.openxava.naviox.*;
import com.openxava.naviox.util.*;

/**
 * 
 * @author Javier Paniza
 */

public class ModulesHelper { 
	
	private static Log log = LogFactory.getLog(ModulesHelper.class);	
	private static IModulesHelperProvider provider;
	
	public static void init(String applicationName) {
		getProvider().init(applicationName); 
	}
	
	public static String getCurrent(HttpServletRequest request) {
		return getProvider().getCurrent(request); 
	}
	
	/** @since 5.6 */
	public static String getUserAccessModule(ServletRequest request) { 
		return getProvider().getUserAccessModule(request); 
	}
	
	public static List<MetaModule> getAll(HttpServletRequest request) {
		return getProvider().getAll(request); 
	}
	
	/** @since 6.5 */
	public static List<MetaModule> getNotInMenu() { 
		return getProvider().getNotInMenu(); 
	}
	
	public static Collection<MetaModule> getInMenu(HttpServletRequest request, Modules modules) { 
		return getProvider().getInMenu(request, modules); 
	}
	
	public static boolean isPublic(HttpServletRequest request, String moduleName) { 
		return getProvider().isPublic(request, moduleName); 
	}
	
	public static boolean showsIndexLink() { 
		return getProvider().showsIndexLink(); 
	}	

	public static boolean showsSearchModules(HttpServletRequest request) {
		return getProvider().showsSearchModules(request);
	}
	
	private static IModulesHelperProvider getProvider() {
		if (provider == null) {
			try {
				provider = (IModulesHelperProvider) Class.forName(NaviOXPreferences.getInstance().getModulesHelperProviderClass()).newInstance();
			} catch (Exception ex) {
				log.warn(XavaResources.getString("provider_creation_error", "ModulesHelper"), ex);
				throw new XavaException("provider_creation_error", "ModulesHelper");
			}
		}
		return provider;
	}

}
