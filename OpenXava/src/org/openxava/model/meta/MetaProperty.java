package org.openxava.model.meta;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.*;
import java.util.*;

import javax.servlet.ServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.calculators.ICalculator;
import org.openxava.calculators.IHibernateIdGeneratorCalculator;
import org.openxava.mapping.ModelMapping;
import org.openxava.mapping.PropertyMapping;
import org.openxava.model.IModel;
import org.openxava.util.*;
import org.openxava.util.meta.MetaSet;
import org.openxava.util.meta.MetaSetsContainer;
import org.openxava.validators.*;
import org.openxava.validators.meta.MetaValidator;
import org.openxava.validators.meta.MetaValidatorFor;
import org.openxava.validators.meta.MetaValidators;


/**
 * @author Javier Paniza; modified by Radoslaw OStrzycki, Newitech Sp. z o.o.
 */
public class MetaProperty extends MetaMember implements Cloneable {
	
	private static Log log = LogFactory.getLog(MetaProperty.class);
		
	private Collection metaValidators;	
	private Collection validators;
	private Collection onlyOnCreateValidators;
	private Class type;
	private int size;
	private Integer scale;
	private boolean required;
	private boolean hidden;
	private boolean version; 
	private java.lang.String stereotype;
	private List validValues;
	private boolean readOnly;
	private boolean readOnlyCalculated = false;
	private MetaCalculator metaCalculator;	
	private MetaCalculator metaCalculatorDefaultValue;		
	private Collection dependentPropertiesNames;
	private String typeName;
	private boolean key;
	private boolean searchKey; 
	private boolean isKeySet;
	private boolean mappingSet;
	private PropertyMapping mapping;
	private DateFormat timeFormat = new SimpleDateFormat("HH:mm"); // 24 hours for all locales
	private boolean _transient;
	private String requiredMessage = "required";
	private String label;
	private String qualifiedLabel;
	private String calculation; 
	private Set<String> propertiesNamesUsedForCalculation; 
	
	public void setLabel(String newLabel) {
		super.setLabel(newLabel);
		label = newLabel;
	}
		
	public void addValidValue(Object validValue) {
		getValidValues().add(validValue);
	}
	public void containsValidValue(Object value) {
		getValidValues().contains(value);
	}
	
	public Object getValidValue(int i) throws XavaException {
		try {		
			if (isNumber()) { // It's a classic valid-value of OX. Uses 0 as no value, and 1 as first value
				if (i == 0) return "";
				return getValidValues().get(i-1);
			}
			else { // It's a Java 5 enum. Uses null as no value, and 0 as first value
				return getValidValues().get(i);
			}
		}
		catch (IndexOutOfBoundsException ex) {
			log.error(XavaResources.getString("valid_value_not_found_for_index_warning", new Integer(i)), ex); 			
			return "[" + i  + "]"; 
		}
	}
	
	/**
	 * The first value is 1, to left 0 for no value case.
	 */
	public int getValidValueIndex(Object value) { 
		return getValidValues().indexOf(value) + 1;
	}
	
	public String getValidValueLabel(int i) throws XavaException { 	
		return getValidValueLabel(Locales.getCurrent(), i);
	}	

	/**
	 * Deprecated since 3.1.
	 * 
	 * @deprecated Use getValidValueLabel(int i) instead  
	 */	
	public String getValidValueLabel(ServletRequest request, int i) throws XavaException { 	
		return getValidValueLabel(i);
	}

	public String getValidValueLabel(Locale locale, Object value) { 	
		return obtainValidValueLabel(locale, value);
	}
	
	public String getValidValueLabel(Object value) { 	
		return obtainValidValueLabel(Locales.getCurrent(), value);
	}
	
	/**
	 * Deprecated since 3.1.
	 * 
	 * @deprecated Use getValidValue(Object value) instead  
	 */
	public String getValidValueLabel(ServletRequest request, Object value) { 	
		return getValidValueLabel(value);
	}
		
	public String getValidValueLabel(Locale locale, int i) throws XavaException { 
		return obtainValidValueLabel(locale, getValidValue(i));
	}
	
