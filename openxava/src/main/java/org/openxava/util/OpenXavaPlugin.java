package org.openxava.util;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.lang.reflect.Modifier;
import java.nio.file.*;
import java.util.*;

import javax.persistence.*;

import org.hotswap.agent.annotation.*;
import org.hotswap.agent.javassist.*;

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
    	// tmr ¿solo cuando se cambien entidades?
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
    
    // tmr ini Prueba
    // Map to store the fields of each previously loaded entity class
    private static final Map<String, Set<String>> classFieldsMap = new HashMap<>();
    
    /*
    @OnClassLoadEvent(classNameRegexp = ".*", events = { LoadEvent.DEFINE })
    public static void onEntityModified(String className) {
    	System.out.println("[OpenXavaPlugin.onEntityModified] className=" + className); // tmr
    	try {
			onEntityModified(Class.forName(className));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    */
    
    @OnClassLoadEvent(classNameRegexp = ".*", events = { LoadEvent.REDEFINE, LoadEvent.DEFINE })
    public static void onEntityModified(CtClass ctClass) throws ClassNotFoundException  {
        // Process only persistent classes (@Entity or @MappedSuperclass)
    	System.out.println("[OpenXavaPlugin.onEntityModified] ctClass=" + ctClass); // tmr
        if (!isPersistentClass(ctClass)) {
            return;
        }

        Class clazz = Class.forName(ctClass.getName());
        System.out.println("[OpenXavaPlugin.onEntityModified] clazz=" + clazz); // tmr
        String className = clazz.getName();
        Set<String> newFields = getPersistentFieldNames(clazz);

        if (classFieldsMap.containsKey(className)) {
            Set<String> oldFields = classFieldsMap.get(className);

            // Detect added fields
            Set<String> addedFields = new HashSet<>(newFields);
            addedFields.removeAll(oldFields);

            // Detect removed fields
            Set<String> removedFields = new HashSet<>(oldFields);
            removedFields.removeAll(newFields);

            if (!addedFields.isEmpty() || !removedFields.isEmpty()) {
                System.out.println("Persistent class modified: " + className);

                if (!addedFields.isEmpty()) {
                    System.out.println("New fields detected: " + addedFields);
                }

                if (!removedFields.isEmpty()) {
                    System.out.println("Removed fields: " + removedFields);
                }
            }
        }

        // Update the stored version of the entity fields
        classFieldsMap.put(className, newFields);
    }

    private static boolean isPersistentClass(CtClass ctClass) {
        return hasAnnotation(ctClass, "javax.persistence.Entity") || hasAnnotation(ctClass, "javax.persistence.MappedSuperclass");
    }

    private static boolean hasAnnotation(CtClass ctClass, String annotationClassName) {
        try {
			for (Object annotation : ctClass.getAnnotations()) {
			    if (((Annotation) annotation).annotationType().getName().equals(annotationClassName)) {
			        return true;
			    }
			}
		} 
        catch (ClassNotFoundException ex) {
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
    // tmr fin Prueba
          
}