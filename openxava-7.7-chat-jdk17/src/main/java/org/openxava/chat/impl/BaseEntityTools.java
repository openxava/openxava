package org.openxava.chat.impl;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.*;
import org.openxava.application.meta.*;
import org.openxava.component.MetaComponent;
import org.openxava.controller.*;
import org.openxava.tab.Tab;
import org.openxava.util.XavaResources;
import org.openxava.view.View;

import com.openxava.naviox.Modules;

/**
 * Base class with common functionality for EntityTools and EntityModifyTools.
 * Provides shared methods for creating View and Tab instances with proper XavaPro permissions support.
 * 
 * @author Javier Paniza
 * @since 7.7
 */
public abstract class BaseEntityTools {
	
	private static Log log = LogFactory.getLog(BaseEntityTools.class);

	protected ModuleContext context;
	protected HttpSession session;
	protected String application;
	protected Map<String, Tab> tabs = new HashMap<>();
	protected Map<String, View> views = new HashMap<>();
	
	/**
	 * Constructor that receives the ModuleContext, HttpSession and application name.
	 * 
	 * @param context The ModuleContext to access session and application context
	 * @param session The HttpSession to assign to the manager
	 * @param application The application name
	 */
	public BaseEntityTools(ModuleContext context, HttpSession session, String application) {
		this.context = context;
		this.session = session;
		this.application = application;
	}
	
	/**
	 * Gets a Tab from the private map and creates it if it doesn't exist.
	 * 
	 * @param entity The entity name
	 * @return The Tab
	 */
	protected Tab getTab(String entity) {
		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null.");
		}

		Tab tab = tabs.get(entity);
		if (tab == null) {
			checkEntityAvailable(entity);
			tab = new Tab();
			// This code is also in execute.jsp, should we refactor?
			ModuleManager manager = (ModuleManager) context.get(application, entity, "manager", "org.openxava.controller.ModuleManager");
			manager.setSession(session);
			log.debug("BaseEntityTools.getTab() application=" + application + ", entity=" + entity);
			manager.setApplicationName(application);
			manager.setModuleName(entity);
			tab.setModelName(manager.getModelName());
			if (tab.getTabName() == null) { 
				tab.setTabName(manager.getTabName());
			}
			tab.setModuleManager(manager); // In this point so the Tab.refine() is not done twice
			tab.setPropertiesNames("*");
			tabs.put(entity, tab);
		}
		tab.reset();
		return tab;
	}
	
	/**
	 * Gets a View from the private map and creates it if it doesn't exist.
	 * 
	 * @param module The module name
	 * @return The View
	 */
	protected View getView(String module) {
		View view = views.get(module);
		if (view == null) {
			checkEntityAvailable(module);
			view = new View();
			ModuleManager manager = (ModuleManager) context.get(application, module, "manager", "org.openxava.controller.ModuleManager");
			manager.setSession(session);
			manager.setApplicationName(application);
			manager.setModuleName(module);
			view.setModuleManager(manager);
			view.setModelName(manager.getModelName());
			view.setViewName(manager.getXavaViewName());
			view.setRequest(null);
			views.put(module, view);
		}
		return view;
	}
	
	private void checkEntityAvailable(String entity) {
		Set<String> availableEntities = getAvailableEntityNames();
		if (!availableEntities.isEmpty() && !availableEntities.contains(entity)) {
			throw new SecurityException(XavaResources.getString("module_not_available_for_user", entity));
		}
	}
	
	/**
	 * Returns the set of entity names the current user has access to.
	 * Uses Modules from session to get the list.
	 * Excludes transient entities and removes duplicates.
	 * 
	 * @since 7.7.1
	 */
	protected Set<String> getAvailableEntityNames() {
		try {
			Modules modules = (Modules) session.getAttribute("modules");
			if (modules == null) {
				modules = new Modules();
				session.setAttribute("modules", modules);
			}
			List<MetaModule> allModules = modules.getAll(null);
			Set<String> result = new LinkedHashSet<>();
			for (MetaModule module : allModules) {
				String modelName = module.getModelName();
				if (modelName == null) continue;
				try {
					MetaComponent component = MetaComponent.get(modelName);
					if (component.isTransient()) {
						continue;
					}
				} catch (Exception ex) {
					// If we can't get the component, include it
				}
				result.add(modelName);
			}
			return result;
		} catch (Exception ex) {
			log.warn(XavaResources.getString("could_not_get_available_entities", ex.getMessage()));
			return Collections.emptySet();
		}
	}
	
}
