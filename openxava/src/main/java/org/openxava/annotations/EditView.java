package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * View of the referenced object used to modify it from a reference. <p>
 * 
 * Applies to references. <p>
 * 
 * If you omit this annotation, then the default view of the referenced object 
 * is used when modifying the object from the reference. With this annotation 
 * you can indicate that it uses another view.<br>
 * Example:
 * <pre>
 * &nbsp;@ManyToOne 
 * &nbsp;@EditView("SimpleEdition")
 * &nbsp;private Seller seller;
 * </pre>
 * 
 * @author Javier Paniza
 * @since 7.7
 */

@Repeatable(EditViews.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface EditView {
	
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
	 * Name of a view present in the referenced object to use for modification. 
	 */
	String value();
	
}
