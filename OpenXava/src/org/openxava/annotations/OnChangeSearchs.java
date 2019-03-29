package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link OnChangeSearch}</code> associated to the same member. <p>
 * 
 * Applies to references.<p>
 * 
 * It allows to define a value different for <code>@{@link OnChangeSearch}</code> 
 * in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@OnChangeSearchs({
 * &nbsp;&nbsp;&nbsp;@OnChangeSearch(forViews="DEFAULT", value= ... ),
 * &nbsp;&nbsp;&nbsp;@OnChangeSearch(forViews="Simple, VerySimple", value= ... ),
 * &nbsp;&nbsp;&nbsp;@OnChangeSearch(forViews="Complete", value= ... )
 * &nbsp;})
 * </pre>
 * 
 * Since 6.1 @OnChangeSearch is repeatable, so you don't need to use @OnChangeSearchs any more.
 *
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface OnChangeSearchs {
	
	OnChangeSearch [] value();
	
}
