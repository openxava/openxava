package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A time stored as an String. <p>
 * 
 * The time is in the format HH:mm, like "13:46" for example.
 * 
 * The data type is String with a length of 5.
 * 
 * Applies to properties.<p>
 * 
 * Example:
 * <pre>
 * &nbsp;@StringTime
 * &nbsp;@Column(length=5)
 * &nbsp;private String startTime;
 * </pre>
 * 
 * It's synonymous of @Stereotype("TIME").
 *
 * @since 6.6
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface StringTime {
	
}
