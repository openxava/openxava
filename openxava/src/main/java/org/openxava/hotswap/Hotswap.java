package org.openxava.hotswap;

import java.lang.reflect.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;

/**
 * To access the current versions of classes and resources loaded in hot 
 * and know if hot reloading is active.
 *
 * @since 7.5
 * @author Javier Paniza
 */
public class Hotswap {
	
	private static Log log = LogFactory.getLog(Hotswap.class);
	private static Boolean active; 
	
	public static boolean isActive() { 
		if (active == null) {
			try {
				Method isActive = Hotswap.class.getClassLoader().getParent().loadClass(HotswapPlugin.class.getName())
					.getDeclaredMethod("isActive");
				isActive.setAccessible(true); 
				active = (Boolean) isActive.invoke(null);
			}
			catch (ClassNotFoundException ex) {
				// Fails on initializing application
				return false;
			}
			catch (Exception ex) {
				log.error(XavaResources.getString("unable_determine_hotswap_active"), ex); 
				return false;
			}
			catch (NoClassDefFoundError er) {
				// Fails when using a JDK with no Hotswap Agent
				active = false;
			}
		} 
		return active;
	}
	
	public static int getModelVersion() {
		if (!isActive()) return 0; // Improves performance when a JDK with no HotswapAgent is used
		try {
			Method getModelVersion = Hotswap.class.getClassLoader().getParent().loadClass(HotswapPlugin.class.getName())
				.getDeclaredMethod("getModelVersion");
			getModelVersion.setAccessible(true);
			return (Integer) getModelVersion.invoke(null);
		} 
		catch (Exception ex) {
			log.error(XavaResources.getString("unable_get_code_version", "model classes"), ex);
			return -1;
		}
	}
		
	public static int getControllersVersion() {
		if (!isActive()) return 0; // Improves performance when a JDK with no HotswapAgent is used
		try {
			Method getControllersVersion = Hotswap.class.getClassLoader().getParent().loadClass(HotswapPlugin.class.getName())
					.getDeclaredMethod("getControllersVersion");
			getControllersVersion.setAccessible(true);
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
		if (!isActive()) return 0; // Improves performance when a JDK with no HotswapAgent is used
		try {
			Method getApplicationVersion = Hotswap.class.getClassLoader().getParent().loadClass(HotswapPlugin.class.getName())
				.getDeclaredMethod("getApplicationVersion");
			getApplicationVersion.setAccessible(true); 
			return (Integer) getApplicationVersion.invoke(null);
		} 
		catch (ClassNotFoundException ex) {
			// Fails on initializing application
			return 0;
		}
		catch (Exception ex) {
			log.error(XavaResources.getString("unable_get_code_version", "application.xml/aplicacion.xml"), ex);
			return -1;
		}
    }
			    
	public static int getPersistentModelVersion() {
		if (!isActive()) return 0; // Improves performance when a JDK with no HotswapAgent is used
		try {
			Method getPersistentModelVersion = Hotswap.class.getClassLoader().getParent().loadClass(HotswapPlugin.class.getName())
				.getDeclaredMethod("getPersistentModelVersion");
			getPersistentModelVersion.setAccessible(true);
			return (Integer) getPersistentModelVersion.invoke(null);
		} catch (Exception ex) {
			log.error(XavaResources.getString("unable_get_code_version", "persistent classes"), ex);
			return -1;
		}
    }
	
	public static int getI18nResourcesVersion() {
		if (!isActive()) return 0; // Improves performance when a JDK with no HotswapAgent is used
		try {
			Method getI18nResourcesVersion = Hotswap.class.getClassLoader().getParent().loadClass(HotswapPlugin.class.getName())
				.getDeclaredMethod("getI18nResourcesVersion");
			getI18nResourcesVersion.setAccessible(true);
			return (Integer) getI18nResourcesVersion.invoke(null);
		} catch (ClassNotFoundException ex) { // For the first time before starting Tomcat with the incorrect classloader
			return 0;
		} catch (Exception ex) {
			log.error(XavaResources.getString("unable_get_code_version", "i18n resources"), ex);
			return -1; 
		}
    }

}
