package org.openxava.validators;



import org.openxava.util.*;


/**
 * @author Javier Paniza
 */

public class ValidationException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private Messages errors;
	
	public ValidationException() {		
	}
	
	public ValidationException(String messageText) {
		super(messageText);
	}

	public ValidationException(ValidationException ex) {		
		errors = ((ValidationException) ex).getErrors();
	}

	public ValidationException(Messages errors) {
		this.errors = errors;		
	}
	
	public String getMessage() {
		return errors==null?super.getMessage():errors.toString();		
	}

	
	public Messages getErrors() {
		if (errors == null) {			
			errors = new Messages();
			errors.add(super.getMessage());			
		}
		return errors;
	}
	
}
