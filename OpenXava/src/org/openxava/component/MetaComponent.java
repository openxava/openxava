package org.openxava.component;

import java.io.*;
import java.util.*;



import org.apache.commons.logging.*;
import org.openxava.component.parse.*;
import org.openxava.mapping.*;
import org.openxava.model.impl.*;
import org.openxava.model.meta.*;
import org.openxava.tab.meta.*;
import org.openxava.util.*;
import org.openxava.view.meta.*;

/**
 * 
 * All meta info about business concept.<p>
 * 
 * A business component is all software artifacts
 * that have relation with a business concept.
 * For example, all view, tables, classes, models, etc.
 * about concept of Seller is the <i>Seller</i> business
 * component.<p>
 * 
 * @author Javier Paniza
 */

public class MetaComponent implements Serializable {
	
	private static Log log = LogFactory.getLog(MetaComponent.class);
	private static Map<String, MetaComponent> components = new HashMap<String, MetaComponent>(); 
	private static Properties packages;
	private static boolean allComponentsLoaded = false;
	private static Set allPackageNames;
	private static Collection<Class> parsersClasses = null; 
	
	private String packageNameWithSlashWithoutModel;
	private String name;
	private MetaEntity metaEntity;
	private Map metaAggregates;	
	private Map aggregatesMapping;
	private MetaTab metaTab;
	private Map metaTabs;
	private EntityMapping entityMapping;
	private String packageName;
	private boolean _transient; 
	private IPersistenceProvider persistenceProvider;  
	private boolean metaDataCached = true; 
	private boolean labelForModule = false; 
		
	/**
	 * 
	 * @exception ElementNotFoundException  If component does not exist.
	 * @exception XavaException  Any other problem. 
	 */
	public static MetaComponent get(String name) throws ElementNotFoundException, XavaException {
		MetaComponent r = (MetaComponent) components.get(name);		
		if (r == null) {		
			if (name.indexOf('.') >= 0) { // A component never is qualified
				throw new ElementNotFoundException("component_not_found", name);
			}
			r = parse(name); 
			if (r == null) {				
				throw new ElementNotFoundException("component_not_found", name);
			}
			r.validate();	
			if (r.isMetaDataCached()) { 
				components.put(name, r); 
			}
		}
		return r;
	}
	
	private static MetaComponent parse(String name) throws XavaException {
		try {
			for (IComponentParser parser: createParsers()) {
				MetaComponent r = parser.parse(name);
				if (r != null) {
					r.setPersistenceProvider(parser.getPersistenceProvider());
					return r;
				}
			}
			return null;
		}
		catch (Exception ex) {
			log.error(XavaResources.getString("component_parse_error", name, ex.getMessage()), ex);  
			if (ex instanceof RuntimeException) throw (RuntimeException) ex;
			else throw new RuntimeException(ex);
		}							
	}
	
	private static Collection<IComponentParser> createParsers() throws Exception { 
		Collection<IComponentParser> parsers = new ArrayList<IComponentParser>();
		for (Class parserClass: getParsersClasses()) {
			parsers.add((IComponentParser) parserClass.newInstance());
		}
		return parsers;
	}
	
	private static Collection<Class> getParsersClasses() throws Exception { 
		if (parsersClasses == null) { 
			parsersClasses = new ArrayList<Class>();
			for (String className: XavaPreferences.getInstance().getComponentParsersClasses().split(",")) {
				parsersClasses.add(Class.forName(className.trim()));
			}	
		}
		return parsersClasses;
	}
		
	public static boolean exists(String name) throws XavaException {
		try {
			get(name);
			return true;
		}
		catch (ElementNotFoundException ex) {			
			return false;
		}		
	}
	
	public static Collection<MetaComponent> getAllLoaded() {
		return components.values();
	}
		
	/**
	 * @return Not null and not empty string
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return Not null.
	 */
	public MetaEntity getMetaEntity() {
		return metaEntity;
	}
	
	/**
	 * @param metaEntity Not null
	 */
	public void setMetaEntity(MetaEntity metaEntity) throws XavaException {
		if (this.metaEntity != null) {
			throw new XavaException("component_only_1_entity", getName());
		}
		this.metaEntity = metaEntity;
		this.metaEntity.setMetaComponent(this);
	}
	
	/**
	 * @param metaView Not null.
	 */	
	public void addMetaView(MetaView metaView) throws XavaException {		
		if (Is.emptyString(metaView.getModelName())) {
			getMetaEntity().addMetaView(metaView);			
		}
		else if (getName().equals(metaView.getModelName())) {
			getMetaEntity().addMetaView(metaView);			
		}
		else {
			getMetaAggregate(metaView.getModelName()).addMetaView(metaView);			
		}
	}
			
	/**
	 * 
	 * @return Not null. 
	 * @exception ElementNotFoundException If the MetaAggregate does not exist in this component.
	 * @exception XavaException Any other problem.
	 */
	public MetaAggregate getMetaAggregate(String name) throws  XavaException {
		if (!hasMetaAggregate(name)) {
			throw new ElementNotFoundException("aggregate_not_found", name, getName());
		}
		return (MetaAggregate) metaAggregates.get(name);
	}
	
