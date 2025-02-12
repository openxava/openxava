package org.openxava.util;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
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
import org.openxava.component.parse.*;

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
	private static int persistentModelCacheVersion = 0; // tmr En otro sitio, ¿otro nombre?
	private static int i18nResourcesCacheVersion = 0; // tmr En otro sitio, ¿otro nombre?
	
    @OnClassLoadEvent(classNameRegexp = ".*", events = LoadEvent.REDEFINE)
    public static void onClassModified() throws Exception {
    	modelCacheVersion++;
    }
    
    private static void onResourceModified(String resource, String directoryPath) {
    	if ("controllers.xml".equals(resource) || "controladores.xml".equals(resource)) {
    		controllersCacheVersion++;
    	}
    	else if ("application.xml".equals(resource) || "aplicacion.xml".equals(resource)) {
    		applicationCacheVersion++;
    	}
    	else if (directoryPath.endsWith("/i18n")) {
    		i18nResourcesCacheVersion++;
    	}
    }
        
    private static void onClassCreated(String className) {
    	try {
			Class.forName(className);
		} 
    	catch (ClassNotFoundException e) {
			e.printStackTrace(); // tmr Algo mejor
		}
    }
    
	@Init
	public static void initResourcesMonitoring() {
	    if (!resourcesMonitoring) {
	    	monitorDirectory("target/classes/xava", ENTRY_MODIFY);
	    	monitorDirectory("target/classes/i18n", ENTRY_MODIFY); 
	    	
	    	Collection<String> managedClassNames = getManagedClassNames();	    	
	        Set<String> monitoredDirectories = new HashSet<>();
	        for (String className : managedClassNames) {
	        	String packageName = Strings.noLastTokenWithoutLastDelim(className, ".");
	            String packagePath = "target/classes/" + packageName.replace('.', '/');
	            if (monitoredDirectories.add(packagePath)) { 
	                monitorDirectory(packagePath, ENTRY_CREATE, packageName);
	            }
	        }	    	
	    	
	    	resourcesMonitoring = true;
	    }
    }

	private static Collection<String> getManagedClassNames() {
		try {
			Collection<String> managedClassNames = AnnotatedClassParser.friendMetaApplicationGetManagedClassNames(); // ¿Cambiar el nombre de método?
			return managedClassNames;
		}
		catch (NoClassDefFoundError er) {
			// tmr Falla cuando se hace mvn install. Intentar otro modo para que no ejecute los plugins en maven
			// tmr System.err.println("Failed obtaining managed class names: " + er.getMessage()); // No poner este mensaje porque sale en mvn install y me van a preguntar
			return Collections.EMPTY_LIST;
		}
	}		
	
	private static void monitorDirectory(String directoryPath, WatchEvent.Kind<Path> kind) {
		monitorDirectory(directoryPath, kind, null);
	}
	
	private static void monitorDirectory(String directoryPath, WatchEvent.Kind<Path> kind, String packageName) {
		Thread watcherThread = new Thread(() -> {
			Path path = Paths.get(directoryPath);
			try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
				path.register(watchService, kind);
				
				while (!Thread.currentThread().isInterrupted()) {
					WatchKey key = watchService.take(); // Espera eventos
					for (WatchEvent<?> event : key.pollEvents()) {
						if (event.kind() == kind) {
							if (packageName == null) onResourceModified(event.context().toString(), directoryPath);
							else onClassCreated(packageName + "." + event.context().toString().replaceAll(".class$", ""));
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
	
		    
    @OnClassLoadEvent(classNameRegexp = ".*", events = { LoadEvent.REDEFINE })
    public static void onPersistentClassModified(CtClass newCtClass, Class oldClass) throws ClassNotFoundException  {
    	if (!isPersistentClass(newCtClass)) return; 
    	
    	if (!isPersistentClass(oldClass)) {
    		applicationCacheVersion++;
    		persistentModelCacheVersion++;
    		System.out.println("[OpenXavaPlugin.onPersistentClassModified] applicationCacheVersion=" + applicationCacheVersion); // tmr
    		return;
    	}

        
        Set<String> newFields = getPersistentFieldNames(newCtClass);              
        Set<String> oldFields = getPersistentFieldNames(oldClass);
        System.out.println("[OpenXavaPlugin.onPersistentClassModified] newFields=" + newFields); // tmr
        System.out.println("[OpenXavaPlugin.onPersistentClassModified] oldFields=" + oldFields); // tmr
        if (!newFields.equals(oldFields)) {
        	persistentModelCacheVersion++;
        	System.out.println("[OpenXavaPlugin.onPersistentClassModified] persistentModelCacheVersion=" + persistentModelCacheVersion); // tmr
        }
    }
    
    private static boolean isPersistentClass(Class clazz) throws ClassNotFoundException {
        return hasAnnotation(clazz, "javax.persistence.Entity") 
        	|| hasAnnotation(clazz, "javax.persistence.MappedSuperclass")
        	|| hasAnnotation(clazz, "javax.persistence.Embeddable");
    }    

    private static boolean isPersistentClass(CtClass ctClass) throws ClassNotFoundException {
        return hasAnnotation(ctClass, "javax.persistence.Entity") 
        	|| hasAnnotation(ctClass, "javax.persistence.MappedSuperclass")
        	|| hasAnnotation(ctClass, "javax.persistence.Embeddable");
    }
    
    private static boolean hasAnnotation(AnnotatedElement element, String annotationClassName) throws ClassNotFoundException {
    	for (Object annotation : element.getAnnotations()) {
		    if (((Annotation) annotation).annotationType().getName().equals(annotationClassName)) {
		        return true;
		    }
		}
        return false;
    }    
    
    private static boolean hasAnnotation(CtClass ctClass, String annotationClassName) throws ClassNotFoundException {
    	for (Object annotation : ctClass.getAnnotations()) {
		    if (((Annotation) annotation).annotationType().getName().equals(annotationClassName)) {
		        return true;
		    }
		}
        return false;
    }

    private static Set<String> getPersistentFieldNames(Class<?> clazz) throws ClassNotFoundException {
        Set<String> fieldNames = new HashSet<>();
        for (Field field : clazz.getDeclaredFields()) {
            int modifiers = field.getModifiers();

            // Exclude static fields, transient fields, and fields annotated with @Transient
            if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || hasAnnotation(field, "javax.persistence.Transient")) {
                continue;
            }

            fieldNames.add(field.getName());
        }
        return fieldNames;
    }
    
    private static Set<String> getPersistentFieldNames(CtClass ctClass) {
        Set<String> fieldNames = new HashSet<>();
        for (CtField field : ctClass.getDeclaredFields()) {
            int modifiers = field.getModifiers();

            // Exclude static fields, transient fields, and fields annotated with @Transient
            if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || field.hasAnnotation(Transient.class)) {
                continue;
            }

            fieldNames.add(field.getName());
        }
        return fieldNames;
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
    
    public static int getPersistentModelCacheVersion() {
    	return persistentModelCacheVersion;
    }
    
    public static int getI18nResourcesCacheVersion() {
    	return i18nResourcesCacheVersion;
    }
              
}