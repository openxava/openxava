package org.openxava.calculators;

import java.text.*;
import java.util.*;
import java.util.Collections;

import javax.ejb.ObjectNotFoundException;

import org.apache.commons.logging.*;
import org.openxava.component.*;
import org.openxava.filters.*;
import org.openxava.model.*;
import org.openxava.model.meta.*;
import org.openxava.tab.impl.*;
import org.openxava.tab.meta.*;
import org.openxava.util.*;
import org.openxava.web.*;

/**
 * It obtain a description collection. <p>
 * 
 * Use tab infrastructure for it, so you can make that this execute
 * within a EJB server or nor configuring tab in xava.properties.
 * 
 * @author Javier Paniza
 */
public class DescriptionsCalculator implements ICalculator {
	
	private static final long serialVersionUID = 3638931156760463239L;

    private static final Log log = LogFactory.getLog(DescriptionsCalculator.class);
	
	private String keyProperty;
	private String keyProperties;
	private String descriptionProperty;
	private String descriptionProperties;
	private String condition;
	private String order;
	private Collection<?> parameters;
	private String model;
	private String componentName;
	private String aggregateName;
	private transient MetaModel metaModel;
	private boolean orderByKey = false;
	private boolean useConvertersInKeys = false;
	private Collection<String> keyPropertiesCollection;
	private MetaTab metaTab;
	private boolean distinct = false;   
	
