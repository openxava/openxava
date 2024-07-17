package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * tmr 
 * A group of <code>@{@link LargeDisplay}</code> associated to the same member. <p>
 * 
 * Applies to properties.<p>
 * 
 * It allows to define a value different for <code>@{@link LargeDisplay}</code> in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@LargeDisplays({
 * &nbsp;&nbsp;&nbsp;@LargeDisplay(forViews="DEFAULT", ... ),
 * &nbsp;&nbsp;&nbsp;@LargeDisplay(forViews="Simple, VerySimple", ... ),
 * &nbsp;&nbsp;&nbsp;@LargeDisplay(forViews="Complete", ... )
 * &nbsp;})
 * </pre>
 * 
 * @LargeDisplays is repeatable, so you don't need to use @LargeDisplays explicitly, 
 * just put several @LargeDisplay annotating the same property.
 *  
 * @version 7.4 
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface LargeDisplays {
	
	LargeDisplay [] value();
	
}
