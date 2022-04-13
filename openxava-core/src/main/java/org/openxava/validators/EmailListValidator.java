package org.openxava.validators;

import org.openxava.validators.IPropertyValidator;
import org.openxava.util.Messages;

import org.apache.commons.validator.GenericValidator;
import java.util.StringTokenizer;

/**
 * @author Janesh Kodikara
 */

public class EmailListValidator implements IPropertyValidator {

	public void validate(Messages errors, Object value, String propertyName,
			String modelName) throws Exception {

		if (value == null || value.toString().length() == 0)
			return;

		StringTokenizer emailAddresses = new StringTokenizer(value.toString(),
				",");
		while (emailAddresses.hasMoreTokens()) {
			if (!GenericValidator.isEmail(emailAddresses.nextToken())) {
				errors.add("email_list_validation_error", propertyName);
			}
		}

	}

}