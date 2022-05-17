package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * The final user can modify existing elements in the collection, 
 * but not add or remove collection elements. <p>	
 *
 * Applies to collections.<p>
 * 
 * Example:
 * <pre>
 * &nbsp;@OneToMany (mappedBy="invoice", cascade=CascadeType.REMOVE)
 * &nbsp;@EditOnly
 * &nbsp;private Collection<InvoiceDetail> details;
 * </pre>
 * 
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface EditOnly {
		
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
