package org.openxava.test.validators;

import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * 
 * @author Javier Paniza
 */

public class PersonNameValidator implements IPropertyValidator {

	public void validate(Messages errors, Object value, String propertyName, String modelName) throws Exception {
		if ("MAKARIO".equalsIgnoreCase((String)value)) {
			errors.add("person_name_validation_error", "'MAKARIO'", propertyName, modelName);
		}
	}

}
