package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Define format for the string in input.<p>	
 *
 * Applies to String.<p>
 * 
 * Example:
 * <pre>
 * &nbsp;@Mask("0000 0000 0000 0000")
 * &nbsp;private String creditCardNumber;
 * </pre>
 * 
 * @author Chungyen Tsai
 */


@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Mask {

	String value() default "";
	
}
