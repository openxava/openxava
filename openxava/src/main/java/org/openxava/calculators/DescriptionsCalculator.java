package org.openxava.calculators;

import java.util.*;

import javax.swing.table.*;
import javax.swing.event.*;

import org.apache.commons.logging.*;
import org.openxava.component.*;
import org.openxava.filters.*;
import org.openxava.model.meta.*;
import org.openxava.tab.impl.*;
import org.openxava.tab.meta.*;
import org.openxava.util.*;
import org.openxava.web.DescriptionsLists;

/**
 * It obtain a description collection. <p>
 * 
 * Use tab infrastructure for it, so you can make that this execute
 * within a EJB server or nor configuring tab in xava.properties.
 * 
 * @author Javier Paniza
 */
public class DescriptionsCalculator implements ICalculator {
	private static Log log = LogFactory.getLog(DescriptionsCalculator.class);
	
	private static final long serialVersionUID = 3638931156760463239L;
	
	private String keyProperty;
	private String keyProperties;
	private String descriptionProperty;
	private String descriptionProperties;
	private String condition;
	private String order;
	private Collection parameters;
	private String model;
	private String componentName;
	private String aggregateName;
	private transient MetaModel metaModel;
	private boolean orderByKey = false;
	private boolean useConvertersInKeys = false;
	private Collection keyPropertiesCollection;
	private MetaTab metaTab;
	private int hiddenPropertiesCount; 
	private boolean distinct = false;   
	
	/**
	 * Implementation of ICalculator interface.
	 * Uses the new paginated approach for compatibility.
	 */
	public Object calculate() throws Exception {
		// Use paginated loading with a reasonable limit for compatibility
		return getDescriptionsPaginated(10000, 0);
	}
	
	private void checkPreconditions() throws XavaException {
		if (Is.emptyString(getModel())) {
			throw new XavaException("descriptions_calculator_model_required", getClass().getName());
		}
		if (Is.emptyString(getKeyProperties())) {
			throw new XavaException("descriptions_calculator_keyProperty_required", getClass().getName());
		}
		if (Is.emptyString(getDescriptionProperties())) {
			throw new XavaException("descriptions_calculator_descriptionProperty_required", getClass().getName());
		}				
	}
	
	
	

	private MetaModel getMetaModel() throws XavaException {
		if (metaModel == null) {
			if (isAggregate()) {
				metaModel = MetaComponent.get(getComponentName()).getMetaAggregate(getAggregateName());
			}	
			else {		
				metaModel = MetaComponent.get(getComponentName()).getMetaEntity();
			}
		}
		return metaModel;
	}
	
	private boolean isMultipleKey() {
		return !Is.emptyString(keyProperties);
	}
			
	/**
	 * Returns descriptions using on-demand loading. This method is deprecated.
	 * Use getDescriptionsPaginated() instead for better performance with large datasets.
	 * 
	 * @return Collection of <tt>KeyAndDescription</tt>. Not null.
	 * @deprecated Use getDescriptionsPaginated() instead
	 */
	public Collection getDescriptions() throws Exception {	
		if (conditionHasArguments() && !hasParameters()) return Collections.EMPTY_LIST;
		// Use paginated loading with a large limit to maintain compatibility
		return getDescriptionsPaginated(10000, 0);
	}
	
	/**
	 * Returns a paginated collection of descriptions with database-level LIMIT and OFFSET.
	 * This method bypasses cache to ensure fresh data for pagination.
	 * 
	 * @param limit Maximum number of results to return
	 * @param offset Number of results to skip
	 * @return Collection of <tt>KeyAndDescription</tt>. Not null.
	 * @since 7.6
	 */
	public Collection getDescriptionsPaginated(int limit, int offset) throws Exception {
		return executeQueryPaginatedCollection(limit, offset, null);
	}
	
