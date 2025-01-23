package org.openxava.util;

import java.lang.reflect.*;

import org.hotswap.agent.annotation.*;

/**
 * tmr Renombrar, poner en otro paquete
 * @author javi
 *
 */

@Plugin(name = "OpenXava", testedVersions = { "7.5+" }) // tmr ¿Este nombre?
public class OpenXavaPlugin {
	
	public static int cacheVersion = 0; // tmr En otro sitio, con un getter
	
	@Init
    private static ClassLoader appClassLoader;

    @OnClassLoadEvent(classNameRegexp = ".*", events = LoadEvent.REDEFINE)
    public static void onAnyReload() throws Exception {
    	// Tmr ¿solo cuando se cambien entidades?
        System.out.println("[OpenXavaPlugin.onAnyReload] " + cacheVersion); // tmr
        System.out.println("[OpenXavaPlugin.onAnyReload] OpenXavaPlugin.class.getClassLoader()=" + OpenXavaPlugin.class.getClassLoader()); // tmr
        System.out.println("[OpenXavaPlugin.onAnyReload] appClassLoader=" + appClassLoader); // tmr
        
        Method incrementCacheVersion = appClassLoader.loadClass(OpenXavaPlugin.class.getName())
        	.getDeclaredMethod("incrementCacheVersion");
        incrementCacheVersion.invoke(null);
    }
    
    private static void incrementCacheVersion() {    	
    	cacheVersion++;
    	System.out.println("[OpenXavaPlugin.incrementCacheVersion] cacheVersion=" + cacheVersion); // tmr
    }
    
}