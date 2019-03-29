package org.openxava.validators.meta;



import org.apache.commons.logging.*;
import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * A validator associated to a type or stereotype. <p>
 * 
 * Used for validator for required and for default validators.<br>
 * 
 * @author Javier Paniza
 */
public class MetaValidatorFor {
	
	private static Log log = LogFactory.getLog(MetaValidatorFor.class);
	
	private java.lang.String forType;
	private String forStereotype;
	private java.lang.String validatorName;
	public java.lang.String validatorClass;
	private IPropertyValidator propertyValidator;
	

	/**
	 * The first time the validator is created, the other times returns the created one.
	 */
	public IPropertyValidator getPropertyValidator() throws XavaException { 
		if (propertyValidator == null) {
			propertyValidator = createPropertyValidator();
		}
		return propertyValidator;
	}

	/**
	 * Creates a validator each time that this method is called;
	 * this validator is configured with the values assigned in XML. 
	 */
	private IPropertyValidator createPropertyValidator() throws XavaException { 
		try {
			Object o = Class.forName(getValidatorClass()).newInstance();
			if (!(o instanceof IPropertyValidator)) {
				throw new XavaException("property_validator_invalid_class", getValidatorClass());
			}
			return (IPropertyValidator) o;			
		}
		catch (XavaException ex) {
			throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("create_validator_error", getValidatorName(), ex.getLocalizedMessage());
		}
	}
		
	public java.lang.String getValidatorClass() throws XavaException {
		if (validatorClass == null) {
			validatorClass =
				MetaValidators
					.getMetaValidator(getValidatorName())
					.getClassName();
		}
		return validatorClass;
	}

	public java.lang.String getValidatorName() {
		return validatorName;
	}

	public java.lang.String getForType() {
		return forType;
	}

	public void setValidatorClass(java.lang.String newValidatorClass) {
		validatorClass = newValidatorClass;
	}

	public void setValidatorName(java.lang.String newValidatorName) {
		validatorName = newValidatorName;
	}

	public void setForType(java.lang.String newForType) {
		forType = newForType;
	}

	public String getForStereotype() {
		return forStereotype;
	}

	public void setForStereotype(String newForStereotype) {
		this.forStereotype = newForStereotype;
	}

}
