package com.openxava.naviox;

import java.io.*;
import java.util.*;
import java.util.prefs.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.application.meta.*;
import org.openxava.util.*;

import com.openxava.naviox.impl.*;
import com.openxava.naviox.util.*;

/**
 * 
 * @author Javier Paniza 
 */
public class Modules implements Serializable {
	
	public final static String FIRST_STEPS = "FirstSteps";
	
	private static Log log = LogFactory.getLog(Modules.class);
	private final static int BOOKMARK_MODULES = 100;
	private final static ModuleComparator comparator = new ModuleComparator();
	private static String preferencesNodeName = null; 
	private List<MetaModule> all;
	private List<MetaModule> bookmarkModules = null;
	private List<MetaModule> regularModules; 
	private List<MetaModule> fixedModules; 
	
	private MetaModule current;

	 

	public static void init(String applicationName) {
		MetaModuleFactory.setApplication(applicationName);
		DB.init();
		createFirstStepsModule(applicationName);
		ModulesHelper.init(applicationName);  
	}	
	
	private static void createFirstStepsModule(String applicationName) {
		MetaApplication app = MetaApplications.getMetaApplication(applicationName);
		MetaModule firstStepsModule = new MetaModule();
		firstStepsModule.setName(FIRST_STEPS);
		firstStepsModule.setModelName("SignIn"); // The model does not matter
		firstStepsModule.setWebViewURL("/naviox/firstSteps.jsp");
		firstStepsModule.setModeControllerName("Void");
		app.addMetaModule(firstStepsModule);		
	}
		
	public void reset() {
		all = null;
		bookmarkModules = null;
		fixedModules = null; 
		regularModules = null; 
		current = null; 		
		if (!NaviOXPreferences.getInstance().isStartInLastVisitedModule()) {
			try {
				getPreferences().remove("current");
			}
			catch (BackingStoreException ex) {
				log.warn(XavaResources.getString("current_module_problem"), ex);
			}
		}
	}
	
	public boolean hasModules() { 
		return (NaviOXPreferences.getInstance().isShowModulesMenuWhenNotLogged() || Users.getCurrent() !=null) && !getAll().isEmpty(); 
	}
	
	private MetaModule createWelcomeModule(MetaApplication app) {
		MetaModule result = new MetaModule();
		result.setName("Welcome");				
		result.setWebViewURL("naviox/welcome");		
		return result;
	}


