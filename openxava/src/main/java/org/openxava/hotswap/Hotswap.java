package org.openxava.hotswap;

import java.lang.reflect.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;

/**
 * To access the current versions of classes and resources loaded in hot.
 * 
 * @author Javier Paniza
 */
public class Hotswap {
	
	private static Log log = LogFactory.getLog(Hotswap.class);
	
	public static int getModelVersion() {
		return modelVersion;
	}
	    
	public static int getControllersVersion() {
    	return controllersVersion;
    }
	    
	public static int getApplicationVersion() {
		try {
			Method getApplicationVersion = Hotswap.class.getClassLoader().getParent().loadClass(HotswapPlugin.class.getName())
					.getDeclaredMethod("getApplicationVersion");
			return (Integer) getApplicationVersion.invoke(null);
		} 
		catch (Exception ex) {
			log.error(XavaResources.getString("unable_get_code_version", "application.xml/aplicacion.xml"), ex);
			return -1;
		}
    }
		    
	public static int getPersistentModelVersion() {
    	return persistentModelVersion;
    }
	    
	public static int getI18nResourcesVersion() {
    	return i18nResourcesVersion;
    }

}
