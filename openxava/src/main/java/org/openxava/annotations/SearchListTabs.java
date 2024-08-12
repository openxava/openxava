package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link SearchListTab}</code> associated to the same reference or collection. <p>
 * 
 * Applies to references and collections.<p>
 * 
 * It allows to define a value different for <code>@{@link SearchListTab}</code> 
 * in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@SearchListTabs({
 * &nbsp;&nbsp;&nbsp;@SearchListTab(forViews="DEFAULT", value= ... ),
 * &nbsp;&nbsp;&nbsp;@SearchListTab(forViews="Simple, VerySimple", value= ... ),
 * &nbsp;&nbsp;&nbsp;@SearchListTab(forViews="Complete", value= ... )
 * &nbsp;})
 * </pre>
 * 
 * @author Chungyen Tsai
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface SearchListTabs {
	
	SearchListTab [] value();
	
}
