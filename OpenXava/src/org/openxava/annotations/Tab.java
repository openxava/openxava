package org.openxava.annotations;

import java.lang.annotation.*;
import org.openxava.filters.*;

/**
 * Define the behavior for <b>tab</b>ular data presentation (List mode). <p>
 * 
 * Applies to entities. <p> 
 * 
 * Example:
 * <pre>
 * &nbsp;@Tab(name="ActiveYear",
 * &nbsp;&nbsp;&nbsp;filter=ActiveYearFilter.class,		
 * &nbsp;&nbsp;&nbsp;properties="year, number, customer.number, customer.name, amountsSum, vat, detailsCount, paid, importance",
 * &nbsp;&nbsp;&nbsp;baseCondition="${year} = ?"
 * &nbsp;)
 * </pre>
 * 
 * @author Javier Paniza
 */

@Repeatable(Tabs.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Tab {
	
	/**
	 * You can define several tabs in a entity, and set a name for each one. <p>
	 *  
	 * This name is used to indicate the tab that you want to use (usually in 
	 * application.xml).
	 */
	String name() default "";
	
	/**
	 * A simple way to specify a different visual style for some rows. <p>
	 * 
	 * Normally to emphasize rows that fulfill certain condition.
	 */
	RowStyle [] rowStyles() default {};
	
	/**
	 * The list of properties to show initially. <p>
	 * 
	 * Can be qualified (that is you can specify referenceName.propertyName 
	 * at any depth level).
	 */
	String properties() default "";
	
	/**
	 * Allows to define programmatically some logic to apply to the values
	 * entered by user when he filters the list data.
	 */
	Class filter() default VoidFilter.class;
	
	/**
	 * Condition to be fulfilled by the displayed data. <p>
	 * 
	 * It's added to the user condition if needed. 
	 */
	String baseCondition() default "";
	
	/**
	 * To specify the initial order for data.
	 */
	String defaultOrder() default "";
	
	/**
	 * Editor from default-editors.xml or editors.xml used for default list format. <p>
	 * 
	 * It replace the "List" editor but does not remove the other ones.
	 */
	String editor() default "";
	
	/**
	 * Editors from default-editors.xml or editors.xml used for all the list format. <p>
	 * 
	 * It replace all editors.
	 */	
	String editors() default ""; 
	
}
