package org.openxava.model.meta;


import java.beans.*;
import java.rmi.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.component.*;
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
	private Collection<String> allKeyPropertiesNames; 
	private Collection<String> allKeyPropertiesNamesOrderedAsInModel; 
	private List<MetaCalculator> metaCalculatorsPostCreate;
	private List<MetaCalculator> metaCalculatorsPostLoad;
	private List<MetaCalculator> metaCalculatorsPostModify;
	private List<MetaCalculator> metaCalculatorsPreRemove;
	private List<String> propertiesNamesWithoutHiddenNorTransient;  
	private String containerReference; 
	private String containerModelName;
	private MetaModel metaModelContainer;
	private Map<String, MetaProperty> mapMetaPropertiesView;
	private Collection<MetaProperty> metaPropertiesViewWithDefaultCalculator;
	private Collection<MetaValidator> metaValidators;
	private Collection<MetaValidator> metaValidatorsRemove;
	private MetaComponent metaComponent;
	private transient Map<String, PropertyDescriptor> propertyDescriptors;
	private Map<String, MetaProperty> mapMetaProperties;
	private Map<String, MetaReference> mapMetaReferences;
	private Map<String, MetaCollection> mapMetaCollections;
	private Map<String, MetaView> mapMetaViews;
	private Map<String, MetaMethod> mapMetaMethods;
	private Collection<String> membersNames = new ArrayList<String>(); 
	private Collection<String> calculatedPropertiesNames;
	private MetaView metaViewByDefault;
	private MetaView metaViewOnlyKeys; 
	private boolean pojoGenerated;
	private Collection<String> keyReferencesNames; 
	private Collection<String> keyPropertiesNames; 
	private Collection<String> searchKeyPropertiesNames;
	
	private Collection<MetaProperty> metaPropertiesWithDefaultValueCalculator;
	private List<String> propertiesNames;
	private Collection<MetaProperty> metaPropertiesWithDefaultValueCalcultaorOnCreate;
	
	private Collection<MetaFinder> metaFinders;

	private Collection<MetaProperty> metaPropertiesPersistents; 

	private Collection<String> persistentPropertiesNames;
	private Collection<String> interfaces;
	private Collection<String> recursiveQualifiedPropertiesNames;
	private Collection<String> recursiveQualifiedPropertiesNamesUntilSecondLevel;
	private Collection<String> recursiveQualifiedPropertiesNamesIncludingCollections;
	private Collection<String> recursiveQualifiedPropertiesNamesUntilSecondLevelIncludingCollections;
	private Collection<MetaReference> metaReferencesWithDefaultValueCalculator;
	private String qualifiedName;
	private boolean hasDefaultCalculatorOnCreate = false;
	private String pojoClassName;
	private Collection<MetaReference> metaReferencesToEntity;
	private boolean annotatedEJB3;
	private boolean xmlComponent;  
	private String versionPropertyName; 
	private boolean versionPropertyNameObtained = false;
	private Collection<MetaReference> metaReferencesKey;
	private Collection<MetaReference> metaReferencesKeyAndSearchKey;
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
	public static Collection<MetaModel> getAllPersistent() throws XavaException {  
		Collection<MetaModel> r = new HashSet<>();
		for (Iterator<MetaComponent> it = MetaComponent.getAllLoaded().iterator(); it.hasNext();) {
			MetaComponent comp = it.next();
			r.add(comp.getMetaEntity());						
			for (Iterator<MetaAggregate> itAggregates = comp.getMetaAggregates().iterator(); itAggregates.hasNext();) {
				MetaAggregate ag = itAggregates.next();
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
	public static Collection<MetaModel> getAllPojoGenerated() throws XavaException { 
		Collection<MetaModel> r = new HashSet<>();
		for (Iterator<MetaComponent> it = MetaComponent.getAll().iterator(); it.hasNext();) {
			MetaComponent comp = it.next();
			MetaEntity en = comp.getMetaEntity();
			if (en.isPojoGenerated()) { 
				r.add(en);
			}
									
			for (Iterator<MetaAggregate> itAggregates = comp.getMetaAggregates().iterator(); itAggregates.hasNext();) {
				MetaAggregate ag = itAggregates.next();
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
		if (metaFinders == null) metaFinders = new ArrayList<>();
		metaFinders.add(metaFinder);		
		metaFinder.setMetaModel(this);
	}
	
	public void addMetaMethod(MetaMethod metaMethod) {		
		if (mapMetaMethods == null) mapMetaMethods = new HashMap<>();
		mapMetaMethods.put(metaMethod.getName(), metaMethod);
	}
	
	public void addMetaCalculatorPostCreate(MetaCalculator metaCalculator) {		
		if (metaCalculatorsPostCreate == null) metaCalculatorsPostCreate = new ArrayList<>();		
		metaCalculatorsPostCreate.add(metaCalculator);
		someModelHasPostCreateCalculator = true;
	}
	
	public void addMetaCalculatorPostLoad(MetaCalculator metaCalculator) {		
		if (metaCalculatorsPostLoad == null) metaCalculatorsPostLoad = new ArrayList<>();		
		metaCalculatorsPostLoad.add(metaCalculator);
		someModelHasPostLoadCalculator = true;
	}
		
	public void addMetaCalculatorPostModify(MetaCalculator metaCalculator) {		
		if (metaCalculatorsPostModify == null) metaCalculatorsPostModify = new ArrayList<>();		
		metaCalculatorsPostModify.add(metaCalculator);
		someModelHasPostModifyCalculator = true;
	}
	
	public void addMetaCalculatorPreRemove(MetaCalculator metaCalculator) {		
		if (metaCalculatorsPreRemove == null) metaCalculatorsPreRemove = new ArrayList<>();		
		metaCalculatorsPreRemove.add(metaCalculator);
		someModelHasPreRemoveCalculator = true;
	}
				
	public void addMetaValidator(MetaValidator metaValidator) {
		if (metaValidators == null) metaValidators = new ArrayList<>();
		metaValidators.add(metaValidator);				
	}
	
	public void addMetaValidatorRemove(MetaValidator metaValidator) {
		if (metaValidatorsRemove == null) metaValidatorsRemove = new ArrayList<>();
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
	public Collection<MetaFinder> getMetaFinders() {
		return metaFinders==null?Collections.emptyList():metaFinders;
	}
	
	/**
	 * @return Collection of MetaMethod. Not null
	 */
	public Collection<MetaMethod> getMetaMethods() {
		return mapMetaMethods==null?Collections.emptyList():mapMetaMethods.values();
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
		getMapMetaCollections().put(newMetaCollection.getName(), newMetaCollection);
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
		return getMapMetaCollections().containsKey(collection);
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
	private Map<String, PropertyDescriptor> getPropertyDescriptors() throws XavaException {
		if (propertyDescriptors == null) {
			try {
				BeanInfo info = Introspector.getBeanInfo(getPropertiesClass());
				PropertyDescriptor[] pds = info.getPropertyDescriptors();
				propertyDescriptors = new HashMap<>();
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
		
	private Map<String, MetaProperty> getMapMetaProperties() {
		if (mapMetaProperties == null) {
			mapMetaProperties = new HashMap<>();
		}
		return mapMetaProperties;
	}
	
	private Map<String, MetaView> getMapMetaViews() {
		if (mapMetaViews == null) {
			mapMetaViews = new HashMap<>();
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
	
	
	private Map<String, MetaReference> getMapMetaReferences() {
		if (mapMetaReferences == null) {
			mapMetaReferences = new HashMap<>();
		}
		return mapMetaReferences;
	}
		
	/**
	 * 
	 * @param name May be qualified, that is mycollection.mynestedcollection
	 */
	public MetaCollection getMetaCollection(String name) throws ElementNotFoundException, XavaException {
		MetaCollection r = (MetaCollection) getMapMetaCollections().get(name);
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
			MetaModel parent = getParentMetaModel();
			if (parent != null) return parent.getMetaView(name);
			throw new ElementNotFoundException("view_not_found_in_model", name, getName());
		}
		return r;		
	}	
	
	public Collection<MetaView> getMetaViews() throws XavaException {
		return getMapMetaViews().values();
	}
		
	private Map<String, MetaCollection> getMapMetaCollections() {
		if (mapMetaCollections == null) {
			mapMetaCollections = new HashMap<>();
		}
		return mapMetaCollections;
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
	public List<String> getPropertiesNames() {
		// We obtain it from memberNames to keep order 
		if (propertiesNames == null) {
			List<String> result = new ArrayList<>();
			Iterator<String> it = getMembersNames().iterator();
			while (it.hasNext()) {
				String name = it.next();
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
	public Collection<String> getReferencesNames() {
		// We wrap it inside array for make it serializable		
		return Collections.unmodifiableCollection(new ArrayList<>(getMapMetaReferences().keySet()));
	}
	
	/**
	 * @return Not null, read only and serializable
	 */
	public Collection<String> getColectionsNames() {
		// We wrap it inside array for make it serializable		
		return Collections.unmodifiableCollection(new ArrayList<>(getMapMetaCollections().keySet()));
	}

	public Collection<String> getEntityReferencesNames() throws XavaException {
		Iterator<MetaReference> it = getMapMetaReferences().values().iterator();
		ArrayList<String> result = new ArrayList<>();
		while (it.hasNext()) {
			MetaReference r = it.next();
			if (!r.isAggregate()) {
				result.add(r.getName());
			}
		}
		return result;
	}
	
	public Collection<String> getAggregateReferencesNames() throws XavaException {
		Iterator<MetaReference> it = getMapMetaReferences().values().iterator();
		ArrayList<String> result = new ArrayList<>();
		while (it.hasNext()) {
			MetaReference r = it.next();
			if (r.isAggregate()) {
				result.add(r.getName());
			}
		}
		return result;
	}
	
	
	/**
	 * @return Collection of <tt>MetaCollection</tt>, not null and read only
	 */
	public Collection<MetaCollection> getMetaCollectionsAgregate() throws XavaException {
		Iterator<MetaCollection> it = getMapMetaCollections().values().iterator();
		ArrayList<MetaCollection> result = new ArrayList<>();
		while (it.hasNext()) {
			MetaCollection c = it.next();
			if (c.getMetaReference().isAggregate()) {
				result.add(c);
			}
		}
		return Collections.unmodifiableCollection(result);
	}
	
	/**
	 * @return Collection of <tt>String</tt>, not null and read only
	 */
	public Collection<String> getRequiredPropertiesNames() throws XavaException {
		Iterator<MetaProperty> it = getMetaProperties().iterator();
		ArrayList<String> result = new ArrayList<>();
		while (it.hasNext()) {
			MetaProperty p = it.next();
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
		Iterator<String> it = getMembersNames().iterator();
		ArrayList<String> result = new ArrayList<>();
		while (it.hasNext()) {
			String nombre = it.next();
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
	 * SearchKey properties names ordered in declaration order.
	 * 
	 * @since 7.4.4
	 */
	public Collection<String> getSarchKeyPropertiesNames() throws XavaException { 
		if (searchKeyPropertiesNames == null) {
			Iterator<String> it = getMembersNames().iterator(); // memberNames to keep order		
			ArrayList<String> result = new ArrayList<>();
			while (it.hasNext()) {
				String name = it.next();
				if (containsMetaProperty(name)) { 	
					MetaProperty p = getMetaProperty(name);
					if (p.isSearchKey()) {
						result.add(name);
					}
				}
			}
			searchKeyPropertiesNames = Collections.unmodifiableCollection(result);
		}
		return searchKeyPropertiesNames;
	}
	
	
	/**
	 * Key properties names ordered in declaration order.
	 * 
	 * @return Collection of <tt>String</tt>, not null and read only 
	 */
	public Collection<String> getKeyPropertiesNames() throws XavaException { 
		if (keyPropertiesNames == null) {
			Iterator<String> it = getMembersNames().iterator(); // memberNames to keep order		
			ArrayList<String> result = new ArrayList<>();
			while (it.hasNext()) {
				String name = it.next();
				if (containsMetaProperty(name)) { 			
					MetaProperty p = getMetaProperty(name);
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
	public Collection<String> getKeyReferencesNames() throws XavaException {
		if (keyReferencesNames == null) {
			Iterator<MetaReference> it = getMetaReferencesKey().iterator();
			ArrayList<String> result = new ArrayList<>();
			while (it.hasNext()) {
				MetaReference ref = it.next();
				result.add(ref.getName());
			}
			keyReferencesNames = Collections.unmodifiableCollection(result);
		}
		return keyReferencesNames;
	}
	
	
	/**
	 * Includes qualified properties in case of key references, ordered alphabetically. <p>
	 * 
	 * @return Collection of <tt>String</tt>, not null and read only 
	 */
	public Collection<String> getAllKeyPropertiesNames() throws XavaException {   
		if (allKeyPropertiesNames==null) {
			allKeyPropertiesNames = Collections.unmodifiableCollection(new TreeSet<String>(getAllKeyPropertiesNamesOrderedAsInModel()));
		}
		return allKeyPropertiesNames;		
	}
	
	/**
	 * Includes qualified properties in case of key references, ordered as in model. <p>
	 * 
	 * @since 7.2.1 
	 * @return Collection of <tt>String</tt>, not null and read only 
	 */
	public Collection<String> getAllKeyPropertiesNamesOrderedAsInModel() throws XavaException {    
		if (allKeyPropertiesNamesOrderedAsInModel==null) {
			ArrayList<String> result = new ArrayList<>();
			Iterator<MetaMember> itRef = getMetaMembersKey().iterator();
			while (itRef.hasNext()) {
				MetaMember member = itRef.next();
				if (member instanceof MetaProperty) {
					result.add(member.getName());
				}
				else { // must be MetaReference
					MetaReference ref = (MetaReference) member; 
					Iterator<String> itProperties = ref.getMetaModelReferenced().getAllKeyPropertiesNamesOrderedAsInModel().iterator();
					while (itProperties.hasNext()) {
						result.add(ref.getName() + "." + itProperties.next());
					}
				}
			}
			allKeyPropertiesNamesOrderedAsInModel = Collections.unmodifiableCollection(result);						
		}
		return allKeyPropertiesNamesOrderedAsInModel;		
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
			List<String> result = new ArrayList<>();
			Iterator<String> it = getMembersNames().iterator();
			while (it.hasNext()) {
				String name = it.next();				
				MetaProperty p = getMapMetaProperties().get(name);
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
		Iterator<String> it = getMembersNames().iterator(); // memberNames to keep order		
		ArrayList<MetaProperty> result = new ArrayList<>();
		while (it.hasNext()) {
			String name = it.next();
			if (!containsMetaProperty(name)) continue;
			MetaProperty p = getMetaProperty(name);
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
	public Collection<MetaMember> getMetaMembersKey() throws XavaException {
		Iterator<String> it = getMembersNames().iterator(); 		
		Collection<MetaMember> result = new ArrayList<>(); 
		while (it.hasNext()) {
			String name = it.next();
			if (containsMetaProperty(name)) { 			
				MetaProperty p = getMetaProperty(name);
				if (p.isKey()) {
					result.add(p);
				}
			}
			else if (containsMetaReference(name)) {
				MetaReference r = getMetaReference(name);
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
			ArrayList<MetaProperty> result = new ArrayList<>(getMetaPropertiesKey());
			Iterator<MetaReference> itRef = getMetaReferencesKey().iterator();
			while (itRef.hasNext()) {
				MetaReference ref = itRef.next();
				Iterator<MetaProperty> itProperties = ref.getMetaModelReferenced().getAllMetaPropertiesKey().iterator();
				while (itProperties.hasNext()) {
					MetaProperty original = itProperties.next();
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
	public Collection<MetaProperty> getMetaPropertiesCalculated() throws XavaException {
		Iterator<String> it = getMembersNames().iterator(); // memberNames to keep order
		ArrayList<MetaProperty> result = new ArrayList<>();
		while (it.hasNext()) {
			String name = it.next();
			if (!containsMetaProperty(name)) continue;			
			MetaProperty p = getMetaProperty(name);
			if (p.isCalculated()) {
				result.add(p);
			}
		}
		return Collections.unmodifiableCollection(result);
	}
	
	
	
	
	/**
	 * @return Collection of <tt>String</tt>, not null and read only
	 */
	public Collection<String> getOnlyReadPropertiesNames() throws XavaException {
		Iterator<MetaProperty> it = getMetaProperties().iterator();
		ArrayList<String> result = new ArrayList<>();
		while (it.hasNext()) {
			MetaProperty p = it.next();
			if (p.isReadOnly()) {
				result.add(p.getName());
			}
		}
		return Collections.unmodifiableCollection(result);
	}
	
	/**
	 * @return Collection of <tt>String</tt>, not null and read only
	 */
	public Collection<String> getOnlyReadWithFormulaPropertiesNames() throws XavaException {
		Iterator<MetaProperty> it = getMetaProperties().iterator();
		ArrayList<String> result = new ArrayList<>();
		while (it.hasNext()) {
			MetaProperty p = it.next();
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
	public Collection<String> getCalculatedPropertiesNames() {
		if (calculatedPropertiesNames == null) {
			Iterator<MetaProperty> it = getMetaProperties().iterator();
			ArrayList<String> result = new ArrayList<>();
			while (it.hasNext()) {
				MetaProperty p = it.next();
				if (p.isCalculated()) {
					result.add(p.getName());
				}
			}
			calculatedPropertiesNames = Collections.unmodifiableCollection(result);
		}
		return calculatedPropertiesNames;
	}
	
	public Collection<MetaProperty> getMetaPropertiesWithDefaultValueCalculator() { 
		if (metaPropertiesWithDefaultValueCalculator == null) {
			Iterator<MetaProperty> it = getMetaProperties().iterator();
			ArrayList<MetaProperty> result = new ArrayList<>();
			while (it.hasNext()) {
				MetaProperty p = it.next();
				if (p.hasDefaultValueCalculator()) {
					result.add(p);
				}
			}
			metaPropertiesWithDefaultValueCalculator = Collections.unmodifiableCollection(result);
		}
		return metaPropertiesWithDefaultValueCalculator;
	}
	
	public Collection<MetaProperty> getMetaPropertiesViewWithDefaultCalculator() {
		if (metaPropertiesViewWithDefaultCalculator == null) {
			Iterator<MetaProperty> it = getMetaPropertiesView().iterator();
			ArrayList<MetaProperty> result = new ArrayList<>();
			while (it.hasNext()) {
				MetaProperty p = it.next();
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
			Iterator<MetaReference> it = getMetaReferences().iterator();
			ArrayList<MetaReference> result = new ArrayList<>();
			while (it.hasNext()) {
				MetaReference ref = it.next();
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
			Iterator<String> it = getMembersNames().iterator(); // memberNames to keep order
			ArrayList<MetaProperty> result = new ArrayList<>();			
			while (it.hasNext()) {
				String name = it.next();
				if (!containsMetaProperty(name)) continue;			
				MetaProperty p = getMetaProperty(name);							
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
	public Collection<MetaProperty> getMetaPropertiesPersistentsFromReference(String referenceName) throws XavaException {
		Iterator<String> it = getMembersNames().iterator(); // memberNames to keep order
		ArrayList<MetaProperty> result = new ArrayList<>();			
		while (it.hasNext()) {
			String name = it.next();
			if (!containsMetaProperty(name)) continue;			
			MetaProperty p = getMetaProperty(name).cloneMetaProperty();
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
	public Collection<String> getPersistentPropertiesNames() throws XavaException {
		if (persistentPropertiesNames == null) {
			Iterator<String> it = getMembersNames().iterator(); // memberNames to keep order
			ArrayList<String> result = new ArrayList<>();
			while (it.hasNext()) {
				String name = it.next();
				if (!containsMetaProperty(name)) continue;			
				MetaProperty p = getMetaProperty(name);
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
			Iterator<MetaProperty> it = getMetaProperties().iterator();
			ArrayList<MetaProperty> result = new ArrayList<>();
			while (it.hasNext()) {
				MetaProperty p = it.next();
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
	public Collection<MetaReference> getMetaEntityReferences() throws XavaException {
		Collection<MetaReference> result = new ArrayList<>();
		Iterator<MetaReference> it = getMapMetaReferences().values().iterator();
		while (it.hasNext()) {
			MetaReference metaReference = it.next();
			if (!metaReference.isAggregate()) {
				result.add(metaReference);
			}
		}
		return result;
	}
	
	/**
	 * @return Collection of <tt>MetaReference</tt>, not null and read only
	 */
	public Collection<MetaReference> getMetaReferencesWithMapping() throws XavaException {
		Collection<MetaReference> result = new ArrayList<>();
		Iterator<MetaReference> it = getMapMetaReferences().values().iterator();
		while (it.hasNext()) {
			MetaReference metaReference = it.next();			
			if (getMapping().hasReferenceMapping(metaReference)) {
				result.add(metaReference);
			}
		}
		return result;
	}
	
	/**
	 * @return Collection of <tt>MetaReference</tt>, not null and read only
	 */
	public Collection<MetaReference> getMetaReferencesToEntity() throws XavaException {
		if (metaReferencesToEntity == null) {
			metaReferencesToEntity = new ArrayList<>();
			Iterator<MetaReference> it = getMapMetaReferences().values().iterator();
			while (it.hasNext()) {
				MetaReference metaReference = it.next();				
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
	public Collection<MetaReference> getMetaReferencesKey() throws XavaException { 
		if (metaReferencesKey == null) {
			metaReferencesKey = new ArrayList<>();
			Iterator<MetaReference> it = getMapMetaReferences().values().iterator();
			while (it.hasNext()) {
				MetaReference metaReference = it.next();
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
	public Collection<MetaReference> getMetaReferencesKeyAndSearchKey() throws XavaException { 
		if (metaReferencesKeyAndSearchKey == null) {
			metaReferencesKeyAndSearchKey = new ArrayList<>();
			Iterator<MetaReference> it = getMapMetaReferences().values().iterator();
			while (it.hasNext()) {
				MetaReference metaReference = it.next();
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
	public Collection<MetaReference> getMetaAggregateReferences() throws XavaException {
		Collection<MetaReference> result = new ArrayList<>();
		Iterator<MetaReference> it = getMapMetaReferences().values().iterator();
		while (it.hasNext()) {
			MetaReference metaReference = it.next();
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
		return Collections.unmodifiableCollection(getMapMetaCollections().values());
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
	
	/** @since 7.1.6 */
	public MetaView getMetaViewOnlyKeys() throws XavaException {  
		if (metaViewOnlyKeys == null) {
			metaViewOnlyKeys = new MetaView();			
			metaViewOnlyKeys.setModelName(this.getName());
			metaViewOnlyKeys.setMetaModel(this);
			metaViewOnlyKeys.setMembersNames(Strings.toString(getKeyPropertiesNames()));
			metaViewOnlyKeys.setLabel(getLabel());
		}
		return metaViewOnlyKeys;
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
		Iterator<MetaReference> it = getMetaReferences().iterator();
		while (it.hasNext()) {
			MetaReference r = it.next();
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
	public Map<String, Object> extractKeyValues(Map<String, Object> values) throws XavaException {
		return extractKeyValues(keyTester, values, false);
	}
	
	public Map<String, Object> extractSearchKeyValues(Map<String, Object> values) throws XavaException {
		return extractKeyValues(searchKeyTester, values, false); 
	}
	
	/** @since 6.2.2 */
	public Map<String, Object> extractKeyValuesFlattenEmbeddedIds(Map<String, Object> values) throws XavaException { 
		return extractKeyValues(keyTester, values, true); 
	}
	
	private Map<String, Object> extractKeyValues(IKeyTester keyTester, Map<String, Object> values, boolean flattenEmbeddedIds) throws XavaException { 
		Iterator it = values.keySet().iterator();
		Map<String, Object> result = new HashMap<>();
		while (it.hasNext()) {
			String name = (String) it.next();
			if (isKey(keyTester, name)) {
				if (isReference(name)) {
					if (getMetaReference(name).isAggregate()) { // @EmbeddedId case  
						if (flattenEmbeddedIds) {
							return (Map<String, Object>) values.get(name);
						}
						else {
							Map<String, Object> embeddedId = new HashMap<>();
							embeddedId.put(name, values.get(name));
							return embeddedId;
						}
					}	
					else {
						Object value = values.get(name);
						if (value instanceof Map) {
							result.put(name, getMetaReference(name).getMetaModelReferenced().extractKeyValues((Map<String, Object>) value));
						}
						else {
							result.put(name, value);
						}
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
	public List<MetaCalculator> getMetaCalculatorsPostCreate() {
		return metaCalculatorsPostCreate == null?Collections.emptyList():metaCalculatorsPostCreate;
	}
	
	public MetaCalculator getMetaCalculatorPostCreate(int idx) {
		if (metaCalculatorsPostCreate == null) {
			throw new IndexOutOfBoundsException(XavaResources.getString("calculator_out_of_bound", Integer.valueOf(idx)));
		}
		return (MetaCalculator) metaCalculatorsPostCreate.get(idx);
	}
	
	/**
	 * 
	 * @return Not null
	 */
	public List<MetaCalculator> getMetaCalculatorsPostLoad() {
		return metaCalculatorsPostLoad == null?Collections.emptyList():metaCalculatorsPostLoad;
	}
	
	public MetaCalculator getMetaCalculatorPostLoad(int idx) {
		if (metaCalculatorsPostLoad == null) {
			throw new IndexOutOfBoundsException(XavaResources.getString("calculator_out_of_bound", Integer.valueOf(idx)));
		}
		return (MetaCalculator) metaCalculatorsPostLoad.get(idx);
	}
	
	/**
	 * 
	 * @return Not null
	 */
	public List<MetaCalculator> getMetaCalculatorsPreRemove() {
		return metaCalculatorsPreRemove == null?Collections.emptyList():metaCalculatorsPreRemove;
	}
	
	public MetaCalculator getMetaCalculatorPreRemove(int idx) {
		if (metaCalculatorsPreRemove == null) {
			throw new IndexOutOfBoundsException(XavaResources.getString("calculator_out_of_bound", Integer.valueOf(idx)));

		}
		return (MetaCalculator) metaCalculatorsPreRemove.get(idx);
	}
		
	/**
	 * 
	 * @return Not null
	 */
	public List<MetaCalculator> getMetaCalculatorsPostModify() {
		return metaCalculatorsPostModify == null?Collections.emptyList():metaCalculatorsPostModify;
	}
	
	public MetaCalculator getMetaCalculatorPostModify(int idx) {
		if (metaCalculatorsPostModify == null) {
			throw new IndexOutOfBoundsException(XavaResources.getString("calculator_out_of_bound", Integer.valueOf(idx)));
		}
		return (MetaCalculator) metaCalculatorsPostModify.get(idx);
	}
				
	/**
	 * 
	 * @return Not null
	 */
	public Collection<MetaValidator> getMetaValidators() {
		return metaValidators == null?Collections.emptyList():metaValidators;
	}
	
	public Collection<MetaValidator> getMetaValidatorsRemove() {
		return metaValidatorsRemove == null?Collections.emptyList():metaValidatorsRemove;
	}
	
	private Map<String, MetaProperty> getMapMetaPropertiesView() {
		if (mapMetaPropertiesView == null) {
			mapMetaPropertiesView = new HashMap<>();		
			Iterator<MetaView> it = getMapMetaViews().values().iterator();
			while (it.hasNext()) {
				MetaView view = it.next();
				Iterator<MetaProperty> itProperties = view.getMetaViewProperties().iterator();
				while (itProperties.hasNext()) {
					MetaProperty pr = itProperties.next();
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
	public void fillPOJO(Object pojo, Map<String, Object> values) throws XavaException { 
		try {
			values = Maps.plainToTree(values); 	
			values.remove(MapFacade.MODEL_NAME);
			PropertiesManager pm = new PropertiesManager(pojo);			
			for (Iterator<Map.Entry<String, Object>> it=values.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, Object> en = it.next();
				if (containsMetaReference(en.getKey())) {
					if (en.getValue() == null) {
						pm.executeSet(en.getKey(), null);
						continue;
					}
					MetaReference ref = getMetaReference(en.getKey());
					MetaModel referencedModel = ref.getMetaModelReferenced();
					Object referencedObject = pm.executeGet(en.getKey());
					Map<String, Object> refValues = (Map<String, Object>) en.getValue(); 
					if (referencedObject == null) {
						if (!ref.isAggregate()) { 							 
							Map<String, Object> key = referencedModel.extractKeyValues(refValues);
							if (Maps.isEmpty(key)) {
								continue;
							}
						}						
						pm.executeSet(en.getKey(), referencedModel.toPOJO(refValues));
					}
					else {
						if (ref.isAggregate()) {
							referencedModel.fillPOJO(referencedObject, refValues);
						}
						else {
							Map<String, Object> newKey = referencedModel.extractKeyValues(refValues);
							Map<String, Object> oldKey = referencedModel.toKeyMap(referencedObject);
							if (newKey.equals(oldKey)) {
								referencedModel.fillPOJO(referencedObject, refValues);
							}
							else {
								pm.executeSet(en.getKey(), referencedModel.toPOJO(refValues));
							}
						}
					}
				}
				else if (containsMetaCollection(en.getKey())) {
					if (en.getValue() == null) { 
						pm.executeSet(en.getKey(), null);
						continue;
					}
					MetaModel referencedModel = getMetaCollection(en.getKey()).getMetaReference().getMetaModelReferenced();
					Object referencedObject = pm.executeGet(en.getKey());
					
					Collection<Object> collection = null;
					if (referencedObject == null) {
						collection = new ArrayList<>(); // It will fail with Sets						
					}
					else {
						collection = (Collection<Object>) referencedObject; 
						collection.clear();
					}					
					
					Collection<Map<String, Object>> collectionValues = (Collection<Map<String, Object>>) en.getValue();
					for (Map<String, Object> elementValues: collectionValues) {
						collection.add(referencedModel.toPOJO(elementValues));
					}							
					pm.executeSet(en.getKey(), collection);
				}
				else {
					if (isViewProperty(en.getKey())) continue;
					MetaProperty property = getMetaProperty(en.getKey());
					if (property.isReadOnly()) continue; 
					Object value = en.getValue();
					try {				
						pm.executeSet(en.getKey(), en.getValue());
					}
					catch (IllegalArgumentException ex) {
						if (property.hasValidValues()) {
							if (value instanceof String) {
								value = Integer.valueOf(property.getValidValueIndex(value));
								pm.executeSet(en.getKey(), value);
							}
							else if (value instanceof Number) {
								value = property.getValidValue(((Number) value).intValue());
								pm.executeSet(en.getKey(), value);								
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
	public Map<String, Object> toMap(Object modelObject) throws XavaException {
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
	public Map<String, Object> toKeyMap(Object modelObject) throws XavaException { 
		return toMap(modelObject, true);
	}
		
	private Map<String, Object> toMap(Object modelObject, boolean onlyKey) throws XavaException { 
		if (modelObject == null) return new HashMap<>(); 
		
		try {
			PropertiesManager pm = new PropertiesManager(modelObject);
			Map<String, Object> values = new HashMap<>();
			for (Iterator<MetaProperty> it = getMetaProperties().iterator(); it.hasNext();) {
				MetaProperty property = it.next();
				if (!onlyKey || property.isKey()) {
					values.put(property.getName(), pm.executeGet(property.getName()));
				}
			}
			for (Iterator<MetaReference> it = getMetaReferences().iterator(); it.hasNext();) {
				MetaReference reference = it.next();
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
	 * @return null if the sent map is null, empty or all values are null
	 */
	public String toString(Map<String, Object> key) throws XavaException { 
		if (Maps.isEmpty(key)) return null; 
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

	public Collection<String> getViewPropertiesNames() {
		return Collections.unmodifiableCollection(getMapMetaPropertiesView().keySet());
	}
	
	public Collection<MetaProperty> getMetaPropertiesView() {
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
		this.metaModelContainer = null; 
	}
	public String getContainerModelName() { 
		return containerModelName;
	}
	
	public Collection<MetaCollection> getMetaCollectionsWithConditionInOthersModels() throws XavaException {
		Collection<MetaCollection> result = new ArrayList<>();
		Iterator<MetaComponent> itComponents = MetaComponent.getAllLoaded().iterator();
		while (itComponents.hasNext()) {
			MetaComponent comp = itComponents.next();			
			result.addAll(comp.getMetaEntity().getMetaColectionsWithConditionReferenceTo(getName()));
			Iterator<MetaAggregate> itAggregates = comp.getMetaAggregates().iterator();
			while (itAggregates.hasNext()) {
				MetaAggregate agr = itAggregates.next();
				result.addAll(agr.getMetaColectionsWithConditionReferenceTo(getName()));
			}			
		}
		return result;
	}
	
	public Collection<MetaCollection> getMetaColectionsWithConditionReferenceTo(String modelName) {
		Collection<MetaCollection> result = new ArrayList<>();
		Iterator<MetaCollection> it = getMetaCollections().iterator();
		while (it.hasNext()) {
			MetaCollection col = it.next();
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
	
	public Collection<String> getInterfacesNames() {
		if (interfaces == null) {
			interfaces = new ArrayList<>();
			interfaces.add("org.openxava.model.IModel"); 
		}
		return interfaces;
	}
	
	/**
	 * String in java format: comma separate interfaces names 
	 */
	public String getImplements() {
		StringBuffer r = new StringBuffer();
		Iterator<String> it = getInterfacesNames().iterator();
		while (it.hasNext()) {			
			r.append(it.next());
			if (it.hasNext()) r.append(", ");
		}
		return r.toString();
	}


	/**
	 * Does not include <i>Transient</i> properties and properties from collections
	 */
	public Collection<String> getRecursiveQualifiedPropertiesNames() throws XavaException {
		if (recursiveQualifiedPropertiesNames == null) {
			Collection<String> parents = new HashSet<>();
			parents.add(getName());			
			recursiveQualifiedPropertiesNames = createQualifiedPropertiesNames(parents, "", false, 4); // The limit cannot be very big because it freezes the add column dialog with plain OpenXava 
																								// and produces OutOfMemoryError with XavaPro on starting module, 
																								// with entities with many nested references
		}
		return recursiveQualifiedPropertiesNames;
	}
	
	/**
	 * Does not include <i>Transient</i> properties and properties from collections
	 * 
	 * @since 4.9
	 */
	public Collection<String> getRecursiveQualifiedPropertiesNamesUntilSecondLevel() throws XavaException { 
		if (recursiveQualifiedPropertiesNamesUntilSecondLevel == null) {
			Collection<String> parents = new HashSet<>();
			parents.add(getName());			
			recursiveQualifiedPropertiesNamesUntilSecondLevel = createQualifiedPropertiesNames(parents, "", false, 2);
		}
		return recursiveQualifiedPropertiesNamesUntilSecondLevel;
	}
	
	/**
	 * Does not include <i>Transient</i> properties
	 * @since 6.5 
	 */
	public Collection<String> getRecursiveQualifiedPropertiesNamesIncludingCollections() throws XavaException { 
		if (recursiveQualifiedPropertiesNamesIncludingCollections == null) {
			Collection<String> parents = new HashSet<>();
			parents.add(getName());			
			recursiveQualifiedPropertiesNamesIncludingCollections = createQualifiedPropertiesNames(parents, "", true, 4); // The limit cannot be very big because it freezes the add column dialog with plain OpenXava 
																								// and produces OutOfMemoryError with XavaPro on starting module, 
																								// with entities with many nested references
		}
		return recursiveQualifiedPropertiesNamesIncludingCollections;
	}
	
	/**
	 * Does not include <i>Transient</i> properties 
	 * 
	 * @since 6.5
	 */
	public Collection<String> getRecursiveQualifiedPropertiesNamesUntilSecondLevelIncludingCollections() throws XavaException {  
		if (recursiveQualifiedPropertiesNamesUntilSecondLevelIncludingCollections == null) {
			Collection<String> parents = new HashSet<>();
			parents.add(getName());			
			recursiveQualifiedPropertiesNamesUntilSecondLevelIncludingCollections = createQualifiedPropertiesNames(parents, "", true, 2);
		}
		return recursiveQualifiedPropertiesNamesUntilSecondLevelIncludingCollections;
	}

	private Collection<String> createQualifiedPropertiesNames(Collection<String> parents, String prefix, boolean includeCollections, int level) throws XavaException { 
		if (level == 0) return Collections.emptyList(); 
		List<String> result = new ArrayList<>();		
		for (Iterator<String> it = getMembersNames().iterator(); it.hasNext();) {
			String name = it.next();
			if (getMapMetaProperties().containsKey(name)) {
				if (getMetaProperty(name).isTransient()) continue;
				if (getMetaProperty(name).isHidden()) continue; 
				if (Is.emptyString(prefix)) result.add(name);
				else result.add(prefix + name);				
			}
			else if (getMapMetaReferences().containsKey(name)) {
				MetaReference ref = getMapMetaReferences().get(name);
				if (ref.isTransient()) continue; 
				if (!parents.contains(ref.getReferencedModelName())) {
					Collection<String> newParents = new HashSet<>();
					newParents.addAll(parents);
					newParents.add(ref.getReferencedModelName());	
					result.addAll(ref.getMetaModelReferenced().createQualifiedPropertiesNames(newParents, prefix + ref.getName() + ".", includeCollections, level - 1));
				}
			}
			else if (includeCollections && getMapMetaCollections().containsKey(name)) {
				MetaCollection collection = getMapMetaCollections().get(name);
				if (collection.hasCalculator()) continue;
				MetaReference ref = collection.getMetaReference();
				if (!parents.contains(ref.getReferencedModelName())) {
					Collection<String> newParents = new HashSet<>();
					newParents.addAll(parents);
					newParents.add(ref.getReferencedModelName());	
					newParents.addAll(ref.getMetaModel().getParentsWithCollection(name)); 
					result.addAll(ref.getMetaModelReferenced().createQualifiedPropertiesNames(newParents, prefix + collection.getName() + ".", includeCollections, level - 1));
				}
			}
		} 
		return result;		
	}
	
	private Collection<String> getParentsWithCollection(String collectionName) { 
		Collection<String> result = new ArrayList<>();
		Class superClass = getPOJOClass().getSuperclass();
		while (!superClass.equals(Object.class)) {
			try {
				boolean exists = new PropertiesManager(superClass).exists(collectionName);
				if (exists) result.add(superClass.getSimpleName());
			} 
			catch (PropertiesManagerException ex) {				
				log.warn(XavaResources.getString("parent_not_excluded_rendundant_columns", superClass.getSimpleName()), ex);
			}
			superClass = superClass.getSuperclass();			
		}
		return result;
	}
	
	private MetaModel getParentMetaModel() { 
		Class superClass = getPOJOClass().getSuperclass();
		if (superClass.equals(Object.class)) return null;
		try {
			return get(superClass.getSimpleName());
		} 
		catch (ElementNotFoundException ex) {				
			return null;
		}
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
			for (Iterator<MetaProperty> it=getMetaProperties().iterator(); it.hasNext(); ) {
				MetaProperty pr = it.next();
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
	
	public boolean hasDateTimeProperty() {
		Collection<MetaProperty> mp = getMetaProperties();
		for (MetaProperty p : mp) {
			if (p.isDateTimeType()) return true;
		}
		return false;
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