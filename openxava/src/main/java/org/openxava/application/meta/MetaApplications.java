package org.openxava.application.meta;


import java.lang.reflect.*;
import java.util.*;

import org.openxava.application.meta.xmlparse.*;
import org.openxava.hotswap.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class MetaApplications {
	
	private static Collection applicationNames;

	private static Map<String, MetaApplication> metaAplicacions; 
	private static MetaApplication mainMetaApplication; 
	private static int sessionCacheVersion = getApplicationCacheVersion(); // tmr ¿Otro nombre?	
	
	/**
	 * Only call this from parser.
	 */
	public static void _addMetaApplication(MetaApplication application) throws XavaException {
		if (metaAplicacions == null) {
			throw new XavaException("only_from_parse", "MetaApplications._addMetaApplication");
		}
		metaAplicacions.put(application.getName(), application);
	}
	
	/**
	 * @return Collection of <tt>MetaApplication</tt>. Not null.
	 */
	public static Collection<MetaApplication> getMetaApplications() throws XavaException {
		// tmr ini		
        if (metaAplicacions != null) { // tmr ¿Poner la optimización metaAplicacions != null en los demás sitios?
        	int applicationCacheVersion = getApplicationCacheVersion();
        	if (sessionCacheVersion < applicationCacheVersion) {  
	        	System.out.println("[MetaApplications.getMetaApplications] Reset metaApplications"); // tmr
	        	metaAplicacions = null;
	        	sessionCacheVersion = applicationCacheVersion;
        	}
        }				
		// tmr fin
		if (metaAplicacions == null) {
			configure();
		}
		return metaAplicacions.values();
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
		if (metaAplicacions == null) configure();
		mainMetaApplication = metaAplicacions.get(applicationName);
	}

	
	private static void configure() throws XavaException {
		metaAplicacions = new HashMap();
		ApplicationParser.configureApplications();
	}
	
	public static MetaApplication getMetaApplication(String name) throws ElementNotFoundException, XavaException {
		/* tmr
		if (metaAplicacions == null) {
			configure();
		}
		*/
		getMetaApplications(); // tmr para iniciar metaAplicacions, ¿un método específico para ello?
		MetaApplication result = (MetaApplication) metaAplicacions.get(name);
		if (result == null) {
			throw new ElementNotFoundException("application_not_found", name);
		}
		return result;
	}

	public static Collection getApplicationsNames() throws XavaException {
		if (applicationNames == null) {
			applicationNames = new ArrayList();
			Iterator it = getMetaApplications().iterator();
			while (it.hasNext()) {
				MetaApplication ap = (MetaApplication) it.next();
				applicationNames.add(ap.getName());
			}
		}
		return applicationNames;
	}
	
	private static int getApplicationCacheVersion() { // tmr En otros sitios, refactorizar 
		// tmr Esto tendría que estar desactivado en producción
		try {
			Method getApplicationCacheVersion = MetaApplications.class.getClassLoader().getParent().loadClass(HotswapPlugin.class.getName())
					.getDeclaredMethod("getApplicationCacheVersion");
			return (Integer) getApplicationCacheVersion.invoke(null);
		} catch (ClassNotFoundException ex) { // For the first time before starting Tomcat with the incorrect classloader
			return 0;
		} catch (Exception ex) {
			ex.printStackTrace(); // tmr i18n ¿Quitar?
			return 0; // ¿0? ¿Poner en los demás?
		}
	}
	
}
