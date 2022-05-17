package org.openxava.annotations;

import java.lang.annotation.*;


/**
 * A group of <code>@{@link LabelFormat}</code> associated to the same member. <p>
 * 
 * Applies to properties and references with descriptions list.<p>
 * 
 * It allows to define a value different for <code>@{@link LabelFormat}</code> in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@LabelFormats({
 * &nbsp;&nbsp;&nbsp;@LabelFormat(forViews="DEFAULT", value= ... ),
 * &nbsp;&nbsp;&nbsp;@LabelFormat(forViews="Simple, VerySimple", value= ... ),
 * &nbsp;&nbsp;&nbsp;@LabelFormat(forViews="Complete", value= ... )
 * &nbsp;})
 * </pre>
 * 
 * Since 6.1 @LabelFormat is repeatable, so you don't need to use @LabelFormats any more.
 *
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface LabelFormats {
	
	LabelFormat [] value();
	
}
