package org.openxava.test.validators;

import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * 
 * @author Javier Paniza
 */
public class BookTitleValidator implements IPropertyValidator, IWithMessage {
	
	private String message;

	public void setMessage(String message) {
		this.message = message;		
	}

	public void validate(Messages errors, Object value, String propertyName, String modelName) {
		if (((String)value).contains("RPG")) {
			errors.add(message);
		}		
	}

}
