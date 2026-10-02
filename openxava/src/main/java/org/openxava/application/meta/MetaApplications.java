package org.openxava.application.meta;


import java.util.*;

import org.openxava.application.meta.xmlparse.*;
import org.openxava.hotswap.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class MetaApplications {
	
	private static volatile Collection<String> applicationNames;

	private static volatile Map<String, MetaApplication> metaAplicacions; 
	private static Map<String, MetaApplication> loadingMetaApplications;
	private static volatile MetaApplication mainMetaApplication; 
	private static int applicationCodeVersion = Hotswap.getApplicationVersion(); 	
	
	/**
	 * Only call this from parser.
	 * @throws XavaException
	 */
	public static void _addMetaApplication(MetaApplication application) {
		if (loadingMetaApplications == null) {
			throw new XavaException("only_from_parse", "MetaApplications._addMetaApplication");
		}
		loadingMetaApplications.put(application.getName(), application);
	}
	
	/**
	 * @return Collection of <tt>MetaApplication</tt>. Not null.
	 * @throws XavaException
	 */
	public static Collection<MetaApplication> getMetaApplications() {
		return configureMetaApplications().values();
	}

	private static synchronized Map<String, MetaApplication> configureMetaApplications() {
		int currentVersion = Hotswap.getApplicationVersion();
		if (metaAplicacions == null || applicationCodeVersion < currentVersion) {
			configure();
			applicationCodeVersion = currentVersion;
		}
		return metaAplicacions;
	}
	
	/**
	 * @since 6.3
	 */
	public static MetaApplication getMainMetaApplication() { 
		if (mainMetaApplication == null) {
			mainMetaApplication = getMetaApplications().iterator().next(); 
		}
		return mainMetaApplication;
	}
	
	/**
	 * @since 6.3
	 */	
	public static void setMainApplicationName(String applicationName) { 
		mainMetaApplication = configureMetaApplications().get(applicationName);
	}

	
	/**
	* @throws XavaException
	 */
	private static void configure() {
		Map<String, MetaApplication> loaded = new HashMap<>();
		loadingMetaApplications = loaded;
		try {
			ApplicationParser.configureApplications();
		}
		finally {
			loadingMetaApplications = null;
		}
		metaAplicacions = loaded;
	}
	
	/**
	* @throws XavaException
	 */
	public static MetaApplication getMetaApplication(String name) throws ElementNotFoundException {
		MetaApplication result = configureMetaApplications().get(name);
		if (result == null) {
			throw new ElementNotFoundException("application_not_found", name);
		}
		return result;
	}

	/**
	* @throws XavaException
	 */
	public static Collection<String> getApplicationsNames() {
		if (applicationNames == null) {
			Collection<String> names = new ArrayList<>();
			Iterator it = getMetaApplications().iterator();
			while (it.hasNext()) {
				MetaApplication ap = (MetaApplication) it.next();
				names.add(ap.getName());
			}
			applicationNames = names;
		}
		return applicationNames;
	}	
	
}
