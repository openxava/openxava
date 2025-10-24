package org.openxava.chatvoice.tools;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openxava.controller.*;
import org.openxava.tab.Tab;
import org.openxava.application.meta.MetaModule;
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
	private HttpServletRequest request;
	private String application;
	
	/**
	 * Constructor that receives the ModuleContext, HttpSession, HttpServletRequest and application name.
	 * 
	 * @param context The ModuleContext to access session and application context
	 * @param session The HttpSession to assign to the manager
	 * @param request The HttpServletRequest to get modules with security
	 * @param application The application name
	 */
	public EntityTools(ModuleContext context, HttpSession session, HttpServletRequest request, String application) {
		this.context = context;
		this.session = session;
		this.request = request;
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
			List<MetaModule> allModules = modules.getAll(request);
			for (MetaModule module : allModules) {
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
			int rowCount = tableModel.getRowCount();
			
			// Iterate over all rows
			for (int row = 0; row < rowCount; row++) {
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
	 * Gets a Tab from the context and initializes it if necessary.
	 * 
	 * @param module The module name
	 * @return The Tab or null if not found
	 */
	private Tab getTab(String module) {
		Tab tab = (Tab) context.get(application, module, "xava_tab");
		if (tab != null) {
			if (tab.getModelName() == null) {
				// This code is also in execute.jsp, should we refactor?
				ModuleManager manager = (ModuleManager) context.get(application, module, "manager", "org.openxava.controller.ModuleManager");
				manager.setSession(session);
				manager.setApplicationName(application);
				manager.setModuleName(module);
				tab.setModelName(manager.getModelName());
				if (tab.getTabName() == null) { 
					tab.setTabName(manager.getTabName());
				}
			}
		}
		return tab;
	}
	
}
