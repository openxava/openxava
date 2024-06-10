package org.openxava.test.annotations;

import java.lang.annotation.*;

/**
 * Custom annotation for collections attached to an editor.
 * 
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface CarriersNames {
	
}
