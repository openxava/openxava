package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * BigDecimal used to represent money. <p>
 * 
 * The data type is BigDecimal with 15 for size and 2 for scale.
 * 
 * Applies to properties.<p>
 * 
 * Example:
 * <pre>
 * &nbsp;@Money
 * &nbsp;private BigDecimal amount;
 * </pre>
 * 
 * It's synonymous of @Stereotype("MONEY").
 *
 * @since 6.6
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Money {
	
}
