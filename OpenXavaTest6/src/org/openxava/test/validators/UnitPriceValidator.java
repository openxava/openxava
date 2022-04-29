package org.openxava.test.validators;

import java.math.*;

import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * @author Javier Paniza
 */

public class UnitPriceValidator implements IPropertyValidator {

	public void validate(
		Messages errors,
		Object object,
		String objectName,
		String propertyName)	{	
		if (object == null) return;
		if (!(object instanceof BigDecimal)) {			
			errors.add("expected_type", propertyName, objectName, "bigdecimal");			
			return;
		}
		BigDecimal n = (BigDecimal) object;
		if (n.intValue() > 1000) {			
			errors.add("not_greater_1000");
		}
	}
}