	public void setCurrent(String application, String module) { 
		this.current = MetaModuleFactory.create(application, module);
		try {			
			Preferences preferences = getPreferences();
			if (!"SignIn".equals(current.getName())) {
				preferences.put("current", current.getName());
			}
			
			preferences.flush();
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("storing_current_module_problem"), ex);   
		}
	}
		
	public boolean showsIndexLink() { 
		return ModulesHelper.showsIndexLink(); 
	}
	
	public boolean showsSearchModules(HttpServletRequest request) { 
		return ModulesHelper.showsSearchModules(request); 
	}	

	public String getCurrent(HttpServletRequest request) { 
		try {
			String current = ModulesHelper.getCurrent(request);
			return current == null?getPreferences().get("current", FIRST_STEPS):current;
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("current_module_problem"), ex); 
			return FIRST_STEPS;
		}
	}

	/** @since 6.0 */
	public String getOrganizationName(HttpServletRequest request) {  
		try {
			String organization = Organizations.getCurrentName(request); 	
			return organization == null?"":organization; 
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("organization_name_problem"), ex); 			
			return XavaResources.getString("unknow_organization"); 
		}
	}

	
	public String getApplicationLabel(HttpServletRequest request) { 
		try {
			return NaviOXPreferences.getInstance().isShowApplicationName()?current.getMetaApplication().getLabel():"";
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("application_name_problem"), ex); 	
			return XavaResources.getString("unknow_application"); 
		}
	}
		
	public String getCurrentModuleDescription(HttpServletRequest request) {
		try {
			return Strings.concat(" - ", getOrganizationName(request), getApplicationLabel(request), current.getLabel());
		}
		catch (Exception ex) { 
			log.warn(XavaResources.getString("module_description_problem"), ex);			
			return XavaResources.getString("unknow_module");
		}
	}
	
	public String getCurrentModuleLabel() {  
		return current == null?XavaResources.getString("unknow_module"):current.getLabel(); 
	}
	
	public String getCurrentModuleName() { 
		return current == null?null:current.getName(); 
	}

	public void bookmarkCurrentModule() { 
		if (indexOf(fixedModules, current) >= 0) return; 
		if (current != null && "Index".equals(current.getName())) return;
		if (bookmarkModules == null) loadBookmarkModules();	
		int idx = indexOf(bookmarkModules, current); 
		if (idx < 0) {
			bookmarkModules.add(current);
			if (regularModules != null) regularModules.remove(current);  
		}		
		storeBookmarkModules();
	}
	
	public void unbookmarkCurrentModule() { 
		if (bookmarkModules == null) loadBookmarkModules();	
		int idx = indexOf(bookmarkModules, current); 
		if (idx >= 0) {
			bookmarkModules.remove(idx);
		}		
		storeBookmarkModules();
	}
	
	public boolean isCurrentBookmarked() {
		return isBookmarked(current);
	}
	
	public boolean isBookmarked(MetaModule module) {
		if (bookmarkModules == null) loadBookmarkModules();
		return indexOf(bookmarkModules, module) >= 0;
	}
		
	private void loadFixedModules() { 
		String fixedModulesOnMenu = NaviOXPreferences.getInstance().getFixModulesOnTopMenu();
		fixedModules = new ArrayList();
		if (Is.emptyString(fixedModulesOnMenu)) return;
		for (String moduleName: Strings.toCollection(fixedModulesOnMenu)) {
			loadModule(fixedModules, moduleName);												
		}
	}

	private void loadModule(Collection<MetaModule> modules, String moduleName) {  
		try {
			MetaModule module = MetaModuleFactory.create(moduleName);
			if (!modules.contains(module) && isModuleAuthorized(module)) { 
				modules.add(module);
			}
		}
		catch (Exception ex) {					
			log.warn(XavaResources.getString("module_not_loaded", moduleName, MetaModuleFactory.getApplication()), ex);
		}
	}

	private void loadBookmarkModules() {  
		bookmarkModules = new ArrayList<MetaModule>();
		loadModulesFromPreferences(bookmarkModules, "bookmark.", BOOKMARK_MODULES);
		bookmarkModules.removeAll(getFixedModules()); 
	}
	
	private void loadModulesFromPreferences(List<MetaModule> modules, String prefix, int limit) { 
		try {
			Preferences preferences = getPreferences();
			for (int i = 0; i < limit; i++) { 
				String applicationName = preferences.get(prefix + "application." + i, null);
				if (applicationName == null) break;
				String moduleName = preferences.get(prefix + "module." + i, null);
				if (moduleName == null) break;				
				loadModule(modules, moduleName); 
			}		
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("loading_modules_problem"), ex); 
		}
	}
	
				
	public boolean isModuleAuthorized(HttpServletRequest request) {
		try {
			if (request.getRequestURI().contains("module.jsp")) return false;
			if (Users.getCurrent() == null && request.getRequestURI().contains("/phone/")) return false; 
			if (!(request.getRequestURI().startsWith(request.getContextPath() + "/m/") ||
					request.getRequestURI().startsWith(request.getContextPath() + "/p/") || 
					request.getRequestURI().startsWith(request.getContextPath() + "/modules/"))) return true;
			String [] uri = request.getRequestURI().split("/");
			if (uri.length < 4) return false;			
			return isModuleAuthorized(request, MetaModuleFactory.create(uri[1], uri[3])); 
		}
		catch (Exception ex) {			
			log.warn(XavaResources.getString("module_not_authorized"), ex); 
			return false;
		}
			
	}
	
	/**
	 * @since 5.7
	 */
	public boolean isModuleAuthorized(String module) { 	
		return isModuleAuthorized(null, module); 			
	}
	
	/**
	 * @since 5.7
	 */
	public boolean isModuleAuthorized(HttpServletRequest request, String module) {
		try {		
			return isModuleAuthorized(request, MetaModuleFactory.create(module)); 
		}
		catch (Exception ex) {			
			log.warn(XavaResources.getString("module_not_authorized"), ex); 
			return false;
		}
			
	}
	
	public String getModuleURI(HttpServletRequest request, MetaModule module) { 
		String organization = Organizations.getCurrent(request); 
		String prefix = organization == null?"":"/o/" + organization;
		return "/" + module.getMetaApplication().getName() + prefix + "/m/" + module.getName();
	}
	
	boolean isModuleAuthorized(MetaModule module) {
		return isModuleAuthorized(null, module); 
	}
	
	private boolean isModuleAuthorized(HttpServletRequest request, MetaModule module) {   
		if (request != null && ModulesHelper.isPublic(request, module.getName())) return true; 
		return Collections.binarySearch(getAll(), module, comparator) >= 0;
	}
	
	private void storeBookmarkModules() { 
		storeModulesInPreferences(bookmarkModules, "bookmark.", BOOKMARK_MODULES); 
	}
	
	private void storeModulesInPreferences(Collection<MetaModule> modules, String prefix, int limit) { 
		try {			
			Preferences preferences = getPreferences();
			int i=0;
			for (MetaModule module: modules) {				
				preferences.put(prefix + "application." + i, module.getMetaApplication().getName());
				preferences.put(prefix + "module." + i, module.getName());
				i++;
			}
			for (; i < limit; i++) {				
				preferences.remove(prefix + "application." + i);
				preferences.remove(prefix + "module." + i);
			}
			
			preferences.flush();
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("storing_modules_problem"), ex);  
		}
	}


	private Preferences getPreferences() throws BackingStoreException { 
		return Users.getCurrentPreferences().node(getPreferencesNodeName());
	}
	
	private static String getPreferencesNodeName() { 
		if (preferencesNodeName == null) {
			Collection<MetaApplication> apps = MetaApplications.getMetaApplications();
			for (MetaApplication app: apps) {
				preferencesNodeName = "naviox." + app.getName();
				break;
			}
			if (preferencesNodeName == null) preferencesNodeName = "naviox.UNKNOWN"; 
		}
		return preferencesNodeName;
	}
	
	public Collection getBookmarkModules() { 
		if (bookmarkModules == null) loadBookmarkModules();
		return bookmarkModules;
	}
	
	/** @since 6.0 */
	public Collection getFixedModules() { 
		if (fixedModules == null) loadFixedModules();
		return fixedModules;
	}
	
	public List getAll() {
		if (all == null) {			
			all = ModulesHelper.getAll();
			Collections.sort(all, comparator);
		}
		return all;
	}
	
	/** @since 6.0 */
	public List getRegularModules() { 
		if (getBookmarkModules().isEmpty() && getFixedModules().isEmpty()) return getAll(); 
		if (regularModules == null) {			
			regularModules = new ArrayList(getAll());
			regularModules.removeAll(getFixedModules());
			regularModules.removeAll(getBookmarkModules());
		}
		return regularModules;
	}

	public String getUserAccessModule(ServletRequest request) { 
		return ModulesHelper.getUserAccessModule(request);
	}

	private int indexOf(Collection<MetaModule> modules, MetaModule current) { 
		if (modules == null) return -1; 
		int idx = 0;
		for (MetaModule module: modules) {
			if (module.getName().equals(current.getName()) &&
					module.getMetaApplication().getName().equals(current.getMetaApplication().getName())) return idx;
			idx++;
		}
		return -1;
	}
	
	private static class ModuleComparator implements Comparator<MetaModule> {

		public int compare(MetaModule a, MetaModule b) {			
			return a.getName().compareTo(b.getName());
		}
		
	}

}
