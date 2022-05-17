package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link OnSelectElementAction}</code> associated to the same collection. <p>
 * 
 * Applies to @OneToMany/@ManyToMany collections.<p>
 * 
 * It allows to define a value different for <code>@{@link OnSelectElementAction}</code> 
 * in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@OnSelectElementActions({
 * &nbsp;&nbsp;&nbsp;@OnSelectElementAction(forViews="DEFAULT", value= ... ),
 * &nbsp;&nbsp;&nbsp;@OnSelectElementAction(forViews="Simple, VerySimple", value= ... ),
 * &nbsp;&nbsp;&nbsp;@OnSelectElementAction(forViews="Complete", value= ... )
 * &nbsp;})
 * </pre>
 * 
 * Since 6.1 @OnSelectElementAction is repeatable, so you don't need to use @OnSelectElementActions any more.
 * 
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface OnSelectElementActions {
	
	OnSelectElementAction [] value();
	
}
