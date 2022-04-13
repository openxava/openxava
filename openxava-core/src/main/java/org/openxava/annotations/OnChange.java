package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Action to execute when the value of this property/reference changes. <p>
 * 
 * Applies to properties and references. <p>
 * 
 * Example:
 * <pre>
 * &nbsp;@OnChange(OnChangeCustomerNameAction.class)
 * &nbsp;private String name;
 * </pre>
 * 
 * @author Javier Paniza
 */

@Repeatable(OnChanges.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface OnChange {
	
	/**
	 * List of comma separated view names where this annotation applies. <p>
	 * 
	 * Exclusive with notForViews.<br>
	 * If both forViews and notForViews are omitted then this annotation
	 * apply to all views.<br>
	 * You can use the string "DEFAULT" for referencing to the default
	 * view (the view with no name).
	 */				
	String forViews() default "";
	
	/**
	 * List of comma separated view names where this annotation does not apply. <p>
	 * 
	 * Exclusive with forViews.<br>
	 * If both forViews and notForViews are omitted then this annotation
	 * apply to all views.<br>
	 * You can use the string "DEFAULT" for referencing to the default
	 * view (the view with no name).
	 */ 				
	String notForViews() default "";
	
	/**
	 * The action to execute. <p>
	 * 
	 * Must to implement {@link org.openxava.actions.IOnChangePropertyAction}, or
	 * extends {@link org.openxava.actions.OnChangePropertyBaseAction}.
	 */
	Class value();
	
}