	public boolean hasMetaAggregate(String name) {		
		return metaAggregates != null && metaAggregates.containsKey(name);
	}
	
	/**
	 *
	 * @return Not null
	 * @exception ElementNotFoundException If does not exist the aggregate mapping in this component.
	 * @exception XavaException Any other problem.
	 */
	public AggregateMapping getAggregateMapping(String name) throws  XavaException {
		if (aggregatesMapping == null || !aggregatesMapping.containsKey(name)) {
			throw new ElementNotFoundException("aggregate_mapping_not_found", name, getName());
		}
		return (AggregateMapping) aggregatesMapping.get(name);
	}
	
	public Collection getAggregateMappings() throws XavaException {
		return aggregatesMapping == null?Collections.EMPTY_LIST:aggregatesMapping.values(); 
	}	
	
	/**
	 * 
	 * @return Elementss <tt>instanceof MetaAggregate</tt>. Not null. 	 
	 * @exception XavaException Any other problem.
	 */
	public Collection getMetaAggregates() throws  XavaException {
		if (metaAggregates == null) return new ArrayList();
		return metaAggregates.values();			
	}	
	
	/**
	 * 
	 * @return Elements <tt>instanceof MetaAggregateBean</tt> and <tt>generate == true</tt>. Not null. 	 
	 * @exception XavaException Any other problem.
	 */
	public Collection getMetaAggregatesBeanGenerated() throws  XavaException {
		Iterator it = getMetaAggregates().iterator();
		Collection result = new ArrayList();
		while (it.hasNext()) {
			Object element = it.next();			
			if (!(element instanceof MetaAggregateForReference)) continue;			
			MetaAggregateForReference aggregate = (MetaAggregateForReference) element;
			if (aggregate.isPojoGenerated()) {				
				result.add(aggregate);
			}			
		}		
		return result;			
	}	
	
	/**
	 *
	 * @return Elements <tt>instanceof MetaAggregateForCollection</tt> and 
	 * 			<tt>pojoGenerated == true</tt>. Not null.
	 * @exception XavaException Any problem.
	 */	
	public Collection getMetaAggregatesForCollectionPojoGenerated() throws  XavaException {
		return getMetaAggregatesForCollectionGenerated();
	}
	
	private Collection getMetaAggregatesForCollectionGenerated() throws  XavaException {
		Iterator it = getMetaAggregates().iterator();
		Collection result = new ArrayList();
		while (it.hasNext()) {
			Object element = it.next();
			if (!(element instanceof MetaAggregateForCollection)) continue;
			MetaAggregateForCollection aggregate = (MetaAggregateForCollection) element;			
			if (aggregate.isPojoGenerated()) {
				result.add(aggregate);
			}
		}
		return result;
	}	
	
	

	/**
	 * @param metaAggregate  Not null.
	 */
	public void addMetaAggregate(MetaAggregate metaAggregate) {		
		if (metaAggregates == null) metaAggregates = new HashMap();
		metaAggregates.put(metaAggregate.getName(), metaAggregate);
		metaAggregate.setMetaComponent(this);
	}
	
	public void addAggregateMapping(AggregateMapping aggregateMapping) throws XavaException { 
		if (aggregatesMapping == null) aggregatesMapping = new HashMap();
		aggregatesMapping.put(aggregateMapping.getModelName(), aggregateMapping);
		aggregateMapping.setMetaComponent(this);	
	}	
	
	/**
	 * <tt>MetaTab</tt> by default.
	 * 
	 * @return Not null.
	 */
	public MetaTab getMetaTab() throws XavaException {
		if (metaTab == null) {
			metaTab = MetaTab.createDefault(this);
		}
		return metaTab;
	}

	public Collection getMetaTabs(){
		Collection metaTabs = new ArrayList();
		metaTabs.add(getMetaTab());
		if (this.metaTabs != null) metaTabs.addAll(this.metaTabs.values());
		return metaTabs;
	}
	
	/**
	 * <tt>MetaTab</tt> from name.
	 * 
	 * @param name If null or empty string return default tab. 
	 * @return Not null
	 */	
	public MetaTab getMetaTab(String name) throws XavaException, ElementNotFoundException {		
		if (Is.emptyString(name)) return getMetaTab();
		if (metaTabs == null) {			
			throw new ElementNotFoundException("tab_not_found", name, getName());
		}
		MetaTab result = (MetaTab) metaTabs.get(name);
		if (result == null) {
			throw new ElementNotFoundException("tab_not_found", name, getName());
		}
		return result;
	}
	

	/**
	 * <tt>MetaTab</tt> by default.
	 */	
	private void setMetaTab(MetaTab metaTab) throws XavaException {
		if (this.metaTab != null) {			
			throw new XavaException("no_more_1_tab", getName());
		}
		this.metaTab = metaTab;
	}
		
	public void addMetaTab(MetaTab metaTab) throws XavaException {
		metaTab.setMetaComponent(this);		
		String name = metaTab.getName();
		if (Is.emptyString(name)) { // by default
			setMetaTab(metaTab);
		}
		else { // with name
			if (metaTabs == null) {
				metaTabs = new HashMap();			
			}
			metaTabs.put(name, metaTab);		
		}
		metaTab.setDefaultValues(); 
	}
	
