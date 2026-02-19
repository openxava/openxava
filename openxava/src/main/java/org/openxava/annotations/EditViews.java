package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link EditView}</code> associated to the same reference. <p>
 * 
 * Applies to references.<p>
 * 
 * It allows to define a value different for <code>@{@link EditView}</code> 
 * in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@EditViews({
 * &nbsp;&nbsp;&nbsp;@EditView(forViews="DEFAULT", value= ... ),
 * &nbsp;&nbsp;&nbsp;@EditView(forViews="Simple, VerySimple", value= ... ),
 * &nbsp;&nbsp;&nbsp;@EditView(forViews="Complete", value= ... )
 * &nbsp;})
 * </pre>
 * 
 * @EditView is repeatable, so you don't need to use @EditViews explicitly.
 *
 * @author Javier Paniza
 * @since 7.7
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface EditViews {
	
	EditView [] value();
	
}
