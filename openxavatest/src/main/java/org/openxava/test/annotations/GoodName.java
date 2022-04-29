package org.openxava.test.annotations;

import java.lang.annotation.*;
import javax.validation.*;
import org.openxava.test.validators.constraints.*;

/**
 * An example of Bean Validation constraint.
 * 
 * @author Javier Paniza 
 */

@Target( ElementType.TYPE )
@Retention( RetentionPolicy.RUNTIME )
@Constraint(validatedBy = GoodNameValidator.class)
@Documented
public @interface GoodName {

	String message() default "{pepe_not_good_name}";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}

