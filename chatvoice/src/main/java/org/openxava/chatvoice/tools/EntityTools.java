package org.openxava.chatvoice.tools;

import java.util.*;

import javax.servlet.http.HttpSession;

import org.openxava.controller.*;
import org.openxava.view.View;
import org.openxava.model.MapFacade;
import org.openxava.tab.Tab;
import org.openxava.tab.impl.IXTableModel;
import org.openxava.application.meta.MetaModule;
import org.openxava.component.MetaComponent;
import org.openxava.model.meta.MetaCollection;
import org.openxava.model.meta.MetaModel;
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

	// tmr Umbral de 600 registros para window de 128k. 600 para ser mútiplo de 120. ¿Hacerlo configurable?
	private final static int MAX_RECORDS = 600;
	
	private ModuleContext context;
	private HttpSession session;
	private String application;
	private Map<String, Tab> tabs = new HashMap<>();
	private Map<String, View> views = new HashMap<>();
	private boolean refreshUINeeded = false;
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
			System.out.println("[TOOL] getAvailableEntities() took " + (System.currentTimeMillis() - startTime) + " ms");
			return entityNames;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("[TOOL] getAvailableEntities() took " + (System.currentTimeMillis() - startTime) + " ms");
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
		System.out.println("[TOOL] getEntityProperties(entity=" + entity + ") called");
		try {
			Tab tab = getTab(entity);
			List<String> properties = new ArrayList<>();
			for (org.openxava.model.meta.MetaProperty p : tab.getMetaProperties()) {
				properties.add(p.getQualifiedName());
			}
			System.out.println("[TOOL] getEntityProperties(entity=" + entity + ") returning: " + properties);
			System.out.println("[TOOL] getEntityProperties() took " + (System.currentTimeMillis() - startTime) + " ms");
			return properties;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("[TOOL] getEntityProperties() took " + (System.currentTimeMillis() - startTime) + " ms");
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
		System.out.println("[TOOL] getEntityCollections(entity=" + entity + ") called");
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
			System.out.println("[TOOL] getEntityCollections(entity=" + entity + ") returning: " + collections);
			System.out.println("[TOOL] getEntityCollections() took " + (System.currentTimeMillis() - startTime) + " ms");
			return collections;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("[TOOL] getEntityCollections() took " + (System.currentTimeMillis() - startTime) + " ms");
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
		System.out.println("[TOOL] getEntityCount(entity=" + entity + ") called");
		try {
			long result = getTab(entity).getTotalSize();
			System.out.println("[TOOL] getEntityCount(entity=" + entity + ") returning: " + result);
			System.out.println("[TOOL] getEntityCount() took " + (System.currentTimeMillis() - startTime) + " ms");
			return result;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("[TOOL] getEntityCount() took " + (System.currentTimeMillis() - startTime) + " ms");
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
		System.out.println("[TOOL] findFirst600Entities(entity=" + entity + ") called");
		List<Map<String, Object>> result = findEntities(entity, null);
		System.out.println("[TOOL] findFirst600Entities() took " + (System.currentTimeMillis() - startTime) + " ms");
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
	@Tool("Get records from an entity and return data in the chat. Use this when the user ASKS FOR SPECIFIC DATA (e.g., 'give me the address of customer 2', 'what is the price of product X', 'tell me the total of invoice Y'). Also use this to access collection data by getting the hiddenKey first, then calling getEntityDetails. For simply DISPLAYING/FILTERING the list in the UI without returning data, use filterList instead. Specify the entity name and a SQL-style condition where property names are wrapped in ${}, like: ${name} = 'John' or ${price} > 100. Returns up to 600 records. Don't show hiddenKey to the user.")
	public List<Map<String, Object>> findEntitiesByCondition(
			@P("The entity name, e.g. Customer, Invoice, Product. Get valid names from getAvailableEntities()") String entity, 
			@P("SQL-style condition with property names in ${}, e.g. ${name} = 'John'") String condition) {
		long startTime = System.currentTimeMillis();
		System.out.println("[TOOL] findEntitiesByCondition(entity=" + entity + ", condition=" + condition + ") called");
		List<Map<String, Object>> result = findEntities(entity, condition);
		System.out.println("[TOOL] findEntitiesByCondition() took " + (System.currentTimeMillis() - startTime) + " ms");
		return result;
	}
	
	private List<Map<String, Object>> findEntities(String entity, String condition) {
		try {
			Tab tab = getTab(entity);
			tab.setBaseCondition(condition);
			List<Map<String, Object>> records = new ArrayList<>();
			
			// tmr Optimizar para que cargue los 600 primeros registros de un golpe
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
					ex.printStackTrace(); // tmr Hacer algo mejor
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
		System.out.println("[TOOL] getEntityDetails(entity=" + entity + ", key=" + key + ") called");
		try {
			View view = getView(entity);
			Map<String, Object> result = MapFacade.getValues(view.getModelName(), key, view.getMembersNames());
			System.out.println("[TOOL] getEntityDetails(entity=" + entity + ") returning: " + result);
			System.out.println("[TOOL] getEntityDetails() took " + (System.currentTimeMillis() - startTime) + " ms");
			return result;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("[TOOL] getEntityDetails() took " + (System.currentTimeMillis() - startTime) + " ms");
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * Updates one or more fields of an entity.
	 * The key can be obtained from the hiddenKey field returned by findEntities methods.
	 * 
	 * @param entity The entity name (e.g., "Customer", "Invoice", "Product")
	 * @param key The entity key as a Map (obtained from hiddenKey)
	 * @param values A map with the field names and their new values
	 * @return A confirmation message or error description
	 */
	@Tool("Update one or more fields of an entity. Use this to modify data like changing a customer name, invoice date, or any other field. The key is obtained from the hiddenKey field returned by findFirst600Entities or findEntitiesByCondition. The values parameter is a map where keys are property names and values are the new values to set.")
	public String updateEntity(
			@P("The entity name, e.g. Customer, Invoice, Product") String entity, 
			@P("The entity key obtained from hiddenKey field") Map<String, Object> key,
			@P("Map of property names to new values, e.g. {name: 'New Name', email: 'new@email.com'}") Map<String, Object> values) {
		long startTime = System.currentTimeMillis();
		System.out.println("[TOOL] updateEntity(entity=" + entity + ", key=" + key + ", values=" + values + ") called");
		try {
			if (key == null || key.isEmpty()) {
				return "ERROR: Key cannot be null or empty. Use hiddenKey from findFirst600Entities or findEntitiesByCondition.";
			}
			if (values == null || values.isEmpty()) {
				return "ERROR: Values cannot be null or empty. Specify at least one field to update.";
			}
			
			View view = getView(entity);
			String modelName = view.getModelName();
			MetaModel metaModel = MetaModel.get(modelName);
			
			// Filter out fields that are not editable or not present in the view
			Map<String, Object> editableValues = new HashMap<>();
			List<String> skippedFields = new ArrayList<>();
			Map<String, Object> membersInView = view.getMembersNames();
			
			for (Map.Entry<String, Object> entry : values.entrySet()) {
				String propertyName = entry.getKey();
				
				// Check if property is in the view
				if (!membersInView.containsKey(propertyName)) {
					skippedFields.add(propertyName + " (not in view)");
					System.out.println("[TOOL] updateEntity() - Skipping " + propertyName + ": not present in view");
					continue;
				}
				
				// Check if property is editable
				if (!view.isEditable(propertyName)) {
					skippedFields.add(propertyName + " (read-only)");
					System.out.println("[TOOL] updateEntity() - Skipping " + propertyName + ": read-only field");
					continue;
				}
				
				editableValues.put(propertyName, entry.getValue());
			}
			
			if (editableValues.isEmpty()) {
				String message = "ERROR: No editable fields to update.";
				if (!skippedFields.isEmpty()) {
					message += " Skipped fields: " + skippedFields;
				}
				System.out.println("[TOOL] updateEntity() returning: " + message);
				return message;
			}
			
			// Convert String values to the correct type using MetaProperty.parse()
			Map<String, Object> convertedValues = new HashMap<>();
			for (Map.Entry<String, Object> entry : editableValues.entrySet()) {
				String propertyName = entry.getKey();
				Object value = entry.getValue();
				
				if (value instanceof String) {
					try {
						MetaProperty metaProperty = metaModel.getMetaProperty(propertyName);
						value = parseValue(metaProperty, (String) value);
					} catch (Exception ex) {
						// If parsing fails, keep the original value
						System.out.println("[TOOL] updateEntity() - Could not parse property " + propertyName + ": " + ex.getMessage());
					}
				}
				
				System.out.println("[TOOL] updateEntity() - Key: " + propertyName + 
					", Value: " + value + 
					", Class: " + (value != null ? value.getClass() : "null"));
				convertedValues.put(propertyName, value);
			}
			
			MapFacade.setValues(modelName, key, convertedValues);
			
			// Mark that UI needs refresh
			refreshUINeeded = true;
			
			String result = "Successfully updated " + editableValues.size() + " field(s) in " + entity + ": " + editableValues.keySet();
			if (!skippedFields.isEmpty()) {
				result += ". Skipped fields: " + skippedFields;
			}
			System.out.println("[TOOL] updateEntity() returning: " + result);
			System.out.println("[TOOL] updateEntity() took " + (System.currentTimeMillis() - startTime) + " ms");
			return result;
		} catch (Exception ex) {
			ex.printStackTrace();
			String errorMessage = "ERROR updating " + entity + ": " + ex.getMessage();
			System.out.println("[TOOL] updateEntity() returning: " + errorMessage);
			System.out.println("[TOOL] updateEntity() took " + (System.currentTimeMillis() - startTime) + " ms");
			return errorMessage;
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
			System.out.println("EntityTools.getTab() application=" + application + ", entity=" + entity); // tmr
			manager.setApplicationName(application);
			manager.setModuleName(entity);
			tab.setModelName(manager.getModelName());
			if (tab.getTabName() == null) { 
				tab.setTabName(manager.getTabName());
			}
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
	private View getView(String module) {
		View view = views.get(module);
		if (view == null) {
			view = new View();
			ModuleManager manager = (ModuleManager) context.get(application, module, "manager", "org.openxava.controller.ModuleManager");
			manager.setSession(session);
			manager.setApplicationName(application);
			manager.setModuleName(module);
			view.setModelName(manager.getModelName());
			view.setViewName(manager.getXavaViewName());
			views.put(module, view);
		}
		return view;
	}
	
	/**
	 * Parses a String value to the correct type for the property.
	 * Handles ISO date format (yyyy-MM-dd) as fallback for dates since LLMs typically use this format.
	 */
	private Object parseValue(MetaProperty metaProperty, String value) throws Exception {
		try {
			return metaProperty.parse(value);
		} catch (Exception ex) {
			// If standard parsing fails, try ISO format for date types
			Class<?> type = metaProperty.getType();
			if (java.time.LocalDate.class.isAssignableFrom(type)) {
				return java.time.LocalDate.parse(value); // ISO format yyyy-MM-dd
			}
			if (java.util.Date.class.isAssignableFrom(type)) {
				java.time.LocalDate localDate = java.time.LocalDate.parse(value);
				return java.sql.Date.valueOf(localDate);
			}
			throw ex;
		}
	}
	
	/**
	 * Returns true if a UI refresh is needed (after an update) and resets the flag.
	 */
	public boolean consumeRefreshUINeeded() {
		boolean result = refreshUINeeded;
		refreshUINeeded = false;
		return result;
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
		System.out.println("[TOOL] filterList(entity=" + entity + ", values=" + values + ", comparators=" + comparators + ") called");
		try {
			setupWindowId();
			
			Modules modules = (Modules) session.getAttribute("modules");
			if (modules == null) {
				return "ERROR: No modules available in session.";
			}
			
			String currentModuleName = modules.getCurrentModuleName();
			if (currentModuleName == null) {
				System.out.println("[DEBUG] No current module selected"); // tmr
				return "ERROR: No current module selected.";
			}

			// Check if the requested entity matches the current module
			if (entity != null && !entity.equalsIgnoreCase(currentModuleName)) {
				System.out.println("[DEBUG] Entity mismatch - current: " + currentModuleName + ", requested: " + entity); // tmr
				return "ERROR: Cannot filter. The user is viewing '" + currentModuleName + "' but asked about '" + entity + "'. Use findEntitiesByCondition to get data from a different entity.";
			}

			// Check if the user is in list mode (not detail mode)
			ModuleManager manager = (ModuleManager) context.get(application, currentModuleName, "manager");
			if (manager != null && !manager.isListMode()) {
				System.out.println("[DEBUG] User is in detail mode, not list mode"); // tmr
				return "ERROR: Cannot filter. The user is in detail mode, not viewing the list. Use findEntitiesByCondition to return data in the chat.";
			}

			Tab tab = (Tab) context.get(application, currentModuleName, "xava_tab");
			if (tab == null) {
				System.out.println("[DEBUG] No tab available for module: " + currentModuleName); // tmr
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
				System.out.println("[DEBUG] Processing property: " + propertyName + ", value: " + value); // tmr
				
				// Convert date values from yyyy-MM-dd to user locale format
				if (value != null && !value.isEmpty() && prop.isDateType()) {
					// tmr Mover esto a un método
					try {
						java.time.LocalDate localDate = java.time.LocalDate.parse(value); // ISO format yyyy-MM-dd
						Class<?> type = prop.getType();
						System.out.println("[DEBUG] Current locale: " + org.openxava.util.Locales.getCurrent()); // tmr
						if (java.time.LocalDate.class.isAssignableFrom(type)) {
							value = prop.format(localDate, org.openxava.util.Locales.getCurrent());
						} else {
							java.util.Date utilDate = java.sql.Date.valueOf(localDate);
							value = prop.format(utilDate, org.openxava.util.Locales.getCurrent());
						}
						System.out.println("[DEBUG] Converted date to UI format: " + value);
					} catch (Exception e) {
						System.out.println("[DEBUG] Could not parse date: " + value + ", error: " + e.getMessage());
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
			System.out.println("[TOOL] filterList() returning: " + result);
			System.out.println("[TOOL] filterList() took " + (System.currentTimeMillis() - startTime) + " ms");
			return result;
		} catch (Exception ex) {
			ex.printStackTrace();
			String errorMessage = "ERROR filtering list: " + ex.getMessage();
			System.out.println("[TOOL] filterList() returning: " + errorMessage);
			System.out.println("[TOOL] filterList() took " + (System.currentTimeMillis() - startTime) + " ms");
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
	public String getCurrentModule() { // tmr ¿Este nombre?
		long startTime = System.currentTimeMillis();
		System.out.println("[TOOL] getCurrentModule() called");
		try {
			setupWindowId();
			
			Modules modules = (Modules) session.getAttribute("modules");
			if (modules == null) return null;
			
			String currentModuleName = modules.getCurrentModuleName();
			System.out.println("[TOOL] getCurrentModule() returning: " + currentModuleName);
			System.out.println("[TOOL] getCurrentModule() took " + (System.currentTimeMillis() - startTime) + " ms");
			return currentModuleName;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("[TOOL] getCurrentModule() took " + (System.currentTimeMillis() - startTime) + " ms");
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
		System.out.println("[TOOL] getCurrentDisplayedEntity() called");
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
			System.out.println("[TOOL] getCurrentDisplayedEntity() returning: " + result);
			System.out.println("[TOOL] getCurrentDisplayedEntity() took " + (System.currentTimeMillis() - startTime) + " ms");
			return result;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("[TOOL] getCurrentDisplayedEntity() took " + (System.currentTimeMillis() - startTime) + " ms");
			return null;
		}
	}
	
}