	public String getQualifiedLabel(Locale locale) throws XavaException {
		if (!Is.emptyString(qualifiedLabel)) return qualifiedLabel;
		String labelId = getId();
		boolean tabReferenceLabel = isTabReferenceLabel(labelId);
		if (!Is.emptyString(label) && !tabReferenceLabel) return label;
		if (Labels.existsExact(labelId, locale)) {
			return getLabel(locale);
		}
		String qualifiedName = getQualifiedName();
		if (tabReferenceLabel && (
				labelId.endsWith(".name") || labelId.endsWith(".nombre") ||
				labelId.endsWith(".description") || labelId.endsWith(".descripcion") ||
				labelId.endsWith(".title") || labelId.endsWith(".titulo") 
			)
		) 
		{
			labelId = Strings.noLastTokenWithoutLastDelim(labelId, ".");
			qualifiedName = Strings.noLastTokenWithoutLastDelim(qualifiedName, ".");
		}
		if (isTabLabel(labelId)) {
			String genericIdForTab = "*" + Strings.noFirstToken(labelId, ".");
			if (Labels.existsExact(genericIdForTab, locale)) {
				return getLabel(locale, genericIdForTab);
			}
			return Labels.getQualified(getMetaModel().getName() + "." + qualifiedName, locale);
		}
		return Labels.getQualified(qualifiedName, locale);
	}

	private boolean isTabReferenceLabel(String labelId) {
		if (!isTabLabel(labelId)) return false;
		int idx = labelId.indexOf(".properties.");
		if (idx < 0) return false;
		String property = labelId.substring(idx + ".properties.".length());
		return property.split("\\.").length > 1;
	}
	
	private boolean isTabLabel(String labelId) {   
		String [] tokens = labelId.split("\\.");		
		if (tokens.length < 2) return false;
		return "tab".equals(tokens[1]) || "tabs".equals(tokens[1]);
	}

	public String getQualifiedLabel(ServletRequest request) throws XavaException { 
		return getQualifiedLabel(getLocale(request));
	}	

	/**
	 * A string with the localized labels separate with '|'.
	 */
	public String getValidValuesLabels(ServletRequest request) {
		return getValidValuesLabels(Locales.getCurrent());
	}
	
	/**
	 * A string with the localized labels separate with '|'.
	 */
	public String getValidValuesLabels(Locale locale) {
		if (!hasValidValues()) return "";
		StringBuffer sb = new StringBuffer();
		Iterator it = getValidValues().iterator();
		while (it.hasNext()) {
			sb.append(getValidValueLabel(locale, it.next()));
			if (it.hasNext()) {
				sb.append('|');
			}
		}
		return sb.toString();
	}
	
	private String obtainValidValueLabel(Locale locale, Object value) {
		if (Is.empty(value)) return ""; 
		String id = getId() + "." + value;											
		return Labels.get(id, locale); 
	}
		
	private IPropertyValidator createRequiredValidator() throws XavaException {
		String validatorClass = null;
		try {
			MetaValidatorFor vr = null;
			if (!Is.emptyString(getStereotype())) {
				vr = MetaValidators.getMetaValidatorRequiredFor(getStereotype());
			}
			if (vr == null) {
				vr = MetaValidators.getMetaValidatorRequiredFor(getType().getName());	
			}
			if (vr == null) {
				log.error(XavaResources.getString("required_validator_not_found_for_type", getType()));
				return new TolerantValidator();
			}
			validatorClass = vr.getValidatorClass();
			IPropertyValidator validator = (IPropertyValidator) Class.forName(validatorClass).newInstance();
			if (validator instanceof IWithMessage) {
				((IWithMessage) validator).setMessage(requiredMessage); 
			}
			return validator;
		} catch (ClassCastException ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("property_validator_invalid_class", validatorClass); 
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("create_validator_error", getName(), ex.getLocalizedMessage());
		}
	}
	
	private IPropertyValidator createDefaultValidator() throws XavaException {
		String validatorClass = null;
		try {
			MetaValidatorFor vr = null;
			if (!Is.emptyString(getStereotype())) {
				vr = MetaValidators.getMetaValidatorDefaultFor(getStereotype());
			}
			if (vr == null) {
				vr = MetaValidators.getMetaValidatorDefaultFor(getType().getName());	
			}
			if (vr == null) return null; 
			validatorClass = vr.getValidatorClass();
			return (IPropertyValidator) Class.forName(validatorClass).newInstance();
		} catch (ClassCastException ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("property_validator_invalid_class", validatorClass); 
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("create_validator_error", getName(), ex.getLocalizedMessage());
		}
	}
	
	
	public java.lang.String getStereotype() {
		return stereotype;
	}
	
	public boolean hasStereotype() {
		return !Is.emptyString(stereotype);
	}
	
