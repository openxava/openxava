package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Allows you to specify your own action for searching. <p>
 * 
 * Applies to references. <p>
 * 
 * Example:
 * <pre>
 * &nbsp;@ManyToOne
 * &nbsp;@SearchAction("MyReference.search") 
 * &nbsp;private Seller seller;
 * </pre>
 * 
 * @author Javier Paniza
 */

@Repeatable(SearchActions.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface SearchAction {
	
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
