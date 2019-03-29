package org.openxava.tab.meta;

import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.component.*;
import org.openxava.filters.*;
import org.openxava.filters.meta.*;
import org.openxava.mapping.*;
import org.openxava.model.meta.*;
import org.openxava.tab.*;
import org.openxava.util.*;
import org.openxava.util.meta.*;

/**
 * 
 * @author Javier Paniza
 */

public class MetaTab implements java.io.Serializable, Cloneable {
	
	private static Log log = LogFactory.getLog(MetaTab.class); 

	private String defaultOrder;
	private String sQLBaseCondition;
	private Collection metaPropertiesHiddenCalculated;
	private Collection metaPropertiesHidden;
	private String name;
	private MetaComponent metaComponent;
	private List<String> propertiesNames = null; 
	private List<String> propertiesNamesWithKeyAndHidden;
	private List<MetaProperty> metaProperties = null; 
	private List metaPropertiesCalculated = null;
	private String properties; // separated by commas, like in xml file	
	private String select;	
	private Collection tableColumns;
	private String modelName; 
	private MetaModel metaModel;
	private boolean excludeAll = false;
	private boolean excludeByKey = false;
	private MetaFilter metaFilter;
	private IFilter filter;
	private List hiddenPropertiesNames;
	private Collection hiddenTableColumns;
	private String baseCondition;
	private Map metaPropertiesTab;
	private Collection rowStyles;
	private String defaultPropertiesNames;
	private String id;
	private Collection<String> sumPropertiesNames;
	private String editor;
	private String editors;  
	private Set<String> droppedMembers; 


	
	public static String getTitleI18n(Locale locale, String modelName, String tabName) throws XavaException {
		String id = null;
		if (Is.emptyString(tabName)) {
			id = modelName + ".tab.title"; 
		}
		else {
			id = modelName + ".tabs." + tabName + ".title";
		}
		if (Labels.existsExact(id, locale)) { 
			return Labels.get(id, locale);
		}		
		else {
			return null;
		}
	}
	
	public MetaModel getMetaModel() throws XavaException {
		return metaModel;
	}
	public void setMetaModel(MetaModel metaModel) {
		this.metaModel = metaModel;
		this.metaComponent = metaModel.getMetaComponent();
		this.modelName = metaModel.getName();		
	}

	public static MetaTab createDefault(MetaComponent component) throws XavaException {
		MetaTab tab = new MetaTab();
		tab.setMetaComponent(component);
		tab.setDefaultValues(); 
		return tab;
	}
	
	public static MetaTab createDefault(MetaModel metaModel) { 
		MetaTab tab = new MetaTab();
		tab.setMetaModel(metaModel);	
		return tab;
	}

	/**
	 * @return Not null, read only and of type <tt>MetaProperty</tt>.
	 */
	public List<MetaProperty> getMetaProperties() throws XavaException { 
		if (metaProperties == null) {
			metaProperties = namesToMetaProperties(getPropertiesNames());
		}
		return metaProperties;
	}

	public Collection getMetaPropertiesHidden() throws XavaException {
		if (metaPropertiesHidden == null) {
			metaPropertiesHidden = namesToMetaProperties(getHiddenPropertiesNames());
		}
		return metaPropertiesHidden;
	}