	/**
	 * Gets descriptions with filtering and pagination.
	 * Uses database-level filtering for regular properties, or in-memory filtering for calculated properties.
	 * 
	 * @param limit Maximum number of records to return
	 * @param offset Number of records to skip
	 * @param searchTerm Term to search for in description properties
	 * @return Collection of KeyAndDescription objects matching the search term
	 * @throws Exception if there's an error executing the query
	 */
	public Collection getDescriptionsPaginatedWithSearch(int limit, int offset, String searchTerm) throws Exception {
		// Check if we have calculated properties that require in-memory filtering
		boolean hasCalculated = hasCalculatedDescriptionProperties();
		System.out.println("DEBUG: hasCalculatedDescriptionProperties: " + hasCalculated);
		
		if (hasCalculated) {
			// For calculated properties, load a larger batch and filter in memory
			int batchSize = Math.min(1000, Math.max(limit * 10, 100));
			System.out.println("DEBUG: Loading " + batchSize + " records for in-memory filtering");
			Collection allDescriptions = executeQueryPaginatedCollection(batchSize, 0, null);
			System.out.println("DEBUG: executeQueryPaginatedCollection returned: " + (allDescriptions == null ? "null" : allDescriptions.size() + " items"));
			
			try {
				System.out.println("DEBUG: About to call filterDescriptionsInMemory...");
				Collection result = filterDescriptionsInMemory(allDescriptions, searchTerm, limit, offset);
				System.out.println("DEBUG: filterDescriptionsInMemory returned: " + (result == null ? "null" : result.size() + " items"));
				return result;
			} catch (Exception e) {
				System.out.println("DEBUG: Exception in filterDescriptionsInMemory: " + e.getMessage());
				e.printStackTrace();
				return Collections.EMPTY_LIST;
			}
		} else {
			// For regular properties, use database-level filtering
			System.out.println("DEBUG: Using database-level filtering");
			return executeQueryPaginatedCollection(limit, offset, searchTerm);
		}
	}
	
	public Collection getDescriptionsPaginated(int limit, int offset, String searchTerm) throws Exception {
		return executeQueryPaginatedCollection(limit, offset, searchTerm);
	}
	
	/**
	 * Returns the total count of descriptions without loading the actual data.
	 * Used to determine if remote mode should be activated.
	 * 
	 * @return Total count of descriptions
	 * @since 7.6
	 */
	public int getDescriptionsCount() throws Exception {
		if (conditionHasArguments() && !hasParameters()) return 0;
		return executeQueryCount();
	}
	
	/**
	 * Finds a specific description by key without loading all data.
	 * Used in remote mode to get the selected item description.
	 * 
	 * @param key The key to search for
	 * @return KeyAndDescription if found, null otherwise
	 * @since 7.6
	 */
	public KeyAndDescription findDescriptionByKey(Object key) throws Exception {
		if (key == null || conditionHasArguments() && !hasParameters()) return null;
		
		// Create a temporary condition to find the specific key
		String originalCondition = getCondition();
		String keyCondition = buildKeyCondition(key);
		
		try {
			// Temporarily modify condition to find specific key
			if (Is.emptyString(originalCondition)) {
				setCondition(keyCondition);
			} else {
				setCondition("(" + originalCondition + ") AND (" + keyCondition + ")");
			}
			
			Collection results = executeQueryPaginatedCollection(1, 0, null);
			if (results != null && !results.isEmpty()) {
				return (KeyAndDescription) results.iterator().next();
			}
			return null;
		} finally {
			// Restore original condition
			setCondition(originalCondition);
		}
	}
	
	private String buildKeyCondition(Object key) {
        try {
            // Always parse using DescriptionsLists (uses WebEditors under the hood)
            Map<String, Object> values = new HashMap<String, Object>();
            DescriptionsLists.fillReferenceValues(values, getMetaModel(), key == null ? null : key.toString());
            StringBuilder condition = new StringBuilder();
            for (String propertyName : getMetaModel().getAllKeyPropertiesNames()) {
                MetaProperty p = getMetaModel().getMetaProperty(propertyName);
                Object value = values == null ? null : values.get(propertyName);
                appendConditionPart(condition, propertyName, p, value);
            }
            return condition.toString();
        }
        catch (Exception ex) {
            // Fallback to previous (simplistic) behavior if anything goes wrong
            return (isMultipleKey()? getKeyProperties().split(",")[0].trim(): getKeyProperty())
                + " = '" + (key==null?"":key.toString()) + "'";
        }
    }

    /**
     * Appends a single property condition into the builder, using proper
     * literal formatting depending on the MetaProperty type and supporting nulls.
     */
    private void appendConditionPart(StringBuilder condition, String propertyName, MetaProperty p, Object value) throws Exception {
        if (condition.length() > 0) condition.append(" AND ");
        if (value == null || (value instanceof String && ((String) value).length() == 0)) {
            condition.append("${").append(propertyName).append("} is null");
            return;
        }
        condition.append("${").append(propertyName).append("} = ").append(formatLiteralValue(p, value));
    }

