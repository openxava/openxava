package org.openxava.model.meta;

import java.util.*;

import org.openxava.util.*;
import org.openxava.util.meta.*;

/**
 * @author Javier Paniza
 */
@SuppressWarnings("serial")
abstract public class MetaMember extends MetaElement implements Comparable<MetaMember> { 

	private MetaModel metaModel;
	private String labelId;
	private String qualifiedName;
	
	private String label;
	private Collection<String> propertyNamesThatIDepend; 
	
	public String getLabel(Locale locale) {
		if (Is.emptyString(label)) return super.getLabel(locale);
		String labelId = Strings.naturalLabelToIdentifier(label);
		if (Labels.exists(labelId, locale)) return super.getLabel(locale, labelId);
		return label;
	}
		
	public void setLabel(String newLabel) {
		super.setLabel(newLabel);
		label = newLabel;
	}
	
	/** @since 5.6 */
	static public <T extends MetaMember> List<String> toQualifiedNames(Collection<T> metaMembers) { 
		List<String> result = new ArrayList<String>();
		for (MetaMember m: metaMembers) result.add(m.getQualifiedName());
		return result;
	}

	public int compareTo(MetaMember o) { 	
		return getName().compareTo(o.getName());
	}
	
	/** @since 6.1.2 */
	public boolean hasCalculator() { 
		return false;
	}
	
	/** @since 6.1.2 */
	public boolean hasDefaultValueCalculator() { 
		return false;
	}
	
	/** @since 6.1.2 */
	public MetaCalculator getMetaCalculator() { 
		return  null;
	}
	
	/** @since 6.1.2 */
	public MetaCalculator getMetaCalculatorDefaultValue() { 
		return null;
	}
	
	/** @since 6.1.2  Moved from MetaProperty */
	public Collection<String> getPropertyNamesThatIDepend() {  
		if (propertyNamesThatIDepend == null) {		
			MetaSetsContainer metaCalculador = null;
			if (hasCalculator()) {
				metaCalculador = getMetaCalculator();
			}
			else if (hasDefaultValueCalculator()) {
				metaCalculador = getMetaCalculatorDefaultValue();
			}
			else {
				propertyNamesThatIDepend = Collections.EMPTY_LIST;
				return propertyNamesThatIDepend;
			}
				
			if (!metaCalculador.containsMetaSets()) {
				propertyNamesThatIDepend = Collections.EMPTY_LIST;
				return propertyNamesThatIDepend;
			} 
			
			propertyNamesThatIDepend = new ArrayList<>();
			Iterator itMetaSets = metaCalculador.getMetaSets().iterator();
			while (itMetaSets.hasNext()) {
				MetaSet metaSet = (MetaSet) itMetaSets.next();
				if (!metaSet.hasValue()) {
					propertyNamesThatIDepend.add(metaSet.getPropertyNameFrom());
				}										
			}
		}
		return propertyNamesThatIDepend;				
	}

	
	public MetaModel getMetaModel() {		
		return metaModel;
	}

	public void setMetaModel(MetaModel newContainer) {
		metaModel = newContainer;		
	}
	
	public String getQualifiedName() {
		if (qualifiedName == null) {
			if (getMetaModel() == null) qualifiedName = getName();
			else qualifiedName = getMetaModel().getName() + "." + getName();
		}		
		return qualifiedName;
	}
	
	/**
	 * For can set a qualified name manually.
	 */
	public void setQualifiedName(String newQualifiedName) {
		qualifiedName = newQualifiedName;
	}
		
	public boolean isHidden() {
		return false;
	}
	
	public String getId() {
		if (!Is.emptyString(labelId)) {
			return labelId;		
		}
		MetaModel m = getMetaModel(); 
		return m==null?getName():m.getId() + "." + getName();
	}
	
	/**
	 * Id used to look up label in resource files. <p>
	 */ 	
	public String getLabelId() {
		return labelId;
	}
	public void setLabelId(String id) {
		this.labelId = id;		
	}
	
}