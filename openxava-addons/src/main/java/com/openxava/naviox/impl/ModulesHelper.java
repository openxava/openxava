package com.openxava.naviox.impl;

import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.application.meta.*;
import org.openxava.util.*;

import com.openxava.naviox.util.*;

/**
 * 
 * @author Javier Paniza
 */

public class ModulesHelper { 
	
	// tmr ini
	private static Log log = LogFactory.getLog(ModulesHelper.class);	
	private static IModulesHelperProvider provider;
	// tmr fin
	
	// tmr private static List<MetaModule> all;
	
	public static void init(String applicationName) {
		getProvider().init(applicationName); // tmr
	}
	
	public static String getCurrent(HttpServletRequest request) {
		// tmr return null;
		return getProvider().getCurrent(request); // tmr
	}
	
	/** @since 5.6 */
	public static String getUserAccessModule(ServletRequest request) { 
		// tmr return "SignIn";
		return getProvider().getUserAccessModule(request); // tmr
	}
	
	public static List<MetaModule> getAll(HttpServletRequest request) {
		/* tmr
		if (Users.getCurrent() == null) return Collections.EMPTY_LIST;
		if (all == null) all = createAll();
		return all;
		*/
		return getProvider().getAll(request); // tmr
	}
	
	/** @since 6.5 */
	public static List<MetaModule> getNotInMenu() { 
		// tmr return Collections.EMPTY_LIST;
		return getProvider().getNotInMenu(); // tmr
	}

	/* tmr
	private static List<MetaModule> createAll() {
		List<MetaModule> result = new ArrayList<MetaModule>();
		for (MetaModule module: MetaModuleFactory.createAll()) {
			if (module.getName().equals("SignIn")) continue;
			if (module.getName().equals("DiscussionComment")) continue;  
			result.add(module);
		}
		return result;
	}
	*/
	
	public static boolean isPublic(HttpServletRequest request, String moduleName) { 
		// tmr return "FirstSteps".equals(moduleName); 
		return getProvider().isPublic(request, moduleName); // tmr
	}
	
	public static boolean showsIndexLink() { 
		// tmr return false;
		return getProvider().showsIndexLink(); // tmr
	}	

	public static boolean showsSearchModules(HttpServletRequest request) {
		/* tmr
		Modules modules = (Modules) request.getSession().getAttribute("modules");
		return modules.getAll(request).size() > 30;
		*/
		// tmr ini
		return getProvider().showsSearchModules(request);
		// tmr fin
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
