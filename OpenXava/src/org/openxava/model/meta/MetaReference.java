package org.openxava.model.meta;

import java.util.*;



import org.apache.commons.logging.*;
import org.openxava.calculators.*;
import org.openxava.component.*;
import org.openxava.filters.meta.*;
import org.openxava.util.*;
import org.openxava.util.meta.*;
import org.openxava.view.meta.MetaDescriptionsList;
import org.openxava.view.meta.MetaView;

/**
 * 
 * @author: Javier Paniza
 */

public class MetaReference extends MetaMember implements Cloneable {

	private static Log log = LogFactory.getLog(MetaReference.class);
		
	private MetaModel metaModelReferenced;
	private String referencedModelName;
	private String referencedModelContainerReference;
	private String referencedModelCorrespondingCollection; 
	private String role;
	private boolean required;
	private boolean key;
	private boolean searchKey;
	private MetaCalculator metaCalculatorDefaultValue;
	private ICalculator defaultValueCalculator;
	private boolean explicitAggregate; 
	private boolean aggregate;
	
	public MetaCollection getMetaCollectionFromReferencedModel() throws XavaException { 				
		Iterator it = getMetaModelReferenced().getMetaCollections().iterator();
		String modelName = getMetaModel().getName();
		while (it.hasNext()) {
			MetaCollection metaCollection = (MetaCollection) it.next();
			if (metaCollection.getMetaReference().getRole().equals(getName()) &&
				modelName.equals(metaCollection.getMetaReference().getReferencedModelName())
			) {
				return metaCollection;
			}  			
		}
		return null;		
	}
	
	public String getOrderFromReferencedModel() throws XavaException {
		MetaCollection metaCollection = getMetaCollectionFromReferencedModel();
		return metaCollection==null?null:metaCollection.getOrder();
	}
	
	private boolean orderHasQualifiedProperties() throws XavaException { 
		MetaCollection metaCollection = getMetaCollectionFromReferencedModel();
		return metaCollection==null?false:metaCollection.orderHasQualifiedProperties();
	}
	
	public MetaModel getMetaModelReferenced() throws XavaException {
		if (metaModelReferenced == null) {
			ElementName modelName = new ElementName(getReferencedModelName());			
			if (modelName.isQualified()) {
				String componentName = modelName.getContainerName();
				String aggregateName = modelName.getUnqualifiedName();
				return MetaComponent.get(componentName).getMetaAggregate(aggregateName);
			}
			
			// Not qualified
			if (!getMetaModel().isAnnotatedEJB3() && getReferencedModelName().equals(getMetaModel().getName())) {
				metaModelReferenced = getMetaModel(); 
			}
			else {
				if (explicitAggregate && !aggregate && getMetaModel().isAnnotatedEJB3()) {
					metaModelReferenced = MetaComponent.get(getReferencedModelName()).getMetaEntity();
					if (!Is.empty(referencedModelContainerReference)) metaModelReferenced.setContainerReference(referencedModelContainerReference); 					
				}
				else {
					try {				
						// look for local aggregate
						metaModelReferenced = getMetaModel().getMetaComponent().getMetaAggregate(getReferencedModelName());
					}
					catch (ElementNotFoundException ex) {
						// look for entity (looking for component)
						metaModelReferenced = MetaComponent.get(getReferencedModelName()).getMetaEntity();
						if (!Is.empty(referencedModelContainerReference)) metaModelReferenced.setContainerReference(referencedModelContainerReference); 
					}
				}
			} 
		}
		return metaModelReferenced;
	}
	
	public boolean isAggregate() throws XavaException {
		if (explicitAggregate) return aggregate;
		return getMetaModelReferenced() instanceof MetaAggregate;
	}
	public void setAggregate(boolean aggregate) {
		this.explicitAggregate = true;
		this.aggregate = aggregate;
	}
			
	public String getLabel() {
		String e = super.getLabel();
		try {
			return Is.emptyString(e)? getMetaModelReferenced().getLabel() : e;
		} catch (XavaException ex) {
			return e;
		}
	}
	
	public String getReferencedModelName() {
		if (Is.emptyString(referencedModelName)) {
			referencedModelName = Strings.firstUpper(super.getName());
		}		
		return referencedModelName;
	}
	
	public void setReferencedModelName(String referencedModelName) {
		this.referencedModelName = referencedModelName;
		this.metaModelReferenced = null; 
	}
	
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getName() {
		if (Is.emptyString(super.getName())) {
			ElementName n = new ElementName(getReferencedModelName());	
			setName(Strings.firstLower(n.getUnqualifiedName()));
		}
		return super.getName();
	}