	public List<MetaProperty> namesToMetaProperties(Collection names) throws XavaException {
		List<MetaProperty> metaProperties = new ArrayList();
		Iterator it = names.iterator();
		int i = -1;
		while (it.hasNext()) {
			i++;
			String name = (String) it.next();
			MetaProperty metaPropertyTab = null;
			try {
				MetaProperty metaProperty = getMetaModel().getMetaProperty( 
						name).cloneMetaProperty();
				metaProperty.setQualifiedName(name);				
				String labelId = null;
				if (representCollection()) {
					labelId = getId() + "." + name;
					// If there is no specific translation for the collection, 
					// we take the translation from the default tab.
					if (!Labels.existsExact(labelId)) {
						labelId = getIdForDefaultTab() + ".properties." + name;  
					}
				}
				else {
					labelId = getId() + ".properties." + name;
				}
				if (Labels.exists(labelId)) {
					metaProperty.setLabelId(labelId);
				} 
				else if (metaPropertiesTab != null) {
					// By now only the label overwritten from the property of tab 
					metaPropertyTab = (MetaProperty) metaPropertiesTab
							.get(name);
					if (metaPropertyTab != null) {
						metaProperty = metaProperty.cloneMetaProperty();
						metaProperty.setLabel(metaPropertyTab.getLabel());
					}
				}
				else{
					metaProperty.setLabelId(labelId);	// fix: some issues with labels in collection
				}
				metaProperties.add(metaProperty);
			} catch (ElementNotFoundException ex) {
				MetaProperty notInEntity = new MetaProperty();
				notInEntity.setName(name);
				if (name.equals(Tab.GROUP_COUNT_PROPERTY)) notInEntity.setTypeName("java.lang.Long"); 
				else notInEntity.setTypeName("java.lang.Object");
				if (metaPropertyTab != null) {
					notInEntity.setLabel(metaPropertyTab.getLabel());
				}
				metaProperties.add(notInEntity);
			}
		}
		return metaProperties;
	}

	private boolean representCollection() {
		return getName().startsWith(Tab.COLLECTION_PREFIX);
	}

	/**
	 * Hidden ones are not included
	 * 
	 * @return Not null, read only and of type <tt>MetaProperty</tt>.
	 */
	public Collection getMetaPropertiesCalculated() throws XavaException {
		if (metaPropertiesCalculated == null) {
			metaPropertiesCalculated = new ArrayList();
			Iterator it = getMetaProperties().iterator();
			while (it.hasNext()) {
				MetaProperty metaProperty = (MetaProperty) it.next();
				if (metaProperty.isCalculated()) {					
					metaPropertiesCalculated.add(metaProperty);
				}
			}
		}
		return metaPropertiesCalculated;
	}

	/**
	 * 
	 * @return Not null, read only and of type <tt>MetaProperty</tt>.
	 */
	public Collection getMetaPropertiesHiddenCalculated() throws XavaException {
		if (metaPropertiesHiddenCalculated == null) {
			metaPropertiesHiddenCalculated = new ArrayList();
			Iterator it = getMetaPropertiesHidden().iterator();
			while (it.hasNext()) {
				MetaProperty metaProperty = (MetaProperty) it.next();
				if (metaProperty.isCalculated()) {
					metaPropertiesHiddenCalculated.add(metaProperty);
				}
			}
		}
		return metaPropertiesHiddenCalculated;
	}

	public boolean hasCalculatedProperties() throws XavaException {
		return !getMetaPropertiesCalculated().isEmpty();
	}

	/**
	 * @return Not null, read only and of type <tt>String</tt>.
	 */
	public Collection getTableColumns() throws XavaException {
		if (tableColumns == null) {
			tableColumns = getTableColumns(getPropertiesNames());
		}
		return tableColumns;
	}

	/**
	 * @return Not null, read only and of type <tt>String</tt>.
	 */
	public Collection getHiddenTableColumns() throws XavaException {
		if (hiddenTableColumns == null) {
			hiddenTableColumns = getTableColumns(getHiddenPropertiesNames());
			hiddenTableColumns
					.addAll(getCmpFieldsColumnsInMultipleProperties());
		}
		return hiddenTableColumns;
	}

	private Collection getTableColumns(Collection propertyNames)
			throws XavaException {
		Collection tableColumns = new ArrayList();
		Iterator it = propertyNames.iterator();
		while (it.hasNext()) {
			String name = (String) it.next();
			try {
				tableColumns
						.add(getMapping().getQualifiedColumn(name));
			} 
			catch (ElementNotFoundException ex) {
				tableColumns.add("0"); // It will be replaced
			}
		}
		return tableColumns;
	}

