package org.openxava.chat.impl;

import java.util.*;

import org.apache.commons.logging.*;

import javax.servlet.http.HttpSession;

import org.openxava.controller.*;
import org.openxava.view.View;
import org.openxava.model.MapFacade;
import org.openxava.tab.Tab;
import org.openxava.tab.impl.IXTableModel;
import org.openxava.application.meta.MetaModule;
import org.openxava.component.MetaComponent;
import org.openxava.model.meta.MetaCollection;
import org.openxava.model.meta.MetaProperty;
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
	
	private static Log log = LogFactory.getLog(EntityTools.class);

	// Threshold of 600 records for 128k window. 600 to be a multiple of 120.
	private final static int MAX_RECORDS = 600;
	
	private ModuleContext context;
	private HttpSession session;
	private String application;
	private Map<String, Tab> tabs = new HashMap<>();
	private Map<String, View> views = new HashMap<>();
	private Map<String, String> pendingFilterValues = null;
	
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
		long startTime = System.currentTimeMillis();
		log.debug("[TOOL] getAvailableEntities() called");
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
			
			log.debug("[TOOL] getAvailableEntities() returning: " + entityNames);
			log.debug("[TOOL] getAvailableEntities() took " + (System.currentTimeMillis() - startTime) + " ms");
			return entityNames;
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			log.debug("[TOOL] getAvailableEntities() took " + (System.currentTimeMillis() - startTime) + " ms");
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
	public List<String> getEntityProperties(
			@P("The entity name, e.g. Customer, Invoice, Product. Get valid names from getAvailableEntities()") String entity) {
		long startTime = System.currentTimeMillis();
		log.debug("[TOOL] getEntityProperties(entity=" + entity + ") called");
		try {
			Tab tab = getTab(entity);
			List<String> properties = new ArrayList<>();
			for (org.openxava.model.meta.MetaProperty p : tab.getMetaProperties()) {
				properties.add(p.getQualifiedName());
			}
			log.debug("[TOOL] getEntityProperties(entity=" + entity + ") returning: " + properties);
			log.debug("[TOOL] getEntityProperties() took " + (System.currentTimeMillis() - startTime) + " ms");
			return properties;
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			log.debug("[TOOL] getEntityProperties() took " + (System.currentTimeMillis() - startTime) + " ms");
			throw ex;
		}
	}
	
	/**
	 * Returns the list of collections for an entity with their properties.
	 * Use this to know which collections an entity has and what properties each collection contains.
	 * Collections cannot be used in conditions, only to understand the entity structure.
	 * 
	 * @param entity The entity name (e.g., "Customer", "Invoice", "Product")
	 * @return A map where keys are collection names and values are lists of property names in each collection
	 */
	@Tool("Get the collections of an entity with their properties. Use this to understand the entity structure and know what related data exists. IMPORTANT: Collections CANNOT be used in conditions for findEntitiesByCondition, they are only for understanding the data model. To get collection data use getEntityDetails with the hiddenKey of a specific record.")
	public Map<String, List<String>> getEntityCollections(
			@P("The entity name, e.g. Customer, Invoice, Product. Get valid names from getAvailableEntities()") String entity) {
		long startTime = System.currentTimeMillis();
		log.debug("[TOOL] getEntityCollections(entity=" + entity + ") called");
		try {
			View view = getView(entity);
			Map<String, List<String>> collections = new LinkedHashMap<>();
			for (MetaCollection metaCollection : view.getMetaModel().getMetaCollections()) {
				String collectionName = metaCollection.getName();
				List<String> collectionProperties = new ArrayList<>();
				for (org.openxava.model.meta.MetaProperty p : metaCollection.getMetaReference().getMetaModelReferenced().getMetaProperties()) {
					collectionProperties.add(p.getName());
				}
				collections.put(collectionName, collectionProperties);
			}
			log.debug("[TOOL] getEntityCollections(entity=" + entity + ") returning: " + collections);
			log.debug("[TOOL] getEntityCollections() took " + (System.currentTimeMillis() - startTime) + " ms");
			return collections;
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			log.debug("[TOOL] getEntityCollections() took " + (System.currentTimeMillis() - startTime) + " ms");
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
	public long getEntityCount(
			@P("The entity name, e.g. Customer, Invoice, Product. Get valid names from getAvailableEntities()") String entity) {
		long startTime = System.currentTimeMillis();
		log.debug("[TOOL] getEntityCount(entity=" + entity + ") called");
		try {
			long result = getTab(entity).getTotalSize();
			log.debug("[TOOL] getEntityCount(entity=" + entity + ") returning: " + result);
			log.debug("[TOOL] getEntityCount() took " + (System.currentTimeMillis() - startTime) + " ms");
			return result;
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			log.debug("[TOOL] getEntityCount() took " + (System.currentTimeMillis() - startTime) + " ms");
			throw ex;
		}
	}
	
	/**
	 * Returns the first 600 records from an entity.
	 * 
	 * @param entity The entity name (e.g., "Customer", "Invoice", "Product")
	 * @return A list with up to 600 records
	 */
	@Tool("Get the first 600 records from an entity. IMPORTANT: First call getAvailableEntities() to get valid entity names. Use this for small entities or to get a sample. For large entities use findEntitiesByCondition to filter results. Don't show hiddenKey to the user.")
	public List<Map<String, Object>> findFirst600Entities(
			@P("The entity name, e.g. Customer, Invoice, Product. Get valid names from getAvailableEntities()") String entity) {
		long startTime = System.currentTimeMillis();
		log.debug("[TOOL] findFirst600Entities(entity=" + entity + ") called");
		List<Map<String, Object>> result = findEntities(entity, null);
		log.debug("[TOOL] findFirst600Entities() took " + (System.currentTimeMillis() - startTime) + " ms");
		return result;
	}
	
	/**
	 * Returns records from an entity that match a SQL-style condition.
	 * The condition uses property names wrapped in ${}, e.g., "${name} = 'John'" or "${price} > 100".
	 * 
	 * @param entity The entity name (e.g., "Customer", "Invoice", "Product")
	 * @param condition SQL-style condition with property names in ${}, e.g., "${status} = 'active' AND ${amount} > 1000"
	 * @return A list of records matching the condition (up to 600)
	 */
	@Tool("Get records from an entity and return data in the chat. Use this when the user ASKS FOR SPECIFIC DATA (e.g., 'give me the address of customer 2', 'what is the price of product X', 'tell me the total of invoice Y'). Also use this to access collection data by getting the hiddenKey first, then calling getEntityDetails. For simply DISPLAYING/FILTERING the list in the UI without returning data, use filterList instead. Specify the entity name and a SQL-style condition where property names are wrapped in ${}, like: ${name} = 'John' or ${price} > 100. For partial text matching use LIKE with %, e.g. ${description} LIKE '%BMW%'. NEVER use 'contains' - it is not valid SQL/JPQL. Returns up to 600 records. Don't show hiddenKey to the user.")
	public List<Map<String, Object>> findEntitiesByCondition(
			@P("The entity name, e.g. Customer, Invoice, Product. Get valid names from getAvailableEntities()") String entity, 
			@P("SQL-style condition with property names in ${}, e.g. ${name} = 'John' or ${description} LIKE '%BMW%'. Use LIKE with % for partial matching, NEVER use 'contains'.") String condition) {
		long startTime = System.currentTimeMillis();
		log.debug("[TOOL] findEntitiesByCondition(entity=" + entity + ", condition=" + condition + ") called");
		List<Map<String, Object>> result = findEntities(entity, condition);
		log.debug("[TOOL] findEntitiesByCondition() took " + (System.currentTimeMillis() - startTime) + " ms");
		return result;
	}
	
	private List<Map<String, Object>> findEntities(String entity, String condition) {
		try {
			Tab tab = getTab(entity);
			tab.setBaseCondition(condition);
			List<Map<String, Object>> records = new ArrayList<>();
			
			// TODO Optimize to load the first 600 records at once
			IXTableModel tableModel = tab.getTableModel();
			int columnCount = tableModel.getColumnCount();
			
			for (int row = 0; row < tableModel.getRowCount() && row < MAX_RECORDS; row++) {
				Map<String, Object> record = new HashMap<>();				
				for (int col = 0; col < columnCount; col++) {
					String columnName = tab.getMetaProperty(col).getQualifiedName();
					Object value = tableModel.getValueAt(row, col);
					record.put(columnName, value);
				}
				try {
					record.put("hiddenKey", tableModel.getObjectAt(row));
				} catch (javax.ejb.FinderException ex) {
					log.warn(ex.getMessage(), ex); // TODO Handle this better
				}
				records.add(record);
			}
			
			log.debug("[TOOL] findEntities(entity=" + entity + ") returning " + records.size() + " records");
			return records;
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw ex;
		}
	}
	
	/**
	 * Returns the details of an entity given its key.
	 * The key can be obtained from the hiddenKey field returned by findEntities methods.
	 * 
	 * @param entity The entity name (e.g., "Customer", "Invoice", "Product")
	 * @param key The entity key as a Map (obtained from hiddenKey)
	 * @return A map with all the entity details
	 */
	@SuppressWarnings("unchecked")
	@Tool("Get the details of an entity given its key. The key is obtained from the hiddenKey field returned by findFirst600Entities or findEntitiesByCondition. Use this to get complete information about a specific record.")
	public Map<String, Object> getEntityDetails(
			@P("The entity name, e.g. Customer, Invoice, Product") String entity, 
			@P("The entity key obtained from hiddenKey field") Map<String, Object> key) {
		long startTime = System.currentTimeMillis();
		log.debug("[TOOL] getEntityDetails(entity=" + entity + ", key=" + key + ") called");
		try {
			View view = getView(entity);
			Map<String, Object> result = MapFacade.getValues(view.getModelName(), key, view.getMembersNames());
			log.debug("[TOOL] getEntityDetails(entity=" + entity + ") returning: " + result);
			log.debug("[TOOL] getEntityDetails() took " + (System.currentTimeMillis() - startTime) + " ms");
			return result;
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			log.debug("[TOOL] getEntityDetails() took " + (System.currentTimeMillis() - startTime) + " ms");
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * Gets a Tab from the private map and creates it if it doesn't exist.
	 * 
	 * @param entity The entity name
	 * @return The Tab
	 */
	private Tab getTab(String entity) {
		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null. Use any of: " + getAvailableEntities());
		}

		Tab tab = tabs.get(entity);
		if (tab == null) {
			tab = new Tab();
			// This code is also in execute.jsp, should we refactor?
			ModuleManager manager = (ModuleManager) context.get(application, entity, "manager", "org.openxava.controller.ModuleManager");
			manager.setSession(session);
			log.debug("EntityTools.getTab() application=" + application + ", entity=" + entity);
			manager.setApplicationName(application);
			manager.setModuleName(entity);
			tab.setModelName(manager.getModelName());
			if (tab.getTabName() == null) { 
				tab.setTabName(manager.getTabName());
			}
			tab.setModuleManager(manager); // In this point so the Tab.refine() is not done twice
			tab.setPropertiesNames("*");
			System.out.println("[EntityTools.getTab()] v2"); // tmr
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
	private View getView(String module) {
		View view = views.get(module);
		if (view == null) {
			view = new View();
			// TMR PROBARLO CON UN MÓDULO QUE TENGA VIEW CON NOMBRE PARA COMPROBAR QUE NO SE REFINA DOS VECES
			ModuleManager manager = (ModuleManager) context.get(application, module, "manager", "org.openxava.controller.ModuleManager");
			manager.setSession(session);
			manager.setApplicationName(application);
			manager.setModuleName(module);
			view.setModuleManager(manager);
			view.setModelName(manager.getModelName());
			System.out.println("[EntityTools.getView()] manager.getXavaViewName()=" + manager.getXavaViewName()); // tmr
			view.setViewName(manager.getXavaViewName());
			views.put(module, view);
		}
		return view;
	}
	
	
	/**
	 * Returns the pending filter values and clears them.
	 * The keys are in the format "conditionValue___N" for JavaScript.
	 */
	public Map<String, String> consumePendingFilterValues() {
		Map<String, String> result = pendingFilterValues;
		pendingFilterValues = null;
		return result;
	}
	
	private void setupWindowId() {
		context.cleanCurrentWindowId();
	}
	
	/**
	 * Filters the list of the current module by setting condition values for the filterable properties.
	 * 
	 * @param entity The entity name the user is asking about
	 * @param values Map of property names to filter values
	 * @param comparators Map of property names to comparators (optional)
	 * @return A confirmation message or error description
	 */
	@Tool("Filter the visible list in the UI. Use this ONLY when the user wants to VISUALIZE/DISPLAY data in the list (e.g., 'show me customers from Madrid', 'display invoices from 2024', 'filter products by category'). This tool updates the UI but does NOT return data. If the user ASKS FOR SPECIFIC DATA to answer in the chat (e.g., 'give me the address', 'what is the price', 'tell me the total'), use findEntitiesByCondition instead to get the data. Before calling, use getEntityProperties to get the exact property names. If the entity does not match the current module or user is in detail mode, this tool will fail. IMPORTANT: For date values, ALWAYS use ISO format yyyy-MM-dd (e.g., 2024-08-13). Available comparators: For numbers/dates: eq (=, default), ne (<>), gt (>), lt (<), ge (>=), le (<=). For strings: contains (default), starts, ends, not_contains, empty, not_empty. For dates also: year, month, year_month. To clear the filter, call with empty maps.")
	public String filterList(
			@P("The entity the user is asking about, e.g. Invoice, Customer, Product") String entity,
			@P("Map of property names to filter values, e.g. {year: '2023', amount: '60000'}") Map<String, String> values,
			@P("Map of property names to comparators, e.g. {amount: 'gt'} for greater than. Optional, defaults to 'eq' for numbers, 'contains' for strings.") Map<String, String> comparators) {
		long startTime = System.currentTimeMillis();
		log.debug("[TOOL] filterList(entity=" + entity + ", values=" + values + ", comparators=" + comparators + ") called");
		try {
			setupWindowId();
			
			Modules modules = (Modules) session.getAttribute("modules");
			if (modules == null) {
				return "ERROR: No modules available in session.";
			}
			
			String currentModuleName = modules.getCurrentModuleName();
			if (currentModuleName == null) {
				log.debug("[DEBUG] No current module selected");
				return "ERROR: No current module selected.";
			}

			// Check if the requested entity matches the current module
			if (entity != null && !entity.equalsIgnoreCase(currentModuleName)) {
				log.debug("[DEBUG] Entity mismatch - current: " + currentModuleName + ", requested: " + entity);
				return "ERROR: Cannot filter. The user is viewing '" + currentModuleName + "' but asked about '" + entity + "'. Use findEntitiesByCondition to get data from a different entity.";
			}

			// Check if the user is in list mode (not detail mode)
			ModuleManager manager = (ModuleManager) context.get(application, currentModuleName, "manager");
			if (manager != null && !manager.isListMode()) {
				log.debug("[DEBUG] User is in detail mode, not list mode");
				return "ERROR: Cannot filter. The user is in detail mode, not viewing the list. Use findEntitiesByCondition to return data in the chat.";
			}

			Tab tab = (Tab) context.get(application, currentModuleName, "xava_tab");
			if (tab == null) {
				log.debug("[DEBUG] No tab available for module: " + currentModuleName);
				return "ERROR: No tab available for module " + currentModuleName;
			}
			
			List<MetaProperty> filterableProperties = tab.getMetaPropertiesNotCalculated();
			int propertyCount = filterableProperties.size();
			
			// Build the filter values map for JavaScript
			pendingFilterValues = new LinkedHashMap<>();
			for (int i = 0; i < propertyCount; i++) {
				MetaProperty prop = filterableProperties.get(i);
				String propertyName = prop.getQualifiedName();
				
				String value = values != null ? values.get(propertyName) : null;
				log.debug("[DEBUG] Processing property: " + propertyName + ", value: " + value);
				
				// Convert date values from yyyy-MM-dd to user locale format
				if (value != null && !value.isEmpty() && prop.isDateType()) {
					// TODO Move this to a method
					try {
						java.time.LocalDate localDate = java.time.LocalDate.parse(value); // ISO format yyyy-MM-dd
						Class<?> type = prop.getType();
						log.debug("[DEBUG] Current locale: " + org.openxava.util.Locales.getCurrent());
						if (java.time.LocalDate.class.isAssignableFrom(type)) {
							value = prop.format(localDate, org.openxava.util.Locales.getCurrent());
						} else {
							java.util.Date utilDate = java.sql.Date.valueOf(localDate);
							value = prop.format(utilDate, org.openxava.util.Locales.getCurrent());
						}
						log.debug("[DEBUG] Converted date to UI format: " + value);
					} catch (Exception e) {
						log.debug("[DEBUG] Could not parse date: " + value + ", error: " + e.getMessage());
					}
				}
				
				pendingFilterValues.put("conditionValue___" + i, value != null ? value : "");
				
				// Set comparator if specified
				String comparator = comparators != null ? comparators.get(propertyName) : null;
				if (comparator != null && !comparator.isEmpty()) {
					String comparatorValue = mapComparator(comparator);
					if (comparatorValue != null) {
						pendingFilterValues.put("conditionComparator___" + i, comparatorValue);
					}
				}
			}
			
			String result;
			if (values == null || values.isEmpty()) {
				result = "Filter cleared for " + currentModuleName + ". Showing all records.";
			} else {
				result = "Filter applied to " + currentModuleName + " with values: " + values + (comparators != null && !comparators.isEmpty() ? " and comparators: " + comparators : "");
			}
			log.debug("[TOOL] filterList() returning: " + result);
			log.debug("[TOOL] filterList() took " + (System.currentTimeMillis() - startTime) + " ms");
			return result;
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			String errorMessage = "ERROR filtering list: " + ex.getMessage();
			log.debug("[TOOL] filterList() returning: " + errorMessage);
			log.debug("[TOOL] filterList() took " + (System.currentTimeMillis() - startTime) + " ms");
			return errorMessage;
		}
	}
	
	private String mapComparator(String comparator) {
		if (comparator == null) return null;
		switch (comparator.toLowerCase()) {
			case "eq": case "=": case "equals": return "eq_comparator";
			case "ne": case "<>": case "!=": case "not_equals": return "ne_comparator";
			case "gt": case ">": case "greater": case "greater_than": return "gt_comparator";
			case "lt": case "<": case "less": case "less_than": return "lt_comparator";
			case "ge": case ">=": case "greater_or_equal": return "ge_comparator";
			case "le": case "<=": case "less_or_equal": return "le_comparator";
			case "contains": return "contains_comparator";
			case "starts": case "starts_with": return "starts_comparator";
			case "ends": case "ends_with": return "ends_comparator";
			case "not_contains": return "not_contains_comparator";
			case "empty": case "is_empty": return "empty_comparator";
			case "not_empty": case "is_not_empty": return "not_empty_comparator";
			case "range": case "between": return "range_comparator";
			case "in": case "in_group": return "in_comparator";
			case "not_in": case "not_in_group": return "not_in_comparator";
			case "year": case "year_equals": return "year_comparator";
			case "month": case "month_equals": return "month_comparator";
			case "year_month": return "year_month_comparator";
			default: return null;
		}
	}
	
	/**
	 * Returns information about the entity currently being displayed in detail mode.
	 * This allows the LLM to know which record the user is viewing/editing,
	 * so the user can say things like "change the address of the current record".
	 * 
	 * @return A map with "entity" (module name) and "key" (the entity key), or null if not in detail mode
	 */
	@Tool("Get the module (entity) the user is currently viewing. Use this to determine if filterList should be used instead of findEntitiesByCondition. Returns the module name that the user is currently on.")
	public String getCurrentModule() {
		long startTime = System.currentTimeMillis();
		log.debug("[TOOL] getCurrentModule() called");
		try {
			setupWindowId();
			
			Modules modules = (Modules) session.getAttribute("modules");
			if (modules == null) return null;
			
			String currentModuleName = modules.getCurrentModuleName();
			log.debug("[TOOL] getCurrentModule() returning: " + currentModuleName);
			log.debug("[TOOL] getCurrentModule() took " + (System.currentTimeMillis() - startTime) + " ms");
			return currentModuleName;
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			log.debug("[TOOL] getCurrentModule() took " + (System.currentTimeMillis() - startTime) + " ms");
			return null;
		}
	}
	
	/**
	 * Returns information about the entity currently being displayed in detail mode.
	 * This allows the LLM to know which record the user is viewing/editing,
	 * so the user can say things like "change the address of the current record".
	 * 
	 * @return A map with "entity" (module name) and "key" (the entity key), or null if not in detail mode
	 */
	@Tool("Get the entity currently being displayed or edited in detail mode. Use this when the user refers to 'current record', 'this record', or similar. Returns the entity name and key that can be used with getEntityDetails or updateEntity. Returns null if the user is not viewing a specific record in detail mode.")
	public Map<String, Object> getCurrentDisplayedEntity() {
		long startTime = System.currentTimeMillis();
		log.debug("[TOOL] getCurrentDisplayedEntity() called");
		try {
			setupWindowId();
			
			Modules modules = (Modules) session.getAttribute("modules");
			if (modules == null) return null;
			
			String currentModuleName = modules.getCurrentModuleName();
			if (currentModuleName == null) return null;
			
			ModuleManager manager = (ModuleManager) context.get(application, currentModuleName, "manager");
			if (!manager.isDetailMode()) return null;
			
			View view = (View) context.get(application, currentModuleName, "xava_view");
			if (view == null) return null;
			
			Map<String, Object> keyValues = view.getKeyValues();
			if (keyValues == null || keyValues.isEmpty()) return null;
			
			Map<String, Object> result = new HashMap<>();
			result.put("entity", currentModuleName);
			result.put("key", keyValues);
			log.debug("[TOOL] getCurrentDisplayedEntity() returning: " + result);
			log.debug("[TOOL] getCurrentDisplayedEntity() took " + (System.currentTimeMillis() - startTime) + " ms");
			return result;
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			log.debug("[TOOL] getCurrentDisplayedEntity() took " + (System.currentTimeMillis() - startTime) + " ms");
			return null;
		}
	}
	
}
