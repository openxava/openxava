package org.openxava.util;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;

import javax.persistence.*;

import org.hotswap.agent.annotation.*;

/**
 * tmr Renombrar, poner en otro paquete
 * @author javi
 *
 */

@Plugin(name = "OpenXava", testedVersions = { "7.5+" }) // tmr ¿Este nombre?
public class OpenXavaPlugin {
	
	private static boolean resourcesMonitoring; 
	private static int modelCacheVersion = 0; // tmr En otro sitio, ¿otro nombre?
	private static int controllersCacheVersion = 0; // tmr En otro sitio, ¿otro nombre?
	private static int applicationCacheVersion = 0; // tmr En otro sitio, ¿otro nombre?
	
    @OnClassLoadEvent(classNameRegexp = ".*", events = LoadEvent.REDEFINE)
    public static void onClassModified() throws Exception {
    	// tmr Reiniciar caché de componentes
    	modelCacheVersion++;
    }
    
    private static void onResourceModified(String resource) {
    	System.out.println("[OpenXavaPlugin.onResourceModified] resource=" + resource); // tmr
    	if ("controllers.xml".equals(resource) || "controladores.xml".equals(resource)) {
    		controllersCacheVersion++;
    	}
    	else if ("application.xml".equals(resource) || "aplicacion.xml".equals(resource)) {
    		applicationCacheVersion++;
    	}    	
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
    
    public static int getModelCacheVersion() {
    	return modelCacheVersion;
    }
    
    public static int getControllersCacheVersion() {
    	return controllersCacheVersion;
    }
    
    public static int getApplicationCacheVersion() {
    	return applicationCacheVersion;
    }
    
    @OnClassLoadEvent(classNameRegexp = ".*", events = { LoadEvent.REDEFINE })
    public static void onPersistentClassModified(Class oldClass) throws ClassNotFoundException  {
    	if (!isPersistentClass(oldClass)) return;
    	
    	String className = oldClass.getName();
        Class newClass = Class.forName(className);
        
        Set<String> newFields = getPersistentFieldNames(newClass);
        Set<String> oldFields = getPersistentFieldNames(oldClass);
        if (!newFields.equals(oldFields)) {
        	// TMR ME QUEDÉ POR AQUÍ: YA VA, AHORA FALTA INCREMENTAR UNA VARIABLE CACHE PROPIA
        	System.out.println("[OpenXavaPlugin.onPersistentClassModified] Fields modified for " + className); // tmr
        }
    }    

    private static boolean isPersistentClass(Class clazz) {
        return hasAnnotation(clazz, "javax.persistence.Entity") || hasAnnotation(clazz, "javax.persistence.MappedSuperclass");
    }

    private static boolean hasAnnotation(Class clazz, String annotationClassName) {
    	for (Object annotation : clazz.getAnnotations()) {
		    if (((Annotation) annotation).annotationType().getName().equals(annotationClassName)) {
		        return true;
		    }
		}
        return false;
    }

    private static Set<String> getPersistentFieldNames(Class<?> clazz) {
        Set<String> fieldNames = new HashSet<>();
        for (Field field : clazz.getDeclaredFields()) {
            int modifiers = field.getModifiers();

            // Exclude static fields, transient fields, and fields annotated with @Transient
            if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || field.isAnnotationPresent(Transient.class)) {
                continue;
            }

            fieldNames.add(field.getName());
        }
        return fieldNames;
    }
          
}