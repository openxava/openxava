package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * tmr Redoc
 * To display the value of the property in large format. <p>
 * 
 * Generally with a large font, inside a small frame with spacing, etc. 
 * To make the value clearly visible, for emphasizing the value in the view, or
 * for creating a dashboard style view.<br>
 * 
 * Applies to properties. <p>
 *
 * Example:
 * <pre>
 * &nbsp;@LargeDisplay
 * &nbsp;int activeUsersCount;
  * </pre>	
 * It will display the active user count with a large number in the user interface.<br>
 * Moreover, you can specify a prefix/suffix and icon, like this:
 * <pre>
 * &nbsp;@LargeDisplay(suffix = "%", icon="label-percent-outline")
 * &nbsp;BigDecimal vatPercentage;
 * </pre>
 * In this case the suffix % is displayed after the value and the icon label-percent-outline is also
 * displayed near the value, the icon is from Material Design Icons, like the ones used for actions.<br>
 * Also if you combine @LargeDisplay with @Money and don't specify any value for suffix or prefix,
 * the value for suffix/prefix is automatic, so if you write:
 * <pre>
 * &nbsp;@Money @LargeDisplay
 * &nbsp;BigDecimal discount;
 * </pre>
 * Now the discount is displayed with the suffix &euro; if the server is configure for Spain, or
 * with the prefix $ if the server is configured for USA.
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
