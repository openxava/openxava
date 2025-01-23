package org.openxava.util;

import org.hotswap.agent.annotation.*;

/**
 * tmr Renombrar, poner en otro paquete
 * @author javi
 *
 */

@Plugin(name = "OpenXava", testedVersions = { "7.5+" }) // tmr ¿Este nombre?
public class OpenXavaPlugin {
	
	public static int cacheVersion = 0; // tmr En otro sitio, con un getter

    @OnClassLoadEvent(classNameRegexp = ".*", events = LoadEvent.REDEFINE)
    public static void onAnyReload() {
    	cacheVersion++;
        System.out.println("[OpenXavaPlugin.onAnyReload] " + cacheVersion); // tmr     
    }
    
}