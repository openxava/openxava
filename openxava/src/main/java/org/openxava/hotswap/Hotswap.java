package org.openxava.hotswap;

import java.lang.reflect.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;

/**
 * To access the current versions of classes and resources loaded in hot.
 *
 * @since 7.5
 * @author Javier Paniza
 */
public class Hotswap {
	
	private static Log log = LogFactory.getLog(Hotswap.class);
	
	public static int getModelVersion() {
		try {
			Method getModelVersion = Hotswap.class.getClassLoader().getParent().loadClass(HotswapPlugin.class.getName())
				.getDeclaredMethod("getModelVersion");
			return (Integer) getModelVersion.invoke(null);
		} 
		catch (Exception ex) {
			log.error(XavaResources.getString("unable_get_code_version", "model classes"), ex);
			return -1;
		}
	}
		
	public static int getControllersVersion() {
		try {
			Method getControllersVersion = Hotswap.class.getClassLoader().getParent().loadClass(HotswapPlugin.class.getName())
					.getDeclaredMethod("getControllersVersion");
			return (Integer) getControllersVersion.invoke(null);
		} catch (ClassNotFoundException ex) {
			// Fails on initializing application
			return 0;
		} catch (Exception ex) {
			log.error(XavaResources.getString("unable_get_code_version", "controllers.xml/controladores.xml"), ex);
			return -1;
		}    	
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
		try {
			Method getPersistentModelVersion = Hotswap.class.getClassLoader().getParent().loadClass(HotswapPlugin.class.getName())
				.getDeclaredMethod("getPersistentModelVersion");
			return (Integer) getPersistentModelVersion.invoke(null);
		} catch (Exception ex) {
			log.error(XavaResources.getString("unable_get_code_version", "persistent classes"), ex);
			return -1;
		}
    }
	
	public static int getI18nResourcesVersion() {
		try {
			Method getI18nResourcesVersion = Hotswap.class.getClassLoader().getParent().loadClass(HotswapPlugin.class.getName())
					.getDeclaredMethod("getI18nResourcesVersion");
			return (Integer) getI18nResourcesVersion.invoke(null);
		} catch (ClassNotFoundException ex) { // For the first time before starting Tomcat with the incorrect classloader
			return 0;
		} catch (Exception ex) {
			log.error(XavaResources.getString("unable_get_code_version", "i18n resources"), ex);
			return -1; 
		}
    }

}
