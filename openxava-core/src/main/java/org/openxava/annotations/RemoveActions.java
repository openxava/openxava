package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link RemoveAction}</code> associated to the same collection. <p>
 * 
 * Applies to @OneToMany/@ManyToMany collections.<p>
 * 
 * It allows to define a value different for <code>@{@link RemoveAction}</code> 
 * in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@RemoveActions({
 * &nbsp;&nbsp;&nbsp;@RemoveAction(forViews="DEFAULT", value= ... ),
 * &nbsp;&nbsp;&nbsp;@RemoveAction(forViews="Simple, VerySimple", value= ... ),
 * &nbsp;&nbsp;&nbsp;@RemoveAction(forViews="Complete", value= ... )
 * &nbsp;})
 * </pre>
 * 
 * Since 6.1 @RemoveAction is repeatable, so you don't need to use @RemoveActions any more.
 * 
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface RemoveActions {
	
	RemoveAction [] value();
	
}
