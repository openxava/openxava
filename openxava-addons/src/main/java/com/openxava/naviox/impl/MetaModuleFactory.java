package com.openxava.naviox.impl;

import java.util.*;
import org.apache.commons.logging.*;
import org.openxava.application.meta.*;
import org.openxava.util.*;
import com.openxava.naviox.util.*;

/**
 * 
 * @author Javier Paniza
 */

public class MetaModuleFactory {
	
	private static Log log = LogFactory.getLog(MetaModuleFactory.class); 
	
	private static String application; 

	public static MetaModule create(String application, String module) {		
		return MetaApplications.getMetaApplication(application).getMetaModule(module);				
	}
	
	/**
	 * @since 5.7
	 */
	public static MetaModule create(String module) { 		
		return MetaApplications.getMetaApplication(application).getMetaModule(module);				
	}
	
	public static List<MetaModule> createAll() {
		MetaApplication app = MetaApplications.getMetaApplication(application);
		List<MetaModule> metaModules = new ArrayList<MetaModule>();	
		for (String moduleName: createNamesProvider().getAllModulesNames(app)) {
			addMetaModule(app, metaModules, moduleName);
		}		
		return metaModules;
	}
	
	private static IAllModulesNamesProvider createNamesProvider() {
		try {
			return (IAllModulesNamesProvider) Class.forName(NaviOXPreferences.getInstance().getAllModulesNamesProviderClass()).newInstance();
		}
		catch (Exception ex) {
			log.error(XavaResources.getString("all_modules_names_provider_error"), ex); 
			throw new XavaException("all_modules_names_provider_error");
		}
	}
	
	private static void addMetaModule(MetaApplication app, List<MetaModule> metaModules, String name) { 
		MetaModule module = new MetaModule();
		module.setMetaApplication(app);
		module.setName(name);			
		module.setModelName(name);
		metaModules.add(module);
	}

	public static String getApplication() {  
		return application;
	}	
	
	public static void setApplication(String newApplication) { 
		application = newApplication;
	}

}
