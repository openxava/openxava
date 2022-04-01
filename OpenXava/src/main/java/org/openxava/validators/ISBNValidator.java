package org.openxava.validators;

import org.openxava.validators.IPropertyValidator;
import org.openxava.util.Messages;

/**
 * 
 * @author Janesh Kodikara
 */

public class ISBNValidator implements IPropertyValidator {

	private org.apache.commons.validator.ISBNValidator validator = new org.apache.commons.validator.ISBNValidator();

	public void validate(Messages errors, Object value, String propertyName,
			String modelName) throws Exception {

		if (value == null || value.toString().length() == 0)
			return;

		if (!validator.isValid(value.toString())) {
			errors.add("isbn_validation_error", propertyName);
		}
		
	}

}