package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Name of the tab used to display when searching on a reference or collection <p>
 * 
 * Applies to references and collections. <p>
 * 
 * If you omit this annotation, then the default view of the referenced object 
 * is used. With this attribute you can indicate that it uses another view.<br>
 * Example:
 * <pre>
 * &nbsp;@OneToMany(mappedBy="seller")
 * &nbsp;@SearchListTab(forViews="SearchListCondition", value="Demo")
 * &nbsp;private Collection<Customer> customers;
 * 
 * 
 * &nbsp;@ManyToOne(optional=false) 
 * &nbsp;@SearchListTab(forViews="Fellows", value="OnlyName")
 * &nbsp;private Warehouse warehouse;
 * </pre>
 * 
 * @author Chungyen Tsai
 */

@Repeatable(SearchListTabs.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface SearchListTab {
	
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
	 * Name of a view of present in the referenced object. 
	 */
	String value();
	
}
