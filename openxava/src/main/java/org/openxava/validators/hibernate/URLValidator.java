package org.openxava.validators.hibernate;

import jakarta.validation.*;
import org.hibernate.validator.constraints.*;

/**
 * 
 * @since 8.0
 * @author Javier Paniza
 */
public class URLValidator implements ConstraintValidator<URL, CharSequence> {

	private org.hibernate.validator.internal.constraintvalidators.hv.URLValidator validator = 
		new org.hibernate.validator.internal.constraintvalidators.hv.URLValidator();

	/**
	 * @since 8.0
	 */
	public void initialize(URL url) {
		validator.initialize(url);
	}

	/**
	 * @since 8.0
	 */
	public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
		if (value == null) return true;
		String urlStr = value.toString().trim().replace(" ", "%20");
		return validator.isValid(urlStr, context);
	}

}
