package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Allows to define an action to be executed when an element of the collection is
 * selected or unselected.<p> 
 * 
 * Applies to @OneToMany/@ManyToMany collections. <p>
 * 
 * This action is executed on click on the checkbox of a row of the collection.<br>
 * Example:
 * <pre>
 * &nbsp;@OnSelectElementAction(value="Formula.onSelectIngredient") 
 * &nbsp;@OneToMany(mappedBy="formula", cascade=CascadeType.REMOVE)
 * &nbsp;private Collection&lt;FormulaIngredient&gt; ingredients;		
 * </pre>
 *
 * @autor Ana Andres
 */

@Repeatable(OnSelectElementActions.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface OnSelectElementAction {

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
	 * name and the action name. <p>
	 * 
	 * This action must be registered in controllers.xml.<br>
	 * The implementation class must extend {link org.openxava.actions.OnSelectElementBaseAction}.
	 */	
	String value();
	
}