	public String getRole() {		
		return Is.emptyString(role)?Strings.firstLower(getMetaModel().getName()):role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
		
	public boolean isKey() {
		return key;
	}

	public void setKey(boolean b) {
		key = b;
	}
	
	public MetaReference cloneMetaReference() throws XavaException {
		try {
			return (MetaReference) super.clone();			
		}
		catch (CloneNotSupportedException ex) {
			log.error(ex.getMessage(), ex);
			throw new RuntimeException(XavaResources.getString("reference_implements_cloneable"));
		}
	}	
	
	public MetaCalculator getMetaCalculatorDefaultValue() {
		return metaCalculatorDefaultValue;
	}
	public void setMetaCalculatorDefaultValue(
			MetaCalculator metaCalculatorDefaultValue) throws XavaException {
		if (metaCalculatorDefaultValue != null && metaCalculatorDefaultValue.isOnCreate()) {
			throw new XavaException("reference_not_default_values_on_create", getName());
		}
		this.metaCalculatorDefaultValue = metaCalculatorDefaultValue;		
	}
	
	/**
	 * 
	 * @return null if this does not have default value calculator
	 */
	public ICalculator getDefaultValueCalculator() throws XavaException {
		if (!hasDefaultValueCalculator()) return null;
		if (defaultValueCalculator == null) {
			defaultValueCalculator = metaCalculatorDefaultValue.createCalculator();
		}
		return defaultValueCalculator;
	}
	
	public boolean hasDefaultValueCalculator() {		
		return metaCalculatorDefaultValue != null;
	}
	
	/**
	 * 
	 * @return null if this does not have calculator for default value
	 */
	public ICalculator createDefaultValueCalculator() throws XavaException {  
		if (!hasDefaultValueCalculator()) return null;
		return metaCalculatorDefaultValue.createCalculator();		
	}
	
	/**
	 * 
	 * @since 5.1
	 */
	public boolean hasNotDependentDefaultValueCalculator() {
		return metaCalculatorDefaultValue != null && !metaCalculatorDefaultValue.isDependent();
	}
	
	public String toString() {		
		return "MetaReference:" + getId();
	}

	public boolean isSearchKey() {
		return searchKey;
	}

	public void setSearchKey(boolean searchKey) {
		this.searchKey = searchKey;
	}

	public String getParameterValuesPropertiesInDescriptionsList(MetaView metaView) throws XavaException {
		MetaDescriptionsList descriptionsList = metaView.getMetaDescriptionList(this);		
		if (descriptionsList == null) return "";
		String depends = descriptionsList.getDepends();		
		if (Is.emptyString(depends)) return "";
		StringTokenizer st = new StringTokenizer(depends, ",");
		StringBuffer result = new StringBuffer();
		while (st.hasMoreTokens()) {
			String member = st.nextToken().trim();
			try {
				String reference = member.startsWith("this.")?member.substring(5):member; 
				MetaModel fromIDepends = getMetaModel().getMetaReference(reference).getMetaModelReferenced();
				for (Iterator it=fromIDepends.getKeyPropertiesNames().iterator(); it.hasNext();) {
					String key = (String) it.next();
					if (result.length() > 0) result.append(',');
					result.append(member);
					result.append('.');
					result.append(key);
				}
			}
			catch (ElementNotFoundException ex) {
				// not reference, it is simple property
				if (result.length() > 0) result.append(',');
				result.append(member);			
			}			
		}		
		return result.toString();
	}
	
	/** @since 6.4 */
	public String getFilterInDescriptionsList(MetaView metaView) throws XavaException { 
		MetaDescriptionsList descriptionsList = metaView.getMetaDescriptionList(this);		
		if (descriptionsList == null) return "";
		MetaFilter filter = descriptionsList.getMetaFilter();
		if (filter == null) return "";
		return filter.getClassName();
	}
	
	public String getKeyProperty(String propertyKey){
		Collection keys = getMetaModelReferenced().getAllKeyPropertiesNames(); 
		if (keys.size() == 1) return keys.iterator().next().toString();
		return "";
	}
	
	public String getKeyProperties(){
		Collection keys = getMetaModelReferenced().getAllKeyPropertiesNames(); 
		if (keys.size() == 1) return "";
		
		Iterator<String> it = keys.iterator();
		StringBuffer sb = new StringBuffer();
		while (it.hasNext()) {
			String property = it.next();		
			sb.append(property);
			if (it.hasNext()) sb.append(',');
		}	
		return sb.toString();
	}

	public void setReferencedModelContainerReference(
			String referencedModelContainerReference) {
		this.referencedModelContainerReference = referencedModelContainerReference;
	}

	public String getReferencedModelContainerReference() {
		return referencedModelContainerReference;
	}

	public String getReferencedModelCorrespondingCollection() {
		return referencedModelCorrespondingCollection;
	}

	public void setReferencedModelCorrespondingCollection(String referencedModelCorrespondingCollection) {
		this.referencedModelCorrespondingCollection = referencedModelCorrespondingCollection;
	}

	
}