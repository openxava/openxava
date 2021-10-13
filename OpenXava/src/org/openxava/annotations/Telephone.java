package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Field used to store a phone number. <p>
 * 
 * The data type is String with length of 15.
 * 
 * Applies to properties.<p>
 * 
 * Example:
 * <pre>
 * &nbsp;@Telephone
 * &nbsp;private String phoneNumber;
 * </pre>
 * 
 * It's synonymous of @Stereotype("TELEPHONE").
 *
 * @since 6.6
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Telephone {
	
}
