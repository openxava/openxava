package org.openxava.web.dwr;

import java.util.*;

import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.model.*;
import org.openxava.util.*;
import org.openxava.web.*;
import org.openxava.web.servlets.*;

/**
 * For accessing to the Tab from DWR. <p>
 * 
 * @author Javier Paniza
 */

public class Tab extends DWRBase {
	
	private static Log log = LogFactory.getLog(Tab.class);

	public void setFilterVisible(HttpServletRequest request, HttpServletResponse response, String application, String module, boolean filterVisible, String tabObject) {
		try { 
			initRequest(request, response, application, module); 
			org.openxava.tab.Tab tab = getTab(request, application, module, tabObject); 
			tab.setFilterVisible(filterVisible);
		}
		finally {
			cleanRequest();
		}
	}
	
	/**
	 * Updates a cell value in an editable list.
	 * 
	 * @param request The HTTP request
	 * @param response The HTTP response
	 * @param application The application name
	 * @param module The module name
	 * @param row The row number (0-based)
	 * @param property The property name
	 * @param value The new value
	 * @param tabObject The tab object name
	 * @return Confirmatior or error message
	 */
	public String updateValue(HttpServletRequest request, HttpServletResponse response, 
			String application, String module, int row, String property, String value) {
		try { 
			System.out.println("[Tab.updateValue] row=" + row + ", property=" + property + ", value=" + value); // tmr
			initRequest(request, response, application, module);
			org.openxava.tab.Tab tab = getTab(request, application, module, "xava_tab"); // By now only in list mode, not int collections
			
			Map key = (Map) tab.getTableModel().getObjectAt(row);
			System.out.println("[Tab.updateValue] key=" + key); // tmr
			Map<String, Object> values = new HashMap<>();
			Messages errors = new Messages();
			Object ovalue = WebEditors.parse(request, tab.getMetaProperty(property), value, errors, value);
			System.out.println("[Tab.updateValue] ovalue=" + ovalue); // tmr
			if (ovalue != null) {
				System.out.println("[Tab.updateValue] ovalue.getClass()=" + ovalue.getClass()); // tmr
			}
			if (errors.contains()) {
				System.out.println("[Tab.updateValue] errors=" + errors); // tmr
				// tmr ¿No seguir?
			}
			values.put(property, ovalue);
			MapFacade.setValues(tab.getModelName(), key, values);
			System.out.println("[Tab.updateValue] Grabado"); // tmr
			return "Grabado nuevo valor para " + property + " en fila " + (row + 1); // tmr i18n
		}
		catch (Exception ex) {
			ex.printStackTrace(); // tmr ¿Qué hacer?
			return "Fallo: " + ex.getMessage();
		}
		finally {
			cleanRequest();
		}
	}
	
	public void removeProperty(HttpServletRequest request, HttpServletResponse response, String application, String module, String property, String tabObject) {
		try {
			initRequest(request, response, application, module); 
			org.openxava.tab.Tab tab = getTab(request, application, module, tabObject);
			tab.removeProperty(property);
		}
		finally {
			cleanRequest();
		}
	}
	
	/**
	 * 
	 * @since 5.2
	 */
	public void moveProperty(HttpServletRequest request, HttpServletResponse response, String tableId, int from, int to) {
		TableId id = new TableId(tableId, 0);
		if (!id.isValid()) {
			log.warn(XavaResources.getString("impossible_store_column_movement"));  
			return;			
		}
		try { 
			initRequest(request, response, id.getApplication(), id.getModule()); 
			org.openxava.tab.Tab tab = getTab(request, id.getApplication(), id.getModule(), id.getTabObject());		
			tab.moveProperty(from, to);
		}
		finally {
			cleanRequest();
		}
	}
	
	public void setColumnWidth(HttpServletRequest request, HttpServletResponse response, String columnId, int index, int width) {
		try {
			TableId id = new TableId(columnId, 1);
			if (!id.isValid()) {
				log.warn(XavaResources.getString("impossible_store_column_width"));  
				return;			
			}
			initRequest(request, response, id.getApplication(), id.getModule()); 
			try {
				org.openxava.tab.Tab tab = getTab(request, id.getApplication(), id.getModule(), id.getTabObject()); 
				tab.setColumnWidth(index, width);
			}
			catch (ElementNotFoundException ex) { 
				// If it has not tab maybe it's a calculated collection
				org.openxava.view.View view = (org.openxava.view.View) getContext(request).get(id.getApplication(), id.getModule(), "xava_view");
				org.openxava.view.View collectionView = view.getSubview(id.getCollection());
				if (collectionView.isCollectionFromModel() || collectionView.isRepresentsElementCollection()) {
					String column=columnId.substring(columnId.lastIndexOf("_col") + 4);
					int columnIndex = Integer.parseInt(column);
					collectionView.setCollectionColumnWidth(columnIndex, width);
				}
				else {
					collectionView.setCollectionColumnWidth(index, width);	
				}
				
				
			}
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("impossible_store_column_width"), ex);
		}		
		finally {
			cleanRequest();
		}
	}
	
	public String filterColumns(HttpServletRequest request, HttpServletResponse response, String application, String module, String searchWord) {   
		try {
			initRequest(request, response, application, module);
			return Servlets.getURIAsString(request, response, "/xava/editors/selectColumns.jsp?application=" + application + "&module=" + module + "&searchWord=" + searchWord); 
		}
		catch (Exception ex) {
			log.error(XavaResources.getString("display_columns_error"), ex);  
			return null; 
		}
		finally {
			cleanRequest();
		}
	}
 
	private static org.openxava.tab.Tab getTab(HttpServletRequest request, String application, String module, String tabObject) {
		org.openxava.tab.Tab tab = (org.openxava.tab.Tab)		
		  getContext(request).get(application, module, tabObject);
		request.setAttribute("xava.application", application);
		request.setAttribute("xava.module", module);
		tab.setRequest(request);
		return tab;
	}
	
}
