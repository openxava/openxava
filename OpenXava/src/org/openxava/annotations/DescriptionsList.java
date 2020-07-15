package org.openxava.annotations;

import java.lang.annotation.*;

import org.openxava.filters.*;

/**
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

@Repeatable(DescriptionsLists.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD }) 
public @interface DescriptionsList {
	
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
	 * The property (or properties) to show in the list. <p>
	 *  
	 * If not specified, the property named description, descripcion, 
	 * name or nombre is assumed. If the referenced object does not have a 
	 * property called this way then it is required to specify a property name
     * here.<br>
     * It's possible to indicate several properties separated by commas. 
	 */
	String descriptionProperties() default "";
	
	/**
	 * Indicates that the list content depends on another value displayed. <p>
	 * 
	 * It's used in together with condition. It can be achieve that the list 
	 * content depends on another value displayed in the main view (if you simply 
	 * type the name of the member) or in the same view (if you type this. before 
	 * the name of the member).
	 */
	String depends() default "";
	
	/**
	 * Allows to specify a condition (with SQL style) to filter the values that are
     * shown in the description list.
	 */
	String condition() default "";
	
	/**
	 * Allows to define the logic to fill the values of the parameters used in the 
	 * condition (the ?). It must implement IFilter and you can use here the same 
	 * filters used for @Tab.
	 * 
	 * @since 6.4
	 */
	Class filter() default VoidFilter.class; 
	
	/**
	 * By default the data is ordered by description, but if you set this
     * property to true it will be ordered by key.
	 */
	boolean orderByKey() default false;
	
	/**
	 * Allows to specify an order (with SQL style) for the values that are shown in
     * the description list.
	 */
	String order() default "";
	
	/**
	 * Shows the descriptions list combo and a detail view of the reference at the same time.
	 * 
	 * The reference view is read only and its value changed when the combo is changed by the user. 
	 * The view used is the one specified in @ReferenceView. 
	 * 
	 * @since 5.5
	 */
	boolean showReferenceView() default false;

	/**
	 * List of comma separated tab names where this annotation applies.
	 */		
	String forTabs() default "";
	

	/**
	 * List of comma separated tab names where this annotation applies.
	 */	
	String notForTabs() default "";
}
