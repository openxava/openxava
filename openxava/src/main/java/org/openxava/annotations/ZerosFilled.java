package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Used to fill the field with zeros, the amount of zero is what remains to complete the field (Leading zero). <p>	
 *
 * Applies to properties.<p>
 * 
 * Example:
 * <pre>
 * &nbsp;@ZerosFilled
 * &nbsp;@Column(length = 4)
 * &nbsp;private int number;
 * </pre>
 * 
 * It's synonymous of @Stereotype("ZEROS_FILLED") and @Stereotype("RELLENADO_CON_CEROS").
 * 
 * @since 7.1
 * @author Chungyen Tsai
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface ZerosFilled {
	
}