    /**
     * Formats a token value as an HQL literal based on property type.
     * Numbers and booleans are unquoted; strings are single-quoted with escaping.
     */
    private String formatLiteralValue(MetaProperty p, Object parsed) throws Exception {
        if (parsed == null) return "null"; // although we handle null/empty earlier

        if (parsed instanceof java.lang.Boolean) {
            return ((Boolean) parsed) ? "true" : "false";
        }
        if (parsed instanceof java.lang.Number) {
            return parsed.toString(); // standard dot decimal
        }
        if (parsed instanceof java.util.Date) {
            // Format as ISO timestamp literal
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formatted = sdf.format((java.util.Date) parsed);
            return "'" + formatted + "'";
        }

        // Default: quote as string and escape single quotes
        String v = String.valueOf(parsed);
        String escaped = v.replace("'", "''");
        return "'" + escaped + "'";
    }

    
	
	/*
	 * @since 7.1.6 
	 * 
	 * It is used to display the item that was previously selected and no longer satisfies the condition.
	 */
	public Collection getDescriptionsWithSelected(String fvalue) throws Exception {
		// Try to find the specific item first
		KeyAndDescription selected = findDescriptionByKey(fvalue);
		if (selected != null) {
			List<KeyAndDescription> result = new ArrayList<>();
			result.add(selected);
			// Add some additional items for context
			Collection additional = getDescriptionsPaginated(50, 0);
			for (Object item : additional) {
				KeyAndDescription kd = (KeyAndDescription) item;
				if (!kd.getKey().toString().equals(fvalue)) {
					result.add(kd);
				}
			}
			return result;
		}
		// Fallback to regular paginated loading
		return getDescriptionsPaginated(100, 0);
	}

	private boolean conditionHasArguments() {
		return this.condition != null && this.condition.indexOf('?') >= 0;		
	}


	/**
	 * It's used when there is only a key property.
	 * 
	 * It's exclusive with <tt>keyProperties</tt>. 
	 */
	public String getKeyProperty() {
		return keyProperty;
	}

	public String getDescriptionProperty() {
		return descriptionProperty;
	}
	
	private Collection getKeyPropertiesCollection() {
		if (keyPropertiesCollection == null) {
			keyPropertiesCollection = new ArrayList();
			String source = Is.emptyString(keyProperty)?keyProperties:keyProperty;
			StringTokenizer st = new StringTokenizer(source, ",;");
			while (st.hasMoreElements()) {
				keyPropertiesCollection.add(st.nextToken().trim());
			}
		}
		return keyPropertiesCollection;
	}
		
	public void setKeyProperty(String keyProperty) {	
		this.keyProperty = keyProperty;		
		metaTab = null;
	}

	public void setDescriptionProperty(String descriptionProperty) {
		this.descriptionProperty = descriptionProperty;
		metaTab = null;
	}
	
	private MetaTab getMetaTab() throws XavaException {
		if (metaTab == null) {
			metaTab = new MetaTab();
			metaTab.setMetaModel(getMetaModel());
			StringBuffer extraProperties = new StringBuffer();
			hiddenPropertiesCount = 0;
			for (Iterator it = createConditionAndOrderProperties().iterator(); it.hasNext(); ) {
				extraProperties.append(", ");
				extraProperties.append(it.next());
				hiddenPropertiesCount++;
			}
			metaTab.setPropertiesNames(getKeyProperties() + ", " +  getDescriptionProperties() + extraProperties); 
		}
		return metaTab;
	}
	
	private Collection createConditionAndOrderProperties() { 
		Set result = new HashSet();
		if (hasCondition()) {
			extractPropertiesFromSentences(result, getCondition());
		}
		if (hasOrder()) {
			extractPropertiesFromSentences(result, getOrder());
		}		
		return result;
	}
	
