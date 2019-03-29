package org.openxava.validators.meta;

import org.apache.commons.logging.*;
import org.openxava.util.*;
import org.openxava.util.meta.*;
import org.openxava.validators.*;

/**
 * 
 * @author Javier Paniza
 */
public class MetaValidator extends MetaSetsContainer {
	
	private static final long serialVersionUID = 5227442365317413802L;

	private static Log log = LogFactory.getLog(MetaValidator.class);
	
	private transient IRemoveValidator removeValidator;
	private transient IPropertyValidator propertyValidator;
	private transient IValidator validator;
	private String name;
	private boolean onlyOnCreate;	
	private String className;
	private String message;  
	

	public MetaValidator() {
		super();
	}

	public java.lang.String getClassName() throws XavaException {
		if (Is.emptyString(className)) {
			className = MetaValidators.getMetaValidator(getName()).getClassName();
		}
		return className;
	}

	public java.lang.String getName() {
		return name;
	}
	
	public void setClassName(java.lang.String newClass) {
		className = newClass;
	}

	public void setName(java.lang.String newName) {
		name = newName;
	}

	/**
	 * Creates a validator each time that this method is called;
	 * this validator is configured with the values assigned in XML. 
	 */
	public IValidator createValidator() throws XavaException {
		try {
			Object o = Class.forName(getClassName()).newInstance();
			if (!(o instanceof IValidator)) {
				throw new XavaException("validator_invalid_class", getClassName());
			}
			IValidator validator = (IValidator) o;
			if (validator instanceof IWithMessage && message != null) {
				((IWithMessage) validator).setMessage(message);
			}
			if (containsMetaSets()) {
				assignPropertiesValues(validator);
			}						
			return validator;
		}
		catch (XavaException ex) {
			throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("create_validator_error", getClassName(), ex.getLocalizedMessage());
		}
	}
	
	/**
	 * The first time the validator is created, the other times returns the created one.
	 */
	public IValidator getValidator() throws XavaException {
		if (validator == null) {
			validator = createValidator();
		}
		return validator;
	}
	
	/**
	 * Creates a validator each time that this method is called;
	 * this validator is configured with the values assigned in XML. 
	 */
	public IPropertyValidator createPropertyValidator() throws XavaException {
		try {
			Object o = Class.forName(getClassName()).newInstance();
			if (!(o instanceof IPropertyValidator)) {
				throw new XavaException("property_validator_invalid_class", getClassName());
			}
			IPropertyValidator validator = (IPropertyValidator) o;
			if (validator instanceof IWithMessage && message != null) { 
				((IWithMessage) validator).setMessage(message);
			}
			if (containsMetaSets()) {
				assignPropertiesValues(validator);
			}			
			return validator;
		}
		catch (XavaException ex) {
			throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("create_validator_error", getClassName(), ex.getLocalizedMessage());
		}
	}
	
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
	public IRemoveValidator createRemoveValidator() throws XavaException {
		try {
			Object o = Class.forName(getClassName()).newInstance();
			if (!(o instanceof IRemoveValidator)) {
				throw new XavaException("remove_validator_invalid_class", getClassName());
			}
			IRemoveValidator validator = (IRemoveValidator) o;
			if (containsMetaSets()) {
				assignPropertiesValues(validator);
			}						
			return validator;
		}
		catch (XavaException ex) {
			throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("create_validator_error", getClassName(), ex.getLocalizedMessage());
		}
	}
	
	/**
	 * The first time the validator is created, the other times returns the created one.
	 */
	public IRemoveValidator getRemoveValidator() throws XavaException {
		if (removeValidator == null) {
			removeValidator = createRemoveValidator();
		}
		return removeValidator;
	}

	public boolean isOnlyOnCreate() {
		return onlyOnCreate;
	}

	public void setOnlyOnCreate(boolean onlyOnCreate) {
		this.onlyOnCreate = onlyOnCreate;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}		
			
	
}