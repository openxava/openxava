package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link ViewAction}</code> associated to the same collection. <p>
 * 
 * Applies to @OneToMany/@ManyToMany collections.<p>
 * 
 * It allows to define a value different for <code>@{@link ViewAction}</code> 
 * in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@ViewActions({
 * &nbsp;&nbsp;&nbsp;@ViewAction(forViews="DEFAULT", value= ... ),
 * &nbsp;&nbsp;&nbsp;@ViewAction(forViews="Simple, VerySimple", value= ... ),
 * &nbsp;&nbsp;&nbsp;@ViewAction(forViews="Complete", value= ... )
 * &nbsp;})
 * </pre>
 * 
 * Since 6.1 @ViewAction is repeatable, so you don't need to use @ViewActions any more.
 *
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface ViewActions {
	
	ViewAction [] value();
	
}