	/**
	 * @return Not null, read only of type <tt>String</tt>.
	 */
	public List<String> getPropertiesNames() throws XavaException { 
		if (propertiesNames == null) {
			if (!areAllProperties()) {
				propertiesNames = createPropertiesNames();
			} 
			else {
				propertiesNames = createAllPropertiesNames();
			}
		}
		return propertiesNames;
	}
	
	public List<String> getPropertiesNamesWithKeyAndHidden() throws XavaException { 
		if (propertiesNamesWithKeyAndHidden == null) {
			propertiesNamesWithKeyAndHidden = new ArrayList<String>();
			propertiesNamesWithKeyAndHidden.addAll(getMetaModel().getAllKeyPropertiesNames());
			propertiesNamesWithKeyAndHidden.addAll(getPropertiesNames());
			propertiesNamesWithKeyAndHidden.addAll(getHiddenPropertiesNames());			
		}
		return propertiesNamesWithKeyAndHidden;
	}

	/**
	 * Names of properties that must to exist but is not needed show they
	 * to users.
	 * <p>
	 * Usually are properties used to calcualte others. <br>
	 * The keys are excluded. <br>
	 * 
	 * @return Not null, read only and of type <tt>String</tt>.
	 */
	public List getHiddenPropertiesNames() throws XavaException {
		if (hiddenPropertiesNames == null) {
			hiddenPropertiesNames = getMetaModel().isAnnotatedEJB3()?Collections.EMPTY_LIST:obtainPropertiesNamesUsedToCalculate(); 
		}
		return hiddenPropertiesNames;
	}

	private List obtainPropertiesNamesUsedToCalculate()
			throws XavaException {
		Set result = new HashSet();
		Iterator itProperties = getMetaPropertiesCalculated().iterator();
		while (itProperties.hasNext()) {
			MetaProperty metaProperty = (MetaProperty) itProperties.next();
			if (!metaProperty.hasCalculator())
				continue;
			MetaSetsContainer metaCalculator = metaProperty
					.getMetaCalculator();
			if (!metaCalculator.containsMetaSets())
				continue;
			Iterator itSets = metaCalculator.getMetaSets().iterator();
			while (itSets.hasNext()) {
				MetaSet set = (MetaSet) itSets.next();
				String propertyNameFrom = set.getPropertyNameFrom();
				if (!Is.emptyString(propertyNameFrom)) {
					String qualifiedName = metaProperty.getQualifiedName();
					int idx = qualifiedName.indexOf('.');
					String ref = idx < 0?"":qualifiedName.substring(0, idx + 1);
					String qualifiedPropertyNameFrom = ref + propertyNameFrom;
					if (!getPropertiesNames().contains(qualifiedPropertyNameFrom)) {					
						result.add(qualifiedPropertyNameFrom);
					}					
				}				
			}
		}		
		return new ArrayList(result);
	}

	private boolean areAllProperties() {
		return properties == null || properties.trim().equals("*");
	}

	// assert(!areAllProperties());
	private List createPropertiesNames() {
		StringTokenizer st = new StringTokenizer(removeTotalProperties(properties), ",;");
		List result = new ArrayList();
		while (st.hasMoreTokens()) {
			String name = st.nextToken().trim();		
			if (name.endsWith("+")) {
				name = name.substring(0, name.length() - 1);
			}
			result.add(name);
		}		
		return result;
	}
	
	

	private String removeTotalProperties(String properties) { 
		if (!properties.contains("[")) return properties;
		return properties.replaceAll("\\[[^\\]]*\\]", "");
	}
	
