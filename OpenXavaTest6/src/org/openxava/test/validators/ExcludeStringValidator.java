package org.openxava.test.validators;

import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * @author Javier Paniza
 */

public class ExcludeStringValidator implements IPropertyValidator {
	
	private String string;

	public void validate(
		Messages errors,
		Object value,
		String objectName,
		String propertyName)
		throws Exception {
		if (value==null) return;
		if (value.toString().indexOf(getString()) >= 0) {
			errors.add("exclude_string", propertyName, objectName, getString());
		}
	}

	public String getString() {
		return string==null?"":string;
	}

	public void setString(String string) {
		this.string = string;
	}

}
