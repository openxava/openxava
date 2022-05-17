package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link EntityValidator}</code> associated to the same entity. <p>
 * 
 * Applies to entities.<p>
 * 
 * Example:
 * <pre>
 * &nbsp;@Entity
 * &nbsp;@EntityValidators({
 * &nbsp;&nbsp;&nbsp;@EntityValidator(value=org.openxava.test.validators.CheapProductValidator.class, properties= {
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;@PropertyValue(name="limit", value="100"),
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;@PropertyValue(name="description"),
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;@PropertyValue(name="unitPrice")
 * &nbsp;&nbsp;&nbsp;}),
 * &nbsp;&nbsp;&nbsp;@EntityValidator(value=org.openxava.test.validators.ExpensiveProductValidator.class, properties= {
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;@PropertyValue(name="limit", value="1000"),
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;@PropertyValue(name="description"),
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;@PropertyValue(name="unitPrice")
 * &nbsp;&nbsp;&nbsp;}),
 * &nbsp;&nbsp;&nbsp;@EntityValidator(value=org.openxava.test.validators.ForbiddenPriceValidator.class, 
 * &nbsp;&nbsp;&nbsp;&nbsp;properties= {
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;@PropertyValue(name="forbiddenPrice", value="555"),
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;@PropertyValue(name="unitPrice")
 * &nbsp;&nbsp;&nbsp;&nbsp;},
 * &nbsp;&nbsp;&nbsp;&nbsp;onlyOnCreate=true
 * &nbsp;&nbsp;&nbsp;)	
 * &nbsp;})
 * &nbsp;public class Product {
 * &nbsp;...
 * </pre>
 * 
 * Since 6.1 @EntityValidator is repeatable, so you don't need to use @EntityValidators any more.
 *
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface EntityValidators {
	
	EntityValidator [] value();
	
}