	private List createAllPropertiesNames() throws XavaException {
		List result = new ArrayList();
		for (String member: getMetaModel().getMembersNames()) {
			if (getMetaModel().isHiddenKey(member)) continue;
			if (getMetaModel().containsMetaProperty(member)) {
				MetaProperty property = getMetaModel().getMetaProperty(member);
				if (property.isHidden() || property.isTransient() || property.getSize() == 32) continue; // We assume it is an oid
				result.add(member);
			}
			else if ((getMetaModel().containsMetaReference(member))) {
				if (member.contains("_")) continue; // For references inside aggregates
				MetaReference ref = getMetaModel().getMetaReference(member);
				if (ref.getMetaCollectionFromReferencedModel() != null && representCollection()) continue;
				
				MetaModel refModel = ref.getMetaModelReferenced();
				for (String key: refModel.getKeyPropertiesNames()) {
					if (!refModel.isHiddenKey(key)) {
						result.add(member + "." + key); 
					}
				}
				boolean added = addPropertyIfExists(result, refModel, member, "name", "nombre", "description", "descripcion", "title", "titulo");
				if (!added) {
					for (String property: refModel.getPropertiesNamesWithoutHiddenNorTransient()) {
						if (refModel.isKeyOrSearchKey(property)) continue;
						result.add(member + "." + property);
						break;
					}
				}
			}
		}
		return result;
	}
	
	private boolean addPropertyIfExists(Collection result, MetaModel metaModel, String member, String ... properties) {
		boolean added = false;
		for (String property: properties) {
			if (metaModel.containsMetaProperty(property)) {
				result.add(member + "." + property);
				added = true;
			}
		}
		return added;
	}

	public void setDefaultPropertiesNames(String properties) {
		this.defaultPropertiesNames = properties;
		if (areAllProperties()) setPropertiesNames(properties); 
	}

	/**
	 * Comma separated.
	 */
	public void setPropertiesNames(String properties) {
		this.properties = properties;

		this.propertiesNames = null;		
		this.metaProperties = null;
		this.metaPropertiesHiddenCalculated = null;
		this.metaPropertiesHidden = null;
		this.metaPropertiesCalculated = null;
		this.tableColumns = null;
		this.hiddenPropertiesNames = null;
		this.propertiesNamesWithKeyAndHidden = null;
		this.hiddenTableColumns = null;
		this.metaPropertiesTab = null;
		
		this.select = null;
	}

	ModelMapping getMapping() throws XavaException {		
		return getMetaModel().getMapping();
	}

	public String getSelect() throws XavaException { 
		if (select == null) {  
			select = createSelect();
		}
		return select;
	}

	private String createSelect() throws XavaException {
		if (hasBaseCondition()) {
			String baseCondition = getBaseCondition();
			if (baseCondition.trim().toUpperCase().startsWith("SELECT ")) {
				return baseCondition;
			}
		}		
		// basic select
		StringBuffer select = new StringBuffer("select ");
		Iterator itProperties = getPropertiesNames().iterator();
		while (itProperties.hasNext()) {
			String property = (String) itProperties.next();
			if (Strings.isModelName(property)) select.append("0");	// the property is a table name not column name
			else{
				select.append("${");
				select.append(property);
				select.append('}');
			}
			if (itProperties.hasNext()) select.append(", ");
		}
		Iterator itHiddenProperties = getHiddenPropertiesNames().iterator();
		while (itHiddenProperties.hasNext()) {
			select.append(", ");
			select.append("${");
			select.append(itHiddenProperties.next());
			select.append('}');
		}
		String baseCondition = getBaseCondition();
		if (baseCondition.trim().toUpperCase().startsWith("FROM ")) {
			select.append(' ');
			select.append(baseCondition); 
		}
		else {
			select.append(" from ${");
			select.append(getModelName()); 
			select.append('}');
			select.append(' ');
			if (hasBaseCondition()) {
				select.append(" where ");
				select.append(getBaseCondition());
			}	
		}
		return select.toString();
	}
		
