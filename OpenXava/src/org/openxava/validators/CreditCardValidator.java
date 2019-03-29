package org.openxava.validators;

import org.openxava.validators.IPropertyValidator;
import org.openxava.util.Messages;
import org.apache.commons.validator.GenericValidator;

/**
 * @author Janesh Kodikara
 */

public class CreditCardValidator implements IPropertyValidator {

	public void validate(Messages errors, Object value, String propertyName,
			String modelName) throws Exception {

		if (value == null || value.toString().length() == 0)
			return;

		if (!GenericValidator.isCreditCard(value.toString())) {
			errors.add("creditcard_validation_error", propertyName);
		}

	}

}
