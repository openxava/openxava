package org.openxava.annotations;

import java.lang.annotation.*;


/**
 * A group of <code>@{@link RowStyle}</code> associated to the same collection. <p>
 * 
 * It allows to define a value different for <code>@{@link RowStyle}</code> in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@RowStyles({
 * &nbsp;&nbsp;&nbsp;@RowStyle(forViews="Specials", style="highlight", property="type", value="special"),
 * &nbsp;&nbsp;&nbsp;@RowStyle(forViews="Complete", style="highlight", property="type", value="steady") 	
 * &nbsp;})
 * </pre>
 * It does not work for @ElementCollection.<br>
 * <br>
 * Since 6.1 @RowStyle is repeatable, so you don't need to use @RowStyles any more.
 *
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface RowStyles {
	
	RowStyle [] value();
	
}
