package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * String to store a media access control address (MAC address). <p>
 * 
 * The data type is String with a length of 17.
 * 
 * Applies to properties.<p>
 * 
 * Example:
 * <pre>
 * &nbsp;@MAC
 * &nbsp;@Column(length=17)
 * &nbsp;private String mac;
 * </pre>
 * 
 * It's synonymous of @Stereotype("MAC").
 *
 * @since 6.6
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface MAC {
	
}
