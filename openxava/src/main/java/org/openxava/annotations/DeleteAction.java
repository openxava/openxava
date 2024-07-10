package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * 
 * @author Chungyen, Tsai
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface DeleteAction {

	String forViews() default "";
	
	String notForViews() default "";
	
	String value();
	
}
