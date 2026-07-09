package org.openxava.mapping;


import java.util.*;



import org.openxava.component.*;
import org.openxava.converters.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;


public class ReferenceMapping implements java.io.Serializable, Cloneable { 
	
	private ModelMapping container;
	private String reference;
	private ModelMapping referencedMapping;
	private Map<String, ReferenceMappingDetail> details = new HashMap<>();
	private String referencedModelName;
	private Collection<String> columns = null;
	

	public ReferenceMapping clone() {
		try {
			return (ReferenceMapping) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void addDetail(ReferenceMappingDetail detail) {
		details.put(detail.getReferencedModelProperty(), detail);
		detail.setContainer(this); 
	}
	
	/**
	* @throws XavaException
	 */
	String getReferencedModelName() {
		if (referencedModelName == null) {
			referencedModelName = getContainer().getMetaModel().getMetaReference(getReference()).getReferencedModelName();
		}
		return referencedModelName;
	}
	
	/**
	* @throws XavaException
	 */
	public String getReferencedTable() {
		return getReferencedMapping().getTable();
	}
	
	/**
	 * Qualified column. <p>
	 * @throws XavaException
	 */
	public String getColumnForReferencedModelProperty(String property) throws ElementNotFoundException {
		Object result = details.get(property);
		if (result == null) {
			throw new ElementNotFoundException("reference_mapping_property_not_found", property, referencedModelName, reference);
		}
		return ((ReferenceMappingDetail) result).getColumn();  
	}
	
	/**
	* @throws XavaException
	 */
	public String getCmpTypeNameForReferencedModelProperty(String property) throws ElementNotFoundException {
		Object result = details.get(property);
		if (result == null) {
			throw new ElementNotFoundException("reference_mapping_property_not_found", property, referencedModelName, reference);
		}
		return ((ReferenceMappingDetail) result).getCmpTypeName();  
	}
	
	/**
	* @throws XavaException
	 */
	public IConverter getConverterForReferencedModelProperty(String property) throws ElementNotFoundException { 
		Object result = details.get(property);
		if (result == null) {
			throw new ElementNotFoundException("reference_mapping_property_not_found", property, referencedModelName, reference);
		}
		return ((ReferenceMappingDetail) result).getConverter();  
	}
		
	/**
	 * Column not qualified. <p>	 
	 */
	public boolean hasColumnForReferencedModelProperty(String property) {
		return details.containsKey(property);
	}
	
	
	/**
	 * @return Not null.
	 */
	public Collection<ReferenceMappingDetail> getDetails() {
		return details.values();
	}
	
	
	/**
	* @throws XavaException
	 */
	ModelMapping getReferencedMapping() {
		if (referencedMapping == null) {
			referencedMapping = MetaComponent.get(getReferencedModelName()).getEntityMapping();
		}
		return referencedMapping;
	}

	public ModelMapping getContainer() {
		return container;
	}
	public void setContainer(ModelMapping container) {
		this.container = container;
	}

	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	
	/**
	* @throws XavaException
	 */
	public Collection<String> getColumns() {
		if (columns == null) {
			columns = new ArrayList<String>();			
			Collection<String> keyProperties = getContainer().getMetaModel().getMetaReference(getReference()).getMetaModelReferenced().getAllKeyPropertiesNames();
			for (Iterator<String> it = keyProperties.iterator(); it.hasNext();) {
				columns.add(getColumnForReferencedModelProperty(it.next()));
			}
		}
		return columns;
	}
	
	/**
	* @throws XavaException
	 */
	public String getCMPAttribute(String propertyNameOfReferencedModel) {
		if (getContainer().isReferenceOverlappingWithSomeProperty(getReference(), propertyNameOfReferencedModel)) {
			return getContainer().getCMPAttributeForColumn(getColumnForReferencedModelProperty(propertyNameOfReferencedModel));
		}
		return "_" + Strings.change(Strings.firstUpper(getReference()) + "_" + propertyNameOfReferencedModel, ".", "_");
	}

	/**
	* @throws XavaException
	 */
	public Collection<CmpField> getCmpFields() {
		Collection<CmpField> fields = new ArrayList<CmpField>();  
		for (Iterator<ReferenceMappingDetail> it=getDetails().iterator(); it.hasNext();) {
			ReferenceMappingDetail d = it.next();
			CmpField field = new CmpField();
			field.setCmpPropertyName( 
					"_" + Strings.firstUpper(getReference()) + "_" + 
					Strings.change(d.getReferencedModelProperty(), ".", "_"));			
			String propertyName = 
				Strings.change(getReference(), "_", ".") + "." +
				Strings.change(d.getReferencedModelProperty(), "_", ".");
			MetaProperty property = getContainer().getMetaModel().getMetaProperty(propertyName);
			field.setCmpTypeName(property.getMapping().toCmpField().getCmpTypeName());			
			field.setColumn(d.getColumn());
			fields.add(field);
		}
		return fields;
	}
	
	public boolean hasConverter(String property) {
		ReferenceMappingDetail detail = (ReferenceMappingDetail) details.get(property);
		if (detail == null) return false;
		return detail.hasConverter();
	}
	
}


