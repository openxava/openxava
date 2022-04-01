package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * In collections to add an action in each row, but not in the collection button bar. <p>
 * 
 * Applies to @OneToMany/@ManyToMany collections.<p>
 * 
 * Example:
 * <pre>
 * &nbsp;@OneToMany(mappedBy="parent", cascade=CascadeType.ALL)		
 * &nbsp;@RowAction("OrderDetail.reduceQuantity") 
 * &nbsp;private Collection<OrderDetail> details;  
 * </pre>
 * 
 * @author Oscar Kozto
 */

@Repeatable(RowActions.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface RowAction {
	
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