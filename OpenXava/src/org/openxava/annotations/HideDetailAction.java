package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * In a collection it allows you to define your custom action to hide 
 * the detail view. <p> 
 * 
 * Applies to @OneToMany/@ManyToMany collections.<p>
 * 
 * This is the action executed on click in 'Close' link.
 * Example:
 * <pre>
 * &nbsp;@OneToMany (mappedBy="delivery", cascade=CascadeType.REMOVE)
 * &nbsp;@HideDetailAction("DeliveryDetails.hideDetail")
 * &nbsp;private Collection<DeliveryDetail> details;	
 * </pre>
 * 
 * @author Javier Paniza
 */

@Repeatable(HideDetailActions.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface HideDetailAction {
	
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
