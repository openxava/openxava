package org.openxava.validators;



import org.openxava.util.*;


/**
 * 
 * 
 * @author Javier Paniza
 */
public class NotBlankCharacterValidator implements IPropertyValidator, IWithMessage {

	private String message = "not_blank_character"; 
	
	public void validate(
		Messages errors,
		Object object,
		String propertyName,
		String modelName) {
		try {
			if (Character.isWhitespace(((Character) object).charValue())) {
				errors.add(message, propertyName, modelName); 
			}
		}
		catch (ClassCastException ex) {
			errors.add("expected_type", propertyName, modelName, "caracter");
		}
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
}
