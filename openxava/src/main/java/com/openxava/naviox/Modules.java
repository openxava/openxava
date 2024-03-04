/*
 * NaviOX. Navigation and security for OpenXava applications.
 * Copyright 2014 Javier Paniza Lucas
 *
 * License: Apache License, Version 2.0.	
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.openxava.naviox;

import java.io.*;
import java.util.*;
import java.util.prefs.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.application.meta.*;
import org.openxava.util.*;
import org.openxava.view.*;

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
	private final static int MODULES_ON_TOP = 20; 
	private final static ModuleComparator comparator = new ModuleComparator();
	private static String preferencesNodeName = null; 
	private List<MetaModule> all;
	private List<MetaModule> bookmarkModules = null;
	private List<MetaModule> regularModules; 
	private List<MetaModule> fixedModules;
	private List<MetaModule> notInMenuModules;
	private List<MetaModule> topModules = null; 
	private int fixedModulesCount = 0;  
	
	private MetaModule current;

	public static void init(String applicationName) {
		MetaModuleFactory.setApplication(applicationName);
		DBInitializer.init();
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
		topModules = null; 
		bookmarkModules = null;
		fixedModules = null; 
		regularModules = null; 
		current = null; 		
		clearCurrent(); 
	}
	
	/** @since 7.2.3 */
	public static void clearCurrent() { 
		if (!NaviOXPreferences.getInstance().isStartInLastVisitedModule()) {
			try {
				getPreferences().remove("current"); 
			}
			catch (BackingStoreException ex) {
				log.warn(XavaResources.getString("current_module_problem"), ex);
			}
		}
	}
	
	public boolean hasModules(HttpServletRequest request) {  
		return (NaviOXPreferences.getInstance().isShowModulesMenuWhenNotLogged() || Users.getCurrent() !=null) && !getAll(request).isEmpty();  
	}
	
	private MetaModule createWelcomeModule(MetaApplication app) {
		MetaModule result = new MetaModule();
		result.setName("Welcome");				
		result.setWebViewURL("naviox/welcome");		
		return result;
	}


	public void setCurrent(HttpServletRequest request, String application, String module) {
		System.out.println("setCurrent" + application + module);
		this.current = MetaModuleFactory.create(application, module);
		if (topModules == null) loadTopModules(request);	
		int idx = indexOf(topModules, current);
		boolean retainOrder = "true".equals(request.getParameter("retainOrder")); 
		if (idx < 0) {
			if (topModules.size() >= MODULES_ON_TOP) {
				topModules.remove(topModules.size() - 1); 
			}				
			topModules.add(fixedModulesCount, current); 
		}		
		else if (!retainOrder && idx >= fixedModulesCount) { 
			topModules.remove(idx);
			topModules.add(fixedModulesCount, current); 
		}
		storeTopModules();		
	}
	
	public void setCurrent(HttpServletRequest request) { 
		setCurrent(request, request.getParameter("application"), request.getParameter("module"));
	}
	
	private void loadTopModules(HttpServletRequest request) { 
		topModules = new ArrayList<MetaModule>();
		loadFixedModules(request, topModules);
		if (NaviOXPreferences.getInstance().isRememberVisitedModules()) { 
			loadModulesFromPreferences(request, topModules, "", MODULES_ON_TOP);	
		}
	}
	
	private void storeTopModules() { 
		storeModulesInPreferences(topModules, "", MODULES_ON_TOP, true);
	}
	
	private void loadFixedModules(HttpServletRequest request, Collection<MetaModule> modules) {  
		String fixedModules = NaviOXPreferences.getInstance().getFixModulesOnTopMenu();
		fixedModulesCount = 0; 
		if (Is.emptyString(fixedModules)) return;
		for (String moduleName: Strings.toCollection(fixedModules)) {
			if (loadModule(request, modules, moduleName)) fixedModulesCount++;												
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
			return current == null?getPreferences().get("current", NaviOXPreferences.getInstance().getInitialModule()):current;
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("current_module_problem"), ex); 
			return NaviOXPreferences.getInstance().getInitialModule(); 
		}
	}

	/** @since 6.0 */
	public String getOrganizationName(HttpServletRequest request) {  
		try {
			String organization = OrganizationsCurrent.getName(request); 	
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

	public void bookmarkCurrentModule(HttpServletRequest request) {  
		if (indexOf(fixedModules, current) >= 0) return; 
		if (current != null && "Index".equals(current.getName())) return;
		if (bookmarkModules == null) loadBookmarkModules(request); 	
		int idx = indexOf(bookmarkModules, current); 
		if (idx < 0) {
			bookmarkModules.add(current);
			if (regularModules != null) regularModules.remove(current);  
		}		
		storeBookmarkModules();
	}
	
	public void unbookmarkCurrentModule(HttpServletRequest request) {  
		if (bookmarkModules == null) loadBookmarkModules(request); 	
		int idx = indexOf(bookmarkModules, current); 
		if (idx >= 0) {
			bookmarkModules.remove(idx);
		}		
		storeBookmarkModules();
	}
	
	public boolean isCurrentBookmarked(HttpServletRequest request) { 
		return isBookmarked(request, current); 
	}
	
	public boolean isBookmarked(HttpServletRequest request, MetaModule module) { 
		if (bookmarkModules == null) loadBookmarkModules(request); 
		return indexOf(bookmarkModules, module) >= 0;
	}
		
	private void loadFixedModules(HttpServletRequest request) {  
		String fixedModulesOnMenu = NaviOXPreferences.getInstance().getFixModulesOnTopMenu();
		fixedModules = new ArrayList();
		if (Is.emptyString(fixedModulesOnMenu)) return;
		for (String moduleName: Strings.toCollection(fixedModulesOnMenu)) {
			loadModule(request, fixedModules, moduleName); 												
		}
	}

	private boolean loadModule(HttpServletRequest request, Collection<MetaModule> modules, String moduleName) {   
		try {
			MetaModule module = MetaModuleFactory.create(moduleName);
			if (!modules.contains(module) && isModuleAuthorized(request, module)) { 
				modules.add(module);
				return true; 
			}
		}
		catch (Exception ex) {					
			log.warn(XavaResources.getString("module_not_loaded", moduleName, MetaModuleFactory.getApplication()), ex);
		}
		return false; 
	}

	private void loadBookmarkModules(HttpServletRequest request) { 
		bookmarkModules = new ArrayList<MetaModule>();
		loadModulesFromPreferences(request, bookmarkModules, "bookmark.", BOOKMARK_MODULES); 
		bookmarkModules.removeAll(getFixedModules(request)); 
	}
	
	private void loadModulesFromPreferences(HttpServletRequest request, List<MetaModule> modules, String prefix, int limit) {  
		try {
			Preferences preferences = getPreferences();
			System.out.println("loadModulesFromPreferences " + prefix);
			for (int i = 0; i < limit; i++) { 
				System.out.println(prefix + "application." + i);
				String applicationName = preferences.get(prefix + "application." + i, null);
				if (applicationName == null) break;
				System.out.println(prefix + "module." + i);
				String moduleName = preferences.get(prefix + "module." + i, null);
				if (moduleName == null) break;
				System.out.println("no null " + moduleName);
				System.out.println(modules);
				loadModule(request, modules, moduleName); 
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
			if (uri.length < 3) {
				return false;			
			}
			String applicationName = MetaModuleFactory.getApplication(); 
			String moduleName = uri[uri.length - 1]; 
			if (moduleName.equals("SignIn")) return true; 
			return isModuleAuthorized(request, MetaModuleFactory.create(applicationName, moduleName)); 
		}
		catch (Exception ex) {			
			log.warn(XavaResources.getString("module_not_authorized"), ex); 
			return false;
		}
			
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
		String organization = OrganizationsCurrent.get(request); 
		String prefix = organization == null?"":"/o/" + organization;
		return request.getContextPath() + prefix + "/m/" + module.getName();
	}
		
	public boolean isModuleAuthorized(HttpServletRequest request, MetaModule module) {   
		if (request != null && ModulesHelper.isPublic(request, module.getName())) return true; 
		return Collections.binarySearch(getAll(request), module, comparator) >= 0; 
	}
	
	/** @since 6.5 */
	public boolean isModuleInMenu(HttpServletRequest request, MetaModule module) { 	
		if (!isModuleAuthorized(request, module)) return false;
		return Collections.binarySearch(getNotInMenuModules(), module, comparator) < 0;
	}
	
	private void storeBookmarkModules() {  
		storeModulesInPreferences(bookmarkModules, "bookmark.", BOOKMARK_MODULES, false); 
	}
	
	private void storeModulesInPreferences(Collection<MetaModule> modules, String prefix, int limit, boolean storeCurrent) {   
		try {			
			Preferences preferences = getPreferences();
			int i=0;
			MetaModule firstSteps = null;
			for (MetaModule module: modules) {			
				if (module.getName().equals(FIRST_STEPS)) {
					firstSteps = module;
					continue; 
				}
				preferences.put(prefix + "application." + i, module.getMetaApplication().getName());
				preferences.put(prefix + "module." + i, module.getName());
				i++;
			}
			for (; i < limit; i++) {				
				preferences.remove(prefix + "application." + i);
				preferences.remove(prefix + "module." + i);
			}
			if (storeCurrent && !"SignIn".equals(current.getName())) {
				preferences.put("current", current.getName());
			}
			if (firstSteps != null && (current == null || !current.getName().equals(FIRST_STEPS))) { 
				modules.remove(firstSteps); 
			}
			
			preferences.flush();
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("storing_modules_problem"), ex);  
		}
	}



	private static Preferences getPreferences() throws BackingStoreException { 
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
	
	public Collection getBookmarkModules(HttpServletRequest request) {  
		if (bookmarkModules == null) loadBookmarkModules(request);
		return bookmarkModules;
	}
	
	/** @since 6.0 */
	public Collection getFixedModules(HttpServletRequest request) {  
		if (fixedModules == null) loadFixedModules(request); 
		return fixedModules;
	}
	
	public List getAll(HttpServletRequest request) { 
		if (all == null) {			
			all = ModulesHelper.getAll(request); 
			Collections.sort(all, comparator);
		}
		return all;
	}
	
	private List<MetaModule> getNotInMenuModules() { 
		if (notInMenuModules == null) {			
			notInMenuModules = ModulesHelper.getNotInMenu(); 
			Collections.sort(notInMenuModules, comparator);
		}
		return notInMenuModules;
	}
	
	/** @since 6.0 */
	public List getRegularModules(HttpServletRequest request) {  
		if (getBookmarkModules(request).isEmpty() && getFixedModules(request).isEmpty()) return getAll(request);  
		if (regularModules == null) {			
			regularModules = new ArrayList(getAll(request));  
			regularModules.removeAll(getBookmarkModules(request)); 
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
	
	public Collection<MetaModule> getTopModules() { 
		return topModules;	
	}
	
	/** @since 7.0 */
	public Collection<MetaModule> getMenuModules(HttpServletRequest request) { 
		return ModulesHelper.getInMenu(request, this);
	}
	
	private static class ModuleComparator implements Comparator<MetaModule> {

		public int compare(MetaModule a, MetaModule b) {			
			return a.getName().compareTo(b.getName());
		}
		
	}
	
	public void removeCurrentModule(int index, HttpServletRequest request, String application, View view) {
		topModules.remove(index);
		System.out.println("current");
		System.out.println(getCurrent(request)); 
		//this.current = MetaModuleFactory.create(application, topModules.get(0).getModelName());
		setCurrent(request, application, topModules.get(0).getModelName());
		storeTopModules();
		view.setModel(topModules.get(index).getModelName());
	}
	
	public void removeModule(int index) {
		System.out.println(topModules.get(index).getDescription());
		System.out.println(topModules.get(index).getName());
		System.out.println(topModules.get(index).getModelName());
		System.out.println(topModules.get(index).getViewName());
		topModules.remove(index);
		storeTopModules();
	}

}
