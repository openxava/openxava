package org.openxava.chatvoice.tools;

import java.util.*;
import javax.servlet.http.HttpSession;

import org.openxava.controller.*;
import org.openxava.tab.Tab;
import org.openxava.application.meta.MetaModule;
import org.openxava.component.MetaComponent;
import com.openxava.naviox.Modules;

import dev.langchain4j.agent.tool.Tool;

/**
 * Generic tools for accessing OpenXava entity data using LangChain4j.
 * Works with any entity in any OpenXava application.
 * 
 * @author Javier Paniza
 */
public class EntityTools {
	
	private ModuleContext context;
	private HttpSession session;
	private String application;
	private Map<String, Tab> tabs = new HashMap<>();
	
	/**
	 * Constructor that receives the ModuleContext, HttpSession and application name.
	 * 
	 * @param context The ModuleContext to access session and application context
	 * @param session The HttpSession to assign to the manager
	 * @param application The application name
	 */
	public EntityTools(ModuleContext context, HttpSession session, String application) {
		this.context = context;
		this.session = session;
		this.application = application;
	}
	
	/**
	 * Returns the list of available entities in the application.
	 * Only returns modules that the current user has permission to access.
	 * 
	 * @return A list of entity names the user can access
	 */
	@Tool("Get the list of available entities in the application. Use this when you need to know what data is available. Only returns entities the current user has permission to access.")
	public List<String> getAvailableEntities() {
		System.out.println("[TOOL] getAvailableEntities() called");
		try {
			// Get the Modules object from session (uses cache)
			Modules modules = (Modules) session.getAttribute("modules");
			if (modules == null) {
				modules = new Modules();
				session.setAttribute("modules", modules);
			}
			
			// Get all modules respecting user security
			List<String> entityNames = new ArrayList<>();
			List<MetaModule> allModules = modules.getAll(null);
			for (MetaModule module : allModules) {
				// Exclude transient modules
				try {
					String modelName = module.getModelName();
					if (modelName != null) {
						MetaComponent component = MetaComponent.get(modelName);
						if (component.isTransient()) {
							continue; // Skip transient entities
						}
					}
				} catch (Exception ex) {
					// If we can't get the component, skip this module
					continue;
				}
				entityNames.add(module.getName());
			}
			
			System.out.println("[TOOL] getAvailableEntities() returning: " + entityNames);
			return entityNames;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
	
	/**
	 * Returns the total number of records in an entity.
	 * 
	 * @param entity The entity name (e.g., "Customer", "Invoice", "Product")
	 * @return The count of records in the entity
	 */
	@Tool("Get the total number of records in an entity. Specify the entity name like Customer, Invoice, or Product.")
	public long getEntityCount(String entity) {
		System.out.println("[TOOL] getEntityCount(entity=" + entity + ") called");
		try {
			long result = getTab(entity).getTotalSize();
			System.out.println("[TOOL] getEntityCount(entity=" + entity + ") returning: " + result);
			return result;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
	
	/**
	 * Returns all records from an entity using the Tab.
	 * 
	 * @param entity The entity name (e.g., "Customer", "Invoice", "Product")
	 * @return A list of all records with their details
	 */
	@Tool("Get all records from an entity. Specify the entity name like Customer, Invoice, or Product. Returns the visible data, respecting any filters applied by the user.")
	public List<Map<String, Object>> getAllFromEntity(String entity) {
		System.out.println("[TOOL] getAllFromEntity(entity=" + entity + ") called");
		try {
			Tab tab = getTab(entity);
			List<Map<String, Object>> records = new ArrayList<>();
			
			// Get the TableModel from the Tab
			javax.swing.table.TableModel tableModel = tab.getTableModel();
			
			int columnCount = tableModel.getColumnCount();
			
			// Iterate over all rows
			for (int row = 0; row < tableModel.getRowCount(); row++) {
				Map<String, Object> record = new HashMap<>();
				
				// Get all available columns
				for (int col = 0; col < columnCount; col++) {
					String columnName = tableModel.getColumnName(col);
					Object value = tableModel.getValueAt(row, col);
					record.put(columnName, value);
				}
				
				records.add(record);
			}
			
			System.out.println("[TOOL] getAllFromEntity(entity=" + entity + ") returning " + records.size() + " records");
			return records;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
	
	/**
	 * Gets a Tab from the private map and creates it if it doesn't exist.
	 * 
	 * @param module The module name
	 * @return The Tab
	 */
	private Tab getTab(String module) {
		Tab tab = tabs.get(module);
		if (tab == null) {
			tab = new Tab();
			// This code is also in execute.jsp, should we refactor?
			ModuleManager manager = (ModuleManager) context.get(application, module, "manager", "org.openxava.controller.ModuleManager");
			manager.setSession(session);
			manager.setApplicationName(application);
			manager.setModuleName(module);
			tab.setModelName(manager.getModelName());
			if (tab.getTabName() == null) { 
				tab.setTabName(manager.getTabName());
			}
			tab.setPropertiesNames("*");
			tabs.put(module, tab);
		}
		return tab;
	}
	
}
