package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Available in collections, when using this annotation, 
 * the actions from the controllers DefaultListActionsForCollections 
 * and DefaultRowActionsForCollections will not be displayed. <p>
 * 
 * Example:
 * <pre>
 * &nbsp;@OneToMany
 * &nbsp;@NoDefaultActions
 * &nbsp;private Collection<Carrier> carriers;
 * </pre>
 * 
 * @since 7.4
 * @author Chungyen Tsai
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface NoDefaultActions {
	
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
	
}
