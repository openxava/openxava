package org.openxava.validators;

import org.openxava.util.*;

/**
 * tmp
 * 
 * @since 6.6
 * @author Javier Paniza
 */
public class CoordinatesValidator implements IPropertyValidator {


	public void validate(Messages errors, Object value, String propertyName, String modelName) throws Exception {
		if (Is.empty(value)) return;
		String coordinates = value.toString();
		if (!coordinates.matches("^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$")) {
			errors.add("invalid_coordinates", propertyName, modelName); 
		}
	}


}
