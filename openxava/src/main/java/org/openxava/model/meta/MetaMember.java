package org.openxava.model.meta;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;
import org.openxava.util.meta.*;

/**
 * @author Javier Paniza
 */
@SuppressWarnings("serial")
abstract public class MetaMember extends MetaElement implements Comparable<MetaMember> {
	
	private static Log log = LogFactory.getLog(MetaMember.class);  

	private MetaModel metaModel;
	private String labelId;
	private String qualifiedName;
	
	private String label;
	private Collection<String> propertyNamesThatIDepend; 
	private boolean _transient; 
	
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
	
	/** @since 7.3.3 Moved from MetaProperty */
	public String getSimpleName() {  
		String name = getName();
		if (!name.contains(".")) return name;
		return Strings.lastToken(name, ".");		
	}
	
	/** @since 7.3.3 Moved from MetaProperty */
	public Annotation[] getAnnotations() {   
		// We avoid to sum everything in a collection to save memory, 
		//   because this method is called a lot of times, while most times
		//   the annotation are in the field, not in the getter
		
		if (getMetaModel() == null) return null;
		Annotation[] result = null;
		try {
			AnnotatedElement element = Classes.getField(getMetaModel().getPOJOClass(), getSimpleName()); 
			result = element.getAnnotations();
		} 
		catch (NoSuchFieldException ex) {
			// It could be a calculated property, without field
		}
				
		try {
			result = getAnnotationsFromGetter(result, "get");
		} 
		catch (NoSuchMethodException ex) {
			// It's a boolean property, with "is"			
			try {
				result = getAnnotationsFromGetter(result, "is");
			} 
			catch (NoSuchMethodException ex2) {
				log.warn(XavaResources.getString("field_getter_not_found", getName(), getMetaModel().getName()), ex2);
			}
		}

		return result;
	}

	private Annotation[] getAnnotationsFromGetter(Annotation[] result, String prefix) throws NoSuchMethodException {   
		AnnotatedElement element = getMetaModel().getPOJOClass().getMethod(prefix + Strings.firstUpper(getSimpleName()));
		Annotation[] getterAnnotations = element.getAnnotations();
		if (getterAnnotations.length > 0) {
			Collection<Annotation> annotations = new ArrayList<>();
			if (result != null) annotations.addAll(Arrays.asList(result));
			annotations.addAll(Arrays.asList(getterAnnotations));
			result = new Annotation[annotations.size()];
			annotations.toArray(result);
		}
		return result;
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

	/** @since 6.5.2 */
	public boolean isTransient() {
		return _transient;
	}

	/** @since 6.5.2 */
	public void setTransient(boolean _transient) {
		this._transient = _transient;
	}
	
}