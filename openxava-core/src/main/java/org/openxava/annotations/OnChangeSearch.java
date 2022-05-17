package org.openxava.annotations;

import java.lang.annotation.*;

/** 
 * Action to execute to do the search of a reference when the user 
 * types the key value. <p>
 * 
 * Applies to references. <p>
 *
 * Example:
 * <pre>
 * &nbsp;@ManyToOne
 * &nbsp;@OnChangeSearch(OnChangeSubfamilySearchAction.class) 	
 * &nbsp;private Subfamily subfamily;
 * </pre>
 * The action receive the value of the key property (or last displayed key 
 * property in class of composite key) that user types.<br>
 * 
 * @author Javier Paniza
 */

@Repeatable(OnChangeSearchs.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface OnChangeSearch {
	
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
