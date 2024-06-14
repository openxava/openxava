package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * tmr Rehacer doc. Mover a openxava..
 * With <code>@DescriptionsList</code> you can instruct OpenXava to visualize references 
 * as a descriptions list (actually a combo). <p>
 * 
 * Applies to references. <p>
 *
 * This can be useful, if there are only a few elements and these elements have
 * a significant name or description.
 *
 * Example:
 * <pre>
 * &nbsp;@DescriptionsList
 * &nbsp;@ManyToOne
 * &nbsp;private DrivingLicence drivingLicence;
 * </pre>	
 *
 * @author Javier Paniza
 */

@Repeatable(Charts.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD }) 
public @interface Chart {
	
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
	 * tmr Redoc. 
	 * tmr Valores por defecto
	 * The property (or properties) to show in the list. <p>
	 *  
	 * If not specified, the property named description, descripcion, 
	 * name or nombre is assumed. If the referenced object does not have a 
	 * property called this way then it is required to specify a property name
     * here.<br>
     * It's possible to indicate several properties separated by commas. 
	 */
	String dataProperties() default "";
	
	/**
	 * tmr Redoc. 
	 * tmr Valores por defecto
	 * The property (or properties) to show in the list. <p>
	 *  
	 * If not specified, the property named description, descripcion, 
	 * name or nombre is assumed. If the referenced object does not have a 
	 * property called this way then it is required to specify a property name
     * here.<br>
     * It's possible to indicate several properties separated by commas. 
	 */
	String labelProperties() default "";	
	
	/**
	 * Shows the descriptions list combo and a detail view of the reference at the same time.
	 * 
	 * The reference view is read only and its value changed when the combo is changed by the user. 
	 * The view used is the one specified in @ReferenceView. 
	 * 
	 * @since 5.5
	 */
	boolean showList() default false; // tmr Falta implementar

}
