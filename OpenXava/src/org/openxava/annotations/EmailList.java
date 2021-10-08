package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * String to save several emails separated by commas. <p>
 * 
 * The data type is String with a length of 200.
 * 
 * Applies to properties.<p>
 * 
 * Example:
 * <pre>
 * &nbsp;@EmailList
 * &nbsp;private String emails;
 * </pre>
 * 
 * It's synonymous of @Stereotype("EMAIL_LIST").
 *
 * @since 6.6
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface EmailList {
	
}
