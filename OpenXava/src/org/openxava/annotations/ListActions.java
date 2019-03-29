package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link ListAction}</code> associated to the same collection. <p>
 * 
 * Applies to @OneToMany/@ManyToMany collections.<p>
 * 
 * It allows to define a value different for <code>@{@link ListAction}</code> in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@ListActions({
 * &nbsp;&nbsp;&nbsp;@ListAction(forViews="DEFAULT", value= ... ),
 * &nbsp;&nbsp;&nbsp;@ListAction(forViews="Simple, VerySimple", value= ... ),
 * &nbsp;&nbsp;&nbsp;@ListAction(forViews="Complete", value= ... )
 * &nbsp;})
 * </pre>
 * 
 * Since 6.1 @ListAction is repeatable, so you don't need to use @ListActions any more.
 *
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface ListActions {
	
	ListAction [] value();
	
}
