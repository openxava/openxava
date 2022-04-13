package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link AddAction}</code> associated to the same collection. <p>
 * 
 * Applies to @OneToMany/@ManyToMany collections.<p>
 * 
 * It allows to define a value different for <code>@{@link AddAction}</code> in 
 * each view.<br>
 * Example:
 * <pre>
 * &nbsp;@AddActions({
 * &nbsp;&nbsp;&nbsp;@AddAction(forViews="DEFAULT", value= ... ),
 * &nbsp;&nbsp;&nbsp;@AddAction(forViews="Simple, VerySimple", value= ... ),
 * &nbsp;&nbsp;&nbsp;@AddAction(forViews="Complete", value= ... )
 * &nbsp;})
 * </pre>
 *
 * Since 6.1 @AddAction is repeatable, so you don't need to use @AddActions any more.
 *
 * @since 5.7
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface AddActions {
	
	AddAction [] value();
	
}