	private void extractPropertiesFromSentences(Set result, String sentence) { 		
		int i = sentence.indexOf("${");
		int f = 0;
		while (i >= 0) {
			f = sentence.indexOf("}", i + 2);
			if (f < 0) break;
			String property = sentence.substring(i + 2, f);
			result.add(property);
			i = sentence.indexOf("${", f);
		}
	}
	

	
	private Collection executeQueryPaginatedCollection(int limit, int offset, String searchTerm) throws Exception {
 		// Build tab with chunk size to avoid loading everything
 		int chunkSize = Math.max(0, offset) + Math.max(0, limit);
 		if (chunkSize <= 0) chunkSize = 50; // sane default
 		EntityTab tab = EntityTabFactory.create(getMetaTab(), chunkSize);
 
 		String condition = "";
 		if (hasCondition()) {
 			condition = getCondition(); 
 		}
 
 		// Add search condition if provided
 		if (!Is.emptyString(searchTerm)) {
 			String searchCondition = buildSearchCondition(searchTerm);
 			if (!Is.emptyString(searchCondition)) {
 				if (Is.emptyString(condition)) {
 					condition = searchCondition;
 				} else {
 					condition = "(" + condition + ") AND (" + searchCondition + ")";
 				}
 			}
 		}
 
 		String order = "";
 		if (hasOrder()) {
 			order = " ORDER BY " + getOrder(); 
 		}
 
 		Object [] key = null;
 		if (hasParameters()) {
 			key = new Object[getParameters().size()];
 			Iterator it = getParameters().iterator();
 			for (int i=0; i<key.length; i++) { 
 				key[i] = it.next();
 				if (key[i] == null) return Collections.EMPTY_LIST;
 			}
 		}
 
 		tab.search(condition + order, key);
 
 		try {
 			DataChunk firstChunk = tab.nextChunk();
 			List result = new ArrayList();
 			List chunkData = firstChunk == null ? Collections.EMPTY_LIST : firstChunk.getData();
 
 			int startIndex = Math.min(Math.max(0, offset), chunkData.size());
 			int endIndex = Math.min(startIndex + Math.max(0, limit), chunkData.size());
 
 			for (int i = startIndex; i < endIndex; i++) {
				Object[] row = (Object[]) chunkData.get(i);
				KeyAndDescription el = new KeyAndDescription();

				int iKey = 0;
				if (isMultipleKey()) {
					Iterator itKeyNames = getKeyPropertiesCollection().iterator();
					Map keyMap = new HashMap();
					boolean isNull = true;
					while (itKeyNames.hasNext()) {
						String name = (String) itKeyNames.next();
						Object v = row[iKey++];
						keyMap.put(name, v);
						if (v != null) isNull = false;
					}
					if (isNull) {
						el.setKey(null);
					} else {
						el.setKey(getMetaModel().toString(keyMap));
					}
				} else {
					el.setKey(row[iKey++]);
				}
 
 				StringBuilder value = new StringBuilder();
				String descPropsStr = getDescriptionProperties();

				String keyPropsStr = getKeyProperties();
				String[] keyProps = keyPropsStr.split(",");

				if (Is.emptyString(descPropsStr) || descPropsStr.equals(getKeyProperties())) {
					// When description properties are same as key properties, use key value as description
					if (el.getKey() != null) {
						value.append(String.valueOf(el.getKey()).trim());
					}
				} else {
					// Description properties are different from key properties
					// Based on debug output: SELECT e.number, e.number, e.description, e.number
					// Structure: [key, key_duplicate, description, key_duplicate]
					String[] descProps = descPropsStr.split(",");
					
					for (int j = 0; j < descProps.length; j++) {
						// Description columns are at positions: keyCount + keyCount + j
						// Structure: [key, key_dup, desc, key_dup] -> desc at position 2
						int colIndex = keyProps.length + keyProps.length + j;
						if (colIndex >= 0 && colIndex < row.length) {
							Object d = row[colIndex];
							if (d != null) {
								if (value.length() > 0) value.append(" - ");
								value.append(String.valueOf(d).trim());
							}
						}
					}
				}
 
 				el.setDescription(value.toString());
 				el.setShowCode(keyProps.length > 1);
 				if (el.getKey() != null) result.add(el);
 			}
 
 			return result;
 		}
 		catch (Exception e) {
 			return Collections.EMPTY_LIST;
 		}
 	}
 
  
  private int executeQueryCount() throws Exception {
		EntityTab tab = EntityTabFactory.createAllData(getMetaTab());		
		String condition = "";
		if (hasCondition()) {
			condition = getCondition(); 
		}
 
 		Object [] key = null;
 		if (hasParameters()) {
 			key = new Object[getParameters().size()];
 			Iterator it = getParameters().iterator();
 			for (int i=0; i<key.length; i++) {	
 				key[i] = it.next();
 				if (key[i] == null) return 0;
 			}				
 		}						
 		tab.search(condition, key); 
 		return tab.getResultSize();
 	}
		
