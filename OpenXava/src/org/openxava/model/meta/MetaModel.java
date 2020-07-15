package org.openxava.model.meta;


import java.beans.*;
import java.rmi.*;
import java.util.*;

import org.apache.commons.beanutils.*;
import org.apache.commons.logging.*;
import org.openxava.component.*;
import org.openxava.jpa.*;
import org.openxava.mapping.*;
import org.openxava.model.*;
import org.openxava.util.*;
import org.openxava.util.meta.*;
import org.openxava.validators.meta.*;
import org.openxava.view.meta.*;

/**
 *
 * 
 * @author Javier Paniza
 */
abstract public class MetaModel extends MetaElement {
	
	private static Log log = LogFactory.getLog(MetaModel.class);
	
	private static boolean someModelHasDefaultCalculatorOnCreate = false;
	private static boolean someModelHasPostCreateCalculator = false;
	private static boolean someModelHasPostModifyCalculator = false;
	private static boolean someModelHasPreRemoveCalculator = false;
	private static boolean someModelHasPostLoadCalculator = false;
	private Class pojoClass;
	private Class pojoKeyClass;
	private Collection allKeyPropertiesNames;
	private List metaCalculatorsPostCreate;
	private List metaCalculatorsPostLoad;
	private List metaCalculatorsPostModify;
	private List metaCalculatorsPreRemove;
	private List<String> propertiesNamesWithoutHiddenNorTransient;  
	private String containerReference; 
	private String containerModelName;
	private MetaModel metaModelContainer;
	private Map mapMetaPropertiesView;
	private Collection metaPropertiesViewWithDefaultCalculator;
	private Collection metaValidators;
	private Collection metaValidatorsRemove;
	private MetaComponent metaComponent;
	private transient Map propertyDescriptors;
	private Map mapMetaProperties;
	private Map mapMetaReferences;
	private Map mapMetaColections;
	private Map mapMetaViews;
	private Map mapMetaMethods;
	private Collection<String> membersNames = new ArrayList<String>(); 
	private Collection calculatedPropertiesNames;
	private MetaView metaViewByDefault;
	private boolean pojoGenerated;
	private Collection keyReferencesNames; 
	private Collection<String> keyPropertiesNames; 
	
	private Collection metaPropertiesWithDefaultValueCalculator;
	private List propertiesNames;
	private Collection metaPropertiesWithDefaultValueCalcultaorOnCreate;
	
	private Collection metaFinders;

	private Collection<MetaProperty> metaPropertiesPersistents; 

	private Collection persistentPropertiesNames;
	private Collection interfaces;
	private Collection recursiveQualifiedPropertiesNames;
	private Collection recursiveQualifiedPropertiesNamesUntilSecondLevel; 
	private Collection metaReferencesWithDefaultValueCalculator;
	private String qualifiedName;
	private boolean hasDefaultCalculatorOnCreate = false;
	private String pojoClassName;
	private Collection metaReferencesToEntity;
	private boolean annotatedEJB3;
	private boolean xmlComponent;  
	private String versionPropertyName; 
	private boolean versionPropertyNameObtained = false;
	private Collection metaReferencesKey;
	private Collection metaReferencesKeyAndSearchKey;
	private List<MetaProperty> allMetaPropertiesKey; 
	
	private interface IKeyTester { 
		boolean isKey(MetaProperty property);
		boolean isKey(MetaReference reference);
	}
	private static IKeyTester keyTester = new IKeyTester() {
		
		public boolean isKey(MetaProperty property) {
			return property.isKey();
		}
		
		public boolean isKey(MetaReference reference) {
			return reference.isKey();
		}
		
	};
	private static IKeyTester searchKeyTester = new IKeyTester() {
		
		public boolean isKey(MetaProperty property) {
			return property.isSearchKey();
		}
		
		public boolean isKey(MetaReference reference) {
			return reference.isSearchKey();
		}
		
	};
	private static IKeyTester keyOrSearchKeyTester = new IKeyTester() {
		
		public boolean isKey(MetaProperty property) {
			return property.isKey() || property.isSearchKey();
		}
		
		public boolean isKey(MetaReference reference) {
			return reference.isKey() || reference.isSearchKey();
		}
		
	};
	private static IKeyTester hiddenKeyTester = new IKeyTester() {
		
		public boolean isKey(MetaProperty property) {
			return property.isKey() && property.isHidden();
		}
		
		public boolean isKey(MetaReference reference) {
			return reference.isKey() && reference.isHidden();
		}
		
	};
	
	/**
	 * All models (Entities and Aggregates) with a mapping associated.
	 * @return of type MetaModel
	 * @throws XavaException
	 */
	public static Collection getAllPersistent() throws XavaException {  
		Collection r = new HashSet();
		for (Iterator it = MetaComponent.getAllLoaded().iterator(); it.hasNext();) {
			MetaComponent comp = (MetaComponent) it.next();
			r.add(comp.getMetaEntity());						
			for (Iterator itAggregates = comp.getMetaAggregates().iterator(); itAggregates.hasNext();) {
				MetaAggregate ag = (MetaAggregate) itAggregates.next();
				if (ag instanceof MetaAggregateForCollection) {
					r.add(ag);
				}
			}
		}		
		return r;
	}	
	
	/**
	 * All models (Entities and Aggregates) where its POJO code and Hiberante mapping is generated.
	 * 
	 * @return of type MetaModel 
	 */
	public static Collection getAllPojoGenerated() throws XavaException { 
		Collection r = new HashSet();
		for (Iterator it = MetaComponent.getAll().iterator(); it.hasNext();) {
			MetaComponent comp = (MetaComponent) it.next();
			MetaEntity en = comp.getMetaEntity();
			if (en.isPojoGenerated()) { 
				r.add(en);
			}
									
			for (Iterator itAggregates = comp.getMetaAggregates().iterator(); itAggregates.hasNext();) {
				MetaAggregate ag = (MetaAggregate) itAggregates.next();
				if (ag instanceof MetaAggregateForCollection) { 
					if (ag.isPojoGenerated()) {
						r.add(ag);
					}
				}
			}
		}		
		return r;
	}
	
	
	public void addMetaFinder(MetaFinder metaFinder) {
		if (metaFinders == null) metaFinders = new ArrayList();
		metaFinders.add(metaFinder);		
		metaFinder.setMetaModel(this);
	}
	
	public void addMetaMethod(MetaMethod metaMethod) {		
		if (mapMetaMethods == null) mapMetaMethods = new HashMap();
		mapMetaMethods.put(metaMethod.getName(), metaMethod);
	}
	
	public void addMetaCalculatorPostCreate(MetaCalculator metaCalculator) {		
		if (metaCalculatorsPostCreate == null) metaCalculatorsPostCreate = new ArrayList();		
		metaCalculatorsPostCreate.add(metaCalculator);
		someModelHasPostCreateCalculator = true;
	}
	
	public void addMetaCalculatorPostLoad(MetaCalculator metaCalculator) {		
		if (metaCalculatorsPostLoad == null) metaCalculatorsPostLoad = new ArrayList();		
		metaCalculatorsPostLoad.add(metaCalculator);
		someModelHasPostLoadCalculator = true;
	}
		
	public void addMetaCalculatorPostModify(MetaCalculator metaCalculator) {		
		if (metaCalculatorsPostModify == null) metaCalculatorsPostModify = new ArrayList();		
		metaCalculatorsPostModify.add(metaCalculator);
		someModelHasPostModifyCalculator = true;
	}
	
	public void addMetaCalculatorPreRemove(MetaCalculator metaCalculator) {		
		if (metaCalculatorsPreRemove == null) metaCalculatorsPreRemove = new ArrayList();		
		metaCalculatorsPreRemove.add(metaCalculator);
		someModelHasPreRemoveCalculator = true;
	}
				
	public void addMetaValidator(MetaValidator metaValidator) {
		if (metaValidators == null) metaValidators = new ArrayList();
		metaValidators.add(metaValidator);				
	}
	