	/**
	 * Implementation of ICalculator interface.
	 * Uses the new paginated approach for compatibility.
	 */
	public Object calculate() throws Exception {
		// Use paginated loading with a reasonable limit for compatibility
		return getDescriptions(10000, 0);
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
	 * Returns descriptions using on-demand loading with a default pagination window.
	 *
	 * @return Collection of <tt>KeyAndDescription</tt>. Not null.
	 */
	public Collection<KeyAndDescription> getDescriptions() throws Exception { 
		if (conditionHasArguments() && !hasParameters()) return Collections.emptyList();
		// Use paginated loading with a large limit to maintain compatibility
		return getDescriptions(10000, 0);
	}
	
	/**
	 * Returns a paginated collection of descriptions with database-level LIMIT and OFFSET.
	 * This method bypasses cache to ensure fresh data for pagination.
	 * 
	 * @param limit Maximum number of records to return
	 * @param offset Number of records to skip
	 * @return Collection of <tt>KeyAndDescription</tt>. Not null.
	 * @since 7.6
	 */
	public Collection<KeyAndDescription> getDescriptions(int limit, int offset) throws Exception {
		return getDescriptions(limit, offset, null);
	}
	
	    /**
     * Returns a paginated collection of descriptions with database-level LIMIT and OFFSET.
     * This method bypasses cache to ensure fresh data for pagination.
     * 
     * @param limit Maximum number of records to return
     * @param offset Number of records to skip
     * @param searchTerm Term to search for in description properties
     * @return Collection of KeyAndDescription objects matching the search term
     * @throws Exception if there's an error executing the query
     */
    public Collection<KeyAndDescription> getDescriptions(int limit, int offset, String searchTerm) throws Exception {
        // If condition has arguments but no parameters provided (dependent reference not set), return empty
        if (conditionHasArguments() && !hasParameters()) {
            return java.util.Collections.emptyList();
        }
        // When searchTerm is empty, delegate directly
        if (Is.emptyString(searchTerm)) {
            return executeQueryPaginatedCollection(limit, offset, null);
        }

        // If description properties are calculated, we cannot filter at DB level
        // and must fall back to in-memory filtering using the formatted description
        if (hasCalculatedDescriptionProperties()) {
            int window = Math.max(0, offset) + Math.max(0, limit);
            int minScan = 20000; // high cap to avoid missing matches across pages
            if (window < minScan) window = minScan;

            Collection<KeyAndDescription> unfiltered = executeQueryPaginatedCollection(window, 0, null);

            // Normalize term like in list mode: remove accents and lowercase
            String normalized = searchTerm == null ? "" : searchTerm;
            if (XavaPreferences.getInstance().isIgnoreAccentsForStringArgumentsInConditions()) {
                normalized = Strings.removeAccents(normalized);
            }
            normalized = normalized.toLowerCase();
            
            return filterDescriptionsInMemory(unfiltered, normalized, Math.max(0, limit), Math.max(0, offset));
        }

        // Non-calculated description properties: use DB-side filtering reusing
        // MetaTab.buildFilterConditionForContent restricted to descriptionProperties
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
        if (key == null || conditionHasArguments() && !hasParameters()) {
            return null;
        }
        // Parse using configured editor key properties when present; fallback to entity primary key order
        Collection<String> cfgKeys = getKeyPropertiesCollection();
        boolean useCfg = cfgKeys != null && !cfgKeys.isEmpty();
        Map<String, Object> keyValues = useCfg
            ? DescriptionsLists.parseKeyValues(getMetaModel(), cfgKeys, (String) key)
            : DescriptionsLists.parseKeyValues(getMetaModel(), (String) key);
        // If any of the parsed key values is null, the key is incomplete -> return null
        if (keyValues == null) {
            return null;
        }
        for (Object v : keyValues.values()) {
            if (v == null) { 
                return null;
            }
        }
		Collection<String> descriptionsPropertiesNames = Strings.toCollection(getDescriptionProperties());
		Map<String, Map> descriptionsProperties = new HashMap<>();
		boolean tree = false;
		for (String descriptionPropertyName: descriptionsPropertiesNames) {
			if (descriptionPropertyName.contains(".")) {
				tree = true;
			}
			descriptionsProperties.put(descriptionPropertyName, null);
		}
		if (tree) {
			descriptionsProperties = Maps.plainToTree(descriptionsProperties);	
		}
		// Decide lookup strategy: by primary key or by any property (stereotype keys)
		boolean useAnyProperty = false;
		if (useCfg) {
			java.util.Set<String> cfgSet = new java.util.LinkedHashSet<>(cfgKeys);
			java.util.Set<String> pkSet = new java.util.LinkedHashSet<>();
			for (Object n : getMetaModel().getAllKeyPropertiesNames()) pkSet.add(String.valueOf(n));
			useAnyProperty = !cfgSet.equals(pkSet); // if configured keys differ from PKs, search by any property
		}
		Map<String, Object> values;
		try {
			if (useAnyProperty) {
				// MapFacade.getValuesByAnyProperty() does not do tracking by default, maybe an omission or bug in its part, but valid for this case
				values = MapFacade.getValuesByAnyProperty(getMetaModel().getName(), keyValues, descriptionsProperties);
			}
			else {
				values = MapFacade.getValuesNotTracking(getMetaModel().getName(), keyValues, descriptionsProperties);
			}
		}
		catch (ObjectNotFoundException ex) {
			// Rare case because we filter the key to discards nulls, but it could be in some cases
			// for example, an int field using a stereotype without a FK restriction that could 
			// be 0 with no data in the other side
			return null;
		}
		String description = formatAndJoin(values, descriptionsPropertiesNames);
        KeyAndDescription result = new KeyAndDescription();
        result.setKey(key);
        result.setDescription(description);
        return result;
    }    

    /**
     * Returns true if the given value can be parsed to the MetaProperty type.
     * Used to detect which key block (first or duplicated) corresponds to the
     * configured editor key properties (e.g., number) instead of a technical OID.
     */
    private boolean isParsable(MetaProperty metaProperty, Object value) {
        if (value == null) return false;
        try {
            Object parsed = metaProperty.parse(String.valueOf(value));
            if (parsed == null) return false;
            Class<?> expected = metaProperty.getType();
            if (expected != null && expected.isInstance(parsed)) return true;
            // As a fallback, accept the raw value if already matches expected
            return expected != null && expected.isInstance(value);
        } catch (Exception ex) {
            return false;
        }
    }

    
	
	/*
	 * @since 7.1.6 
	 * 
	 * It is used to display the item that was previously selected and no longer satisfies the condition.
	 */
	public Collection<KeyAndDescription> getDescriptionsWithSelected(String fvalue) throws Exception {
		// Try to find the specific item first
		KeyAndDescription selected = findDescriptionByKey(fvalue);
		if (selected != null) {
			List<KeyAndDescription> result = new ArrayList<>();
			result.add(selected);
			// Add some additional items for context
			Collection<KeyAndDescription> additional = getDescriptions(50, 0);
			for (Object item : additional) {
				KeyAndDescription kd = (KeyAndDescription) item;
				if (!kd.getKey().toString().equals(fvalue)) {
					result.add(kd);
				}
			}
			return result;
		}
		// Fallback to regular paginated loading
		return getDescriptions(100, 0);
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
	
	private Collection<String> getKeyPropertiesCollection() {
		if (keyPropertiesCollection == null) {
			keyPropertiesCollection = new ArrayList<>();
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
			for (Iterator it = createConditionAndOrderProperties().iterator(); it.hasNext(); ) {
				extraProperties.append(", ");
				extraProperties.append(it.next());
			}
			metaTab.setPropertiesNames(getKeyProperties() + ", " +  getDescriptionProperties() + extraProperties); 
		}
		return metaTab;
	}
	
	private Collection<String> createConditionAndOrderProperties() { 
		Set<String> result = new HashSet<>();
		if (hasCondition()) {
			extractPropertiesFromSentences(result, getCondition());
		}
		if (hasOrder()) {
			extractPropertiesFromSentences(result, getOrder());
		}		
		return result;
	}
	
	private void extractPropertiesFromSentences(Set<String> result, String sentence) { 		
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
		
	private Collection<KeyAndDescription> executeQueryPaginatedCollection(int limit, int offset, String searchTerm) throws Exception {
        // Avoid running a query with unbound parameters
        if (conditionHasArguments() && !hasParameters()) {
            return java.util.Collections.emptyList();
        }
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
			String descProps = getDescriptionProperties();
			Collection<String> props = Strings.toCollection(descProps);
			String searchCondition = getMetaTab().buildFilterConditionForContent(searchTerm, props);
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
			order = " ORDER BY " + Strings.wrapVariables(getOrder()); 
		}
		else {
			// Default ordering: by description when available unless explicit key ordering is requested
			if (!isOrderByKey()) {
				String descProps = getDescriptionProperties();
				if (!Is.emptyString(descProps) && !hasCalculatedDescriptionProperties()) {
					order = " ORDER BY " + Strings.wrapVariables(descProps);
				} else {
					order = " ORDER BY " + Strings.wrapVariables(getKeyProperties());
				}
			}
			else {
				// Explicit order by key when requested
				order = " ORDER BY " + Strings.wrapVariables(getKeyProperties());
			}
		}

		Object [] key = null;
		if (hasParameters()) {
			key = new Object[getParameters().size()];
			Iterator it = getParameters().iterator();
			for (int i=0; i<key.length; i++) { 
				key[i] = it.next();
				if (key[i] == null) return Collections.emptyList();
			}
		}

		tab.search(condition + order, key);

		DataChunk firstChunk = tab.nextChunk();
		List<KeyAndDescription> result = new ArrayList<>();
		List chunkData = firstChunk == null ? Collections.emptyList() : firstChunk.getData();

		int startIndex = Math.min(Math.max(0, offset), chunkData.size());
		int endIndex = Math.min(startIndex + Math.max(0, limit), chunkData.size());

		        // Pre-calc
        Collection<String> keyProperties = getKeyPropertiesCollection();
        String descriptionProperties = getDescriptionProperties();

        // Retrieve MetaTab properties order and count to compute baseOffset
        java.util.List propertiesOrder;
        try { propertiesOrder = new java.util.ArrayList(getMetaTab().getPropertiesNames()); } catch (Exception ignore) { propertiesOrder = java.util.Collections.emptyList(); }
        int totalPropertiesCount = propertiesOrder.size();

		for (int i = startIndex; i < endIndex; i++) {
			Object[] row = (Object[]) chunkData.get(i);

			// Compute baseOffset for potential leading technical column(s) (e.g., oid)
			int baseOffset = (totalPropertiesCount > 0) ? Math.max(0, row.length - totalPropertiesCount) : 0; // usually 0 or 1

			// Prepare sequences
			            java.util.List<String> keySeq = new java.util.ArrayList<>();
            for (Object kp : keyProperties) keySeq.add(String.valueOf(kp));
            java.util.List<String> descSeq = new java.util.ArrayList<>();
            if (!Is.emptyString(descriptionProperties)) { for (String d : descriptionProperties.split(",")) descSeq.add(d.trim()); }

			// Find key block occurrences and pick the best candidate using parsability
            int keyStartRel = 0;
            if (!propertiesOrder.isEmpty() && !keySeq.isEmpty()) {
                java.util.List<Integer> starts = new java.util.ArrayList<>();
                int bestStart = -1;
                int bestScore = -1;
                for (int s = 0; s + keySeq.size() <= propertiesOrder.size(); s++) {
                    boolean match = true;
                    for (int j = 0; j < keySeq.size(); j++) {
                        if (!String.valueOf(propertiesOrder.get(s + j)).equals(String.valueOf(keySeq.get(j)))) { match = false; break; }
                    }
                    if (!match) continue;
                    starts.add(s);
                    // Score this candidate: how many values parse to their MetaProperty types
                    int score = 0;
                    for (int j = 0; j < keySeq.size(); j++) {
                        String propertyName = String.valueOf(propertiesOrder.get(s + j));
                        MetaProperty metaProperty = getMetaModel().getMetaProperty(propertyName);
                        int col = baseOffset + s + j;
                        Object v = col < row.length ? row[col] : null;
                        if (isParsable(metaProperty, v)) score++;
                    }
                    if (score > bestScore) { bestScore = score; bestStart = s; }
                }
				// Heuristic only for composite keys: if there are two consecutive key blocks (duplicate sequence), pick the second one
				if (keySeq.size() > 1) {
					for (int i2 = 0; i2 + 1 < starts.size(); i2++) {
						if (starts.get(i2 + 1) == starts.get(i2) + keySeq.size()) { bestStart = starts.get(i2 + 1); }
					}
				}
				if (bestStart >= 0) keyStartRel = bestStart; // otherwise keep default 0
			}
			int iKey = baseOffset + keyStartRel;

			KeyAndDescription el = new KeyAndDescription();

            if (isMultipleKey()) {
                // Build composite key string in the format: "[.v1.v2.]" following MetaModel key order (toKeyString order)
                StringBuilder keyBuilder = new StringBuilder();
                keyBuilder.append("[.");
                // Map key block names -> values from the detected contiguous block (propertiesOrder)
                java.util.Map<String, Object> keyValuesByName = new java.util.HashMap<>();
                int blockLen = keySeq.size();
                for (int j = 0; j < blockLen; j++) {
                    String propertyName = String.valueOf(propertiesOrder.get(keyStartRel + j));
                    int col = baseOffset + keyStartRel + j;
                    Object keyValue = col < row.length ? row[col] : null;
                    keyValuesByName.put(propertyName, keyValue);
                }
                // Emit in MetaModel.getAllKeyPropertiesNames() order to match toKeyString() and tests
                int p = 0;
                for (Object on : getMetaModel().getAllKeyPropertiesNames()) {
                    String propertyName = String.valueOf(on);
                    if (!keyValuesByName.containsKey(propertyName)) continue;
                    Object keyValue = keyValuesByName.get(propertyName);
                    if (p > 0) keyBuilder.append('.');
                    keyBuilder.append(keyValue == null ? "" : String.valueOf(keyValue));
                    p++;
                }
                keyBuilder.append(".]");
                String compositeKey = keyBuilder.toString();
                el.setKey(compositeKey);
            } else {
                // Simple key is at iKey
                Object simpleKey = iKey < row.length ? row[iKey] : null;
                el.setKey(simpleKey);
            }

			// Build description using a shared formatter to keep behavior consistent
			            String description;
            if (!descSeq.isEmpty()) {
                int descStartRel = -1;
                if (!propertiesOrder.isEmpty()) {
                    for (int s = 0; s + descSeq.size() <= propertiesOrder.size(); s++) {
                        boolean match = true;
                        for (int j = 0; j < descSeq.size(); j++) {
                            if (!String.valueOf(propertiesOrder.get(s + j)).equals(String.valueOf(descSeq.get(j)))) { match = false; break; }
                        }
                        if (match) { descStartRel = s; break; }
                    }
                }
                java.util.Map<String, Object> valuesByProperty = new java.util.LinkedHashMap<>();
                for (int j = 0; j < descSeq.size(); j++) {
                    String propertyName = descSeq.get(j);
                    int col = (descStartRel < 0 ? -1 : baseOffset + descStartRel + j);
                    Object v = (col < 0 || col >= row.length) ? null : row[col];
                    valuesByProperty.put(propertyName, v);
                }
                description = formatAndJoin(valuesByProperty, descSeq);
            } else {
                description = "";
            }
            el.setDescription(description);

            
            result.add(el);
        }
		// Apply distinct semantics if requested: remove duplicates by key, keeping first occurrence
		if (isDistinct() && !result.isEmpty()) {
			java.util.LinkedHashMap<String, KeyAndDescription> unique = new java.util.LinkedHashMap<>();
			for (KeyAndDescription kd : result) {
				String k = kd.getKey() == null ? "" : kd.getKey().toString();
				if (!unique.containsKey(k)) unique.put(k, kd);
			}
			return new java.util.ArrayList<>(unique.values());
		}

		return result;
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
    return !XCollections.isEmptyOrZero(parameters);
}
	public Collection<?> getParameters() {
		return parameters;
	}

	public void setParameters(Collection<?> parameters) {
		this.parameters = parameters;		
	}
	
	public void setParameters(Collection<?> parameters, IFilter filter) throws FilterException {
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
	 * Filters descriptions in memory for calculated properties.
	 * This is used when description properties are calculated and can't be filtered at DB level.
	 * 
	 * @param descriptions Collection of KeyAndDescription objects to filter
	 * @param searchTerm Term to search for in descriptions
	 * @param limit Maximum number of results to return
	 * @param offset Number of results to skip
	 * @return Filtered and paginated collection
	 */
	private Collection<KeyAndDescription> filterDescriptionsInMemory(Collection<KeyAndDescription> descriptions, String searchTerm, int limit, int offset) {
		if (descriptions == null || descriptions.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<KeyAndDescription> filteredResults = new ArrayList<>();
		String normalizedSearchTerm = searchTerm.toLowerCase().trim();
		boolean ignoreAccentsPref = XavaPreferences.getInstance().isIgnoreAccentsForStringArgumentsInConditions();
		
		// Filter descriptions that contain the search term
		for (KeyAndDescription item : descriptions) {
			Object descObj = item.getDescription();
			if (descObj != null) {
				String description = String.valueOf(descObj);
				if (ignoreAccentsPref) description = Strings.removeAccents(description);
				if (description.toLowerCase().contains(normalizedSearchTerm)) {
					filteredResults.add(item);
				}
			}
		}
		
		// Apply pagination to filtered results
		int startIndex = Math.min(offset, filteredResults.size());
		int endIndex = Math.min(startIndex + limit, filteredResults.size());
		
		if (startIndex >= filteredResults.size()) {
			return Collections.emptyList();
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
     * Formats and joins the given property values like they would be displayed in the UI.
     * Uses WebEditors to format each property, trims each part, concatenates with a single space,
     * and normalizes the final result to NFC to keep consistency across paths.
     */
    private String formatAndJoin(Map<?,?> valuesByProperty, Collection<String> propertyNames) {
        if (propertyNames == null || propertyNames.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String pname : propertyNames) {
			Object v = valuesByProperty.get(pname);
			if (v == null) v = Maps.getValueFromQualifiedName(valuesByProperty, pname);
            try {
                MetaProperty mp = getMetaModel().getMetaProperty(pname);
                String formatted = WebEditors.format(null, mp, v, new Messages(), "");
                v = formatted;
            } catch (Exception ex) {
                log.warn(XavaResources.getString("no_convert_to_string", pname, getMetaModel().getName()), ex);
            }
            if (v != null) {
                if (sb.length() > 0) sb.append(' ');
                sb.append(String.valueOf(v).trim());
            }
        }
        String out = sb.toString();
        try { out = Normalizer.normalize(out, Normalizer.Form.NFC); } catch (Throwable ignore) {}
        return out;
    }

}
