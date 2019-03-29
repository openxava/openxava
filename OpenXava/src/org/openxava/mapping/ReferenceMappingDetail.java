package org.openxava.mapping;

import java.util.*;



import org.apache.commons.logging.*;
import org.openxava.component.*;
import org.openxava.converters.*;
import org.openxava.util.*;
import org.openxava.util.meta.*;


public class ReferenceMappingDetail extends MetaSetsContainer {
	
	private static Log log = LogFactory.getLog(ReferenceMappingDetail.class);
	private static boolean someMappingUsesConverters = false;
	
	private String column;
	private String referencedModelProperty;
	private ReferenceMapping container;
	private String referencedTableColumn;
	private String converterClassName;
	private boolean converterCreated = false;
	private IConverter converter;
	private String cmpTypeName;
	
	 
	
	public String getColumn() {
		return column;
	}
	public void setColumn(String tableColumn) {
		this.column = tableColumn;
	}

	public String getReferencedModelProperty() {
		return referencedModelProperty;
	}
	public void setReferencedModelProperty(String referencedModelProperty) {
		this.referencedModelProperty = referencedModelProperty;
	}
	
	/**
	 * From a 'invoice.delivery.shipment.number' returns '.getInvoice().getDelivery().getShipment().getNumber()' 
	 */
	public String getReferenceModelPropertyAsJavaMethodCall() {
		StringBuffer result = new StringBuffer();
		StringTokenizer st = new StringTokenizer(getReferencedModelProperty(), ".");
		while (st.hasMoreTokens()) {
			result.append(".get");
			result.append(Strings.firstUpper(st.nextToken()));
			result.append("()");
		}
		return result.toString();
	}
	
	public String getQualifiedColumnOfReferencedTable() throws XavaException {		
		return getContainer().getReferencedTable() + "." + getReferencedTableColumn(); 		
	}
	
	public String getReferencedTableColumn() throws XavaException {
		if (referencedTableColumn == null) {
			ReferenceMapping referenceMapping = getContainer();			
			EntityMapping referencedMapping = MetaComponent.get(referenceMapping.getReferencedModelName()).getEntityMapping();
			referencedTableColumn = referencedMapping.getColumn(getReferencedModelProperty());
		}
		return referencedTableColumn;
	}
	
	public String getQualifiedColumn() throws XavaException {
		return getContainer().getContainer().getTableToQualifyColumn() + "." +  getColumn(); 
	}
	
	public ReferenceMapping getContainer() {
		return container;
	}
	public void setContainer(ReferenceMapping contenedor) {
		this.container = contenedor;
	}


	public String getConverterClassName() {
		return converterClassName;
	}
	public void setConverterClassName(String converterClassName) {
		someMappingUsesConverters = true;
		this.converterClassName = converterClassName;
	}
	public boolean hasConverter() {
		return !Is.emptyString(converterClassName);
	}
	
	public  IConverter getConverter() throws XavaException {  
		if (!converterCreated) {
			converter = createConverter();					
			converterCreated = true;
		}
		return converter;
	}
	
	private IConverter createConverter() throws XavaException {  
		try {
			if (!hasConverter()) return null;
			IConverter converter = (IConverter) Class.forName(converterClassName).newInstance();
			if (containsMetaSets()) {
				assignPropertiesValues(converter);
			}						
			return converter;
		}
		catch (ClassCastException ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("create_converter_classcast_error",  converterClassName, "IConverter");
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("create_converter_error", ex.getLocalizedMessage());
		}
	}
	
	public static boolean someMappingUsesConverters() {
		return someMappingUsesConverters;
	}
	
	public String getCmpTypeName() {
		if ("String".equals(cmpTypeName)) return "java.lang.String";
		if ("Integer".equals(cmpTypeName)) return "java.lang.Integer";
		if ("Long".equals(cmpTypeName)) return "java.lang.Long";		
		return cmpTypeName;
	}
	
	public void setCmpTypeName(String cmpTypeName) {
		this.cmpTypeName = cmpTypeName;
	}
}

