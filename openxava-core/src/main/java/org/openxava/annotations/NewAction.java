package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Allows you to define your custom action to start
 * creating a new element to a collection. <p>
 * 
 * Applies to @OneToMany/@ManyToMany collections. <p>
 * 
 * This is the action executed on click in 'New' link.<br>
 * Example:
 * <pre>
 * &nbsp;@OneToMany (mappedBy="delivery", cascade=CascadeType.REMOVE)
 * &nbsp;@NewAction("DeliveryDetails.new")
 * &nbsp;private Collection<DeliveryDetail> details;	
 * </pre>
 * 
 * @author Javier Paniza
 */

@Repeatable(NewActions.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface NewAction {

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
	
}