	public boolean isNumber() throws XavaException {
		return 
			java.lang.Integer.class.equals(getType()) ||
			int.class.equals(getType()) ||
			java.lang.Long.class.equals(getType()) ||
			long.class.equals(getType()) ||
			java.math.BigDecimal.class.equals(getType()) ||
			java.lang.Short.class.equals(getType()) ||
			short.class.equals(getType()) ||
			java.lang.Float.class.equals(getType()) ||
			float.class.equals(getType()) ||
			java.lang.Double.class.equals(getType()) ||
			double.class.equals(getType());		
	}
	
	/**
	 * 
	 * @return Can return -1 if the datatype does not have length concept
	 */
	public int getSize() throws XavaException {
		if (size == 0) {
			if (!Is.emptyString(getStereotype())) {				
				try {					
					size = DefaultSize.forStereotype(getStereotype());					
					return size;
				}
				catch (ElementNotFoundException ex) {
					// left that get size from type
				}
			}
						
			if (hasValidValues()) {				
				size = createLengthFromValidValues();				
			}
			else {				
				try {
					size = DefaultSize.forType(getType());
				}
				catch (ElementNotFoundException ex) {
					size = -1; // Without length. In some datatypes this is fine
				}
			}
		}		
		return size;
	}
	
	
	/**
	 * 
	 * @return scale param
	 * @throws XavaException
	 * @author Radoslaw Ostrzycki, Newitech Sp. z o.o. ; based on Javier's code for size
	 */
	public int getScale() throws XavaException {
		if (scale == null) { 
			if (!Is.emptyString(getStereotype())) {				
				try {					
					scale = DefaultSize.scaleForStereotype(getStereotype());
					return scale;
				}
				catch (ElementNotFoundException ex) {
					// left that get size from type
				}
			}
						
			if (hasValidValues()) {
				scale = 0;
			}
			else {
				try {
					scale = DefaultSize.scaleForType(getType());
				}
				catch (ElementNotFoundException ex) {
					scale = 0; // default scale is 0
				}
			}
		}		
		return scale;		
	}
	

	private int createLengthFromValidValues() {		
		Iterator it = getValidValues().iterator();
		int t = 0;
		while (it.hasNext()) {
			Object value = it.next();			 
			if (value != null && value.toString().length() > t) t = value.toString().length();
		}
		return t;
	}

	public Class getType() throws XavaException {				
		if (type == null) {			
			if (Is.emptyString(getTypeName())) {				
				type = obtainTypeFromModel(getName());
			}
			else {
				try { 					
					type = obtainType(getTypeName());
				}
				catch (XavaException ex) {
					try {
						type = obtainTypeFromModel(getName());
					}
					catch (XavaException ex2) {
						throw new XavaException("incorrect_type_for_property", getName(), getTypeName());
					}
				}
			}
		}
		return type;		
	}
	
	public String getCMPTypeName() throws XavaException {
		PropertyMapping mapeo = getMapping();
		if (mapeo == null) return getType().getName();
		if (!mapeo.hasConverter()) return getType().getName();
		return mapeo.getCmpTypeName();
	}
	
	private Class obtainTypeFromModel(String propertyName) throws XavaException {
		try {
			return getMetaModel().getPropertyDescriptor(propertyName).getPropertyType();
		}
		catch (ElementNotFoundException ex) {
			return java.lang.Object.class;
		}		
		catch (Exception ex) {			
			throw new XavaException("type_from_model_error", propertyName);
		}
	}
		
	
	public String getTypeName() throws XavaException {
		if (Is.emptyString(typeName)){
			if (hasValidValues()) setTypeName("int");
			if (!Is.emptyString(getStereotype())) {		
				try {		
					String t = TypeStereotypeDefault.forStereotype(getStereotype());
					setTypeName(t);
				}
				catch (ElementNotFoundException ex) {					
					log.warn(XavaResources.getString("type_from_stereotype_warning", getStereotype()));
				}
			}
		}
		return typeName;
	}
	
	public void setTypeName(String type) throws XavaException {
		this.typeName = type;
	}
	
	private Class obtainType(String type) throws XavaException {		
		try {
			return Primitives.classForName(type);
		}
		catch (ClassNotFoundException ex2) {
			throw new XavaException("set_type_error", getName(), type);
		}
	}
	
	/**
	 * 
	 * @since 5.1
	 */
	public boolean hasNotDependentDefaultValueCalculator() {
		return metaCalculatorDefaultValue != null && !metaCalculatorDefaultValue.isDependent();
	}

	public boolean hasDefaultValueCalculator() {		
		return metaCalculatorDefaultValue != null;
	}
	
