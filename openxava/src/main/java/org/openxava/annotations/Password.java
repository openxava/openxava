package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A password field that shows * when user types. <p>
 * 
 * The data type is String.
 * 
 * Applies to properties.<p>
 * 
 * Example:
 * <pre>
 * &nbsp;@Password
 * &nbsp;@Column(length=40)
 * &nbsp;private String password;
 * </pre>
 * 
 * It's synonymous of @Stereotype("PASSWORD").
 *
 * @since 6.6
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Password {
	
}
