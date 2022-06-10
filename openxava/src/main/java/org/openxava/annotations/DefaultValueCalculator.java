package org.openxava.annotations;

import java.lang.annotation.*;

import org.openxava.calculators.*;

/**
 * For calculating its initial value. <p>
 * 
 * Applies to properties and references.<p>
 * 
 * With <code>@DefaultValueCalculator</code> you can associate logic to a 
 * property or reference. 
 * This calculator is for calculating its initial value.<br>
 * For example:
 * <pre>
 * &nbsp;@DefaultValueCalculator(CurrentYearCalculator.class)
 * &nbsp;private int year;
 * </pre>
 * In this case when the user tries to create a new Invoice (for example) he 
 * will find that the year field already has a value, that he can change 
 * if he wants to do.
 * 
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface DefaultValueCalculator {
	
	/**
	 * Class with the logic for calculating the initial value, must
	 * implements ICalculator.
	 */
	Class value();
	
	/**
	 * Inject values to the properties of the calculator before
	 * calculating.
	 */
	PropertyValue [] properties() default {};
	
}
