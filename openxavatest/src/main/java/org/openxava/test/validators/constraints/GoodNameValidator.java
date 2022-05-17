package org.openxava.test.validators.constraints;

import javax.validation.*;

import org.openxava.test.annotations.*;
import org.openxava.test.model.*;

/**
 * An example of Bean Validation validator.
 * 
 * @author Javier Paniza 
 */

public class GoodNameValidator implements ConstraintValidator<GoodName, Author> {

	public void initialize(GoodName constraintAnnotation) {
	}
	
	public boolean isValid(Author author, ConstraintValidatorContext constraintContext) {
		return !author.getAuthor().equals("PEPE");
	}	

}

