package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link SaveAction}</code> associated to the same collection. <p>
 * 
 * Applies to @OneToMany/@ManyToMany collections.<p>
 * 
 * It allows to define a value different for <code>@{@link SaveAction}</code> 
 * in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@SaveActions({
 * &nbsp;&nbsp;&nbsp;@SaveAction(forViews="DEFAULT", value= ... ),
 * &nbsp;&nbsp;&nbsp;@SaveAction(forViews="Simple, VerySimple", value= ... ),
 * &nbsp;&nbsp;&nbsp;@SaveAction(forViews="Complete", value= ... )
 * &nbsp;})
 * </pre>
 * 
 * Since 6.1 @SaveAction is repeatable, so you don't need to use @SaveActions any more.
 *
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface SaveActions {
	
	SaveAction [] value();
	
}
