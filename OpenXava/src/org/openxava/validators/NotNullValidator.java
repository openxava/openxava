package org.openxava.validators;



import org.openxava.util.*;


/**
 * 
 * @author Javier Paniza
 */
public class NotNullValidator implements IPropertyValidator, IWithMessage {

	private String message = "not_null"; 
	
	public void validate(
		Messages errors,
		Object object,
		String propertyName,
		String modelName) {
		if (object == null) {
			errors.add(message, propertyName, modelName); 
		}
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
}
