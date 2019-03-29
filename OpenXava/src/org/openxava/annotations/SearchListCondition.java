package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Defines a condition to be used when showing list of selectable items
 * for adding elements to a collection or assigning value to a reference. <p>
 * 
 * This one differs from @Condition that it does not affect
 * the collection list, it only affects the list of selectable items. <p>
 * 
 * Applies to references and @OneToMany/@ManyToMany collections. <p>
 * 
 * Example:
 * <pre>
 * &nbsp;@ManyToOne
 * &nbsp;@SearchListCondition(value="${id} &lt; 'C'")
 * &nbsp;private SellerLevel level;
 * 
 * &nbsp;@OneToMany(mappedBy="seller")
 * &nbsp;@SearchListCondition(value="${number} &lt; 5")
 * &nbsp;private Collection<Customer> customers;
 * </pre>
 *
 * @author Federico Alcantara
 */

@Repeatable(SearchListConditions.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface SearchListCondition {
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
	 * Condition value 
	 * @return
	 */
	String value();
}
