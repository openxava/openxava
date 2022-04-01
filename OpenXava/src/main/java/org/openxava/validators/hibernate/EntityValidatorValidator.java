package org.openxava.validators.hibernate;

import java.util.*;

import javax.validation.*;

import org.openxava.annotations.*;
import org.openxava.component.parse.*;
import org.openxava.util.*;
import org.openxava.util.meta.*;
import org.openxava.validators.*;
import org.openxava.validators.meta.*;

/**
 * Implements a EntityValidator of OpenXava as a Bean Validation Constraint. <p>
 *  
 * @author Javier Paniza
 */

public class EntityValidatorValidator implements ConstraintValidator<EntityValidator, Object> {
	
	private MetaValidator metaValidator;

	public void initialize(EntityValidator entityValidator) {
		metaValidator = AnnotatedClassParser.createEntityValidator(entityValidator);
	}

	public boolean isValid(Object entity, ConstraintValidatorContext context) { 	
		if (HibernateValidatorInhibitor.isInhibited()) return true;  // Usually when saving from MapFacade, MapFacade already has done the validation
		if (metaValidator.isOnlyOnCreate()) return true;
		try {			
			Iterator<MetaSet> itSets =  metaValidator.getMetaSetsWithoutValue().iterator();			 
			IValidator v = metaValidator.createValidator();
			PropertiesManager validatorProperties = new PropertiesManager(v);
			PropertiesManager entityProperties = new PropertiesManager(entity);
			while (itSets.hasNext()) {
				MetaSet set = itSets.next();					
				Object value = entityProperties.executeGet(set.getPropertyNameFrom());								
				validatorProperties.executeSet(set.getPropertyName(), value);									
			}							
			v.validate(FailingMessages.getInstance());
			return true;
		}
		catch (IllegalStateException ex) {
			if (!FailingMessages.EXCEPTION_MESSAGE.equals(ex.getMessage())) throw ex;
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(ex.getCause().getMessage())
				   .addConstraintViolation();
			return false;
		}
		catch (javax.validation.ValidationException|org.openxava.validators.ValidationException  ex) { 
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(ex.getMessage())
				   .addConstraintViolation();
			return false;
		}
		catch (RuntimeException ex) {
			throw ex;
		}
		catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}		
	}
}
