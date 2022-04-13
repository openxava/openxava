package org.openxava.mapping;

import java.util.*;



import org.apache.commons.logging.*;
import org.openxava.converters.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.util.meta.*;

public class PropertyMapping extends MetaSetsContainer {
	
	private static Log log = LogFactory.getLog(PropertyMapping.class);
	
	private ArrayList cmpFields;
	private String property;
	private String column;
	private String converterClassName;
	private String multipleConverterClassName;
	private IConverter converter;
	private IMultipleConverter multipleConverter;
	private boolean converterCreated = false;
	private boolean multpleConverterCreated = false;
	private String cmpTypeName;
	private ModelMapping modelMapping;
	private String formula; 
	
	
	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public PropertyMapping(ModelMapping parent) {
		this.modelMapping = parent;
	}
	
	public String getColumn() {
		return column;
	}
	public void setColumn(String tableColumn) {
		this.column = tableColumn;
	}

	public String getProperty() {
		return property;
	}
	public void setProperty(String modelProperty) {
		this.property = modelProperty;
	}
	
	public void setConverterClassName(String converterClassName) {
		this.converterClassName = converterClassName;		
	}

	/** 
	 * @return Null if mapping does not have converter
	 */	
	public  IConverter getConverter() throws XavaException {
		if (!converterCreated) {
			converter = createConverter();
			converterCreated = true;
		}
		return converter;
	}
	
	/** 
	 * @return Null if mapping does not have multiple converter
	 */	
	public  IMultipleConverter getMultipleConverter() throws XavaException {
		if (!multpleConverterCreated) {
			multipleConverter = createMultipleConverter();
			multpleConverterCreated = true;
		}
		return multipleConverter;
	}
	
	
	public boolean hasConverter() {
		return !Is.emptyString(converterClassName);
	}
	
	public boolean hasMultipleConverter() {
		return !Is.emptyString(multipleConverterClassName); 
	}
	
	public Collection getCmpFields() throws XavaException {
		if (cmpFields == null) return Collections.singletonList(toCmpField());
		return cmpFields;		
	}
	
	CmpField toCmpField() throws XavaException {
		CmpField f = new CmpField();
		if (Is.emptyString(getCmpTypeName())) {
			String typeName = getMetaProperty().getType().isArray()?getMetaProperty().getTypeName():getMetaProperty().getType().getName();
			f.setCmpTypeName(typeName);
		}
		else {
			f.setCmpTypeName(getCmpTypeName());
		}
		f.setColumn(getColumn());
		if (hasConverter()) {
			f.setCmpPropertyName("_" + Strings.firstUpper(getProperty()));
		}
		else {
			f.setCmpPropertyName(getProperty());
		}
		return f;
	}
		
	private IConverter createConverter() throws XavaException {
		try {
			if (!hasConverter()) return null;
			IConverter conversor = (IConverter) Class.forName(converterClassName).newInstance();
			if (containsMetaSets()) {
				assignPropertiesValues(conversor);
			}						
			return conversor;
		}
		catch (ClassCastException ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("create_converter_classcast_error", getProperty(), converterClassName, "IConverter");
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("create_converter_error", getProperty(), ex.getLocalizedMessage());
		}
	}
	
	private IMultipleConverter createMultipleConverter() throws XavaException {
		try {
			if (!hasMultipleConverter()) return null;
			IMultipleConverter conversor = (IMultipleConverter) Class.forName(multipleConverterClassName).newInstance();
			if (containsMetaSets()) {
				assignPropertiesValues(conversor);
			}						
			return conversor;
		}
		catch (ClassCastException ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("create_converter_classcast_error", getProperty(), converterClassName, "IMultipleConverter");
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("create_converter_error", getProperty(), ex.getLocalizedMessage());
		}
	}
		
	public String getCmpTypeName() throws XavaException {
		if (Is.emptyString(cmpTypeName)) cmpTypeName = getMetaProperty().getTypeName();
		
		if ("String".equals(cmpTypeName)) return "java.lang.String";
		if ("Integer".equals(cmpTypeName)) return "java.lang.Integer";
		if ("Long".equals(cmpTypeName)) return "java.lang.Long";
		if ("BigDecimal".equals(cmpTypeName)) return "java.math.BigDecimal";
		if ("Boolean".equals(cmpTypeName)) return "java.lang.Boolean";
		if ("Byte".equals(cmpTypeName)) return "java.lang.Byte";
		if ("Short".equals(cmpTypeName)) return "java.lang.Short";
		if ("Character".equals(cmpTypeName)) return "java.lang.Character";
		if ("Double".equals(cmpTypeName)) return "java.lang.Double";
		if ("Float".equals(cmpTypeName)) return "java.lang.Float";
		if ("BigInteger".equals(cmpTypeName)) return "java.math.BigInteger";

		return cmpTypeName;
	}
	
	public Class getCmpType() throws ClassNotFoundException, XavaException {
		return Primitives.classForName(getCmpTypeName());
	}

	public void setCmpTypeName(String cmpTypeName) {				
		this.cmpTypeName = Strings.change(cmpTypeName, " ", ""); 
	}

	public String getConverterClassName() {
		return converterClassName;
	}
	
	public void addCmpField(CmpField cmp) {
		if (cmpFields == null) cmpFields = new ArrayList();
		cmp.setCmpPropertyName(getProperty() + "_" + cmp.getConverterPropertyName());
		cmpFields.add(cmp);		
	}

	public String getMultipleConverterClassName() {
		return multipleConverterClassName;
	}

	public void setMultipleConverterClassName(String string) {
		this.multipleConverterClassName = string;
	}
	
	MetaProperty getMetaProperty() throws XavaException { 
		String property = Strings.change(getProperty(), "_", ".");
		return modelMapping.getMetaModel().getMetaProperty(property);
	}
	
	public void setDefaultConverter() throws XavaException {
		if (hasConverter() || hasMultipleConverter()) return;
		MetaProperty p = null;
		
		try {
			p =	modelMapping.getMetaModel().getMetaProperty(
					Strings.change(getProperty(), "_", "."));
		}
		catch (ElementNotFoundException ex) {			
			return;
		}
		
		// Converters in keys are troublesome for programmer and
		// usually disadvantages.
		// If you need a converter in key then you can put it explicitly.
		if (p.isKey()) return;
		
		setConverterClassName(Converters.getConverterClassNameFor(p));
		setCmpTypeName(Converters.getCmpTypeFor(p));
		
		if (!hasConverter()) {
			// In this way every no key property will have a converter
			// this is util because in code generated for properties with converter
			// there are things needed for every property (at least in ejb implementation)
			setConverterClassName(NoConversionConverter.class.getName());
			String cmpType = p.getType().isPrimitive()?Primitives.toWrapperClass(p.getType()).getName():p.getType().getName();
			if ("[B".equals(cmpType)) cmpType = "byte[]";
			setCmpTypeName(cmpType);
		}					
	}

	public boolean hasFormula() { 
		return !Is.emptyString(formula);
	}
	
}
