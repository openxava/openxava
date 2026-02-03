package org.openxava.chat.impl;

import java.util.*;

import org.apache.commons.logging.*;

import javax.servlet.http.HttpSession;

import org.openxava.controller.*;
import org.openxava.view.View;
import org.openxava.model.MapFacade;
import org.openxava.model.meta.MetaModel;
import org.openxava.model.meta.MetaProperty;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

/**
 * Tools for modifying OpenXava entity data using LangChain4j.
 * Separated from EntityTools to allow disabling data modification via configuration.
 * 
 * @author Javier Paniza
 * @since 7.7
 */
public class EntityModifyTools {
	
	private static Log log = LogFactory.getLog(EntityModifyTools.class);

	private ModuleContext context;
	private HttpSession session;
	private String application;
	private Map<String, View> views = new HashMap<>();
	private boolean refreshUINeeded = false;
	
	/**
	 * Constructor that receives the ModuleContext, HttpSession and application name.
	 * 
	 * @param context The ModuleContext to access session and application context
	 * @param session The HttpSession to assign to the manager
	 * @param application The application name
	 */
	public EntityModifyTools(ModuleContext context, HttpSession session, String application) {
		this.context = context;
		this.session = session;
		this.application = application;
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
		log.debug("[TOOL] updateEntity(entity=" + entity + ", key=" + key + ", values=" + values + ") called");
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
					log.debug("[TOOL] updateEntity() - Skipping " + propertyName + ": not present in view");
					continue;
				}
				
				// Check if property is editable
				if (!view.isEditable(propertyName)) {
					skippedFields.add(propertyName + " (read-only)");
					log.debug("[TOOL] updateEntity() - Skipping " + propertyName + ": read-only field");
					continue;
				}
				
				editableValues.put(propertyName, entry.getValue());
			}
			
			if (editableValues.isEmpty()) {
				String message = "ERROR: No editable fields to update.";
				if (!skippedFields.isEmpty()) {
					message += " Skipped fields: " + skippedFields;
				}
				log.debug("[TOOL] updateEntity() returning: " + message);
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
						log.debug("[TOOL] updateEntity() - Could not parse property " + propertyName + ": " + ex.getMessage());
					}
				}
				
				log.debug("[TOOL] updateEntity() - Key: " + propertyName + 
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
			log.debug("[TOOL] updateEntity() returning: " + result);
			log.debug("[TOOL] updateEntity() took " + (System.currentTimeMillis() - startTime) + " ms");
			return result;
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			String errorMessage = "ERROR updating " + entity + ": " + ex.getMessage();
			log.debug("[TOOL] updateEntity() returning: " + errorMessage);
			log.debug("[TOOL] updateEntity() took " + (System.currentTimeMillis() - startTime) + " ms");
			return errorMessage;
		}
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
	
}
