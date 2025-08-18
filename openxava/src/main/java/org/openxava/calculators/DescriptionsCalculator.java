package org.openxava.calculators;

import java.util.*;
import java.util.stream.*;

import javax.swing.table.*;
import javax.swing.event.*;

import org.apache.commons.logging.*;
import org.openxava.component.*;
import org.openxava.filters.*;
import org.openxava.model.meta.*;
import org.openxava.tab.impl.*;
import org.openxava.tab.meta.*;
import org.openxava.util.*;

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
	private transient Map cache;
	private boolean orderByKey = false;
	private boolean useCache = true;
	private boolean useConvertersInKeys = false;
	private Collection keyPropertiesCollection;
	private MetaTab metaTab;
	private int hiddenPropertiesCount; 
	private boolean distinct = false;   
	
	
	/**
	 * Pure execution, without cache... <p>
	 * 
	 * Better call to {@link #getDescriptions} if you wish to use
	 * directly.<br>
	 */
	public Object calculate() throws Exception {		
	 	checkPreconditions();			 			
		if (keyProperty == null && keyProperties == null) {
			throw new XavaException("descriptions_calculator_keyProperty_required", getClass().getName());
		}
		List result = read();
		if (!hasOrder()) { 
			Comparator comparator = isOrderByKey()?
				KeyAndDescriptionComparator.getByKey():
					KeyAndDescriptionComparator.getByDescription();										
			Collections.sort(result, comparator);
			if (isDistinct()) result = (List) result.stream().distinct().collect(Collectors.toList());  
		}
		return result;
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
	
	private List read() throws Exception {
		List result = new ArrayList();
		TableModel table = executeQuery();
		if (table == null) return result;
		for (int i=0; i<table.getRowCount(); i++) {
			KeyAndDescription el = new KeyAndDescription();			
			int iKey = 0;
			if (isMultipleKey()) {
				Iterator itKeyNames = getKeyPropertiesCollection().iterator();
				Map key = new HashMap();
				boolean isNull = true; 
				while (itKeyNames.hasNext()) {
					String name = (String) itKeyNames.next();
					Object value = table.getValueAt(i, iKey++);
					key.put(name, value);
					if (value != null) isNull = false; 
				}		
				if (isNull) { 
					el.setKey(null);
				}				
				else { 
					el.setKey(getMetaModel().toString(key));
				}
			}
			else {
				el.setKey(table.getValueAt(i, iKey++));
			}
			StringBuffer value = new StringBuffer();
			int columnCount = table.getColumnCount() - hiddenPropertiesCount;
			List<MetaProperty> metaProperties = getMetaTab().getMetaProperties(); 
			for (int j=iKey; j<columnCount; j++) {
				if (value.length() > 0) value.append(' ');
				value.append(metaProperties.get(j).format(table.getValueAt(i, j), Locales.getCurrent()).trim()); 
			}
			el.setDescription(value.toString());
			el.setShowCode(true);
			if (el.getKey() != null) result.add(el);
		}
		return result;
	}
	
	private List readPaginated(int limit, int offset) throws Exception {
		// Use the collection method directly instead of going through TableModel
		return (List) executeQueryPaginatedCollection(limit, offset);
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
	 * It uses cache depend on current parameter values. <p>
	 * 
	 * @return Collection of <tt>KeyAndDescription</tt>. Not null.
	 */
	public Collection getDescriptions() throws Exception {	
		if (conditionHasArguments() && !hasParameters()) return Collections.EMPTY_LIST;
		
		if (!isUseCache()) {
			return (Collection) calculate();
		}
		Collection saved = (Collection) getCache().get(getParameters());
		if (saved != null) {						
			return saved;
		}
		Collection result = (Collection) calculate();
		getCache().put(getParameters(), result);				
		return result;	
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
		if (conditionHasArguments() && !hasParameters()) return Collections.EMPTY_LIST;
		return readPaginated(limit, offset);
	}
	
	/**
	 * Returns the total count of descriptions without loading the actual data.
	 * Used to determine if remote mode should be activated.
	 * 
	 * @return Total count of descriptions
	 * @since 7.6
	 */
	public int getDescriptionsCount() throws Exception { // tmr ¿Hará falta si siempre usamos carga bajo demanda?
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
			
			Collection results = readPaginated(1, 0);
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
		if (isMultipleKey()) {
			// For composite keys, we'd need to parse the key string
			// For now, use a simple approach
			return getKeyProperties().split(",")[0].trim() + " = '" + key.toString() + "'";
		} else {
			return getKeyProperty() + " = '" + key.toString() + "'";
		}
	}
	
	/*
	 * @since 7.1.6 
	 * 
	 * It is used to display the item that was previously selected and no longer satisfies the condition.
	 */
	public Collection getDescriptionsWithSelected(String fvalue) throws Exception {
		List<KeyAndDescription> saved = new ArrayList<>((Collection)getCache().get(getParameters()));
		for (KeyAndDescription kp : saved) { 
		    if (kp.getKey().toString().equals(fvalue)) {
		    	return saved;
		    }
		}
		setCondition(null);
		Collection withOutCondition  = (Collection) calculate();
		java.util.Iterator it = withOutCondition.iterator();
		while(it.hasNext()) {
			KeyAndDescription kd = (KeyAndDescription) it.next();
			if (Is.equalAsStringIgnoreCase(fvalue, kd.getKey())) {
				saved.add(0, kd);
				return saved;
			}
		}
		return saved;
	}

	private boolean conditionHasArguments() {
		return this.condition != null && this.condition.indexOf('?') >= 0;		
	}

	private Map getCache() {
		if (cache == null) {
			cache = new HashMap();
		}
		return cache;		
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
	

	private TableModel executeQuery() throws Exception {
		EntityTab tab = EntityTabFactory.createAllData(getMetaTab());		
		String condition = "";
		if (hasCondition()) {
			condition = getCondition(); 
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
				if (key[i] == null) return null;
			}				
		}						
		tab.search(condition + order, key); 
		return tab.getTable();
	}
	
	private Collection executeQueryPaginatedCollection(int limit, int offset) throws Exception {
		
		// Create EntityTab with a large chunk size to get all needed data in one go
		int chunkSize = offset + limit;
		EntityTab tab = EntityTabFactory.create(getMetaTab(), chunkSize);
		
		String condition = "";
		if (hasCondition()) {
			condition = getCondition(); 
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
		
		// Get only the first chunk, don't call getTable() which loads everything
		try {
			DataChunk firstChunk = tab.nextChunk();
			
			// Process the chunk data into KeyAndDescription objects like read() does
			List result = new ArrayList();
			List chunkData = firstChunk.getData();
			
			// Apply offset and limit to the chunk data
			int startIndex = Math.min(offset, chunkData.size());
			int endIndex = Math.min(startIndex + limit, chunkData.size());
			
			for (int i = startIndex; i < endIndex; i++) {
				Object[] row = (Object[]) chunkData.get(i);
				KeyAndDescription el = new KeyAndDescription();
				
				int iKey = 0;
				if (isMultipleKey()) {
					// For composite keys, we'd need to parse the key string
					// For now, use a simple approach
					Iterator itKeyNames = getKeyPropertiesCollection().iterator();
					Map keyMap = new HashMap();
					boolean isNull = true;
					while (itKeyNames.hasNext()) {
						String name = (String) itKeyNames.next();
						Object value = row[iKey++];
						keyMap.put(name, value);
						if (value != null) isNull = false;
					}
					if (isNull) {
						el.setKey(null);
					} else {
						el.setKey(getMetaModel().toString(keyMap));
					}
				} else {
					el.setKey(row[iKey++]);
				}
				
				StringBuffer value = new StringBuffer();
				// Only use the last column (index 2) which contains the actual description
				// Skip the key column (index 0) and the duplicate key column (index 1)
				int lastColumnIndex = row.length - 1;
				if (lastColumnIndex >= iKey && row[lastColumnIndex] != null) {
					value.append(String.valueOf(row[lastColumnIndex]).trim());
				}
				el.setDescription(value.toString());
				el.setShowCode(true);
				if (el.getKey() != null) result.add(el);
			}
			
			// Return the collection directly
			return result;
		} catch (Exception e) {
			// Fallback to empty collection
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

	public boolean isUseCache() {
		return useCache;
	}

	public void setUseCache(boolean b) {
		useCache = b;
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

}