	public boolean hasCalculator() {		
		return metaCalculator != null;
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
	 * @return null if this does not have calculator 
	 */
	public ICalculator getCalculator() throws XavaException {
		if (!hasCalculator()) return null;		
		return metaCalculator.createCalculator();
	}
	
	public boolean isDefaultCalculatorHibernateIdGenerator() throws XavaException {
		try {
			if (!hasCalculatorDefaultValueOnCreate()) return false;		
			Class calculatorClass = Class.forName(getMetaCalculatorDefaultValue().getClassName()); 
			return IHibernateIdGeneratorCalculator.class.isAssignableFrom(calculatorClass);
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("is_default_calculator_hibernate_id_generator_error", getName());
		}
	}	
	
	/**
	 * 	 
	 * @precondition  isDefaultCalculatorHibernateIdGenerator() == true
	 */
	public String getHbmGeneratorCode() throws XavaException {		
		return metaCalculatorDefaultValue.createHibernateIdGeneratorCalculator().hbmGeneratorCode();
	}
	
	/**
	 * @return of IPropertyValidator
	 */
	private Collection getValidators() throws XavaException {
		if (validators == null) {
			validators = new ArrayList();
			if (metaValidators != null) {
				Iterator it = metaValidators.iterator();
				while (it.hasNext()) {
					MetaValidator metaValidator = (MetaValidator) it.next();
					if (metaValidator.isOnlyOnCreate()) {						
						if (onlyOnCreateValidators == null) onlyOnCreateValidators = new ArrayList();						
						onlyOnCreateValidators.add(metaValidator.createPropertyValidator());						
					}
					else {
						validators.add(metaValidator.createPropertyValidator());						
					}
				}
			}
			if (isRequired()) {
				validators.add(createRequiredValidator());
			}
			IPropertyValidator defaultValidator = createDefaultValidator();
			if (defaultValidator != null) validators.add(defaultValidator);
		}
		return validators;
	}
	
	public Collection getMetaValidators(){
		return metaValidators;
	}
	
	private Collection getOnlyOnCreateValidators() throws XavaException {
		return onlyOnCreateValidators;
	}
	
		
	private List getValidValues() {
		if (validValues == null) {
			validValues = new ArrayList();
		}
		return validValues;
	}
	
	public boolean isKey() {
		if (!isKeySet) {			
			try {
				if (!(getMetaModel() instanceof MetaEntity)) {								
					key = false;
				}										
				else if (((MetaEntity) getMetaModel()).isAnnotatedEJB3() || getMetaModel().isPojoGenerated()) {
					key = false;						
				}
				else {				
					key = ((MetaEntity) getMetaModel()).isKey(getName());
				}
			}
			catch (XavaException ex) {  
				// false is assumed, but isKeySet is not changed, for retry in future 
				log.warn(XavaResources.getString("is_key_warning", getName()),ex);
				return false;
			}
			isKeySet = true;
		}		
		return key;
	}
	
	public void setKey(boolean key) {		
		this.key = key;
		isKeySet = true;		
	}
	
	
	public boolean isCalculated() {
		return getMetaCalculator() != null;
	}
			
	public boolean isPersistent() throws XavaException {
		return getMapping() != null;
	}
	
	public boolean isRequired() {
		return required;
	}

	public void setStereotype(String newStereotype) {
		stereotype = newStereotype;
	}
	
	public void setRequired(boolean newRequired) {
		required = newRequired;
	}
	
	public void setSize(int newSize) {
		size = newSize;
	}
	
	public void setScale(int newScale) {
		scale = newScale;
	}	
	
	public boolean hasValidValues() {
		if (validValues == null) return false;
		return validValues.size() > 0;
	}
			
	public void validate(Messages errors, Object object) throws RemoteException {		
		try {
			validate(errors, object, getValidators(), null); 
		} 
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new RemoteException(
					XavaResources.getString("validate_error", getName(), getMetaModel().getName()));					
		}
	}
	
	public void validate(Messages errors, Object object, boolean creating) throws RemoteException {
		validate(errors, object, creating, null);
	}
	
