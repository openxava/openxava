package org.openxava.calculators;

import java.util.*;
import java.text.Normalizer;
import java.util.Collections;

import javax.swing.event.*;
import javax.swing.table.*;

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
	
	private static final long serialVersionUID = 3638931156760463239L;
	
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
        // When searchTerm is empty, delegate directly
        if (Is.emptyString(searchTerm)) {
            return executeQueryPaginatedCollection(limit, offset, null);
        }

        // Try to build a DB-side search condition
        String searchCond = buildSearchCondition(searchTerm);

        // If we can filter in DB, do so
        if (!Is.emptyString(searchCond)) {
            return executeQueryPaginatedCollection(limit, offset, searchTerm);
        }

        // Otherwise, fall back to in-memory filtering (calculated description properties)
        // Fetch a large unfiltered window to cover results beyond the first page
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
		Collection<?> originalParameters = getParameters();
		        String keyCondition = buildKeyCondition(key);
        if (Is.emptyString(keyCondition)) {
            // Safety net: if MetaModel-based build produced empty (e.g. business key not in PK),
            // fall back to calculator keyProperties order also for simple keys
            String calcCond = buildKeyConditionByCalculatorOrder(key);
            if (!Is.emptyString(calcCond)) keyCondition = calcCond;
        }
		
		try {
			// Apply only key condition for exact match search (ignore original filters)
			setCondition(keyCondition);
			// Clear parameters for this search
			setParameters(null);
			
			            Collection<KeyAndDescription> results = executeQueryPaginatedCollection(1, 0, null);
            
            if (results != null && !results.isEmpty()) {
                KeyAndDescription kd = (KeyAndDescription) results.iterator().next();
                // Normalize the key to the incoming one so UI selection matches exactly
                kd.setKey(key);
                return kd;
            }

            // Fallback: try with calculator keyProperties order (stereotypes may serialize like this)
            String altKeyCondition = buildKeyConditionByCalculatorOrder(key);
            if (!Is.emptyString(altKeyCondition) && !altKeyCondition.equals(keyCondition)) {
                
                // Again, only key condition
                setCondition(altKeyCondition);
                setParameters(null);
                results = executeQueryPaginatedCollection(1, 0, null);
                if (results != null && !results.isEmpty()) {
                    KeyAndDescription kd = (KeyAndDescription) results.iterator().next();
                    kd.setKey(key);
                    return kd;
                }
            }
            return null;
        } finally {
			// Restore original condition
			setCondition(originalCondition);
			// Restore original parameters
			setParameters(originalParameters);
		}
	}

	/**
	 * Builds a key condition using MetaModel key property order, but only
	 * including the properties configured for this calculator.
	 */
	private String buildKeyCondition(Object key) {
	    try {
	        String keyStr = key == null ? null : key.toString();
	        String[] parts;
	        if (keyStr != null && keyStr.startsWith("[")) {
	            int len = keyStr.length();
	            if (len >= 3 && keyStr.endsWith("]") && keyStr.charAt(len - 2) == '.') {
	                String inner = keyStr.substring(2, len - 2); // between "[." and ".]"
	                parts = inner.isEmpty() ? new String[0] : inner.split("\\.");
	            } else {
	                parts = new String[] { keyStr };
	            }
	        } else {
	            parts = new String[] { keyStr };
	        }

	        // Explicit simple-key handling: build using calculator key property directly
	        if (!isMultipleKey()) {
	            String propertyName = !Is.emptyString(getKeyProperty()) ? getKeyProperty() : null;
	            if (Is.emptyString(propertyName)) {
	                java.util.Iterator it = getKeyPropertiesCollection().iterator();
	                if (it.hasNext()) propertyName = String.valueOf(it.next());
	            }
	            if (!Is.emptyString(propertyName)) {
	                MetaProperty p = getMetaModel().getMetaProperty(propertyName);
	                String token = (parts != null && parts.length > 0) ? parts[0] : null;
	                Object parsed = token == null ? null : p.parse(token);
	                StringBuilder condition = new StringBuilder();
	                appendConditionPart(condition, propertyName, p, parsed);
	                return condition.toString();
	            }
	        }

	        StringBuilder condition = new StringBuilder();
	        int idx = 0;
	        java.util.Set<String> calcKeys = new java.util.HashSet<>();
	        for (Object k : getKeyPropertiesCollection()) calcKeys.add(String.valueOf(k));
	        int appended = 0;
	        for (Object on : getMetaModel().getAllKeyPropertiesNames()) {
	            String propertyName = String.valueOf(on);
	            if (!calcKeys.contains(propertyName)) continue;
	            MetaProperty p = getMetaModel().getMetaProperty(propertyName);
	            String token = (parts != null && idx < parts.length) ? parts[idx] : null;
	            Object parsed = token == null ? null : p.parse(token);
	            appendConditionPart(condition, propertyName, p, parsed);
	            idx++;
	            appended++;
	        }
	        // Fallback: if none of the MetaModel key properties matched calculator keys (business key not in PK),
	        // build condition directly from calculator keyProperties order
	        if (appended == 0 && !calcKeys.isEmpty()) {
	            condition.setLength(0);
	            idx = 0;
	            for (Object k : getKeyPropertiesCollection()) {
	                String propertyName = String.valueOf(k);
	                MetaProperty p = getMetaModel().getMetaProperty(propertyName);
	                String token = (parts != null && idx < parts.length) ? parts[idx] : null;
	                Object parsed = token == null ? null : p.parse(token);
	                appendConditionPart(condition, propertyName, p, parsed);
	                idx++;
	            }
	        }
	        return condition.toString();
	    }
	    catch (Exception ex) {
	        return (isMultipleKey()? getKeyProperties().split(",")[0].trim(): getKeyProperty())
	            + " = '" + (key==null?"":key.toString()) + "'";
	    }
	}

	/**
	 * Builds a key condition using the calculator's configured keyProperties order.
	 * Useful as a fallback for stereotype-based keys whose serialization order
	 * may follow editor configuration instead of MetaModel order.
	 */
	private String buildKeyConditionByCalculatorOrder(Object key) {
	    try {
	        String keyStr = key == null ? null : key.toString();
	        String[] parts;
	        if (keyStr != null && keyStr.startsWith("[")) {
	            int len = keyStr.length();
	            if (len >= 3 && keyStr.endsWith("]") && keyStr.charAt(len - 2) == '.') {
	                String inner = keyStr.substring(2, len - 2);
	                parts = inner.isEmpty() ? new String[0] : inner.split("\\.");
	            } else {
	                parts = new String[] { keyStr };
	            }
	        } else {
	            parts = new String[] { keyStr };
	        }

	        StringBuilder condition = new StringBuilder();
	        int idx = 0;
	        for (Object k : getKeyPropertiesCollection()) {
	            String propertyName = String.valueOf(k);
	            MetaProperty p = getMetaModel().getMetaProperty(propertyName);
	            String token = (parts != null && idx < parts.length) ? parts[idx] : null;
	            Object parsed = token == null ? null : p.parse(token);
	            appendConditionPart(condition, propertyName, p, parsed);
	            idx++;
	        }
	        return condition.toString();
	    }
	    catch (Exception ex) {
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

    /**
     * Returns true if the given value can be parsed to the MetaProperty type.
     * Used to detect which key block (first or duplicated) corresponds to the
     * configured editor key properties (e.g., number) instead of a technical OID.
     */
    private boolean isParsable(MetaProperty mp, Object value) {
        if (value == null) return false;
        try {
            Object parsed = mp.parse(String.valueOf(value));
            if (parsed == null) return false;
            Class<?> expected = mp.getType();
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
			order = " ORDER BY " + Strings.wrapVariables(getOrder()); 
		}
		else if (!isOrderByKey()) {
			order = " ORDER BY " + Strings.wrapVariables(getDescriptionProperties());
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
		Collection<String> keyPropsCol = getKeyPropertiesCollection();
		String descPropsStr = getDescriptionProperties();

		// Retrieve MetaTab properties order and count to compute baseOffset
		java.util.List propsOrder;
		try { propsOrder = new java.util.ArrayList(getMetaTab().getPropertiesNames()); } catch (Exception ignore) { propsOrder = java.util.Collections.emptyList(); }
		int propsCountTotal = propsOrder.size();

		for (int i = startIndex; i < endIndex; i++) {
			Object[] row = (Object[]) chunkData.get(i);

			// Compute baseOffset for potential leading technical column(s) (e.g., oid)
			int baseOffset = (propsCountTotal > 0) ? Math.max(0, row.length - propsCountTotal) : 0; // usually 0 or 1

			// Prepare sequences
			java.util.List<String> keySeq = new java.util.ArrayList<>();
			for (Object kp : keyPropsCol) keySeq.add(String.valueOf(kp));
			java.util.List<String> descSeq = new java.util.ArrayList<>();
			if (!Is.emptyString(descPropsStr)) { for (String d : descPropsStr.split(",")) descSeq.add(d.trim()); }

			// Find key block occurrences and pick the best candidate using parsability
			int keyStartRel = 0;
			if (!propsOrder.isEmpty() && !keySeq.isEmpty()) {
				java.util.List<Integer> starts = new java.util.ArrayList<>();
				int bestStart = -1;
				int bestScore = -1;
				for (int s = 0; s + keySeq.size() <= propsOrder.size(); s++) {
					boolean match = true;
					for (int j = 0; j < keySeq.size(); j++) {
						if (!String.valueOf(propsOrder.get(s + j)).equals(String.valueOf(keySeq.get(j)))) { match = false; break; }
					}
					if (!match) continue;
					starts.add(s);
					// Score this candidate: how many values parse to their MetaProperty types
					int score = 0;
					for (int j = 0; j < keySeq.size(); j++) {
						String pname = String.valueOf(propsOrder.get(s + j));
						MetaProperty mp = getMetaModel().getMetaProperty(pname);
						int col = baseOffset + s + j;
						Object v = col < row.length ? row[col] : null;
						if (isParsable(mp, v)) score++;
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
				StringBuilder ksb = new StringBuilder();
				ksb.append("[.");
				// Map key block names -> values from the detected contiguous block (propsOrder)
				java.util.Map<String, Object> keyValuesByName = new java.util.HashMap<>();
				int blockLen = keySeq.size();
				for (int j = 0; j < blockLen; j++) {
					String pname = String.valueOf(propsOrder.get(keyStartRel + j));
					int col = baseOffset + keyStartRel + j;
					Object kv = col < row.length ? row[col] : null;
					keyValuesByName.put(pname, kv);
				}
				// Emit in MetaModel.getAllKeyPropertiesNames() order to match toKeyString() and tests
				int p = 0;
				for (Object on : getMetaModel().getAllKeyPropertiesNames()) {
					String pname = String.valueOf(on);
					if (!keyValuesByName.containsKey(pname)) continue;
					Object kv = keyValuesByName.get(pname);
					if (p > 0) ksb.append('.');
					ksb.append(kv == null ? "" : String.valueOf(kv));
					p++;
				}
				ksb.append(".]");
				String compositeKey = ksb.toString();
				el.setKey(compositeKey);
			} else {
				// Simple key is at iKey
				Object simpleKey = iKey < row.length ? row[iKey] : null;
				el.setKey(simpleKey);
			}

			// Build description from the first contiguous occurrence of descriptionProperties
			StringBuilder sb = new StringBuilder();
			if (!descSeq.isEmpty()) {
				int descStartRel = -1;
				if (!propsOrder.isEmpty()) {
					for (int s = 0; s + descSeq.size() <= propsOrder.size(); s++) {
						boolean match = true;
						for (int j = 0; j < descSeq.size(); j++) {
							if (!String.valueOf(propsOrder.get(s + j)).equals(String.valueOf(descSeq.get(j)))) { match = false; break; }
						}
						if (match) { descStartRel = s; break; }
					}
				}
				for (int j = 0; j < descSeq.size(); j++) {
					int col = (descStartRel < 0 ? -1 : baseOffset + descStartRel + j);
					if (col < 0 || col >= row.length) continue;
					Object v = row[col];
					if (v != null) {
						if (sb.length() > 0) sb.append(' ');
						// Trim to avoid padding from fixed-length CHAR columns
						sb.append(String.valueOf(v).trim());
					}
				}
			}
			String descStr = sb.toString();
            // Ensure composed form so comparisons/tests match (e.g., E + \u0301 -> É)
            try { descStr = Normalizer.normalize(descStr, Normalizer.Form.NFC); } catch (Throwable ignore) {}
            el.setDescription(descStr);

            
            result.add(el);
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
		return parameters != null && !parameters.isEmpty();
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
		        // Normalize parameter according to preferences (remove accents, optional upper)
        String value = searchTerm.trim();
        if (XavaPreferences.getInstance().isIgnoreAccentsForStringArgumentsInConditions()) {
            value = Strings.removeAccents(value);
        }
        if (XavaPreferences.getInstance().isToUpperForStringArgumentsInConditions()) {
            value = value.toUpperCase();
        }
        String like = "%" + value.replace("'", "''") + "%";

        // Build condition using translateSQLFunction for the column side when configured
        StringBuilder condition = new StringBuilder();
        String[] properties = descriptionProperties.split(",");
        boolean ignoreAccents = XavaPreferences.getInstance().isIgnoreAccentsForStringArgumentsInConditions();
        boolean toUpper = XavaPreferences.getInstance().isToUpperForStringArgumentsInConditions();

        for (int i = 0; i < properties.length; i++) {
            if (i > 0) condition.append(" OR ");
            String property = properties[i].trim();
            String columnExpr = "${" + property + "}";
            try {
                if (ignoreAccents) {
                    // Apply DB-side accent normalization like list mode
                    columnExpr = getMetaModel().getMetaComponent().getEntityMapping().translateSQLFunction(columnExpr);
                }
            } catch (Exception ex) {
                // If mapping is not available for some reason, fallback to raw column
            }
            if (toUpper) {
                columnExpr = "upper(" + columnExpr + ")";
            }
            condition.append(columnExpr).append(" LIKE '").append(like).append("'");
        }

        if (properties.length > 1) {
            return "(" + condition.toString() + ")";
        } else {
            return condition.toString();
        }
    }

}
