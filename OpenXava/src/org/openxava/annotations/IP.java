package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * String used to store a IP address. <p>
 * 
 * The data type is String with 15 for length.
 * 
 * Applies to properties.<p>
 * 
 * Example:
 * <pre>
 * &nbsp;@IP
 * &nbsp;@Column(length=15)
 * &nbsp;private String ip;
 * </pre>
 * 
 * It's synonymous of @Stereotype("IP").
 *
 * @since 6.6
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface IP {
	
}