	/** @since 6.2.1 */
	public void validate(Messages errors, Object object, boolean creating, String container) throws RemoteException { 
		try {
			validate(errors, object, getValidators(), container);
			if (creating) validate(errors, object, getOnlyOnCreateValidators(), container);
		} 
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new RemoteException(
					XavaResources.getString("validate_error", getName(), getMetaModel().getName()));					
		}
	}

			
	private void validate(Messages errors, Object object, Collection validators, String container) throws Exception {
		if (validators == null) return;
		Iterator it = validators.iterator();			
		while (it.hasNext()) {
			IPropertyValidator v = (IPropertyValidator) it.next();
			if (container == null) container = getMetaModel().getName(); 
			v.validate(errors, object, getName(), container);
		}
	}
		
	public Iterator validValues() {
		return getValidValues().iterator();	
	}
	
	public Iterator validValuesLabels() {
		return validValuesLabels(Locales.getCurrent());
	}	
	
	/**
	 * Deprecated since 3.1.
	 * 
	 * @deprecated Use validValuesLabels() instead
	 */ 
	public Iterator validValuesLabels(ServletRequest request) { 
		return validValuesLabels();
	}
	
	public Iterator validValuesLabels(Locale locale) {
		Iterator it = validValues();
		Collection labels = new ArrayList();
		while (it.hasNext()) {
			labels.add(obtainValidValueLabel(locale, it.next()));
		}
		return labels.iterator();
	}
	
	public boolean isReadOnly() throws XavaException {
		if (!readOnlyCalculated) {
			if (isKey()) readOnly = false;
			else if (isCalculated()) readOnly = true;
			else readOnly = calculateIfReadOnly();
			readOnlyCalculated = true;
		}		
		return readOnly;		
	}
	
	private boolean calculateIfReadOnly() { 
		try {			
			if (getMetaModel() == null) return false;
			if (!getMetaModel().isAnnotatedEJB3()) return false; 
			PropertiesManager man = new PropertiesManager(
				getMetaModel().getPropertiesClass());
			return !man.hasSetter(getName());
		}
		catch (Exception ex) {
			log.error(XavaResources.getString("read_only_property_warning", getName(), getMetaModel().getName()), ex);
			return true; 		
		}
	}
	
	public MetaProperty cloneMetaProperty() throws XavaException {
		try {
			MetaProperty clon = (MetaProperty) super.clone();
			// The next is to force calculate its properties and thus
			// the cloned metaproperty can be disconnected from model 
			clon.isReadOnly(); 
			clon.getType();
			
			// Sometimes it isn't possible to calculate mapping from the original property , 
			// but it will be possible in the cloned one. 
			// The next code is for leaving it the chance to try
			if (clon.mapping == null) clon.mappingSet = false; 

			return clon;
		}
		catch (XavaException ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("property_clone_error", getName(), ex.getLocalizedMessage());	
		}
		catch (CloneNotSupportedException ex) {
			log.error(ex.getMessage(), ex);
			throw new RuntimeException(XavaResources.getString("property_implements_cloneable"));
		}
	}
	
	public MetaCalculator getMetaCalculator() {
		return metaCalculator;
	}

	public void setMetaCalculator(MetaCalculator metaCalculator) {
		this.metaCalculator = metaCalculator;
	}

	public MetaCalculator getMetaCalculatorDefaultValue() {
		return metaCalculatorDefaultValue;
	}

	public void setMetaCalculatorDefaultValue(MetaCalculator metaCalculatorDefaultValue) {
		this.metaCalculatorDefaultValue = metaCalculatorDefaultValue;
	}
	
	public boolean hasDependentProperties() throws XavaException {
		return !getDependentPropertiesNames().isEmpty();
	}
	
	public Collection getDependentPropertiesNames() throws XavaException {
		if (dependentPropertiesNames == null) {
			dependentPropertiesNames = new ArrayList();	
			if (hasMetaModel()) {
				fillDependedPropertiesNames(getMetaModel().getMetaPropertiesCalculated(), false);
				fillDependedPropertiesNames(getMetaModel().getMetaPropertiesWithDefaultValueCalculator(), true);
			}
		}
		return dependentPropertiesNames;
	}
	
	/**
	 * 
	 * @param other  Can be null, in which case return false
	 */
	public boolean depends(MetaProperty other) throws XavaException {
		if (other == null) return false;
		if (!other.hasDependentProperties()) return false;
		return other.getDependentPropertiesNames().contains(getName());
	}
	
	public boolean hasMetaModel() {		
		return getMetaModel() != null;
	}
	
	private void fillDependedPropertiesNames(Collection metaProperties, boolean initialValue) {
		Iterator it = metaProperties.iterator();
		while (it.hasNext()) {
			MetaProperty metaProperty = (MetaProperty) it.next();
			MetaSetsContainer metaCalculator = initialValue?metaProperty.getMetaCalculatorDefaultValue():metaProperty.getMetaCalculator();
			if (!metaCalculator.containsMetaSets()) continue;
			Iterator itMetaSets = metaCalculator.getMetaSets().iterator();
			while (itMetaSets.hasNext()) {
				MetaSet metaSet = (MetaSet) itMetaSets.next();
				if (metaSet.getPropertyNameFrom().equals(getName())) {
					dependentPropertiesNames.add(metaProperty.getName());
				}					
			}
		}		
	}
			
	public void setReadOnly(boolean readOnly) {		
		this.readOnly = readOnly;
		this.readOnlyCalculated = true;				
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
		
	public boolean hasCalculatorDefaultValueOnCreate() {
		return 	metaCalculatorDefaultValue != null &&
				metaCalculatorDefaultValue.isOnCreate();		
	}
	
	/**
	 * Can be null
	 */
	public PropertyMapping getMapping() throws XavaException {
		if (!mappingSet) {
			mappingSet = true;
			if (getMetaModel() == null) {
				mapping = null;
				return null;
			}
			String propertyName = null;
			ModelMapping modelMapping = null;			
			if (getMetaModel() instanceof MetaAggregateForReference) {				
				if (getQualifiedName().indexOf('.')>= 0) {
					propertyName = Strings.change(getQualifiedName(), ".", "_");
					modelMapping = getMetaModel().getMetaComponent().getEntityMapping();
				}
				else {
					mapping = null;					
					return null;
				}
			}
			if (propertyName == null) propertyName = getName();
			if (modelMapping == null) modelMapping = getMetaModel().getMapping();									
			if (modelMapping == null) mapping = null;			
			else {
				try {
					mapping = modelMapping.getPropertyMapping(propertyName);
				}
				catch (ElementNotFoundException ex) {
					mapping = null;
				}
			}
		}	
		return mapping;
	}

	
	/**
	 * Convert the argument in a object of type valid
	 * for assign to this property. <p>
	 * Convierte el argumento enviado en un objeto de tipo v�lido
	 * para asignar a esta propiedad. <p>
	 * 
	 * If argument is primitive return the match wrapper.
	 * 
	 * @return Can be null 
	 */
	public Object parse(String value) throws ParseException, XavaException {
		return parse(value, Locale.getDefault());
	}
	
	/**
	 * Convert the argument in a object of type valid
	 * for assign to this property. <p>
	 * 
	 * If argument is primitive return the match wrapper.
	 * 
	 * @return Can be null 
	 */
	public Object parse(String value, Locale locale) throws ParseException, XavaException { 
		if (value == null) return null;		
		boolean emptyString = Is.emptyString(value);
		Class type = getType();
		if (String.class.isAssignableFrom(type)) return value;
		value = value.trim();
		try { 
			if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) { 
				return emptyString? null : new Integer(value); // we needed null
			}
			
			if (BigDecimal.class.isAssignableFrom(type)) {
				if (emptyString) return null; // It was: new BigDecimal("0.00"); and this was changing data. You can change this behaviour with a formatter in editor.xml
				value = Strings.change(value, " ", ""); // In order to work with Polish
				Number n = NumberFormat.getNumberInstance(locale).parse(value);
				return new BigDecimal(n.toString());
			}
			
			if (isTimestamp()) {  
				if (emptyString) return null;
				java.util.Date date = null;
				try {
					date = Dates.getDateTimeFormat(locale).parse(value); 
				}
				catch (ParseException ex) {
					date = DateFormat.getDateInstance(DateFormat.SHORT, locale).parse(value);
				}
				return java.sql.Timestamp.class.isAssignableFrom(type)?new Timestamp(date.getTime()):date; 
			}		
			
			if (java.sql.Time.class.isAssignableFrom(type)) { 
				if (emptyString) return null;
				java.util.Date date = timeFormat.parse(value);
				return new Time(date.getTime());
			}
			
			if (java.util.Date.class.isAssignableFrom(type)) {
				return emptyString?null:DateFormat.getDateInstance(DateFormat.SHORT, locale).parse(value);
			}
			
			if (java.time.LocalDate.class.isAssignableFrom(type)) {
				return emptyString?null:java.time.LocalDate.parse(value, DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale));
			}
			
			if (Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type)) {
				return emptyString ? null : new Long(value);
			}
			
			if (Short.class.isAssignableFrom(type) || short.class.isAssignableFrom(type)) {
				return emptyString ? null : new Short(value);
			}
			
			if (Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type)) {
				if (emptyString) 
					return type.isPrimitive() ? new Float(0) : null;
				value = Strings.change(value, " ", ""); // In order to work with Polish
				Number n = NumberFormat.getNumberInstance(locale).parse(value);
				return new Float(n.floatValue());
			}
			
			if (Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type)) {
				if (emptyString) 
					return type.isPrimitive() ? new Double(0) : null;
				value = Strings.change(value, " ", ""); // In order to work with Polish					
				Number n = NumberFormat.getNumberInstance(locale).parse(value);
				return new Double(n.doubleValue());
			}
			
			if (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type)) {							
				return (!type.isPrimitive() && emptyString) ? null : Boolean.valueOf(value);
			}
			
			if (BigInteger.class.isAssignableFrom(type)) {
				if (emptyString) return null;		
				value = Strings.change(value, " ", ""); // In order to work with Polish				
				Number n = NumberFormat.getNumberInstance(locale).parse(value);
				return new BigInteger(n.toString());
			}
			
			// This is for processing Java 5 enums, but the code compile and works with a Java 1.4
			Class enumClass = getEnumClass(); 
			if (enumClass != null && enumClass.isAssignableFrom(type)) {
				return parseEnum(value);				
			}
						
			if (IModel.class.isAssignableFrom(type) || MetaModel.existsForPOJOClass(type)) { 
				return parseModelObject(value);
			}
						
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new ParseException(XavaResources.getString("from_string_on_property_error", value, type.getName(), getName()), -1);
		}
		throw new ParseException(XavaResources.getString("from_string_on_property_not_supported", type), -1);
	}
	
	private boolean isTimestamp() { 
		return isTypeOrStereotypeCompatibleWith(java.sql.Timestamp.class);
	}
	
	/**
	 * 
	 * @since 5.3.1
	 */
	public boolean isTypeOrStereotypeCompatibleWith(Class type) { 
		if (type.isAssignableFrom(getType())) return true;
		if (Is.emptyString(getStereotype())) return false;
		try {
			String typeName = TypeStereotypeDefault.forStereotype(getStereotype());
			Class stereotypeType = Class.forName(typeName);
			return type.isAssignableFrom(stereotypeType);
		}
		catch (ClassNotFoundException ex) {
			return false; 
		}
		catch (ElementNotFoundException ex) {
			return false;
		}
	}
	

	private Object parseEnum(String value) throws Exception { 
		// We parse as an int
		if (Is.emptyString(value) || "null".equals(value)) return null;
		try {
			Integer ordinal = new Integer(value);
			return getValidValue(ordinal.intValue());			
		}
		catch (NumberFormatException ex) {
			// We try parse from the string representation of the enum
			// We use introspection in order that this code compile and run in a Java 1.4
			return XObjects.execute(getEnumClass(), "valueOf", Class.class, getType(), String.class, value); 			
		}
	}
		
	private Object parseModelObject(String string) throws Exception {		
		if (Is.emptyString(string)) return null;
		Object model = getType().newInstance(); 
		StringTokenizer stringValues = new StringTokenizer(string, "[.]");
		parseModelObject(model, stringValues, "");
		return model;
	}
	 
	private void parseModelObject(Object model, StringTokenizer stringValues, String prefix) throws Exception {
		java.lang.reflect.Field [] fields = model.getClass().getDeclaredFields();
		Arrays.sort(fields, FieldComparator.getInstance());
		MetaModel metaModel = MetaModel.getForPOJO(model);
		PropertiesManager pm = new PropertiesManager(model);
		for (int i=0; i < fields.length; i++) {			
			if (metaModel.isKey(fields[i].getName())) {
				if (metaModel.containsMetaReference(fields[i].getName())) {
					Object ref = pm.executeGet(fields[i].getName());
					if (ref == null) {
						ref = metaModel.getMetaReference(fields[i].getName()).getMetaModelReferenced().getPOJOClass().newInstance();
						pm.executeSet(fields[i].getName(), ref);
					}
					parseModelObject(ref, stringValues, prefix + fields[i].getName() + ".");
				}
				else {
					pm.executeSetFromString(fields[i].getName(), stringValues.nextToken());
				}
			}
		}		
	}
	
	/**
	 * Convert a valid value for this property in a String
	 * valid for display to user. <p>
	 * 
	 * @return Can be null. 	 	 
	 */
	public String format(Object value) throws XavaException {
		return format(value, Locale.getDefault());
	}
	
	/**
	 * Convert a valid value for this property in a String
	 * valid for display to user. <p>
	 * 
	 * @return Can be null. 	 	 
	 */
	public String format(Object value, Locale locale) throws XavaException { 
		if (value == null) return "";				
		Class type = getType();
		if (String.class.isAssignableFrom(type)) return (String) value;
		try { 
			if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
				return value.toString();
			}
			if (BigDecimal.class.isAssignableFrom(type)) {
				NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
				numberFormat.setMaximumFractionDigits(getScale());
				numberFormat.setMinimumFractionDigits(getScale());
				return numberFormat.format(value);
			}
			if (java.sql.Time.class.isAssignableFrom(type)) { 
				return timeFormat.format(value);
			}
			if (isTimestamp()) { 
				return Dates.getDateTimeFormat(locale).format(value); 
			}		
			if (java.util.Date.class.isAssignableFrom(type)) {
				return DateFormat.getDateInstance(DateFormat.SHORT, locale).format(value);
			}
			if (java.time.LocalDate.class.isAssignableFrom(type)) {
				return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale).format((java.time.LocalDate) value);
			}
			if (Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type)) {
				return value.toString();
			}
			if (Short.class.isAssignableFrom(type) || short.class.isAssignableFrom(type)) {
				return value.toString();
			}
			if (Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type) ||
				Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type)) 
			{
				NumberFormat nf = NumberFormat.getNumberInstance(locale);
				nf.setMaximumFractionDigits(getScale()); 
				return nf.format(value);
			}
			if (BigInteger.class.isAssignableFrom(type)) {
				NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
				numberFormat.setMaximumFractionDigits(0);
				return numberFormat.format(value);				
			}			
			Class enumClass = getEnumClass(); 
			if (enumClass != null && enumClass.isAssignableFrom(type)) {
				return value.toString();
			}
			if (IModel.class.isAssignableFrom(type)) { 
				return value.toString();
			}			
			if (MetaModel.existsForPOJOClass(type)) { 
				return MetaModel.getForPOJO(value).toString(value);
			}
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("to_string_on_property_error", value, type.getName(), getName());
		}
		throw new XavaException("to_string_on_property_not_supported", type);
	}
		
	public void addMetaValidator(MetaValidator metaValidator) {
		if (metaValidators == null) metaValidators = new ArrayList();
		metaValidators.add(metaValidator);		
	}
	
	public boolean usesForCalculation(String qualifiedPropertyName) { 
		if (!hasCalculation()) return false;
		return getPropertiesNamesUsedForCalculation().contains(qualifiedPropertyName);
	}
	
	public Set<String> getPropertiesNamesUsedForCalculation() { 
		if (propertiesNamesUsedForCalculation == null) {
			propertiesNamesUsedForCalculation = new HashSet<String>();
			String [] properties = calculation.replaceAll("[Ss][Uu][Mm]\\((.*)\\)", "$1_SUM_").split("[\\*()/+\\- ]"); 
		    for (String property: properties) {
		    	if (!Is.emptyString(property) && !Strings.isNumeric(property)) {
		    		propertiesNamesUsedForCalculation.add(property);
		    	}
		    }
		}
		return propertiesNamesUsedForCalculation;
	}
	
	public String toString() {		
		return "MetaProperty:" + getId();
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof MetaProperty)) return false;
		MetaProperty other = (MetaProperty) o;
		return getQualifiedName().equals(other.getQualifiedName());
	}
		
	public int hashCode() {		
		return getQualifiedName().hashCode();
	}
	
	public boolean isTransient() {
		return _transient;
	}
	public void setTransient(boolean _transient) {
		this._transient = _transient;
	}
	
	private Class getEnumClass() { // Enum
		try {
			return Class.forName("java.lang.Enum");
		} 
		catch (ClassNotFoundException e) {
			// Not Java 5
			return null;
		}		
	}
	public boolean isVersion() {
		return version;
	}
	public void setVersion(boolean version) {
		this.version = version;
		if (version) setHidden(true);
	}
	public boolean isSearchKey() {
		return searchKey;
	}
	public void setSearchKey(boolean searchKey) {
		this.searchKey = searchKey;
	}
	public String getRequiredMessage() {
		return requiredMessage;
	}
	public void setRequiredMessage(String requiredMessage) {
		this.requiredMessage = requiredMessage;
	}

	public String getQualifiedLabel() {
		return qualifiedLabel;
	}

	public void setQualifiedLabel(String qualifiedLabel) {
		this.qualifiedLabel = qualifiedLabel;
	}

	public String getCalculation() {
		return calculation;
	}

	public void setCalculation(String calculation) {
		this.calculation = calculation;
	}
	
	/**
	 * @since 5.7
	 */
	public boolean hasCalculation() { 
		return !Is.emptyString(calculation);
	}


	
}