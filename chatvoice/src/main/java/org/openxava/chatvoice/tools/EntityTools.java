package org.openxava.chatvoice.tools;

import java.util.*;
import javax.servlet.http.HttpSession;

import org.openxava.controller.*;
import org.openxava.tab.Tab;
import org.openxava.application.meta.MetaModule;
import org.openxava.component.MetaComponent;
import com.openxava.naviox.Modules;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

/**
 * Generic tools for accessing OpenXava entity data using LangChain4j.
 * Works with any entity in any OpenXava application.
 * 
 * @author Javier Paniza
 */
public class EntityTools {

	// tmr Umbral de 600 registros para window de 128k. 600 para ser mútiplo de 120. ¿Hacerlo configurable?
	private final static int MAX_RECORDS = 600;
	
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
	 * Returns the list of available properties for an entity.
	 * Use this to know which properties can be used in conditions.
	 * 
	 * @param entity The entity name (e.g., "Customer", "Invoice", "Product")
	 * @return A list of property names available for the entity
	 */
	@Tool("Get the list of available properties for an entity. Use this before building conditions to know which property names you can use wrapped in ${}.")
	public List<String> getEntityProperties(@P("entity") String entity) {
		System.out.println("[TOOL] getEntityProperties(entity=" + entity + ") called");
		try {
			Tab tab = getTab(entity);
			List<String> properties = new ArrayList<>();
			for (org.openxava.model.meta.MetaProperty p : tab.getMetaProperties()) {
				properties.add(p.getQualifiedName());
			}
			System.out.println("[TOOL] getEntityProperties(entity=" + entity + ") returning: " + properties);
			return properties;
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
	public long getEntityCount(@P("entity") String entity) {
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
	 * Returns the first 600 records from an entity.
	 * 
	 * @param entity The entity name (e.g., "Customer", "Invoice", "Product")
	 * @return A list with up to 600 records
	 */
	@Tool("Get the first 600 records from an entity. Use this for small entities or to get a sample. For large entities use findEntitiesByCondition to filter results.")
	public List<Map<String, Object>> findFirst600Entities(@P("entity") String entity) {
		System.out.println("[TOOL] findFirst600Entities(entity=" + entity + ") called");
		return findEntities(entity, null);
	}
	
	/**
	 * Returns records from an entity that match a SQL-style condition.
	 * The condition uses property names wrapped in ${}, e.g., "${name} = 'John'" or "${price} > 100".
	 * 
	 * @param entity The entity name (e.g., "Customer", "Invoice", "Product")
	 * @param condition SQL-style condition with property names in ${}, e.g., "${status} = 'active' AND ${amount} > 1000"
	 * @return A list of records matching the condition (up to 600)
	 */
	@Tool("Get records from an entity that match a condition. Specify the entity name and a SQL-style condition where property names are wrapped in ${}, like: ${name} = 'John' or ${price} > 100 AND ${active} = true. Returns up to 600 records.")
	public List<Map<String, Object>> findEntitiesByCondition(@P("entity") String entity, @P("condition") String condition) {
		System.out.println("[TOOL] findEntitiesByCondition(entity=" + entity + ", condition=" + condition + ") called");
		return findEntities(entity, condition);
	}
	
	private List<Map<String, Object>> findEntities(String entity, String condition) {
		try {
			Tab tab = getTab(entity);
			tab.setBaseCondition(condition);
			List<Map<String, Object>> records = new ArrayList<>();
			
			// tmr Optimizar para que cargue los 600 primeros registros de un golpe
			javax.swing.table.TableModel tableModel = tab.getTableModel();
			int columnCount = tableModel.getColumnCount();
			
			for (int row = 0; row < tableModel.getRowCount() && row < MAX_RECORDS; row++) {
				Map<String, Object> record = new HashMap<>();
				for (int col = 0; col < columnCount; col++) {
					String columnName = tableModel.getColumnName(col);
					Object value = tableModel.getValueAt(row, col);
					record.put(columnName, value);
				}
				records.add(record);
			}
			
			System.out.println("[TOOL] findEntities(entity=" + entity + ") returning " + records.size() + " records");
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
