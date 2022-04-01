package org.openxava.validators;


import org.openxava.util.*;


/**
 * Validator. <p>
 *  
 * The method of this interfaces does not receive the value to validate,
 * this must to be assigned as property.
 * 
 * For example, you can use a validator in the next way:  
 * <pre>
 * IValidator v = new LimitValidator();
 * v.setLimit(1000);
 * v.setValue(invoice.getAmount()); // For example
 * Messages errors = new Messages();
 * v.validate(errors);
 * // If there are validation errors are added to 'errors'. 
 * </pre>
 *  
 * @author Javier Paniza
 */

public interface IValidator extends java.io.Serializable {
	
	/**
	 * Does validation. <p>
	 * 
	 * @param errors  Validation errors list; a list of id to read in
	 * 								the resources file  
	 * @throws Exception  Any unexpected problem
	 */
	void validate(Messages errors) throws Exception;

}