	/**
	 * 
	 * @return Not null.
	 * @exception XavaException Any problem, including that mapping for
	 * 		this component does not exist.
	 */
	public EntityMapping getEntityMapping() throws XavaException {
		if (entityMapping == null) {
			throw new XavaException("entity_mapping_not_found", getName());
		}
		return entityMapping;
	}
	
	public void setEntityMapping(EntityMapping mapping) throws XavaException {
		if (mapping != null) {
			mapping.setMetaComponent(this);
		}
		this.entityMapping = mapping;
	}
	
	private void validate() throws XavaException {		
		if (Is.emptyString(getName())) {
			throw new XavaException("component_name_required");
		}
		if (metaEntity == null) {
			throw new XavaException("component_entity_required", getName());
		}
	}
				
	/**
	 * Java package where the model classes resides.
	 */
	public String getPackageName() throws XavaException {
		if (packageName==null) {
			try {
				packageName = getPackages().getProperty(getName());				
			}
			catch (Exception ex) {
				log.error(ex.getMessage(),ex);
				throw new XavaException("component_package_error", getName());
			}
		}		
		return packageName;
	}
	
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	
	private static Properties getPackages() throws IOException { 
		if (packages == null) {
			PropertiesReader reader = new PropertiesReader(MetaComponent.class, "packages.properties");
			packages = reader.get();
		}
		return packages;
	}
	
	/**
	 * The names of all the root Java packages used by the components 
	 * of all OpenXava application in the classpath. <p>
	 * 
	 * It's the root package for each application, that it returns
	 * <code>com.gestion400.invoicing</code> and not 
	 * <code>com.gestion400.invoicing.model</code>. 
	 * 
	 * @return Of <code>String</code>
	 */
	public static Set getAllPackageNames() throws XavaException { 
		if (allPackageNames == null) {
			allPackageNames = new HashSet();
			try {
				for (Iterator it = getPackages().values().iterator(); it.hasNext(); ) {
					String name = (String) it.next();
					int idx = name.lastIndexOf('.'); 
					if (idx >= 0) {										
						allPackageNames.add(name.substring(0, idx));
					}
				}
			}
			catch (Exception ex) {
				log.error(XavaResources.getString("package_names_error"), ex);
				throw new XavaException(XavaResources.getString("package_names_error"));
			}
		}
		return allPackageNames;
	}
	
			
	/** 
	 * @param unqualifiedPackage For example, of org.openxava.test is test, 
	 * 			that is to say, without domain (org.openxava).
	 */
	public static String getQualifiedPackageForUnqualifiedPackage(String unqualifiedPackage) throws XavaException {
		try {
			String domain = getPackages().getProperty("package.domain." + unqualifiedPackage, "");
			return domain	 + "/" + unqualifiedPackage;
		}
		catch (Exception ex) {			
			log.error(ex.getMessage(),ex);
			throw new XavaException("read_packages_error");						
		}
	}
	
	/**
	 * Package using / instead of .	and 
	 * it does not includes the model package. 
	 */
	public String getPackageNameWithSlashWithoutModel() throws XavaException {
		if (packageNameWithSlashWithoutModel == null) {
			String packageName = getPackageName();			
			if (packageName == null) return null;
			packageNameWithSlashWithoutModel = Strings.change(packageName, ".", "/");
			if (packageNameWithSlashWithoutModel.contains("/")) {  
				packageNameWithSlashWithoutModel = packageNameWithSlashWithoutModel.substring(0, packageNameWithSlashWithoutModel.lastIndexOf('/'));
			}
		}		
		return packageNameWithSlashWithoutModel;
	}

	public static Collection<MetaComponent> getAll() throws XavaException { 
		if (!allComponentsLoaded) {
			try {
				for (Iterator it = getPackages().keySet().iterator(); it.hasNext();) {
					String name = (String) it.next();
					if (!name.startsWith("package.")) {
						get(name);
					}	
				}
				allComponentsLoaded = true;
			}
			catch (IOException ex) {
				log.error(ex.getMessage(),ex);
				throw new XavaException("loading_components_error");
			}
		}
		return getAllLoaded();
	}

	public boolean isTransient() {
		return _transient;
	}


	public void setTransient(boolean _transient) {
		this._transient = _transient;
	}

	public IPersistenceProvider getPersistenceProvider() {
		if (isTransient()) return TransientPersistenceProvider.getInstance();  
		return persistenceProvider;
	}

	public void setPersistenceProvider(IPersistenceProvider persistenceProvider) {
		this.persistenceProvider = persistenceProvider;
	}

	public boolean isMetaDataCached() {
		return metaDataCached;
	}

	public void setMetaDataCached(boolean metaDataCached) {
		this.metaDataCached = metaDataCached;
	}

	public boolean isLabelForModule() {
		return labelForModule;
	}

	public void setLabelForModule(boolean labelForModule) {
		this.labelForModule = labelForModule;
	}
		
}

