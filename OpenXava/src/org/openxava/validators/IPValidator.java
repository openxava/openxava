package org.openxava.validators;

import org.openxava.validators.IPropertyValidator;
import org.openxava.util.Messages;
import org.apache.commons.validator.GenericValidator;

/**
 * @author Janesh Kodikara
 */

public class IPValidator implements IPropertyValidator {

	public void validate(Messages errors, Object value, String propertyName,
			String modelName) throws Exception {

		String numberRegExp = "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
		String ipRegExp = "\\b" + numberRegExp + "\\." + numberRegExp + "\\."
				+ numberRegExp + "\\." + numberRegExp + "\\b";

		if (value == null || value.toString().length() == 0)
			return;

		if (!GenericValidator.matchRegexp(value.toString(), ipRegExp)) {
			errors.add("invalid_ip_error", propertyName);
		}

	}

}