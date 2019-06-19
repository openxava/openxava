package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * The member never will be editable by the final user in the indicated views. <br>
 *
 * Applies to properties, references and collection.<p>
 * 
 * An alternative to this is to make the property editable or not editable
 * programmatically using {@link org.openxava.view.View}.<br>
 * Example:
 * <pre>
 * &nbsp;@ReadOnly
 * &nbsp;private String name;
 * </pre>
 * 
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface ReadOnly {
		
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
	 * By default true. If you put false when you are creating the record this property will be editable.
	 * 
	 * @since 6.2
	 */
	boolean onCreate() default true;
	
}