	public Collection getCmpFieldsColumnsInMultipleProperties() 
			throws XavaException {
		Collection cmpFieldsColumnsInMultipleProperties = new ArrayList();
		Iterator it = getMetaProperties().iterator();
		String table = getMapping().getTableToQualifyColumn();
		while (it.hasNext()) {
			MetaProperty p = (MetaProperty) it.next();
			PropertyMapping mapping = p.getMapping();
			if (mapping != null) {
				if (mapping.hasMultipleConverter()) {
					Iterator itFields = mapping.getCmpFields().iterator();
					while (itFields.hasNext()) {
						CmpField field = (CmpField) itFields.next();
						cmpFieldsColumnsInMultipleProperties.add(table + "."
								+ field.getColumn());
					}
				}
			}
		}
		return cmpFieldsColumnsInMultipleProperties;
	}
				
	public MetaComponent getMetaComponent() {
		return metaComponent;
	}

	public void setMetaComponent(MetaComponent component) {
		this.metaComponent = component;
		this.metaModel = this.metaComponent.getMetaEntity();
		this.modelName = this.metaComponent.getName();
	}

	public boolean isExcludeByKey() {
		return excludeByKey;
	}

	public boolean isExcludeAll() {
		return excludeAll;
	}

	public void setExcludeByKey(boolean excludeByKey) {
		this.excludeByKey = excludeByKey;
	}

	public void setExcludeAll(boolean excludeAll) {
		this.excludeAll = excludeAll;
	}

	public String getName() {
		return name == null ? "" : name;
	}

	private boolean hasName() {
		return name != null && !name.trim().equals("");
	}

	public void setName(String name) {
		this.name = name;
		this.id = null;
	}

	public MetaFilter getMetaFilter() {
		return metaFilter;
	}
	public void setMetaFilter(MetaFilter metaFilter) {
		this.metaFilter = metaFilter;
	}

	public boolean hasFilter() {
		return this.metaFilter != null;
	}

	/**
	 * Apply the filter associated to this tab if there is is.
	 */
	Object filterParameters(Object o) throws XavaException {
		if (getMetaFilter() == null)
			return o;
		return getFilter().filter(o);
	}

	private IFilter getFilter() throws XavaException {
		if (filter == null) {
			filter = getMetaFilter().createFilter();
		}
		return filter;
	}

	public void addMetaProperty(MetaProperty metaProperty) {
		if (metaPropertiesTab == null) {
			metaPropertiesTab = new HashMap();
		}
		metaPropertiesTab.put(metaProperty.getName(), metaProperty);
	}

	/**
	 * For dynamically add properties to this tab
	 */
	public void addProperty(String propertyName) {
		if (propertiesNames == null)
			return;
		propertiesNames.add(propertyName);
		resetAfterAddRemoveProperty();
	}

	/**
	 * For dynamically add properties to this tab
	 */
	public void addProperty(int index, String propertyName) {
		if (propertiesNames == null)
			return;
		propertiesNames.add(index, propertyName);
		resetAfterAddRemoveProperty();
	}

	/**
	 * For dynamically remove properties to this tab
	 */
	public void removeProperty(String propertyName) {
		if (propertiesNames == null)
			return;
		propertiesNames.remove(propertyName);
		resetAfterAddRemoveProperty();
	}
	
	/**
	 * @since 5.6
	 */
	public void dropMember(String memberName) { 
		removeMember(memberName);
		if (droppedMembers == null) droppedMembers = new HashSet<String>();
		droppedMembers.add(memberName);
	}

	
	private void removeMember(String memberName) { 
		for (String property: new ArrayList<String>(getPropertiesNames())) {
			if (property.equals(memberName)) {
				removeProperty(property);
				return;
			}
			if (property.startsWith(memberName + ".")) {
				removeProperty(property);
			}
		}
		
	}

	/**
	 * For dynamically remove properties to this tab
	 */
	public void removeProperty(int index) {
		if (propertiesNames == null)
			return;
		propertiesNames.remove(index);
		resetAfterAddRemoveProperty();
	}

