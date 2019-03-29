package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Encapsulate the value for inject it in a property. <p>
 * 
 * It's used to fill the values of a validator, calculator, etc
 * before execute it. <p>
 * 
 * Example:
 * <pre>
 * &nbsp;@EntityValidator(validator=CheapProductValidator.class, properties= {
 * &nbsp;&nbsp;&nbsp;@PropertyValue(name="limit", value="100"),
 * &nbsp;&nbsp;&nbsp;@PropertyValue(name="description"),
 * &nbsp;&nbsp;&nbsp;@PropertyValue(name="price", from="unitPrice")
 * &nbsp;})
 * &nbsp;public class Product {
 * &nbsp;...
 * </pre>
 * For example, in this case before executing the validation of CheapProductValidator
 * OpenXava does:
 * <ul>
 * <li> Put "100" in the property "limit" of CheapProductValidator.
 * <li> Moves the value of 'description' of the current product to 'description'
 *      of CheapProductValidator.
 * <li> Moves the value of 'unitPrice' of the current product to 'price'
 *      of CheapProductValidator.
 * </ul>
 * 
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface PropertyValue {
	
	/**
	 * The name of the property to fill. <p>
	 */
	String name();
	
	/**
	 * The property name of the the current object from when we obtain
	 * the data to fill the property of the target calculator, validator, etc.
	 * 
	 * By default, name() is assumed.<br>
	 * 'from' is exclusive with 'value'
	 */
	String from() default "";
	
	/**
	 * Fix value to fill the property of the target calculator, validator, etc. <p>
	 * 
	 * 'value' is exclusive with 'from' 
	 */
	String value() default "";
	
}
