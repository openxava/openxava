package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link DisplaySize}</code> associated to the same property. <p>
 * 
 * Applies to properties.<p>
 * 
 * It allows to define a value different for <code>@{@link DisplaySize}</code> in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@DisplaySizes({
 * &nbsp;&nbsp;&nbsp;@DisplaySize(forViews="DEFAULT", value= ... ),
 * &nbsp;&nbsp;&nbsp;@DisplaySize(forViews="Simple, VerySimple", value= ... ),
 * &nbsp;&nbsp;&nbsp;@DisplaySize(forViews="Complete", value= ... )
 * &nbsp;})
 * </pre>
 *
 * Since 6.1 @DisplaySize is repeatable, so you don't need to use @DisplaySizes any more.
 *
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface DisplaySizes {
	
	DisplaySize [] value();
	
}
