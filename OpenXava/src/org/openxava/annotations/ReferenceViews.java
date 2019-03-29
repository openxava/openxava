package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link ReferenceView}</code> associated to the same reference. <p>
 * 
 * Applies to references.<p>
 * 
 * It allows to define a value different for <code>@{@link ReferenceView}</code> 
 * in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@ReferenceViews({
 * &nbsp;&nbsp;&nbsp;@ReferenceView(forViews="DEFAULT", value= ... ),
 * &nbsp;&nbsp;&nbsp;@ReferenceView(forViews="Simple, VerySimple", value= ... ),
 * &nbsp;&nbsp;&nbsp;@ReferenceView(forViews="Complete", value= ... )
 * &nbsp;})
 * </pre>
 * 
 * Since 6.1 @ReferenceView is repeatable, so you don't need to use @ReferenceViews any more.
 *
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface ReferenceViews {
	
	ReferenceView [] value();
	
}
