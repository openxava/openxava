package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link NewAction}</code> associated to the same collection. <p>
 * 
 * Applies to @OneToMany/@ManyToMany collections.<p>
 * 
 * It allows to define a value different for <code>@{@link NewAction}</code> in 
 * each view.<br>
 * Example:
 * <pre>
 * &nbsp;@NewActions({
 * &nbsp;&nbsp;&nbsp;@NewAction(forViews="DEFAULT", value= ... ),
 * &nbsp;&nbsp;&nbsp;@NewAction(forViews="Simple, VerySimple", value= ... ),
 * &nbsp;&nbsp;&nbsp;@NewAction(forViews="Complete", value= ... )
 * &nbsp;})
 * </pre>
 * 
 * Since 6.1 @NewAction is repeatable, so you don't need to use @NewActions any more.
 *
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface NewActions {
	
	NewAction [] value();
	
}
