package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * The view of the referenced object (each collection element) which is used to
 * display the detail. <p>
 * 
 * Applies to @OneToMany/@ManyToMany collections.<br> 
 * By default the default view is used.<br>
 * Example:
 * <pre>
 * &nbsp;@OneToMany (mappedBy="invoice")
 * &nbsp;@CollectionView("InInvoice")
 * &nbsp;private Collection<Delivery> deliveries;
 * </pre>
 * 
 * @author Javier Paniza
 */

@Repeatable(CollectionViews.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface CollectionView {
	
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
	 * The name of the view in the referenced object which is used to
	 * display the detail. <p>
	 */ 
	String value();
	
}