	public void addMetaValidatorRemove(MetaValidator metaValidator) {
		if (metaValidatorsRemove == null) metaValidatorsRemove = new ArrayList();
		metaValidatorsRemove.add(metaValidator);				
	}	
	
	/**
	 * If entity the name of component, if aggregate the name of component + the name of
	 * aggregate. <p>
	 */
	public String getQualifiedName() {
		return qualifiedName==null?getName():qualifiedName;
	}
	/**
	 * If entity the name of component, if aggregate the name of component + the name of
	 * aggregate. <p>
	 */	
	public void setQualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}

		
	/**	 
	 * @return Collection of MetaFinder. Not null
	 */
	public Collection getMetaFinders() {
		return metaFinders==null?Collections.EMPTY_LIST:metaFinders;
	}
	
	/**
	 * @return Collection of MetaMethod. Not null
	 */
	public Collection getMetaMethods() {
		return mapMetaMethods==null?Collections.EMPTY_LIST:mapMetaMethods.values();
	}
	
	/**
	 * 
	 * @param newMetaProperty  not null
	 */
	public void addMetaProperty(MetaProperty newMetaProperty) throws XavaException {
		getMapMetaProperties().put(newMetaProperty.getName(), newMetaProperty);
		membersNames.add(newMetaProperty.getName());
		newMetaProperty.setMetaModel(this);
		propertiesNames = null;
		recursiveQualifiedPropertiesNames = null;
		if (newMetaProperty.hasCalculatorDefaultValueOnCreate()) {
			someModelHasDefaultCalculatorOnCreate = true;
			hasDefaultCalculatorOnCreate = true;
		}
	}
	
	public boolean hasDefaultCalculatorOnCreate() {
		return hasDefaultCalculatorOnCreate;
	}
	
	/**
	 * 
	 * @param newMetaReference  not null
	 * @throws XavaException 
	 */
	public void addMetaReference(MetaReference newMetaReference) throws XavaException {
		getMapMetaReferences().put(newMetaReference.getName(), newMetaReference);
		membersNames.add(newMetaReference.getName());
		newMetaReference.setMetaModel(this);
	}
	
	public void addMetaView(MetaView newMetaView) throws XavaException {		
		getMapMetaViews().put(newMetaView.getName(), newMetaView);		
		newMetaView.setModelName(this.getName());
		newMetaView.setMetaModel(this);		
		if (Is.emptyString(newMetaView.getName())) {
			if (this.metaViewByDefault != null) {
				throw new XavaException("no_more_1_default_view", getName());
			}
			this.metaViewByDefault = newMetaView;
		}						
	}	
	
	/**
	 * 
	 * @param newMetaCollection  not null
	 */	
	public void addMetaCollection(MetaCollection newMetaCollection) {
		getMapMetaColections().put(newMetaCollection.getName(), newMetaCollection);
		membersNames.add(newMetaCollection.getName());
		newMetaCollection.setMetaModel(this);
	}
	
	public boolean containsMetaProperty(String property) {
		return getMapMetaProperties().containsKey(property);
	}

	public boolean containsMetaPropertyView(String property) {		
		return getMapMetaPropertiesView().containsKey(property);
	}	
	
	public boolean containsMetaReference(String reference) {
		return getMapMetaReferences().containsKey(reference);
	}
	
	public boolean containsMetaCollection(String collection) {
		return getMapMetaColections().containsKey(collection);
	}
	
	/**
	 * Class that contains the properties defined in this model. <p>
	 * 
	 * @return Not null
	 */
	public Class getPropertiesClass() throws XavaException {
		try {
			return Class.forName(getInterfaceName());
		} 
		catch (ClassNotFoundException ex) {
			return getPOJOClass();
		}
	}
	
	public MetaMember getMetaMember(String name) throws ElementNotFoundException, XavaException {
		try {
			return getMetaProperty(name);
		} catch (ElementNotFoundException ex) {
			try {
				return getMetaReference(name);
			}
			catch (ElementNotFoundException ex2) {
				try {
					return getMetaCollection(name);
				}
				catch (ElementNotFoundException ex3) {
					throw new ElementNotFoundException("member_not_found", name, getName());
				}
			}			
		}
	}
	
	PropertyDescriptor getPropertyDescriptor(String propertyName)
		throws XavaException {
		PropertyDescriptor pd =
			(PropertyDescriptor) getPropertyDescriptors().get(propertyName);
		if (pd == null) {
			throw new ElementNotFoundException("property_not_found", propertyName, getPropertiesClass().getName());
		}
		return pd;
	}
					
	/**
	 * Of the properties.
	 */
	private Map getPropertyDescriptors() throws XavaException {
		if (propertyDescriptors == null) {
			try {
				BeanInfo info = Introspector.getBeanInfo(getPropertiesClass());
				PropertyDescriptor[] pds = info.getPropertyDescriptors();
				propertyDescriptors = new HashMap();
				for (int i = 0; i < pds.length; i++) {
					propertyDescriptors.put(pds[i].getName(), pds[i]);
				}
			} 
			catch (Exception ex) {
				log.error(ex.getMessage(), ex);
				throw new XavaException("property_descriptors_error", getPropertiesClass());
			}
		}
		return propertyDescriptors;
	}
	
	/**
	 * Support qualified for properties of references with dot (.). <p>
	 */
	public MetaProperty getMetaProperty(String name) throws ElementNotFoundException, XavaException {
		MetaProperty r = (MetaProperty) getMapMetaProperties().get(name);		
		if (r == null) {
			int idx = name.indexOf('.');
			if (idx >= 0) {				
				String referenceName = name.substring(0, idx);								
				String propertyName = name.substring(idx + 1);				
				try {
					return getMetaReference(referenceName).getMetaModelReferenced().getMetaProperty(propertyName);
				}
				catch (ElementNotFoundException ex) {
					String collectionName = referenceName;
					return getMetaCollection(collectionName).getMetaReference().getMetaModelReferenced().getMetaProperty(propertyName);
				}
			}
			throw new ElementNotFoundException("property_not_found", name, getName());
		}
		return r;
	}
	
	public MetaProperty getMetaPropertyView(String name) throws ElementNotFoundException, XavaException {
		if (!containsMetaPropertyView(name)) {
			throw new ElementNotFoundException("property_not_found_in_any_view", name, getName());
		}
		return (MetaProperty) getMapMetaPropertiesView().get(name);
	}
		
	private Map getMapMetaProperties() {
		if (mapMetaProperties == null) {
			mapMetaProperties = new HashMap();
		}
		return mapMetaProperties;
	}
	
	private Map getMapMetaViews() {
		if (mapMetaViews == null) {
			mapMetaViews = new HashMap();
		}
		return mapMetaViews;
	}
	
	/**
	 * 
	 * @param name May be qualified, that is myreference.mynestedreference
	 */	
	public MetaReference getMetaReference(String name) throws ElementNotFoundException, XavaException {
		if (name == null) {
			throw new ElementNotFoundException("reference_not_found", "null", getName());
		}
		MetaReference r = (MetaReference) getMapMetaReferences().get(name);
		if (r == null) {
			name = Strings.change(name, "_", "."); 			
			int idx = name.indexOf('.');
			if (idx >= 0) {
				String aggregate = name.substring(0, idx);			
				String nestedReference = name.substring(idx + 1);
				return getMetaReference(aggregate).getMetaModelReferenced().getMetaReference(nestedReference);
			}
			else {
				throw new ElementNotFoundException("reference_not_found", name, getName());
			}
		}
		return r;
	}
		
	public MetaMethod getMetaMethod(String name) throws ElementNotFoundException, XavaException {
		if (mapMetaMethods == null) {
			throw new ElementNotFoundException("method_not_found", name, getName());
		}
		MetaMethod m = (MetaMethod) mapMetaMethods.get(name);
		if (m == null) {
			throw new ElementNotFoundException("method_not_found", name, getName());
		}
		return m;
	}
	
	
	private Map getMapMetaReferences() {
		if (mapMetaReferences == null) {
			mapMetaReferences = new HashMap();
		}
		return mapMetaReferences;
	}
		
	/**
	 * 
	 * @param name May be qualified, that is mycollection.mynestedcollection
	 */
	public MetaCollection getMetaCollection(String name) throws ElementNotFoundException, XavaException {
		MetaCollection r = (MetaCollection) getMapMetaColections().get(name);
		if (r == null) {			
			int idx = name.indexOf('.');
			if (idx >= 0) {
				String collection = name.substring(0, idx);			
				String nestedCollection = name.substring(idx + 1);
				return getMetaCollection(collection).getMetaReference().getMetaModelReferenced().getMetaCollection(nestedCollection);
			}
			else {
				throw new ElementNotFoundException("collection_not_found", name, getName());
			}
		}
		return r;
	}
	
	public MetaView getMetaView(String name) throws ElementNotFoundException, XavaException {
		MetaView r = (MetaView) getMapMetaViews().get(name == null?"":name);		
		if (r == null) {			
			if (Is.emptyString(name)) return getMetaViewByDefault();						
			throw new ElementNotFoundException("view_not_found_in_model", name, getName());
		}
		return r;		
	}	
	
	public Collection getMetaViews() throws XavaException {
		return getMapMetaViews().values();
	}
		
	private Map getMapMetaColections() {
		if (mapMetaColections == null) {
			mapMetaColections = new HashMap();
		}
		return mapMetaColections;
	}
	
	
	/**
	 * Ordered as in component definition file.
	 * @return Not null, read only and serializable
	 */
	public Collection<String> getMembersNames() { 
		return Collections.unmodifiableCollection(membersNames);
		// It is not obtained from map to keep order
	}
	
	public Collection<String> getMembersNamesNestingAggregates() { 
		Collection<String> result = new ArrayList<>();
		for (String member: membersNames) {
			result.add(member);
			if (isReference(member)) {
				MetaReference ref = getMetaReference(member); 
				if (ref.isAggregate()) {
					for (String submember: ref.getMetaModelReferenced().getMembersNamesNestingAggregates()) {
						result.add(member + "." + submember);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * The number of members that are key and hidden. <p>
	 * @return
	 * @throws XavaException 
	 */
	/**
	 * Ordered as in component definition.
	 * @return Not null, read only and serializable
	 */
	public List getPropertiesNames() {
		// We obtain it from memberNames to keep order 
		if (propertiesNames == null) {
			List result = new ArrayList();
			Iterator it = getMembersNames().iterator();
			while (it.hasNext()) {
				Object name = it.next();
				if (getMapMetaProperties().containsKey(name)) {
					result.add(name);
				}
			}		
			propertiesNames = Collections.unmodifiableList(result);
		}
		return propertiesNames;
	}

	/**
	 * @return Not null, read only and serializable
	 */
	public Collection getReferencesNames() {
		// We wrap it inside array for make it serializable		
		return Collections.unmodifiableCollection(new ArrayList(getMapMetaReferences().keySet()));
	}
	
	/**
	 * @return Not null, read only and serializable
	 */
	public Collection getColectionsNames() {
		// We wrap it inside array for make it serializable		
		return Collections.unmodifiableCollection(new ArrayList(getMapMetaColections().keySet()));
	}

	public Collection getEntityReferencesNames() throws XavaException {
		Iterator it = getMapMetaReferences().values().iterator();
		ArrayList result = new ArrayList();
		while (it.hasNext()) {
			MetaReference r = (MetaReference) it.next();
			if (!r.isAggregate()) {
				result.add(r.getName());
			}
		}
		return result;
	}
	
	public Collection getAggregateReferencesNames() throws XavaException {
		Iterator it = getMapMetaReferences().values().iterator();
		ArrayList result = new ArrayList();
		while (it.hasNext()) {
			MetaReference r = (MetaReference) it.next();
			if (r.isAggregate()) {
				result.add(r.getName());
			}
		}
		return result;
	}
	
	
	/**
	 * @return Collection of <tt>MetaCollection</tt>, not null and read only
	 */
	public Collection getMetaCollectionsAgregate() throws XavaException {
		Iterator it = getMapMetaColections().values().iterator();
		ArrayList result = new ArrayList();
		while (it.hasNext()) {
			MetaCollection c = (MetaCollection) it.next();
			if (c.getMetaReference().isAggregate()) {
				result.add(c);
			}
		}
		return Collections.unmodifiableCollection(result);
	}
	
	/**
	 * @return Collection of <tt>String</tt>, not null and read only
	 */
	public Collection getRequiredPropertiesNames() throws XavaException {
		Iterator it = getMetaProperties().iterator();
		ArrayList result = new ArrayList();
		while (it.hasNext()) {
			MetaProperty p = (MetaProperty) it.next();
			if (p.isRequired()) {
				result.add(p.getName());
			}
		}
		return Collections.unmodifiableCollection(result);
	}
	
	/**
	 * @return Collection of <tt>String</tt>, not null and read only
	 */
	public Collection<String> getRequiredMemberNames() throws XavaException { 
		Iterator it = getMembersNames().iterator();
		ArrayList result = new ArrayList();
		while (it.hasNext()) {
			String nombre = (String) it.next();
			if (containsMetaProperty(nombre)) {
				MetaProperty p = getMetaProperty(nombre);
				if (p.isRequired()) { 
					result.add(p.getName());
				}				
			}
			else if (containsMetaReference(nombre)){
				MetaReference ref = getMetaReference(nombre);
				if (ref.isRequired()) {
					result.add(ref.getName());
				}				
			}
		}
		return Collections.unmodifiableCollection(result);
	}
	
	
	/**
	 * Key properties names ordered in declaration order.
	 * 
	 * @return Collection of <tt>String</tt>, not null and read only 
	 */
	public Collection<String> getKeyPropertiesNames() throws XavaException { 
		if (keyPropertiesNames == null) {
			Iterator it = getMembersNames().iterator(); // memberNames to keep order		
			ArrayList result = new ArrayList();
			while (it.hasNext()) {
				String name = (String) it.next();
				if (containsMetaProperty(name)) { 			
					MetaProperty p = (MetaProperty) getMetaProperty(name);
					if (p.isKey()) {
						result.add(name);
					}
				}
			}
			keyPropertiesNames = Collections.unmodifiableCollection(result);
		}
		return keyPropertiesNames;
	}
		
	/**
	 * Key reference names in undetermined order.
	 * 
	 * @return Collection of <tt>String</tt>, not null and read only 
	 */
	public Collection getKeyReferencesNames() throws XavaException {
		if (keyReferencesNames == null) {
			Iterator it = getMetaReferencesKey().iterator();
			ArrayList result = new ArrayList();
			while (it.hasNext()) {
				MetaReference ref = (MetaReference) it.next();
				result.add(ref.getName());
			}
			keyReferencesNames = Collections.unmodifiableCollection(result);
		}
		return keyReferencesNames;
	}
	
	
	/**
	 * Includes qualified properties in case of key references. <p>
	 * 
	 * @return Collection of <tt>String</tt>, not null and read only 
	 */
	public Collection<String> getAllKeyPropertiesNames() throws XavaException {   
		if (allKeyPropertiesNames==null) {
			ArrayList result = new ArrayList();
			Iterator itRef = getMetaMembersKey().iterator();
			while (itRef.hasNext()) {
				MetaMember member = (MetaMember) itRef.next();
				if (member instanceof MetaProperty) {
					result.add(member.getName());
				}
				else { // must be MetaReference
					MetaReference ref = (MetaReference) member; 
					Iterator itProperties = ref.getMetaModelReferenced().getAllKeyPropertiesNames().iterator();
					while (itProperties.hasNext()) {
						result.add(ref.getName() + "." + itProperties.next());
					}
				}
			}
			Collections.sort(result); 
			allKeyPropertiesNames = Collections.unmodifiableCollection(result);						
		}
		return allKeyPropertiesNames;
	}
	
	
	/**
	 * Ordered as in component definition. <p>
	 * 
	 * Calculated properties are included. <br>
	 * 
	 * @return Collection of <tt>String</tt>, not null and read only
	 */
	public List<String> getPropertiesNamesWithoutHiddenNorTransient() throws XavaException { 
		// We get it from memberNames to keep order
		if (propertiesNamesWithoutHiddenNorTransient == null) {
			List result = new ArrayList();
			Iterator it = getMembersNames().iterator();
			while (it.hasNext()) {
				Object name = it.next();				
				MetaProperty p = (MetaProperty) getMapMetaProperties().get(name);
				if (p != null && !p.isHidden() && !p.isTransient()) {
					result.add(name);  
				}									
			}		
			propertiesNamesWithoutHiddenNorTransient = Collections.unmodifiableList(result);
		}
		return propertiesNamesWithoutHiddenNorTransient;
	}
		
	/**
	 * @return Collection of <tt>MetaProperty</tt>, not null and read only
	 */
	public Collection<MetaProperty> getMetaPropertiesKey() throws XavaException { 
		Iterator it = getMembersNames().iterator(); // memberNames to keep order		
		ArrayList result = new ArrayList();
		while (it.hasNext()) {
			String name = (String) it.next();
			if (!containsMetaProperty(name)) continue;
			MetaProperty p = (MetaProperty) getMetaProperty(name);
			if (p.isKey()) {
				result.add(p);
			}
		}
		return Collections.unmodifiableCollection(result);
	}
	
	/**
	 * Key members.
	 * 
	 * @return Collection of <tt>MetaMember</tt>, not null and read only
	 */
	public Collection getMetaMembersKey() throws XavaException {
		Iterator it = getMembersNames().iterator(); 		
		SortedSet result = new TreeSet(); 
		while (it.hasNext()) {
			String name = (String) it.next();
			if (containsMetaProperty(name)) { 			
				MetaProperty p = (MetaProperty) getMetaProperty(name);
				if (p.isKey()) {
					result.add(p);
				}
			}
			else if (containsMetaReference(name)) {
				MetaReference r = (MetaReference) getMetaReference(name);
				if (r.isKey()) {				
					result.add(r);
				}
			}
		}
		return Collections.unmodifiableCollection(result);
	}
	
	/**
	 * Include qualified properties in case of key references. <p<
	 * 
	 * @return Collection of <tt>MetaProperty</tt>, not null and read only
	 */
	public List<MetaProperty> getAllMetaPropertiesKey() throws XavaException { 
		if (allMetaPropertiesKey == null) {
			ArrayList result = new ArrayList(getMetaPropertiesKey());
			Iterator itRef = getMetaReferencesKey().iterator();
			while (itRef.hasNext()) {
				MetaReference ref = (MetaReference) itRef.next();
				Iterator itProperties = ref.getMetaModelReferenced().getAllMetaPropertiesKey().iterator();
				while (itProperties.hasNext()) {
					MetaProperty original = (MetaProperty) itProperties.next();
					original.getMapping(); // Thus the clon will have the mapping
					MetaProperty p = original.cloneMetaProperty();
					p.setName(ref.getName() + "." + p.getName());
					result.add(p);
				}
			}
			allMetaPropertiesKey = Collections.unmodifiableList(result); 
		}
		return allMetaPropertiesKey;
	}
	
	
	/**
	 * @return Collection of <tt>MetaProperty</tt>, not null and read only
	 */
	public Collection getMetaPropertiesCalculated() throws XavaException {
		Iterator it = getMembersNames().iterator(); // memberNames to keep order
		ArrayList result = new ArrayList();
		while (it.hasNext()) {
			String name = (String) it.next();
			if (!containsMetaProperty(name)) continue;			
			MetaProperty p = (MetaProperty) getMetaProperty(name);
			if (p.isCalculated()) {
				result.add(p);
			}
		}
		return Collections.unmodifiableCollection(result);
	}
	
	
	
	
	/**
	 * @return Collection of <tt>String</tt>, not null and read only
	 */
	public Collection getOnlyReadPropertiesNames() throws XavaException {
		Iterator it = getMetaProperties().iterator();
		ArrayList result = new ArrayList();
		while (it.hasNext()) {
			MetaProperty p = (MetaProperty) it.next();
			if (p.isReadOnly()) {
				result.add(p.getName());
			}
		}
		return Collections.unmodifiableCollection(result);
	}
	
	/**
	 * @return Collection of <tt>String</tt>, not null and read only
	 */
	public Collection getOnlyReadWithFormulaPropertiesNames() throws XavaException {
		Iterator it = getMetaProperties().iterator();
		ArrayList result = new ArrayList();
		while (it.hasNext()) {
			MetaProperty p = (MetaProperty) it.next();
			PropertyMapping pMapping =  p.getMapping();
			if (p.isReadOnly() && pMapping != null && pMapping.hasFormula()) {
				result.add(p.getName());
			}
		}
		return Collections.unmodifiableCollection(result);
	}
	
	
	
	/**
	 * @return Collection of <tt>String</tt>, not null and read only
	 */
	public Collection getCalculatedPropertiesNames() {
		if (calculatedPropertiesNames == null) {
			Iterator it = getMetaProperties().iterator();
			ArrayList result = new ArrayList();
			while (it.hasNext()) {
				MetaProperty p = (MetaProperty) it.next();
				if (p.isCalculated()) {
					result.add(p.getName());
				}
			}
			calculatedPropertiesNames = Collections.unmodifiableCollection(result);
		}
		return calculatedPropertiesNames;
	}
	
	public Collection getMetaPropertiesWithDefaultValueCalculator() { 
		if (metaPropertiesWithDefaultValueCalculator == null) {
			Iterator it = getMetaProperties().iterator();
			ArrayList result = new ArrayList();
			while (it.hasNext()) {
				MetaProperty p = (MetaProperty) it.next();
				if (p.hasDefaultValueCalculator()) {
					result.add(p);
				}
			}
			metaPropertiesWithDefaultValueCalculator = Collections.unmodifiableCollection(result);
		}
		return metaPropertiesWithDefaultValueCalculator;
	}
	
	public Collection getMetaPropertiesViewWithDefaultCalculator() {
		if (metaPropertiesViewWithDefaultCalculator == null) {
			Iterator it = getMetaPropertiesView().iterator();
			ArrayList result = new ArrayList();
			while (it.hasNext()) {
				MetaProperty p = (MetaProperty) it.next();
				if (p.hasDefaultValueCalculator()) {
					result.add(p);
				}
			}
			metaPropertiesViewWithDefaultCalculator = Collections.unmodifiableCollection(result);
		}
		return metaPropertiesViewWithDefaultCalculator;
	}
	
	public Collection<MetaReference> getMetaReferencesWithDefaultValueCalculator() { 
		if (metaReferencesWithDefaultValueCalculator == null) {
			Iterator it = getMetaReferences().iterator();
			ArrayList result = new ArrayList();
			while (it.hasNext()) {
				MetaReference ref = (MetaReference) it.next();
				if (ref.hasDefaultValueCalculator()) {
					result.add(ref);
				}
			}
			metaReferencesWithDefaultValueCalculator = Collections.unmodifiableCollection(result);
		}
		return metaReferencesWithDefaultValueCalculator;
	}
	
	
	/**
	 * Ordered as in component definition.
	 */
	public Collection<MetaProperty> getMetaPropertiesPersistents() throws XavaException { 
		if (metaPropertiesPersistents == null) {
			Iterator it = getMembersNames().iterator(); // memberNames to keep order
			ArrayList result = new ArrayList();			
			while (it.hasNext()) {
				String name = (String) it.next();
				if (!containsMetaProperty(name)) continue;			
				MetaProperty p = (MetaProperty) getMetaProperty(name);							
				if (p.isPersistent()) {
					result.add(p);
				}
			}					
			metaPropertiesPersistents = Collections.unmodifiableCollection(result);
		}
		return metaPropertiesPersistents;
	}
	
	/**
	 * Ordered as in component definition.
	 */
	public Collection getMetaPropertiesPersistentsFromReference(String referenceName) throws XavaException {
		Iterator it = getMembersNames().iterator(); // memberNames to keep order
		ArrayList result = new ArrayList();			
		while (it.hasNext()) {
			String name = (String) it.next();
			if (!containsMetaProperty(name)) continue;			
			MetaProperty p = (MetaProperty) getMetaProperty(name).cloneMetaProperty();
			p.setQualifiedName(referenceName + "." + p.getName());
			if (p.isPersistent()) {
				result.add(p);
			}
		}					
		return result;
	}
	
	
	/**
	 * Ordered as in component definition.
	 */
	public Collection getPersistentPropertiesNames() throws XavaException {
		if (persistentPropertiesNames == null) {
			Iterator it = getMembersNames().iterator(); // memberNames to keep order
			ArrayList result = new ArrayList();
			while (it.hasNext()) {
				String name = (String) it.next();
				if (!containsMetaProperty(name)) continue;			
				MetaProperty p = (MetaProperty) getMetaProperty(name);
				if (p.isPersistent()) {
					result.add(name);
				}
			}
			persistentPropertiesNames = result;							
		}		
		return persistentPropertiesNames;
	}
	
	
	
	/**
	 * Excludes calculator that implements <code>IHibernateIdGeneratorCalculator</code>. <p>
	 * 
	 * @return Collection of <tt>String</tt>, not null and read only
	 */
	public Collection<MetaProperty> getMetaPropertiesWithDefaultValueOnCreate() throws XavaException { 
		if (metaPropertiesWithDefaultValueCalcultaorOnCreate == null) {
			Iterator it = getMetaProperties().iterator();
			ArrayList result = new ArrayList();
			while (it.hasNext()) {
				MetaProperty p = (MetaProperty) it.next();
				if (p.hasCalculatorDefaultValueOnCreate() && !p.isDefaultCalculatorHibernateIdGenerator()) {
					result.add(p);
				}
			}
			metaPropertiesWithDefaultValueCalcultaorOnCreate = Collections.unmodifiableCollection(result);
		}
		return metaPropertiesWithDefaultValueCalcultaorOnCreate;
	}	
	
	
	
	/**
	 * @return Collection of <tt>MetaProperty</tt>, not null and read only
	 */
	public Collection<MetaProperty> getMetaProperties() {
		return Collections.unmodifiableCollection(getMapMetaProperties().values());
	}
	
	/**
	 * @return Collection of <tt>MetaReference</tt>, not null and read only
	 */
	public Collection<MetaReference> getMetaReferences() {
		return Collections.unmodifiableCollection(getMapMetaReferences().values());
	}
	
	/**
	 * @return Collection of <tt>MetaReference</tt>, not null and read only
	 */
	public Collection getMetaEntityReferences() throws XavaException {
		Collection result = new ArrayList();
		Iterator it = getMapMetaReferences().values().iterator();
		while (it.hasNext()) {
			MetaReference metaReference = (MetaReference) it.next();
			if (!metaReference.isAggregate()) {
				result.add(metaReference);
			}
		}
		return result;
	}
	
	/**
	 * @return Collection of <tt>MetaReference</tt>, not null and read only
	 */
	public Collection getMetaReferencesWithMapping() throws XavaException {
		Collection result = new ArrayList();
		Iterator it = getMapMetaReferences().values().iterator();
		while (it.hasNext()) {
			MetaReference metaReference = (MetaReference) it.next();			
			if (getMapping().hasReferenceMapping(metaReference)) {
				result.add(metaReference);
			}
		}
		return result;
	}
	
	/**
	 * @return Collection of <tt>MetaReference</tt>, not null and read only
	 */
	public Collection getMetaReferencesToEntity() throws XavaException {
		if (metaReferencesToEntity == null) {
			metaReferencesToEntity = new ArrayList();
			Iterator it = getMapMetaReferences().values().iterator();
			while (it.hasNext()) {
				MetaReference metaReference = (MetaReference) it.next();				
				if (!metaReference.isAggregate()) {
					metaReferencesToEntity.add(metaReference);
				}
			}
		}
		return metaReferencesToEntity;		
	}
	
	
	/**
	 * @return Collection of <tt>MetaReference</tt>, not null and read only
	 */
	public Collection getMetaReferencesKey() throws XavaException { 
		if (metaReferencesKey == null) {
			metaReferencesKey = new ArrayList();
			Iterator it = getMapMetaReferences().values().iterator();
			while (it.hasNext()) {
				MetaReference metaReference = (MetaReference) it.next();
				if (metaReference.isKey()) {
					metaReferencesKey.add(metaReference);
				}
			}					
		}
		return metaReferencesKey;
	}
	
	/**
	 * @return Collection of <tt>MetaReference</tt>, not null and read only
	 */
	public Collection getMetaReferencesKeyAndSearchKey() throws XavaException { 
		if (metaReferencesKeyAndSearchKey == null) {
			metaReferencesKeyAndSearchKey = new ArrayList();
			Iterator it = getMapMetaReferences().values().iterator();
			while (it.hasNext()) {
				MetaReference metaReference = (MetaReference) it.next();
				if (metaReference.isKey() || metaReference.isSearchKey()) {
					metaReferencesKeyAndSearchKey.add(metaReference);
				}
			}		
		}
		return metaReferencesKeyAndSearchKey;
	}
	
	
	/**
	 * @return Collection of <tt>MetaReference</tt>, not null and read only
	 */
	public Collection getMetaAggregateReferences() throws XavaException {
		Collection result = new ArrayList();
		Iterator it = getMapMetaReferences().values().iterator();
		while (it.hasNext()) {
			MetaReference metaReference = (MetaReference) it.next();
			if (metaReference.isAggregate()) {
				result.add(metaReference);
			}
		}
		return result;
	}
	
	
	/**
	 * @return Collection of <tt>MetaCollection</tt>, not null and read only
	 */
	public Collection<MetaCollection> getMetaCollections() { 
		return Collections.unmodifiableCollection(getMapMetaColections().values());
	}
	
	/**
	 * @return Not null. If no view is set, then it will generate a default one
	 */
	public MetaView getMetaViewByDefault() throws XavaException {
		if (metaViewByDefault == null) {
			metaViewByDefault = new MetaView();			
			metaViewByDefault.setModelName(this.getName());
			metaViewByDefault.setMetaModel(this);
			metaViewByDefault.setMembersNames("*");			
			metaViewByDefault.setLabel(getLabel());
			getMapMetaViews().put("", metaViewByDefault);  
		}
		return metaViewByDefault;
	}
	
	/**
	 * Container component of model. <p>
	 *
	 * @return Not null
	 */
	public MetaComponent getMetaComponent() {
		return metaComponent;
	}
	
	/**
	 * Container component of model. <p>
	 * 
	 * @param metaComponent Not null
	 */
	public void setMetaComponent(MetaComponent metaComponent) {
		this.metaComponent = metaComponent;
	}
	
	public boolean isCalculated(String propertyName) throws XavaException {
		boolean r = getCalculatedPropertiesNames().contains(propertyName);
		if (r) return r;
		
		int idx = propertyName.indexOf('.');
		if (idx >= 0) {				
			String refName = propertyName.substring(0, idx);		
			String property = propertyName.substring(idx + 1);		
			return getMetaReference(refName).getMetaModelReferenced().isCalculated(property);
		}
		
		return false;		
	}	

	public String toString() {
		return getName();
	}
		
	public boolean isPojoGenerated() {
		return pojoGenerated;
	}

	public void setPojoGenerated(boolean generated) {		
		this.pojoGenerated = generated;
	}
		
	abstract public ModelMapping getMapping() throws XavaException;
	
	public boolean containsMetaReferenceWithModel(String name) {
		Iterator it = getMetaReferences().iterator();
		while (it.hasNext()) {
			MetaReference r = (MetaReference) it.next();
			if (r.getReferencedModelName().equals(name)) return true;
		}
		return false;
	}
				
	/**
	 * A map with the values that are keys in the sent map. <p> 
	 * 
	 * @param values  Not null
	 * @return Not null
	 */
	public Map extractKeyValues(Map values) throws XavaException {
		return extractKeyValues(keyTester, values, false);
	}
	
	public Map extractSearchKeyValues(Map values) throws XavaException {
		return extractKeyValues(searchKeyTester, values, false); 
	}
	
	/** @since 6.2.2 */
	public Map extractKeyValuesFlattenEmbeddedIds(Map values) throws XavaException { 
		return extractKeyValues(keyTester, values, true); 
	}
	
	private Map extractKeyValues(IKeyTester keyTester, Map values, boolean flattenEmbeddedIds) throws XavaException { 
		Iterator it = values.keySet().iterator();
		Map result = new HashMap();
		while (it.hasNext()) {
			String name = (String) it.next();
			if (isKey(keyTester, name)) {
				if (isReference(name) && getMetaReference(name).isAggregate()) { // @EmbeddedId case  
					if (flattenEmbeddedIds) {
						return (Map) values.get(name);
					}
					else {
						Map embeddedId = new HashMap();
						embeddedId.put(name, values.get(name));
						return embeddedId;
					}
				}
				else {
					result.put(name, values.get(name));
				}
			}
		}
		return result;
	}	
	
	private boolean isKey(IKeyTester keyTester, String name) throws XavaException {   		
		try { 					
			return keyTester.isKey(getMetaProperty(name));
		}
		catch (ElementNotFoundException ex) {					
			try {
				return keyTester.isKey(getMetaReference(name));
			}
			catch (ElementNotFoundException ex2) {
				return false; // If is Metacollection, does no exist or is of another type
			}			
		}				
	}
	

	
	public boolean isKeyOrSearchKey(String name) throws XavaException {
		return isKey(keyOrSearchKeyTester, name);
	}
	
	public boolean isKey(String name) throws XavaException {
		return isKey(keyTester, name);
	}
	
	public boolean isHiddenKey(String name) throws XavaException {
		return isKey(hiddenKeyTester, name); 
	}
	
	public boolean isVersion(String name) throws XavaException {
		try { 					
			return getMetaProperty(name).isVersion();		
		}
		catch (ElementNotFoundException ex) {					
			return false; // If it does no exist or is of another type		
		}				
	}
	
	
	private boolean isReference(String name) throws XavaException { 
		return getMapMetaReferences().containsKey(name);
	}
	
	
	public boolean containsValidadors() {
		return metaValidators != null && !metaValidators.isEmpty();
	}
	
	/**
	 * 
	 * @return Not null
	 */
	public List getMetaCalculatorsPostCreate() {
		return metaCalculatorsPostCreate == null?Collections.EMPTY_LIST:metaCalculatorsPostCreate;
	}
	
	public MetaCalculator getMetaCalculatorPostCreate(int idx) {
		if (metaCalculatorsPostCreate == null) {
			throw new IndexOutOfBoundsException(XavaResources.getString("calculator_out_of_bound", new Integer(idx)));
		}		
		return (MetaCalculator) metaCalculatorsPostCreate.get(idx);
	}
	
	/**
	 * 
	 * @return Not null
	 */
	public List getMetaCalculatorsPostLoad() {
		return metaCalculatorsPostLoad == null?Collections.EMPTY_LIST:metaCalculatorsPostLoad;
	}
	
	public MetaCalculator getMetaCalculatorPostLoad(int idx) {
		if (metaCalculatorsPostLoad == null) {
			throw new IndexOutOfBoundsException(XavaResources.getString("calculator_out_of_bound", new Integer(idx)));
		}		
		return (MetaCalculator) metaCalculatorsPostLoad.get(idx);
	}
	
	/**
	 * 
	 * @return Not null
	 */
	public List getMetaCalculatorsPreRemove() {
		return metaCalculatorsPreRemove == null?Collections.EMPTY_LIST:metaCalculatorsPreRemove;
	}
	
	public MetaCalculator getMetaCalculatorPreRemove(int idx) {
		if (metaCalculatorsPreRemove == null) {
			throw new IndexOutOfBoundsException(XavaResources.getString("calculator_out_of_bound", new Integer(idx)));
		}		
		return (MetaCalculator) metaCalculatorsPreRemove.get(idx);
	}
		
	/**
	 * 
	 * @return Not null
	 */
	public List getMetaCalculatorsPostModify() {
		return metaCalculatorsPostModify == null?Collections.EMPTY_LIST:metaCalculatorsPostModify;
	}
	
	public MetaCalculator getMetaCalculatorPostModify(int idx) {
		if (metaCalculatorsPostModify == null) {
			throw new IndexOutOfBoundsException(XavaResources.getString("calculator_out_of_bound", new Integer(idx)));
		}		
		return (MetaCalculator) metaCalculatorsPostModify.get(idx);
	}
				
	/**
	 * 
	 * @return Not null
	 */
	public Collection getMetaValidators() {
		return metaValidators == null?Collections.EMPTY_LIST:metaValidators;
	}
	
	public Collection getMetaValidatorsRemove() {
		return metaValidatorsRemove == null?Collections.EMPTY_LIST:metaValidatorsRemove;
	}
	
	private Map getMapMetaPropertiesView() {
		if (mapMetaPropertiesView == null) {
			mapMetaPropertiesView = new HashMap();		
			Iterator it = getMapMetaViews().values().iterator();
			while (it.hasNext()) {
				MetaView view = (MetaView) it.next();
				Iterator itProperties = view.getMetaViewProperties().iterator();
				while (itProperties.hasNext()) {
					MetaProperty pr = (MetaProperty) itProperties.next();
					pr.setMetaModel(this); 
					pr.setReadOnly(false);
					mapMetaPropertiesView.put(pr.getName(), pr);
				}												
			}			
		}
		return mapMetaPropertiesView;
	}
		
	/**
	 * Create a POJO corresponding to this model, and populate it with
	 * the sent values in map format. <p>
	 * 
	 * @param values  Values to populate the pojo. Can contains nested maps. Cannot be null 
	 */
	public Object toPOJO(Map values) throws XavaException {
		try {			 
			Object pojo = getPOJOClass().newInstance();
			fillPOJO(pojo, values);
			return pojo;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("to_pojo_error", getName());
		}		
	}
	
	/** 
	 * Fill an already existing POJO corresponding to this model, and populate it with
	 * the sent values in map format. <p>
	 * 
	 * @param values  Values to populate the pojo. Can contain nested maps. Cannot be null 
	 */
	public void fillPOJO(Object pojo, Map values) throws XavaException { 
		try {
			values = Maps.plainToTree(values); 	
			values.remove(MapFacade.MODEL_NAME);
			PropertiesManager pm = new PropertiesManager(pojo);			
			for (Iterator it=values.entrySet().iterator(); it.hasNext();) {
				Map.Entry en = (Map.Entry) it.next();
				if (containsMetaReference((String)en.getKey())) { 
					if (en.getValue() == null) {
						pm.executeSet((String)en.getKey(), null);
						continue;
					}
					MetaReference ref = getMetaReference((String)en.getKey());
					MetaModel referencedModel = ref.getMetaModelReferenced();
					Object referencedObject = pm.executeGet((String)en.getKey());
					Map refValues = (Map) en.getValue(); 
					if (referencedObject == null) {
						if (!ref.isAggregate()) { 							 
							Map key = referencedModel.extractKeyValues(refValues);
							if (Maps.isEmpty(key)) {
								continue;
							}
						}						
						pm.executeSet((String)en.getKey(), referencedModel.toPOJO(refValues));
					}
					else {
						if (ref.isAggregate()) {
							referencedModel.fillPOJO(referencedObject, refValues);
						}
						else {
							Map newKey = referencedModel.extractKeyValues(refValues);
							Map oldKey = referencedModel.toKeyMap(referencedObject); 
							if (newKey.equals(oldKey)) {
								referencedModel.fillPOJO(referencedObject, refValues);
							}
							else {
								pm.executeSet((String)en.getKey(), referencedModel.toPOJO(refValues));
							}
						}
					}
				}
				else if (containsMetaCollection((String)en.getKey())) {
					if (en.getValue() == null) { 
						pm.executeSet((String)en.getKey(), null);
						continue;
					}
					MetaModel referencedModel = getMetaCollection((String)en.getKey()).getMetaReference().getMetaModelReferenced();
					Object referencedObject = pm.executeGet((String)en.getKey());
					
					Collection collection = null;
					if (referencedObject == null) {
						collection = new ArrayList(); // It will fail with Sets						
					}
					else {
						collection = (Collection) referencedObject; 
						collection.clear();
					}					
					
					Collection<Map> collectionValues = (Collection<Map>) en.getValue();
					for (Map elementValues: collectionValues) {
						collection.add(referencedModel.toPOJO(elementValues));
					}							
					pm.executeSet((String)en.getKey(), collection);
				}
				else {
					if (isViewProperty((String)en.getKey())) continue;
					MetaProperty property = getMetaProperty((String)en.getKey());
					if (property.isReadOnly()) continue; 
					Object value = en.getValue();
					try {				
						pm.executeSet((String)en.getKey(), en.getValue());
					}
					catch (IllegalArgumentException ex) {
						if (property.hasValidValues()) {
							if (value instanceof String) {
								value = new Integer(property.getValidValueIndex(value));
								pm.executeSet((String)en.getKey(), value);
							}
							else if (value instanceof Number) {
								value = property.getValidValue(((Number) value).intValue());
								pm.executeSet((String)en.getKey(), value);								
							}
							else throw ex;
						}
						else {
							throw ex;
						}
					}
				}
			}			
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("to_pojo_error", getName()); 
		}		
	}
	
	
	
	/**
	 * Convert an object of this model in a map of values. <p>
	 * 
	 * The model object can be a POJO (of class in {@link #getPOJOClass()}) 
	 * or any object that implements the interface of this model, 
	 * that is, IInvoice, ICustomer, ISeller, etc.
	 * Hence you can use POJOs or EJB2 CMP beans, 
	 * or whatever object that implements the interface.<br> 
	 * 
	 * @param modelObject  
	 * @return if modelObject is null returns an empty map
	 */
	public Map toMap(Object modelObject) throws XavaException {
		return toMap(modelObject, false);
	}
	
	/**
	 * Convert an object of this model in a map of values with its key values. <p>
	 * 
	 * The model object can be a POJO (of class in {@link #getPOJOClass()}) 
	 * or any object that implements the interface of this model, 
	 * that is, IInvoice, ICustomer, ISeller, etc.
	 * Hence you can use POJOs or EJB2 CMP beans, 
	 * or whatever object that implements the interface.<br> 
	 * 
	 * @param modelObject  
	 * @return if modelObject is null returns an empty map
	 */
	public Map toKeyMap(Object modelObject) throws XavaException { 
		return toMap(modelObject, true);
	}
		
	private Map toMap(Object modelObject, boolean onlyKey) throws XavaException { 
		if (modelObject == null) return new HashMap(); 
		
		try {
			PropertiesManager pm = new PropertiesManager(modelObject);
			Map values = new HashMap();
			for (Iterator it = getMetaProperties().iterator(); it.hasNext();) {
				MetaProperty property = (MetaProperty) it.next();
				if (!onlyKey || property.isKey()) {
					values.put(property.getName(), pm.executeGet(property.getName()));
				}
			}
			for (Iterator it = getMetaReferences().iterator(); it.hasNext();) {
				MetaReference reference = (MetaReference) it.next();
				if (!onlyKey || reference.isKey()) {
					values.put(reference.getName(), reference.getMetaModelReferenced().toMap(pm.executeGet(reference.getName()), onlyKey));
				}
			}	
			return values;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("to_map_error");
		}		
	}
	
	
	/**
	 * The string representation of models represented by this meta model, from
	 * a map with its values. <p> 
	 * 
	 * @return null if the sent map is null
	 */
	public String toString(Map key) throws XavaException { 
		if (key == null) return null;		
		return toString(toPOJO(key));
	}

	/**
	 * The string representation of models represented by this meta model, from
	 * a pojo object. <p> 
	 * 
	 * @return null if the sent object is null
	 */	
	public String toString(Object pojo) throws XavaException {  
		if (pojo == null) return null;
		StringBuffer toStringValue = new StringBuffer("[.");
		PropertiesManager pm = new PropertiesManager(pojo);
		for (String propertyName: getAllKeyPropertiesNames()) {			
			try {
				if (isKey(propertyName)) {					
					if (isReference(propertyName)) {
						MetaModel refModel = getMetaReference(propertyName).getMetaModelReferenced();
						toStringValue.append(refModel.toString(pm.executeGet(propertyName)));
					}
					else {
						toStringValue.append(pm.executeGet(propertyName));
					}
					toStringValue.append('.');
				}
			}
			catch (Exception ex) {
				log.error(ex.getMessage(), ex);
				toStringValue.append(" ").append('.');
			}
		}		
		toStringValue.append(']');
		return toStringValue.toString();
	}	
	
	private boolean isViewProperty(String name) { // View properties only exist in XML components
		if (isAnnotatedEJB3()) return false;
		return getMapMetaPropertiesView().containsKey(name);
	}

	public Collection getViewPropertiesNames() {
		return Collections.unmodifiableCollection(getMapMetaPropertiesView().keySet());
	}
	
	public Collection getMetaPropertiesView() {
		return Collections.unmodifiableCollection(getMapMetaPropertiesView().values());
	}
	
	/**
	 * If this is a aggregate the return the container, else the main entity.
	 * 
	 * @return Not null
	 */
	public MetaModel getMetaModelContainer() throws XavaException { 
		if (metaModelContainer == null) {
			if (Is.emptyString(this.containerModelName)) {
				metaModelContainer = getMetaComponent().getMetaEntity();
			}
			else {
				try {
					metaModelContainer = getMetaComponent().getMetaAggregate(this.containerModelName);
				}
				catch (ElementNotFoundException ex) {
					metaModelContainer = getMetaComponent().getMetaEntity();
				}
			}			 			
		}
		return metaModelContainer;
	}
			
	public void setContainerModelName(String modelName) {
		this.containerModelName = modelName;
	}
	public String getContainerModelName() { 
		return containerModelName;
	}
	
	public Collection getMetaCollectionsWithConditionInOthersModels() throws XavaException {
		Collection result = new ArrayList();
		Iterator itComponents = MetaComponent.getAllLoaded().iterator();
		while (itComponents.hasNext()) {
			MetaComponent comp = (MetaComponent) itComponents.next();			
			result.addAll(comp.getMetaEntity().getMetaColectionsWithConditionReferenceTo(getName()));
			Iterator itAggregates = comp.getMetaAggregates().iterator();
			while (itAggregates.hasNext()) {
				MetaAggregate agr = (MetaAggregate) itAggregates.next();
				result.addAll(agr.getMetaColectionsWithConditionReferenceTo(getName()));
			}			
		}
		return result;
	}
	
	public Collection getMetaColectionsWithConditionReferenceTo(String modelName) {
		Collection result = new ArrayList();
		Iterator it = getMetaCollections().iterator();
		while (it.hasNext()) {
			MetaCollection col = (MetaCollection) it.next();
			if (!Is.emptyString(col.getCondition()) && 
				col.getMetaReference().getReferencedModelName().equals(modelName)) {
				result.add(col);		
			}
		}
		return result;
	}
	
	public void addInterfaceName(String name) {
		getInterfacesNames().add(name);
	}
	
	public Collection getInterfacesNames() {
		if (interfaces == null) {
			interfaces = new ArrayList();
			interfaces.add("org.openxava.model.IModel"); 
		}
		return interfaces;
	}
	
	/**
	 * String in java format: comma separate interfaces names 
	 */
	public String getImplements() {
		StringBuffer r = new StringBuffer();
		Iterator it = getInterfacesNames().iterator();
		while (it.hasNext()) {			
			r.append(it.next());
			if (it.hasNext()) r.append(", ");
		}
		return r.toString();
	}


	/**
	 * Does not include <i>Transient</i> properties
	 */
	public Collection getRecursiveQualifiedPropertiesNames() throws XavaException {
		if (recursiveQualifiedPropertiesNames == null) {
			Collection parents = new HashSet();
			parents.add(getName());			
			recursiveQualifiedPropertiesNames = createQualifiedPropertiesNames(parents, "", 4); // The limit cannot be very big because it freezes the add column dialog with plain OpenXava 
																								// and produces OutOfMemoryError with XavaPro on starting module, 
																								// with entities with many nested references
		}
		return recursiveQualifiedPropertiesNames;
	}
	
	/**
	 * Does not include <i>Transient</i> properties
	 * 
	 * @since 4.9
	 */
	public Collection getRecursiveQualifiedPropertiesNamesUntilSecondLevel() throws XavaException { 
		if (recursiveQualifiedPropertiesNamesUntilSecondLevel == null) {
			Collection parents = new HashSet();
			parents.add(getName());			
			recursiveQualifiedPropertiesNamesUntilSecondLevel = createQualifiedPropertiesNames(parents, "", 2);
		}
		return recursiveQualifiedPropertiesNamesUntilSecondLevel;
	}

	
	private Collection createQualifiedPropertiesNames(Collection parents, String prefix, int level) throws XavaException {
		if (level == 0) return Collections.EMPTY_LIST; 
		List result = new ArrayList();		
		for (Iterator it = getMembersNames().iterator(); it.hasNext();) {
			Object name = it.next();
			if (getMapMetaProperties().containsKey(name)) {
				if (getMetaProperty((String)name).isTransient()) continue;
				if (getMetaProperty((String)name).isHidden()) continue; 
				if (Is.emptyString(prefix)) result.add(name);
				else result.add(prefix + name);				
			}
			else if (getMapMetaReferences().containsKey(name)) {
				MetaReference ref = (MetaReference) getMapMetaReferences().get(name);
				if (!parents.contains(ref.getReferencedModelName())) {
					Collection newParents = new HashSet();
					newParents.addAll(parents);
					newParents.add(ref.getReferencedModelName());	
					result.addAll(ref.getMetaModelReferenced().createQualifiedPropertiesNames(newParents, prefix + ref.getName() + ".", level - 1));
				}
			}
		} 
		return result;		
	}

	/**
	 * Gets the MetaModel from its name. <p>
	 * 
	 * Qualified names are supported, that is you can use:
	 * <pre>
	 * MetaModel.get("Invoice.InvoceDetail");
	 * </pre>
	 * If InvoiceDetail is a aggregate of Invoice component.<p>
	 * For obtaining the entity metamodel you can use the name of the component, in this way
	 * <pre>
	 * MetaModel.get("Invoice");
	 * </pre>
	 *
	 * @throws ElementNotFoundException If the component does not have associated any MetaModel
	 * @throws XavaException Any problem
	 */	
	public static MetaModel get(String modelName) throws ElementNotFoundException, XavaException { 
		if (modelName.indexOf('.') < 0) {
			return MetaComponent.get(modelName).getMetaEntity();
		}
		else {
			return MetaAggregate.getAggregate(modelName);
		}
	}

	/**
	 * Gets the MetaModel for the pojo class specified. <p>
	 * 
	 * @throws ElementNotFoundException If the pojo does not have associated any MetaModel
	 * @throws XavaException Any problem
	 */	
	public static MetaModel getForPOJO(Object pojo) throws ElementNotFoundException, XavaException {
		if (pojo == null) {
			throw new ElementNotFoundException("component_for_pojo_not_found", "null");
		}
		if (pojo instanceof IModel) {
			try {
				return ((IModel) pojo).getMetaModel();
			} 
			catch (RemoteException ex) {
				throw new XavaException(ex.getMessage()); // Really difficult 
			} 
		}
		return getForPOJOClass(pojo.getClass());
	}
	
	/**
	 * Gets the MetaModel for the pojo class specified. <p>
	 * 
	 * @throws ElementNotFoundException If the pojoClass does not have associated any MetaModel
	 * @throws XavaException Any problem
	 */
	public static MetaModel getForPOJOClass(Class pojoClass) throws ElementNotFoundException, XavaException {
		String componentName = Strings.lastToken(pojoClass.getName(), ".");
		MetaModel metaModel = get(componentName);
		if (!metaModel.getPOJOClass().equals(pojoClass)) {
			throw new XavaException("component_for_pojo_not_found", pojoClass);
		}
		return metaModel;
	}
	
	/**
	 * To ask if the pojo class has an <code>MetaModel</code> associated.
	 */
	public static boolean existsForPOJOClass(Class pojoClass) throws Exception { 
		try {
			getForPOJOClass(pojoClass);
			return true;
		}
		catch (ElementNotFoundException ex) {
			return false;
		}
	}
	
	
	
	public String getInterfaceName()  throws XavaException {
		return getMetaComponent().getPackageName() + ".I" + getName();
	}
	
	/**
	 * @since 5.6.1
	 */
	public boolean isPOJOAvailable() {
		return getPOJOClassName() != null;
	}
	
	public String getPOJOClassName()  throws XavaException {
		if (pojoClassName != null) return pojoClassName; 
		if (getMetaComponent().getPackageName() == null) return null; 
		return getMetaComponent().getPackageName() + "." + getName();
	}
	public void setPOJOClassName(String pojoClassName)  throws XavaException {
		this.pojoClassName = pojoClassName;
	}
	
	public Class getPOJOClass() throws XavaException { 
		if (pojoClass==null){
			try {
				pojoClass =  Class.forName(getPOJOClassName());
			} 
			catch (Exception ex) {
				log.error(ex.getMessage(), ex); 
				throw new XavaException("create_class_error", getPOJOClassName());
			}
		}
		return pojoClass;
			
	}
	
	public Class getPOJOKeyClass() throws XavaException {
		if (pojoKeyClass==null) return getPOJOClass(); // POJO is used as key too
		return pojoKeyClass;			
	}
	public void setPOJOKeyClass(Class pojoKeyClass) {
		this.pojoKeyClass = pojoKeyClass;
	}
			
	public boolean isAnnotatedEJB3() {
		return annotatedEJB3;
	}

	public void setAnnotatedEJB3(boolean annotatedEJB3) {
		this.annotatedEJB3 = annotatedEJB3;
	}
	
	public String getVersionPropertyName() throws XavaException {
		if (!versionPropertyNameObtained) {
			for (Iterator it=getMetaProperties().iterator(); it.hasNext(); ) {
				MetaProperty pr = (MetaProperty) it.next();
				if (pr.isVersion()) {
					if (versionPropertyName == null) {
						versionPropertyName = pr.getName();
					}
					else {
						throw new XavaException("only_one_version_property", getName());
					}
				}
			}
			versionPropertyNameObtained = true;
		}
		return versionPropertyName;
	}		
	
	public boolean hasVersionProperty() throws XavaException { 
		return getVersionPropertyName() != null;
	}
	
	/**
	 * 
	 * @sincd 5.7.1
	 */
	public boolean hasHiddenKey() { 
		for (MetaProperty p: getMetaPropertiesKey()) {
			if (p.isHidden()) return true;
		}
		return false;
	}


	public static boolean someModelHasDefaultCalculatorOnCreate() {		
		return someModelHasDefaultCalculatorOnCreate;
	}
	
	public static boolean someModelHasPostCreateCalculator() {		
		return someModelHasPostCreateCalculator;
	}
	
	public static boolean someModelHasPostModifyCalculator() {		
		return someModelHasPostModifyCalculator;
	}
	
	public static boolean someModelHasPreRemoveCalculator() {		
		return someModelHasPreRemoveCalculator;
	}	
	
	public static boolean someModelHasPostLoadCalculator() {		
		return someModelHasPostLoadCalculator;
	}

	public String getContainerReference() {
		return containerReference;
	}

	public void setContainerReference(String containerReference) {
		this.containerReference = containerReference;
	}

	/** @since 5.6 */
	public boolean isXmlComponent() {
		return xmlComponent;
	}

	/** @since 5.6 */
	public void setXmlComponent(boolean xmlComponent) {
		this.xmlComponent = xmlComponent;
	}
	
}