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
	private Map details = new HashMap();
	private String referencedModelName;
	private Collection columns = null;
	

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
	
	String getReferencedModelName() throws XavaException {
		if (referencedModelName == null) {
			referencedModelName = getContainer().getMetaModel().getMetaReference(getReference()).getReferencedModelName();
		}
		return referencedModelName;
	}
	
	public String getReferencedTable() throws XavaException {
		return getReferencedMapping().getTable();
	}
	
	/**
	 * Qualified column. <p>
	 */
	public String getColumnForReferencedModelProperty(String property) throws ElementNotFoundException, XavaException {
		Object result = details.get(property);
		if (result == null) {
			throw new ElementNotFoundException("reference_mapping_property_not_found", property, referencedModelName, reference);
		}
		return ((ReferenceMappingDetail) result).getColumn();  
	}
	
	public String getCmpTypeNameForReferencedModelProperty(String property) throws ElementNotFoundException, XavaException {
		Object result = details.get(property);
		if (result == null) {
			throw new ElementNotFoundException("reference_mapping_property_not_found", property, referencedModelName, reference);
		}
		return ((ReferenceMappingDetail) result).getCmpTypeName();  
	}
	
	public IConverter getConverterForReferencedModelProperty(String property) throws ElementNotFoundException, XavaException { 
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
	public Collection getDetails() {
		return details.values();
	}
	
	
	ModelMapping getReferencedMapping() throws XavaException {
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
	
	public Collection getColumns() throws XavaException {
		if (columns == null) {
			columns = new ArrayList();			
			Collection keyProperties = getContainer().getMetaModel().getMetaReference(getReference()).getMetaModelReferenced().getAllKeyPropertiesNames();
			for (Iterator it = keyProperties.iterator(); it.hasNext();) {
				columns.add(getColumnForReferencedModelProperty((String) it.next()));
			}
		}
		return columns;
	}
	
	public String getCMPAttribute(String propertyNameOfReferencedModel) throws XavaException {
		if (getContainer().isReferenceOverlappingWithSomeProperty(getReference(), propertyNameOfReferencedModel)) {
			return getContainer().getCMPAttributeForColumn(getColumnForReferencedModelProperty(propertyNameOfReferencedModel));
		}
		return "_" + Strings.change(Strings.firstUpper(getReference()) + "_" + propertyNameOfReferencedModel, ".", "_");
	}

	public Collection getCmpFields() throws XavaException {
		Collection fields = new ArrayList();  
		for (Iterator it=getDetails().iterator(); it.hasNext();) {
			ReferenceMappingDetail d = (ReferenceMappingDetail) it.next();
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


