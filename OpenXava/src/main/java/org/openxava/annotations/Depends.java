package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Declares that a property depends on other one(s). <p>
 * 
 * Applies to properties.<p>
 * 
 * OpenXava uses this info in order to know when to recalculate
 * values of a property in the user interface.<br>
 * Example:
 * <pre>
 * &nbsp;@Depends("unitPrice")  	
 * &nbsp;public BigDecimal getUnitPriceInPesetas() {
 * &nbsp;&nbsp;&nbsp;if (unitPrice == null) return null;
 * &nbsp;&nbsp;&nbsp;return unitPrice.multiply(new BigDecimal("166.386")).setScale(0, BigDecimal.ROUND_HALF_UP);
 * &nbsp;}
 * </pre>
 * In this case if the unitPrice changes in the user interface, the value of 
 * unitPriceInPesetas is recalculated and redisplayed.
 * 
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Depends {

	/**
	 * Comma separated list of the properties this property depend on.
	 */
	String value();
	
}
