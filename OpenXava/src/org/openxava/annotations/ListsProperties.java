package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link ListProperties}</code> associated to the same collection. <p>
 * 
 * Applies to collections.<p>
 * 
 * It allows to define a value different for <code>@{@link ListProperties}</code> in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@ListsProperties({
 * &nbsp;&nbsp;&nbsp;@ListProperties(forViews="DEFAULT", value= ... ),
 * &nbsp;&nbsp;&nbsp;@ListProperties(forViews="Simple, VerySimple", value= ... ),
 * &nbsp;&nbsp;&nbsp;@ListProperties(forViews="Complete", value= ... )
 * &nbsp;})
 * </pre>
 * 
 * Since 6.1 @ListProperties is repeatable, so you don't need to use @ListsProperties any more.
 * 
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface ListsProperties {
	
	ListProperties [] value();
	
}