	private boolean hasCondition() {
		return !Is.emptyString(condition);
	}
	
	private boolean hasOrder() {
		return !Is.emptyString(order);
	}
	
	
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model==null?"":model;
		this.metaModel = null;		
		this.componentName = null;
		this.aggregateName = null;
		StringTokenizer st = new StringTokenizer(this.model, ".");		
		if (st.hasMoreTokens()) this.componentName = st.nextToken();
		if (st.hasMoreTokens()) this.aggregateName = st.nextToken();
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {	    
		if (condition!=null && condition.toLowerCase().indexOf("year(curdate())")>=0){
			Calendar cal=Calendar.getInstance();
			cal.setTime(new java.util.Date());
			condition = Strings.change(condition,"year(curdate())",String.valueOf(cal.get(Calendar.YEAR)));
		}	    
		this.condition = condition;		
	}

	public boolean hasParameters() {
		return parameters != null && !parameters.isEmpty();
	}
	public Collection getParameters() {
		return parameters;
	}

	public void setParameters(Collection parameters) {
		this.parameters = parameters;		
	}
	
	public void setParameters(Collection parameters, IFilter filter) throws FilterException {
		if (filter != null) {
			Object [] param = parameters==null?null:parameters.toArray();			
			param = (Object []) filter.filter(param);
			parameters = param == null ? null : Arrays.asList(param);
		}
		this.parameters = parameters;				
	}
		
	/**
	 * It's used when there are more than one property that
	 * it's key, or with only one It's preferred use a wrapper
	 * class as primary key. <p>
	 * 
	 * It's exclusive with <tt>keyProperties</tt>. 
	 */
	public String getKeyProperties() {
		return Is.emptyString(keyProperties)?getKeyProperty():keyProperties;
	}

	public void setKeyProperties(String keyProperties) {		
		this.keyProperties = keyProperties;
		metaTab = null;
	}


	private String getAggregateName() {
		return aggregateName;
	}

	private String getComponentName() {
		return componentName;
	}
	
	private boolean isAggregate() {
		return !Is.emptyString(aggregateName);
	}
	
	public boolean isOrderByKey() {		
		return orderByKey;
	}

	public void setOrderByKey(boolean b) {		
		orderByKey = b;
	}
	
	public void setOrderByKey(String b) {
		setOrderByKey("true".equalsIgnoreCase(b));
	}


	public String getDescriptionProperties() {
		return Is.emptyString(descriptionProperties)?getDescriptionProperty():descriptionProperties;
	}

	public void setDescriptionProperties(String string) {
		descriptionProperties = string;
		metaTab = null;
	}

	public boolean isUseConvertersInKeys() {
		return useConvertersInKeys;		
	}

	public void setUseConvertersInKeys(boolean b) {
		useConvertersInKeys = b;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {		
		this.order = order;
	}

	/** 
	 * To allow not duplicated results.
	 *  
	 * @since 7.0.3
	 */
	public boolean isDistinct() {
		return distinct;
	}

	/** 
	 * To allow not duplicated results.
	 *  
	 * @since 7.0.3
	 */	
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}
	
	/**
	 * TableModel that wraps processed KeyAndDescription objects
	 */
	private static class KeyDescriptionTableModel implements TableModel {
		private final List<KeyAndDescription> data;
		
		public KeyDescriptionTableModel(List<KeyAndDescription> data) {
			this.data = data;
		}
		
		@Override
		public int getRowCount() {
			return data.size();
		}
		
		@Override
		public int getColumnCount() {
			return 2; // key and description
		}
		
