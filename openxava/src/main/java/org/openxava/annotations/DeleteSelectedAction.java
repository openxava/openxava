package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Allows to define your a custom action to delete the selected elements 
 * from the collection and data base. <p>
 *  
 * Applies to @OneToMany/@ManyToMany collections. <p>
 * 
 * This is the action executed when a
 * user select some rows and then click in 'Delete selected' link.<br>
 * Example:
 * <pre>
 * &nbsp;@OneToMany (mappedBy="delivery")
 * &nbsp;@DeleteSelectedAction("DeliveryDetails.deleteSelected")
 * &nbsp;private Collection<DeliveryDetail> details;	
 * </pre>
 * @author Chungyen, Tsai
 *
 */
@Repeatable(DeleteSelectedActions.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface DeleteSelectedAction {
	
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