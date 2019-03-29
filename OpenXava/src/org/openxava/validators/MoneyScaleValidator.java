package org.openxava.validators;

import java.math.*;

import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */

public class MoneyScaleValidator implements IPropertyValidator {
	
	static final private int MAX_SCALE = 2;

	public void validate(Messages errors, Object value, String propertyName, String modelName) throws Exception {
		if (value == null) return; // The validity of a null as number is not the business of this validator
		if (!(value instanceof Number)) {
			errors.add("numeric", propertyName, modelName);
			return;
		}
		BigDecimal number = new BigDecimal( value.toString() );
		if (number.scale() > MAX_SCALE) {
			errors.add("max_scale", propertyName, modelName, new Integer(MAX_SCALE)); 
		}
	}

}
