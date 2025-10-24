package org.openxava.chatvoice.tools;

import java.util.*;
import javax.servlet.http.HttpSession;

import org.openxava.controller.*;
import org.openxava.tab.Tab;

import dev.langchain4j.agent.tool.Tool;

/**
 * Tools for customer-related operations using LangChain4j
 */
public class CustomerTools {
	
	private ModuleContext context;
	private HttpSession session;
	
	/**
	 * Constructor that receives the ModuleContext and HttpSession from the action
	 * 
	 * @param context The ModuleContext to access session and application context
	 * @param session The HttpSession to assign to the manager
	 */
	public CustomerTools(ModuleContext context, HttpSession session) {
		this.context = context;
		this.session = session;
	}
	
	/**
	 * Returns the total number of customers in the database
	 * 
	 * @return The count of customers
	 */
	@Tool("Get the total number of customers in the database")
	public long getCustomerCount() {
		try {
			return getCustomerTab().getTotalSize();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
	
	/**
	 * Gets the Customer tab from the context and initializes it if necessary
	 * 
	 * @return The Customer Tab or null if not found
	 */
	private Tab getCustomerTab() {
		Tab customerTab = (Tab) context.get("chatvoice", "Customer", "xava_tab");
		if (customerTab != null) {
			if (customerTab.getModelName() == null) {
				// Este código está en execute.jsp también, ¿refactorizar?
				ModuleManager manager = (ModuleManager) context.get("chatvoice", "Customer", "manager", "org.openxava.controller.ModuleManager");
				manager.setSession(session);
				manager.setApplicationName("chatvoice");
				manager.setModuleName("Customer");
				customerTab.setModelName(manager.getModelName());
				if (customerTab.getTabName() == null) { 
					customerTab.setTabName(manager.getTabName());
				}
			}
		}
		return customerTab;
	}
	
	/**
	 * Returns all customers from the database using the Tab
	 * 
	 * @return A list of all customers with their details
	 */
	@Tool("Get all customers from the database")
	public List<Map<String, Object>> getAllCustomers() {
		try {
			Tab customerTab = getCustomerTab();
			List<Map<String, Object>> customers = new ArrayList<>();
			
			// Obtener el TableModel del Tab
			javax.swing.table.TableModel tableModel = customerTab.getTableModel();
			
			int columnCount = tableModel.getColumnCount();
			int rowCount = tableModel.getRowCount();
			
			// Iterar sobre todas las filas
			for (int row = 0; row < rowCount; row++) {
				Map<String, Object> customer = new HashMap<>();
				
				// Obtener todas las columnas disponibles
				for (int col = 0; col < columnCount; col++) {
					String columnName = tableModel.getColumnName(col);
					Object value = tableModel.getValueAt(row, col);
					customer.put(columnName, value);
				}
				
				customers.add(customer);
			}
			
			return customers;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
	
}
