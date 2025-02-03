package org.openxava.application.meta;


import java.lang.reflect.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.component.*;
import org.openxava.component.parse.*;
import org.openxava.controller.meta.*;
import org.openxava.util.*;
import org.openxava.util.meta.*;

/**
 * @author Javier Paniza
 */
public class MetaApplication extends MetaElement implements java.io.Serializable {

	private static Log log = LogFactory.getLog(MetaApplication.class);

	// tmr private Map metaModules = new HashMap();
	private Map<String, MetaModule> metaModules = new HashMap<>(); // tmr
	private Collection modulesNames = new ArrayList(); // to preserve the order
	private Collection folders;
	private Collection controllersForDefaultModule;
	private boolean defaultModulesGenerated = false;
	private int sessionCacheVersion = -1; // tmr ¿Otro nombre?
	
	
	/**
	 * 
	 * @param newModule Not null
	 */
	public void addMetaModule(MetaModule newModule) {
		metaModules.put(newModule.getName(), newModule);
		newModule.setMetaApplication(this);
		modulesNames.add(newModule.getName()); // to preserve the order
	}
	
	public String getFolderLabel(Locale locale, String folder) {
		folder = Strings.change(folder, "/", ".");					
		return Labels.get(folder, locale);
	}
	
	public String getFolderLabel(String folder) {
		return getFolderLabel(Locale.getDefault(), folder);
	}
	
	
	public Collection getFolders() throws XavaException { 
		if (folders == null) {
			folders = new HashSet();
			for (Iterator it = getMetaModules().iterator(); it.hasNext(); ) {
				MetaModule metaModule = (MetaModule) it.next(); 
				folders.add(metaModule.getFolder());
			}
		}
		return folders;
	}

	
	
	/**
	 * 
	 * @exception XavaException  Any problem 
	 * @return of <tt>MetaModule</tt>. Not null.
	 */
	public Collection<MetaModule> getMetaModules() throws XavaException { // tmr <MetaModule>
		generateDefaultModules();
		return metaModules.values();
	}
	
	private void resetControllersForDefaultModules() { // tmr
		for (MetaModule module: metaModules.values()) {
			if (module.isGeneratedByDefault()) {
				generateDefaultControllers(module);
			}
		}
	}

	private void generateDefaultModules() throws XavaException { 
		if (defaultModulesGenerated) return;
		generateDefaultModulesFromJPAEntities();
		generateDefaultModulesFromXMLComponents();
		defaultModulesGenerated = true;
	}

	private void generateDefaultModulesFromXMLComponents() throws XavaException {
		boolean generateDefaultModules = XavaPreferences.getInstance().isGenerateDefaultModules();
		for (Iterator it=MetaComponent.getAll().iterator(); it.hasNext(); ) {
			MetaComponent component = (MetaComponent) it.next();
			if (component.getMetaEntity().isAnnotatedEJB3()) break; // If there is just one JPA this is not a XML application, so we don't continue
			String modelName = component.getName();
			if (!metaModules.containsKey(modelName) && generateDefaultModules) {
				createDefaultModule(modelName);
			}			
		}		
	}

	private void generateDefaultModulesFromJPAEntities() throws XavaException {
		boolean generateDefaultModules = XavaPreferences.getInstance().isGenerateDefaultModules();
		try {
			Collection classNames = AnnotatedClassParser.friendMetaApplicationGetManagedClassNames();
			for (Iterator it=classNames.iterator(); it.hasNext(); ) {
				String className = (String) it.next();
				String modelName = Strings.lastToken(className, ".");
				if (!metaModules.containsKey(modelName) && generateDefaultModules) {
					createDefaultModule(modelName);
				}			
			}		
		}
		catch (Exception ex) {
			log.error(ex);
			throw new XavaException("default_modules_from_jpa_error");
		}
	}			

	/**
	 * In the same order that they are found in application.xml/aplicacion.xml. <p>
	 *  
	 * @return of <tt>String</tt>. Not null.
	 */	
	public Collection getModulesNames() { 
		return modulesNames;
	}
	
	/**
	 * The modules in the indicated folder 
	 * in the same order that they are found in application.xml/aplicacion.xml. <p>
	 *  
	 * @return of <tt>String</tt>. Not null.
	 * @throws XavaException 
	 */	
	public Collection getModulesNamesByFolder(String folder) throws XavaException {
		if (Is.emptyString(folder) || folder.trim().equals("/")) folder = ""; 
		Collection result = new ArrayList();
		for (Iterator it=getModulesNames().iterator(); it.hasNext();) {
			String moduleName = (String) it.next();
			String moduleFolder = getMetaModule(moduleName).getFolder();
			if (Is.equal(folder, moduleFolder)) {
				result.add(moduleName);
			}
		}
		return result;
	}
		
	/**
     * @exception ElementNotFoundException
	 */
	public MetaModule getMetaModule(String name) throws ElementNotFoundException, XavaException {
		// tmr ini
        if (sessionCacheVersion < getControllersCacheVersion()) {
        	System.out.println("[MetaApplication.getMetaModule] Reset controllers for default modules"); // tmr
        	resetControllersForDefaultModules();
        	sessionCacheVersion = getControllersCacheVersion();     
        }		
		// tmr fin
		MetaModule result = (MetaModule) metaModules.get(name);
		if (result == null) {
			if (existsModel(name)) {				
				result = createDefaultModule(name);		
			}
		}
		if (result == null) {
			throw new ElementNotFoundException("module_not_found", name);
		}
		return result;
	}

	private boolean existsModel(String name) throws XavaException {
		return MetaComponent.exists(name);
	}
	
	private MetaModule createDefaultModule(String modelName) throws XavaException { 				
		MetaModule module = new MetaModule();
		module.setMetaApplication(this);
		module.setName(modelName);			
		module.setModelName(modelName);
		/* tmr
		if (MetaControllers.contains(modelName)) {
			module.addControllerName(modelName);
		}
		else {
			for (Iterator it = getControllersForDefaultModule().iterator(); it.hasNext();) {
				module.addControllerName((String) it.next()); 
			}
		}
		*/
		generateDefaultControllers(module); // tmr
		module.setGeneratedByDefault(true); // tmr
		metaModules.put(modelName, module);
		return module;		
	}

	private void generateDefaultControllers(MetaModule module) { // tmr
		module.clearControllers();
		if (MetaControllers.contains(module.getModelName())) {
			module.addControllerName(module.getModelName());
		}
		else {
			for (Iterator it = getControllersForDefaultModule().iterator(); it.hasNext();) {
				module.addControllerName((String) it.next()); 
			}
		}
	}
	
	public void addControllerForDefaultModule(String controllerName) { 
		if (controllersForDefaultModule == null) controllersForDefaultModule = new ArrayList();
		controllersForDefaultModule.add(controllerName);
	}

	private Collection getControllersForDefaultModule() { 
		if (controllersForDefaultModule == null) return Collections.EMPTY_LIST;
		return controllersForDefaultModule;
	}

	public String toString() {
		return "Application: " + getName(); 
	}

	public String getId() {
		return getName();
	}
	
	private static int getControllersCacheVersion() { // tmr En otros sitios, refactorizar 
		// tmr Esto tendría que estar desactivado en producción
		try {
			Method getControllersCacheVersion = MetaController.class.getClassLoader().getParent().loadClass(OpenXavaPlugin.class.getName())
					.getDeclaredMethod("getControllersCacheVersion");
			return (Integer) getControllersCacheVersion.invoke(null);
		} catch (Exception ex) {
			ex.printStackTrace(); // tmr i18n ¿Quitar?
			return -1;
		}
	}
	
}


