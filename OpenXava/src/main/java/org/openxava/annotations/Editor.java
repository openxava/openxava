package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Name of the editor to use for displaying the member in this view. <p>
 * 
 * Applies to properties, references and collections. <p> 
 *
 * The editor must be declared in OpenXava/xava/default-editors.xml or 
 * xava/editors.xml of your project.<br>
 * Example:
 * <pre>
 * &nbsp;@Editor("ValidValuesRadioButton")
 * &nbsp;private Type type;
 * &nbsp;public enum Type { NORMAL, STEADY, SPECIAL }; 
 * </pre>
 * 
 * @author Javier Paniza
 */

@Repeatable(Editors.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Editor {
	
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
	 * Name of the editor from OpenXava/xava/default-editors.xml or 
     * xava/editors.xml of your project.
	 */
	String value();
	
}