	/**
	 * @since 5.2
	 */
	public void moveProperty(int from, int to) { 
		if (propertiesNames == null) return;
		XCollections.move(propertiesNames, from, to);
		resetAfterAddRemoveProperty();
	}
	

	/**
	 * For dynamically remove all properties to this tab
	 */
	public void clearProperties() { 
		if (propertiesNames == null)
			return;
		propertiesNames.clear();
		resetAfterAddRemoveProperty();
	}
	
	public void restoreDefaultProperties() { 
		setPropertiesNames(defaultPropertiesNames); 
		removeDroppedMembers(getPropertiesNames()); 
		resetAfterAddRemoveProperty();
	}
	
	private void resetAfterAddRemoveProperty() {
		metaProperties = null;
		metaPropertiesCalculated = null;
		select = null;
		tableColumns = null;
		hiddenPropertiesNames = null;
		propertiesNamesWithKeyAndHidden = null;
		hiddenTableColumns = null;
	}

	public String getBaseCondition() {
		return baseCondition == null ? "" : baseCondition;
	}

	public void setBaseCondition(String string) {
		baseCondition = string;
		sQLBaseCondition = null;
	}

	public boolean hasBaseCondition() {
		return !Is.emptyString(this.baseCondition);
	}

	/**
	 * Apply the tab filter to sent objects.
	 * <p>
	 * It's used to filter arguments. <br>
	 */
	public Object filter(Object[] objects) throws FilterException,
			XavaException {
		if (getMetaFilter() == null)
			return objects;
		return getMetaFilter().filter(objects);
	}

	public String getId() {
		if (id == null) {
			if (representCollection()) id = getIdForTabOfCollection();			
			else id = getIdForTabOfEntity();
		}
		return id;
	}
	
	private String getIdForTabOfEntity() {
		if (!hasName()) return getIdForDefaultTab();
		return getMetaComponent().getName() + ".tabs." + getName();		
	}
	
	private String getIdForDefaultTab() {
		return getMetaComponent().getName() + ".tab";
	}
	
	private String getIdForTabOfCollection() {
		return getName().replaceFirst(Tab.COLLECTION_PREFIX, getMetaComponent().getName() + ".");
	}

	public String getDefaultOrder() {
		return defaultOrder;
	}

	public void setDefaultOrder(String defaultOrder) {
		this.defaultOrder = defaultOrder;
	}

	public boolean hasDefaultOrder() {
		return !Is.emptyString(this.defaultOrder);
	}

	public MetaTab cloneMetaTab() {
		try {			
			MetaTab r = (MetaTab) clone();
			if (r.metaPropertiesHiddenCalculated != null) {
				r.metaPropertiesHiddenCalculated = new ArrayList(
						metaPropertiesHiddenCalculated);
			}
			if (r.metaPropertiesHidden != null) {
				r.metaPropertiesHidden = new ArrayList(metaPropertiesHidden);
			}
			if (r.propertiesNames != null) {
				r.propertiesNames = new ArrayList(propertiesNames);
			}
			if (r.metaProperties != null) {
				r.metaProperties = new ArrayList(metaProperties);
			}
			if (r.metaPropertiesCalculated != null) {
				r.metaPropertiesCalculated = new ArrayList(
						metaPropertiesCalculated);
			}
			if (r.tableColumns != null) {
				r.tableColumns = new ArrayList(tableColumns);
			}
			if (r.hiddenPropertiesNames != null) {
				r.hiddenPropertiesNames = new ArrayList(hiddenPropertiesNames);
			}
			if (r.propertiesNamesWithKeyAndHidden != null) {
				r.propertiesNamesWithKeyAndHidden = new ArrayList(propertiesNamesWithKeyAndHidden);
			} 
			if (r.hiddenTableColumns != null) {
				r.hiddenTableColumns = new ArrayList(hiddenTableColumns);
			}
			if (r.metaPropertiesTab != null) {
				r.metaPropertiesTab = new HashMap(metaPropertiesTab);
			}
			return r;
		} catch (CloneNotSupportedException ex) {
			throw new RuntimeException(XavaResources.getString("clone_error",
					getClass()));
		}
	}

