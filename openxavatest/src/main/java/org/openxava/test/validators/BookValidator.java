package org.openxava.test.validators;

import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * 
 * @author Javier Paniza
 */

public class BookValidator implements IValidator, IWithMessage {
	
	private String message;
	private String title;
	private String synopsis;

	public void validate(Messages errors) {
		if (title.contains("EL QUIJOTE") && synopsis.contains("JAVA")) {
			errors.add(message);
		}		
	}	
	public void setMessage(String message) {
		this.message = message;		
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSynopsis() {
		return synopsis;
	}
	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}

}
