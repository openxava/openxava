package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Associates an action to a property or reference in the view. <p>
 * 
 * Applies to properties and references.<p>
 * 
 * Example:
 * <pre>
 * &nbsp;@Action("Deliveries.generateNumber")
 * &nbsp;private int number;
 * </pre>
 * The actions are displayed as a link or an image beside the property.<br>
 * By default the action link is present only when the property is editable, 
 * but if the property is read only or calculated then it is always present.<br>
 * 
 * @author Javier Paniza
 */

@Repeatable(Actions.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Action {
	
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
	 * You have to write the action identifier that is the controller
	 * name and the action name. This action must be registered in controllers.xml
	 */
	String value();
	
	/**
     * You can put the attribute alwaysEnabled to true so that the link is always 
     * present, even if the property is not editable:
	 */
	boolean alwaysEnabled() default false;
}
