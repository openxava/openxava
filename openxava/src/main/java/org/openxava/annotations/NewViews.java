package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link NewView}</code> associated to the same reference. <p>
 * 
 * Applies to references.<p>
 * 
 * It allows to define a value different for <code>@{@link NewView}</code> 
 * in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@NewViews({
 * &nbsp;&nbsp;&nbsp;@NewView(forViews="DEFAULT", value= ... ),
 * &nbsp;&nbsp;&nbsp;@NewView(forViews="Simple, VerySimple", value= ... ),
 * &nbsp;&nbsp;&nbsp;@NewView(forViews="Complete", value= ... )
 * &nbsp;})
 * </pre>
 * 
 * Since 6.1 @NewView is repeatable, so you don't need to use @NewViews any more.
 *
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface NewViews {
	
	NewView [] value();
	
}
