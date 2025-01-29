package org.openxava.util;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.*;
import java.nio.file.*;

import org.hotswap.agent.annotation.*;

/**
 * tmr Renombrar, poner en otro paquete
 * @author javi
 *
 */

@Plugin(name = "OpenXava", testedVersions = { "7.5+" }) // tmr ¿Este nombre?
public class OpenXavaPlugin {
	
	private static boolean resourcesMonitoring; 
	private static int cacheVersion = 0; // tmr En otro sitio, ¿otro nmbre?
	
	@Init
    private static ClassLoader appClassLoader;

    @OnClassLoadEvent(classNameRegexp = ".*", events = LoadEvent.REDEFINE)
    public static void onClassModified() throws Exception {
    	// tmr ¿solo cuando se cambien entidades?
    	// tmr Reiniciar caché de componentes
    	cacheVersion++;
    }
    
    private static void onResourceModified(String resource) {
    	// TMR ME QUEDÉ POR AQUÍ: YA SE EJECUTA, AHORA FALTA IMPLEMENTARLO PARA QUE RECARGE LOS CONTROLADORES
    	System.out.println("[OpenXavaPlugin.onResourceModified] resource=" + resource); // tmr
    }
    
	@Init
	public static void initResourcesMonitoring() {
	    if (!resourcesMonitoring) {
	    	monitorDirectory("target/classes/xava");
	    	resourcesMonitoring = true;
	    }
    }		
	
	private static void monitorDirectory(String directoryPath) {
		Thread watcherThread = new Thread(() -> {
			Path path = Paths.get(directoryPath);
			try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
				path.register(watchService, ENTRY_MODIFY); 
				while (!Thread.currentThread().isInterrupted()) {
					WatchKey key = watchService.take(); // Espera eventos
					for (WatchEvent<?> event : key.pollEvents()) {
						WatchEvent.Kind<?> kind = event.kind();

						if (kind == ENTRY_MODIFY) {
							onResourceModified(event.context().toString());
						}
					}
					key.reset(); // Reinicia la clave para seguir escuchando eventos
				}
			} catch (IOException | InterruptedException e) {
				// tmr ¿Qué hacer aquí?
			}
		});

		watcherThread.setDaemon(true); 
		watcherThread.start(); 
	}    
    
    public static int getCacheVersion() {
    	return cacheVersion;
    }
        
}