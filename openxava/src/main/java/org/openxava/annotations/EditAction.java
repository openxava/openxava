package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Allows you to define your custom action to view/edit a collection element or reference. <p> 
 * 
 * Applies to @OneToMany/@ManyToMany collections and @ManyToOne/@OneToOne references.<br>
 * Support for references since v7.7.<p>
 * 
 * For collections, this is the action showed in each row, if the collection is read only.<br>
 * For references, this is the action executed on click in the 'Edit' link.<br>
 * Example for collection:
 * <pre>
 * &nbsp;@EditAction("Invoice.editDetail")
 * &nbsp;private Collection<InvoiceDetail> details;
 * </pre>
 * Example for reference:
 * <pre>
 * &nbsp;@EditAction("Invoice.editCustomer")
 * &nbsp;@ManyToOne
 * &nbsp;private Customer customer;
 * </pre>
 * 
 * @author Javier Paniza
 */

@Repeatable(EditActions.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface EditAction {

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
