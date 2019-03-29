package org.openxava.validators;


import org.openxava.util.*;

/**
 * Validator for a single property.
 * 
 * @author Javier Paniza
 */
public interface IPropertyValidator extends java.io.Serializable {

	/**
	 * Validate. <p>
	 *
	 * The validation errors are added to an object of type <code>Messages</code>. <br> 
	 *
	 * @param errors  Not null. Ids list to read in the resources file
	 * @param value  Value to validate. It can be null
	 * @param propertyName  Property id in the resources file 
	 * @param modelName  Object id in the resources file.	 
	 * @exception Exception  Any unexpected problem.
	 */
	void validate(Messages errors, Object value, String propertyName, String modelName) throws Exception;

}
