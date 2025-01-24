package org.openxava.util;

import org.hotswap.agent.annotation.*;

/**
 * tmr Renombrar, poner en otro paquete
 * @author javi
 *
 */

@Plugin(name = "OpenXava", testedVersions = { "7.5+" }) // tmr ¿Este nombre?
public class OpenXavaPlugin {
	
	private static int cacheVersion = 0; // tmr En otro sitio, ¿otro nmbre?
	
	@Init
    private static ClassLoader appClassLoader;

    @OnClassLoadEvent(classNameRegexp = ".*", events = LoadEvent.REDEFINE)
    public static void onAnyReload() throws Exception {
    	// tmr ¿solo cuando se cambien entidades?
    	// tmr Reiniciar caché de componentes
    	cacheVersion++;
    }
    
    public static int getCacheVersion() {
    	return cacheVersion;
    }
        
}