	public List<String> getRemainingPropertiesNames() throws XavaException { 
		List<String> result = new ArrayList<String>(getMetaModel().getRecursiveQualifiedPropertiesNames()); 
		result.removeAll(getPropertiesNames());
		removeDroppedMembers(result);
		return result;
	}
	
	/**
	 * @since 5.2
	 */
	public List getRemainingPropertiesNamesUntilSecondLevel() throws XavaException {  
		List result = new ArrayList(getMetaModel().getRecursiveQualifiedPropertiesNamesUntilSecondLevel());
		result.removeAll(getPropertiesNames());
		removeDroppedMembers(result); 
		return result;
	}


	private void removeDroppedMembers(List result) { 
		if (droppedMembers == null) return;
		for (String droppedMember: droppedMembers) {
			for (String member: new ArrayList<String>(result)) {
				if (droppedMember.equals(member) || 
					member.startsWith(droppedMember + ".")) 
				{
					result.remove(member);
				}
			}
		}
	}

	public void addMetaRowStyle(MetaRowStyle style) {
		if (rowStyles == null) rowStyles = new ArrayList();
		rowStyles.add(style);
	}
	
	public void setMetaRowStyles(Collection styles) { 
		rowStyles = styles;
	}
	
	public boolean hasRowStyles() {
		return rowStyles != null;
	}
	
	public Collection getMetaRowStyles() {
		return rowStyles==null?Collections.EMPTY_LIST:rowStyles;
	}

	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) throws XavaException {
		this.modelName = modelName;		
		this.metaModel = MetaModel.get(modelName);
		this.metaComponent = this.metaModel.getMetaComponent();
	}

	public void setDefaultValues() { 
		for (MetaTab t: MetaTabsDefaultValues.getMetaTabsForModel(getMetaComponent().getName())) {
			if (t.getMetaFilter() != null && getMetaFilter() == null) setMetaFilter(t.getMetaFilter());
			if (t.getMetaRowStyles() != null && getMetaRowStyles() == null) setMetaRowStyles(t.getMetaRowStyles());
			if (t.properties != null && properties == null) properties = t.properties;
			if (!Is.emptyString(t.getBaseCondition()) && Is.emptyString(getBaseCondition())) setBaseCondition(t.getBaseCondition());
			if (!Is.emptyString(t.getDefaultOrder()) & Is.emptyString(getDefaultOrder())) setDefaultOrder(t.getDefaultOrder());		
			if (!Is.emptyString(t.getEditors()) && Is.emptyString(getEditors())) setEditors(t.getEditors());
		}
	}

	/**
	 * Sum properties names. <p>
	 * 
	 * It was renamed in v4.3 from getTotalPropertiesNames() to getSumPropertiesNames() 
	 * 
	 * @since 4.3
	 */
	public Collection<String> getSumPropertiesNames() {
		if (sumPropertiesNames == null) {
			if (defaultPropertiesNames == null) return Collections.EMPTY_SET;
			StringTokenizer st = new StringTokenizer(removeTotalProperties(defaultPropertiesNames), ",;");
			while (st.hasMoreTokens()) {
				String name = st.nextToken().trim();			
				if (name.endsWith("+")) {
					name = name.substring(0, name.length() - 1);
					if (sumPropertiesNames == null) sumPropertiesNames = new HashSet();
					sumPropertiesNames.add(name);
				}
			}		
			if (sumPropertiesNames == null) sumPropertiesNames = Collections.EMPTY_SET; 
		}
		return sumPropertiesNames;
	}

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public String getEditors() {
		return editors;
	}

	public void setEditors(String editors) {
		this.editors = editors;
	}
		
}

