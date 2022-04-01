package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * The member will be shown collapsed for the indicated views. 
 * Visually this means that the frame surrounding the member 
 * view will be initially closed. Later the user will be able 
 * to set his preferences clicking on the expansion icon.<br>
 *
 * Applies to references and collections.<br>
 * 
 * Example:
 * <pre>
 * &nbsp;@Collapsed
 * &nbsp;private Report longReport;
 * 
 * &nbsp;@Collapsed
 * &nbsp;private Collection&lt;ReportLine&gt; longReport;
 * </pre>
 * 
 * @author Paco Valsera
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Collapsed {
		
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