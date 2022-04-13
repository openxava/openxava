package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Style to display the label of this property or reference (displayed as descriptions list). <p>
 * 
 * Applies to properties and references displayed as descriptions list. <p>
 * 
 * Example:
 * <pre>
 * &nbsp;@LabelStyle("bold-label")
 * &nbsp;private int zipCode;
 * </pre> 
 * 
 * Create on 07/05/2010 (10:06:57)
 * @author Ana Andres
 */

@Repeatable(LabelStyles.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface LabelStyle {
	
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
	 * The style of the label
	 */
	String value() default "";

}
