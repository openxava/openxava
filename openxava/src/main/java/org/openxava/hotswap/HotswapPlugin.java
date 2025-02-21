package org.openxava.hotswap;

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
import org.openxava.util.*;

/**
 * Plugin for HotswapAgent, also monitors resources in project. <p>
 * 
 * Basically keep a version number for different kinds of classes/resorce.
 * You can get this version number using Hotswap class from this package.<br>
 * Used for manage hot code reloading.
 * 
 * @since 7.5
 * @author Javier Paniza
 */

@Plugin(name = "OpenXava", testedVersions = { "7.5+" }) 
public class HotswapPlugin {
	
	private static boolean active; 
	private static boolean resourcesMonitoring; 
	private static int modelVersion = 0; 
	private static int controllersVersion = 0; 
	private static int applicationVersion = 0; 
	private static int persistentModelVersion = 0; 
	private static int i18nResourcesVersion = 0; 
	
    @OnClassLoadEvent(classNameRegexp = ".*", events = LoadEvent.REDEFINE)
    public static void onClassModified() throws Exception {
    	modelVersion++;
    }
    
    private static void onResourceModified(String resource, String directoryPath) {
    	if ("controllers.xml".equals(resource) || "controladores.xml".equals(resource)) { // If changed try to modify in hot controladores.xml in an application in Spanish
    		controllersVersion++;
    	}
    	else if ("application.xml".equals(resource) || "aplicacion.xml".equals(resource)) { // If changed try to modify in hot aplicacion.xml in an application in Spanish
    		applicationVersion++;
    	}
    	else if (directoryPath.endsWith("/i18n")) {
    		i18nResourcesVersion++;
    	}
    }
        
    private static void onClassCreated(String className) {
    	try {
			Class.forName(className);
		} 
    	catch (ClassNotFoundException ex) {
    		ex.printStackTrace(); // We cannot use a log library because it fails when we do a mvn clean and then mvn install in a project
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
	    active = true; 
    }

	private static Collection<String> getManagedClassNames() {
		try {
			Collection<String> managedClassNames = AnnotatedClassParser.getManagedClassNamesFromFileClassPath(); 
			return managedClassNames;
		}
		catch (NoClassDefFoundError er) {
			// Don't show any log because it fails on doing mvn install, so it generates an too alarming message
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
					WatchKey key = watchService.take(); 
					for (WatchEvent<?> event : key.pollEvents()) {
						if (event.kind() == kind) {
							if (packageName == null) onResourceModified(event.context().toString(), directoryPath);
							else onClassCreated(packageName + "." + event.context().toString().replaceAll(".class$", ""));
						}
					}
					key.reset(); 
				}
			} catch (IOException | InterruptedException ex) {
				ex.printStackTrace(); // We cannot use a log library because it fails when we do a mvn clean and then mvn install in a project
			}
		});

		watcherThread.setDaemon(true); 
		watcherThread.start(); 
	}
	
		    
    @OnClassLoadEvent(classNameRegexp = ".*", events = { LoadEvent.REDEFINE })
    public static void onPersistentClassModified(CtClass newCtClass, Class oldClass) throws ClassNotFoundException  {
    	if (!isPersistentClass(newCtClass)) return; 
    	
    	if (!isPersistentClass(oldClass)) {
    		applicationVersion++;
    		persistentModelVersion++;
    		return;
    	}

        
        Set<String> newFields = getPersistentFieldNames(newCtClass);              
        Set<String> oldFields = getPersistentFieldNames(oldClass);
        if (!newFields.equals(oldFields)) {
        	persistentModelVersion++;
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
    
    static boolean isActive() { 
    	return active;
    }
    
    static int getModelVersion() {
    	return modelVersion;
    }
    
    static int getControllersVersion() {
    	return controllersVersion;
    }
    
    static int getApplicationVersion() {
    	return applicationVersion;
    }
    
    static int getPersistentModelVersion() {
    	return persistentModelVersion;
    }
    
    static int getI18nResourcesVersion() {
    	return i18nResourcesVersion;
    }
              
}