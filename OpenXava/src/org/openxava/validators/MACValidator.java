package org.openxava.validators;

import org.apache.commons.validator.*;
import org.openxava.util.*;

/**
 * MAC address validator.
 * 
 * @author Nelson Florez
 * 
 */

public class MACValidator implements IPropertyValidator {

	public void validate(Messages errors, Object value, String propertyName,
			String modelName) throws Exception {

		String numberRegExp = "([0-9a-fA-F]{2}[:-]){5}([0-9a-fA-F]{2})";
		String macRegExp = "\\b" + numberRegExp + "\\b";

		if (value == null || value.toString().length() == 0) return;

		if (!GenericValidator.matchRegexp(value.toString(), macRegExp))
			errors.add("invalid_mac_error", propertyName);

	}

}
