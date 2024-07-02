package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * To display a chart using the values from a collection. <p>
 * 
 * Applies to collections. <p>
 *
 * Example:
 * <pre>
 * &nbsp;@Chart
 * &nbsp;Collection<CorporationEmployee> employees;  
 * </pre>	
 * It could display a chart with several bars, one for each employee, for example.<br>
 * In this case OpenXava tries to determine automatically whose properties identifies each entity, 
 * to use them as labels, and also try to choose numeric values from entities susceptible to be
 * shown in the chart.<br>
 * However, you can determine explicitly what properties use, like in this example:
 * <pre>
 * &nbsp;@Chart(labelProperties = "firstName, lastName", dataProperties = "salary, bonus")
 * &nbsp;Collection<CorporationEmployee> employees; * 
 * </pre>
 * In this case the concatenation of firstName and lastName is used as label, and the salary and
 * bonus properties as data.
 *
 * @since 7.4
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
	 * The property (or properties) to use to get the data of each entity to display 
	 * in the chart. <p>
	 *  
	 * If not specified, all numeric properties except those with year, number, code 
	 * or id in the name, are chosen.<br>
     * It's possible to indicate several properties separated by commas. 
	 */
	String dataProperties() default "";
	
	/**
	 * The property (or properties) to use as label to identify each entity shown in the chart. <p>
	 *  
	 * If not specified, if year/number(or id or code) combination exists is preferred. Otherwise, 
	 * the property named title, name or description is use, with year, number, code or id as prefix 
	 * if exists<br>
	 * As fallback the first property is used.<br>
     * It's possible to indicate several properties separated by commas. 
	 */
	String labelProperties() default "";	
	
}
