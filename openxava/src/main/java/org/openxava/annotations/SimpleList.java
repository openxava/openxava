package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * To display a collection as a simple read-only list, without actions, filters, pagination, sorting, etc. <p>
 * 
 * Applies to collections. <p>
 *
 * Example:
 * <pre>
 * &nbsp;@OneToMany(mappedBy="parent")
 * &nbsp;@SimpleList
 * &nbsp;Collection<StaffTurnover> turnoverByYear;
 * </pre>
 * 
 * It will display the collection showing only the header and the data with a simple user interface, with no ornament.<br>
 *
 * @since 7.4
 * @author Javier Paniza
 */

@Repeatable(SimpleLists.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD }) 
public @interface SimpleList {
	
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
