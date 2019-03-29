package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * The <code>@RemoveValidator</code> is a level model validator, it is executed 
 * just before removing an object, and it has the possibility to deny the deletion. <p>
 * 
 * Applies to entities. <p>
 * 
 * Example:
 * <pre>
 * &nbsp;@RemoveValidator(value=DeliveryTypeRemoveValidator.class,
 * &nbsp;&nbsp;&nbsp;properties=@PropertyValue(name="number")
 * &nbsp;)
 * &nbsp;public class DeliveryType {
 * &nbsp;...
 * </pre>
 * 
 * RemoveValidator has no effect if you remove the entity directly using
 * JPA or Hibernate. It only works when you use {@link org.openxava.model.MapFacade}
 * or delete from a standar OX action). If you can write a contraint for removing
 * that works with JPA and Hibernate use a <code>@PreRemove</code> JPA method.
 * 
 * @author Javier Paniza
 */

@Repeatable(RemoveValidators.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface RemoveValidator {
	
	/**
	 * Class that implements the validation logic. <p>
	 * 
 	 * Must implement IRemoveValidator.
	 */
	Class value();
	
	/**
	 * To set the value of the validator properties before executing it.
	 */
	PropertyValue [] properties() default {};
	
}
