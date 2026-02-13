package org.openxava.web.dwr;

import java.util.*;

import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.controller.*;
import org.openxava.model.*;
import org.openxava.model.meta.*;
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
	 * @return Confirmation or error message
	 */
	public String updateValue(HttpServletRequest request, HttpServletResponse response, 
			String application, String module, int row, String property, String value) {
		try { 
			initRequest(request, response, application, module);
			org.openxava.tab.Tab tab = getTab(request, application, module, "xava_tab"); // By now only in list mode, not int collections
			
			Map key = (Map) tab.getTableModel().getObjectAt(row);
			
			// Read old value before saving for undo support
			String oldValue = getFormattedValue(request, tab, key, property);
			
			Map<String, Object> values = new HashMap<>();
			try {
				Messages parsingErrors = new Messages();
				Object ovalue = WebEditors.parse(request, tab.getMetaProperty(property), value, parsingErrors, value);
				if (parsingErrors.contains()) {
					return "ERROR: " + parsingErrors;
				}
				values.put(property, ovalue);
			}
			catch (ElementNotFoundException ex) {
				if (!tab.getMetaTab().getMetaModel().containsMetaReference(property)) {
					throw ex;
				}
				Messages parsingErrors = new Messages(); 
				Map<String, Object> referenceValues = getReferenceValues(tab.getMetaTab().getMetaModel().getMetaReference(property), value, request, parsingErrors);
				if (parsingErrors.contains()) {
					return "ERROR: " + parsingErrors;
				}
				values.put(property, referenceValues);
			}
			MapFacade.setValues(tab.getModelName(), key, values);
			String propertyLabel = Labels.get(property, request.getLocale()).toLowerCase();
			String message = XavaResources.getString(request, "value_saved_for_property_in_row", propertyLabel, row + 1);
			String undoLabel = XavaResources.getString(request, "undo");
			String restoreMessage = XavaResources.getString(request, "value_restored_for_property_in_row", propertyLabel, row + 1);
			return message + "\tUNDO:" + undoLabel + "\t" + oldValue + "\t" + restoreMessage; 
		}
		catch (Exception ex) {
			Messages errors = ModuleManager.manageException(ex); 		
			return "ERROR: " + errors;
		}
		finally {
			cleanRequest();
		}
	}
	
	private String getFormattedValue(HttpServletRequest request, org.openxava.tab.Tab tab, Map key, String property) {
		try {
			if (tab.getMetaTab().getMetaModel().containsMetaReference(property)) {
				MetaReference ref = tab.getMetaTab().getMetaModel().getMetaReference(property);
				MetaModel refModel = ref.getMetaModelReferenced();
				Collection<String> keyPropertiesNames = refModel.getAllKeyPropertiesNames();
				Map<String, Object> memberNames = new HashMap<>();
				for (String keyName : keyPropertiesNames) {
					memberNames.put(property + "." + keyName, null);
				}
				Map refValues = MapFacade.getValues(tab.getModelName(), key, memberNames);
				Map<String, Object> keyValues = new HashMap<>();
				for (String keyName : keyPropertiesNames) {
					keyValues.put(keyName, refValues.get(property + "." + keyName));
				}
				return DescriptionsLists.toKeyString(refModel, keyValues);
			}
			else {
				Map<String, Object> memberNames = new HashMap<>();
				memberNames.put(property, null);
				Map currentValues = MapFacade.getValues(tab.getModelName(), key, memberNames);
				Object oldRawValue = currentValues.get(property);
				return WebEditors.format(request, tab.getMetaProperty(property), oldRawValue, new Messages(), "", true);
			}
		}
		catch (Exception ex) {
			log.warn("Could not get old value for undo", ex);
			return "";
		}
	}

	/**
	 * Gets a map with reference values from a composite key string.
	 * 
	 * @param ref MetaReference containing the reference definition
	 * @param value String value containing the composite key
	 * @param request HttpServletRequest for parsing values
	 * @param errors Messages object for error handling during parsing
	 * @return Map with reference values
	 */
	private Map<String, Object> getReferenceValues(MetaReference ref, String value, HttpServletRequest request, Messages errors) {
		Map<String, Object> referenceValues = new HashMap<>();
		// Delegating to the common implementation in DescriptionsLists
		// Using emptyIfNotBracketed=false to maintain original Tab behavior
		org.openxava.web.DescriptionsLists.fillReferenceValues(
			referenceValues, ref, value, null, null, 
			request, errors, "", false);
		return referenceValues;
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
