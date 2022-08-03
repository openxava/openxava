package org.openxava.validators;

import java.util.*;

import org.apache.commons.validator.*;
import org.openxava.util.*;

/**
 * @author Janesh Kodikara
 */

public class EmailListValidator implements IPropertyValidator {

	public void validate(Messages errors, Object value, String propertyName, String modelName) throws Exception {
		if (value == null || value.toString().trim().length() == 0) return; 

		StringTokenizer emailAddresses = new StringTokenizer(value.toString(),	",");
		while (emailAddresses.hasMoreTokens()) {
			if (!GenericValidator.isEmail(emailAddresses.nextToken().trim())) { 
				errors.add("email_list_validation_error", propertyName);
			}
		}
	}

}