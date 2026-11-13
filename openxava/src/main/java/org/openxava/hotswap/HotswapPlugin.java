package org.openxava.hotswap;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.lang.reflect.Modifier;
import java.nio.file.*;
import java.util.*;

import jakarta.persistence.*;

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
	private static Set<String> initialManagedClassNames; 
	
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
    
    private static void onDirectoryRecreated(String directoryPath) {
    	// Called when the monitored directory itself was deleted and recreated.
    	// Some IDEs (e.g. IntelliJ) rebuild resources by removing and recreating
    	// target/classes/i18n (and similar) instead of overwriting files in place,
    	// which invalidates the WatchKey and drops the file-level events.
    	if (directoryPath.endsWith("/i18n")) {
    		i18nResourcesVersion++;
    	}
    	else if (directoryPath.endsWith("/xava")) {
    		controllersVersion++;
    		applicationVersion++;
    	}
    }
        
    private static void onClassCreated(String className) {
    	try {
    		if (initialManagedClassNames != null && initialManagedClassNames.contains(className)) return;
			Class newClass = Class.forName(className);
	    	if (isPersistentClass(newClass)) {
	    		applicationVersion++;
	    		persistentModelVersion++;
	    	}
		} 
    	catch (ClassNotFoundException ex) {
    		ex.printStackTrace(); // We cannot use a log library because it fails when we do a mvn clean and then mvn install in a project
		}
    }
    
	@Init
	public static void initResourcesMonitoring() {
		Hotswap.setPluginClass(HotswapPlugin.class);
	    if (!resourcesMonitoring) {
	    	for (Path classesDir : getClasspathDirectories()) {
	    		Path xavaDir = classesDir.resolve("xava");
	    		Path i18nDir = classesDir.resolve("i18n");
		    	monitorDirectory(xavaDir.toString(), ENTRY_MODIFY);
		    	monitorDirectory(xavaDir.toString(), ENTRY_CREATE);
		    	monitorDirectory(i18nDir.toString(), ENTRY_MODIFY);
		    	monitorDirectory(i18nDir.toString(), ENTRY_CREATE);
		    	Path sourceResourcesDir = getSourceResourcesDir(classesDir);
		    	if (sourceResourcesDir != null) {
		    		// Some launchers (e.g. Spring Boot) do not copy resources to the
		    		// classpath output when only resources change, so we also watch the
		    		// source resources dir and copy changed files to the classpath ourselves
		    		monitorSourceDirectory(sourceResourcesDir.resolve("xava"), xavaDir);
		    		monitorSourceDirectory(sourceResourcesDir.resolve("i18n"), i18nDir);
		    	}
	    	}

	    	Collection<String> managedClassNames = getManagedClassNames();
	    	initialManagedClassNames = new HashSet<>(managedClassNames);
	        Set<String> monitoredDirectories = new HashSet<>();
	        for (String className : managedClassNames) {
	        	String packageName = Strings.noLastTokenWithoutLastDelim(className, ".");
	            for (Path classesDir : getClasspathDirectories()) {
	            	String packagePath = classesDir.resolve(packageName.replace('.', '/')).toString();
		            if (monitoredDirectories.add(packagePath)) {
		                monitorDirectory(packagePath, ENTRY_CREATE, packageName);
		            }
	            }
	        }

	    	resourcesMonitoring = true;
	    }
	    active = true;
    }

	private static Set<Path> getClasspathDirectories() {
		Set<Path> dirs = new LinkedHashSet<>();
		for (String entry : System.getProperty("java.class.path", "").split(File.pathSeparator)) {
			if (Is.emptyString(entry)) continue;
			Path dir = Paths.get(entry);
			if (java.nio.file.Files.isDirectory(dir)) dirs.add(dir);
		}
		return dirs;
	}

	private static Path getSourceResourcesDir(Path classesDir) {
		// Maven: <project>/target/classes -> <project>/src/main/resources
		if (classesDir.endsWith(Paths.get("target", "classes"))) {
			return classesDir.resolve("../../src/main/resources").normalize();
		}
		// Gradle: <project>/build/resources/main -> <project>/src/main/resources
		if (classesDir.endsWith(Paths.get("build", "resources", "main"))) {
			return classesDir.resolve("../../../src/main/resources").normalize();
		}
		return null;
	}

	private static void monitorSourceDirectory(Path sourceDir, Path classesDir) {
		// Copies modified/created source resources to the classpath output dir, so the
		// application sees the new content even if the build has not copied it, and
		// bumps the corresponding version so the metadata is reloaded
		monitorDirectory(sourceDir.toString(), ENTRY_MODIFY, null, classesDir);
		monitorDirectory(sourceDir.toString(), ENTRY_CREATE, null, classesDir);
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
		monitorDirectory(directoryPath, kind, packageName, null);
	}

	private static void monitorDirectory(String directoryPath, WatchEvent.Kind<Path> kind, String packageName, Path copyToDir) {
		Thread watcherThread = new Thread(() -> {
			Path path = Paths.get(directoryPath);
			try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
				Set<String> existingFiles = new HashSet<>();
				if (packageName != null) {
					File dir = path.toFile();
					if (dir.isDirectory()) {
						for (String f : dir.list()) {
							existingFiles.add(f);
						}
					}
				}
				while (!java.nio.file.Files.isDirectory(path) && !Thread.currentThread().isInterrupted()) {
					Thread.sleep(1000);
				}
				path.register(watchService, kind);
				
				while (!Thread.currentThread().isInterrupted()) {
					WatchKey key = watchService.take(); 
					for (WatchEvent<?> event : key.pollEvents()) {
						if (event.kind() == kind) {
							if (copyToDir != null) {
								onSourceResourceModified(path, event.context().toString(), copyToDir);
							}
							else if (packageName == null) onResourceModified(event.context().toString(), directoryPath);
							else {
								String fileName = event.context().toString();
								if (!existingFiles.contains(fileName)) {
									existingFiles.add(fileName);
									onClassCreated(packageName + "." + fileName.replaceAll(".class$", ""));
								}
							}
						}
					}
					if (!key.reset()) {
						while (!java.nio.file.Files.isDirectory(path) && !Thread.currentThread().isInterrupted()) {
							Thread.sleep(1000);
						}
						path.register(watchService, kind);
						// The directory was deleted and recreated (e.g. by IntelliJ rebuilding
						// resources): events emitted during the recreation may have been lost
						// because the WatchKey was already invalid. Notify so versions get bumped.
						if (packageName == null && copyToDir == null) {
							onDirectoryRecreated(directoryPath);
						}
					}
				}
			} catch (IOException | InterruptedException ex) {
				ex.printStackTrace(); // We cannot use a log library because it fails when we do a mvn clean and then mvn install in a project
			}
		});

		watcherThread.setDaemon(true);
		watcherThread.start();
	}

	private static void onSourceResourceModified(Path sourceDir, String fileName, Path copyToDir) {
		Path source = sourceDir.resolve(fileName);
		if (!java.nio.file.Files.isRegularFile(source)) return;
		try {
			java.nio.file.Files.createDirectories(copyToDir);
			java.nio.file.Files.copy(source, copyToDir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException ex) {
			ex.printStackTrace(); // We cannot use a log library because it fails when we do a mvn clean and then mvn install in a project
		}
		onResourceModified(fileName, sourceDir.toString());
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
        return hasAnnotation(clazz, "jakarta.persistence.Entity")
        	|| hasAnnotation(clazz, "jakarta.persistence.MappedSuperclass")
        	|| hasAnnotation(clazz, "jakarta.persistence.Embeddable");
    }

    private static boolean isPersistentClass(CtClass ctClass) throws ClassNotFoundException {
        return hasAnnotation(ctClass, "jakarta.persistence.Entity")
        	|| hasAnnotation(ctClass, "jakarta.persistence.MappedSuperclass")
        	|| hasAnnotation(ctClass, "jakarta.persistence.Embeddable");
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
            if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || hasAnnotation(field, "jakarta.persistence.Transient")) {
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
