package org.openxava.validators.hibernate;

import javax.validation.*;
import org.openxava.annotations.*;
import org.openxava.validators.*;
import org.openxava.validators.meta.*;

/**
 * Implements Required annotation of OpenXava as a Bean Validation Constraint. <p>
 *  
 * @author Javier Paniza
 */

public class RequiredValidator implements ConstraintValidator<Required, Object> {	

	
	public void initialize(Required required) {
	}

	public boolean isValid(Object value, ConstraintValidatorContext context) { 
		if (HibernateValidatorInhibitor.isInhibited()) return true; // Usually when saving from MapFacade, MapFacade already has done the validation
		if (value == null) return false;		
		try {
			IPropertyValidator validator = 
				MetaValidators.getMetaValidatorRequiredFor(value.getClass().getName()).
					getPropertyValidator();			
			validator.validate(FailingMessages.getInstance(), value, "", "");
			return true;
		} 
		catch (IllegalStateException ex) {
			if (!FailingMessages.EXCEPTION_MESSAGE.equals(ex.getMessage())) throw ex;
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(ex.getCause().getMessage())
				   .addConstraintViolation();
			return false;
		}
		catch (RuntimeException ex) {
			throw ex;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage(), ex);
		}		
	}

}
