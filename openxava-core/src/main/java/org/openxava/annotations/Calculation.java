package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Expression to do the calculation for a property. <p>
 * 
 * Applies to properties.<p>
 * 
 * The expression can contain +, -, *, (), numeric values and properties names of 
 * the same entity. For example:
 * 
 * <pre>
 * &nbsp;@Calculation("((hours * worker.hourPrice) + tripCost - discount) * vatPercentage / 100") 
 * &nbsp;private BigDecimal total;
 * </pre>
 * 
 * You can note as <code>worker.hourPrice</code> can be used to get value from references.<p>
 * 
 * The calculation is executed and displayed when the user changes any value of the properties 
 * used in the expression in the UI, however the value is not saved until the user clicks on save button.<p> 
 * 
 * @since 5.7
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Calculation {

	/**
	 * Expression with +, -, *, (), numeric values and other properties names of the same entity.
	 */
	String value();
	
}
