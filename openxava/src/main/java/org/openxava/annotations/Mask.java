package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Define format for the string in input.<p>	
 *
 * Applies to String.<p>
 * 
 * <ul>
 *     <li>'L': the user must enter an alphabetical letter from A ~ z.</li>
 *     <li>'0': the user must enter a digit.</li>
 *     <li>'A': the user must enter an alphanumeric character.</li>
 *     <li>'#': the user must enter a digit, blank space, '+' or '-'.</li>
 * </ul>
 * 
 * Also, can define the mask with special characters. <p>
 * 
 * Example:
 * <pre>
 * &nbsp;@Mask("LL 000 AA")
 * &nbsp;private String carPlate;
 * </pre>
 * 
 * @author Chungyen Tsai
 */


@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Mask {

	String value() default "";
	
}