		@Override
		public String getColumnName(int columnIndex) {
			return columnIndex == 0 ? "key" : "description";
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return Object.class;
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex >= data.size()) return null;
			KeyAndDescription item = data.get(rowIndex);
			return columnIndex == 0 ? item.getKey() : item.getDescription();
		}
		
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			// Not supported
		}
		
		@Override
		public void addTableModelListener(TableModelListener l) {
			// Not supported
		}
		
		@Override
		public void removeTableModelListener(TableModelListener l) {
			// Not supported
		}
	}
	
	/**
	 * Empty TableModel for fallback cases
	 */
	private static class SimpleTableModel implements TableModel {
		@Override
		public int getRowCount() { return 0; }
		
		@Override
		public int getColumnCount() { return 0; }
		
		@Override
		public String getColumnName(int columnIndex) { return ""; }
		
		@Override
		public Class<?> getColumnClass(int columnIndex) { return Object.class; }
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) { return false; }
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) { return null; }
		
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
		
		@Override
		public void addTableModelListener(TableModelListener l) {}
		
		@Override
		public void removeTableModelListener(TableModelListener l) {}
	}
	
	/**
	 * Filters descriptions in memory for calculated properties.
	 * This is used when description properties are calculated and can't be filtered at DB level.
	 * 
	 * @param descriptions Collection of KeyAndDescription objects to filter
	 * @param searchTerm Term to search for in descriptions
	 * @param limit Maximum number of results to return
	 * @param offset Number of results to skip
	 * @return Filtered and paginated collection
	 */
	private Collection filterDescriptionsInMemory(Collection descriptions, String searchTerm, int limit, int offset) {
		if (descriptions == null || descriptions.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		
		List filteredResults = new ArrayList();
		String normalizedSearchTerm = searchTerm.toLowerCase().trim();
		
		// Filter descriptions that contain the search term
		Iterator it = descriptions.iterator();
		while (it.hasNext()) {
			Object next = it.next();
			
			if (next instanceof KeyAndDescription) {
				KeyAndDescription item = (KeyAndDescription) next;
				Object descObj = item.getDescription();
				if (descObj != null) {
					String description = String.valueOf(descObj);
					if (description.toLowerCase().contains(normalizedSearchTerm)) {
						filteredResults.add(item);
					}
				}
			}
		}
		
		// Apply pagination to filtered results
		int startIndex = Math.min(offset, filteredResults.size());
		int endIndex = Math.min(startIndex + limit, filteredResults.size());
		
		if (startIndex >= filteredResults.size()) {
			return Collections.EMPTY_LIST;
		}
		
		return filteredResults.subList(startIndex, endIndex);
	}
	
	/**
	 * Checks if any of the description properties are calculated properties.
	 * Calculated properties don't have database columns and must be filtered in memory.
	 * 
	 * @return true if any description property is calculated, false otherwise
	 */
	private boolean hasCalculatedDescriptionProperties() {
		try {
			String descriptionProperties = getDescriptionProperties();
			if (Is.emptyString(descriptionProperties)) {
				return false;
			}
			
			MetaModel metaModel = getMetaModel();
			String[] properties = descriptionProperties.split(",");
			
			for (String property : properties) {
				property = property.trim();
				if (metaModel.containsMetaProperty(property)) {
					MetaProperty metaProperty = metaModel.getMetaProperty(property);
					if (metaProperty.isCalculated()) {
						return true;
					}
				}
			}
			return false;
		} catch (Exception e) {
			// If we can't determine, assume they are calculated to be safe
			return true;
		}
	}
	
	/**
	 * Builds a search condition for filtering descriptions based on the search term.
	 * This method constructs a LIKE condition for the description properties.
	 * Only works for non-calculated properties that have database columns.
	 * 
	 * @param searchTerm The term to search for
	 * @return SQL condition string for filtering, or empty if properties are calculated
	 */
	private String buildSearchCondition(String searchTerm) {
		if (searchTerm == null || searchTerm.trim().isEmpty()) {
			return "";
		}
		
		// Get description properties to build the search condition
		String descriptionProperties = getDescriptionProperties();
		if (Is.emptyString(descriptionProperties)) {
			return "";
		}
		
		// Check if any properties are calculated - if so, we can't filter at DB level
		if (hasCalculatedDescriptionProperties()) {
			return "";
		}
		
		// Build LIKE condition for each description property
		StringBuilder condition = new StringBuilder();
		String[] properties = descriptionProperties.split(",");
		
		for (int i = 0; i < properties.length; i++) {
			if (i > 0) {
				condition.append(" OR ");
			}
			String property = properties[i].trim();
			// Use UPPER for case-insensitive search and ${} for OpenXava property substitution
			condition.append("UPPER(${").append(property).append("}) LIKE UPPER('%").append(searchTerm.replace("'", "''")).append("%')");
		}
		
		if (properties.length > 1) {
			return "(" + condition.toString() + ")";
		} else {
			return condition.toString();
		}
	}

}
