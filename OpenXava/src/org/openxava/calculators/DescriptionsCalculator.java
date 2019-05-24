package org.openxava.calculators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.component.MetaComponent;
import org.openxava.filters.FilterException;
import org.openxava.filters.IFilter;
import org.openxava.model.meta.*;
import org.openxava.tab.impl.EntityTabFactory;
import org.openxava.tab.impl.EntityTab;
import org.openxava.tab.meta.MetaTab;
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
	 * It uses cach� depend on current parameter values. <p>
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

}
