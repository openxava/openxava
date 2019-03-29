package org.openxava.validators;

/**
 * Validates entity (or aggregate too) before removing. <p>
 * 
 * If the validation fails the entity not will be removed.
 * 
 * @author Javier Paniza
 *
 */
public interface IRemoveValidator extends IValidator {
	
	void setEntity(Object entity) throws Exception;

}
