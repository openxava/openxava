package org.openxava.annotations;

import java.lang.annotation.*;

import javax.validation.*;

import org.openxava.validators.hibernate.*;

/**
 * The validator execute validation logic on the value assigned to the property 
 * just before storing. <p>
 * 
 * Applies to properties. <p>
 * 
 * Example:
 * <pre>
 * &nbsp;@PropertyValidator(UnitPriceValidator.class)
 * &nbsp;private BigDecimal unitPrice;
 * </pre>
 * 
 * <code>PropertyValidator</code> is also implemented as a Hibernate validation, 
 * therefore it's executed when you save using JPA or Hibernate API, with the 
 * exception of onlyOnCreate=true, in this last case the validation is only applied when
 * you save with OpenXava (using {@link org.openxava.model.MapFacade} or standard
 * OX actions).<br>
 * 
 * @author Javier Paniza
 */

@Repeatable(PropertyValidators.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Constraint(validatedBy = PropertyValidatorValidator.class) 
public @interface PropertyValidator {
	
	/**
	 * Class with the validation logic. <p>
	 * 
	 * Must implements {@link org.openxava.validators.IPropertyValidator}.
	 */
	Class value();
	
	/**
	 * To set values to the validator properties before executing it.
	 */	
	PropertyValue [] properties() default {};
	
	/**
     * If true the validator is executed only when creating a new object,
     * not when an existing object is modified. 
	 */	
	boolean onlyOnCreate() default false; 
	
	/**
	 * The message to be shown if validation fails. <p>
	 * 
	 * The validator class have to implement {@link org.openxava.validators.IWithMessage} 
	 * to receive this message. <br/>
	 * If not specified the message is not injected in the validator, in this case the validator
	 * should produce an appropriate default message.<br>
	 * You can specify an id of the messages i18n file using braces:
	 * <pre>
	 * message="{color_not_available}"
	 * </pre> 
	 * Or directly the message:
	 * <pre>
	 * message="That color is not available. This car can be only red or yellow"
	 * </pre>
	 */
	String message() default ""; 
	
	Class<?>[] groups() default {}; 
	Class<? extends Payload>[] payload() default {}; 

	
}
