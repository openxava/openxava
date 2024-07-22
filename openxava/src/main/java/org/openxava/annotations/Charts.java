package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link Chart}</code> associated to the same member. <p>
 * 
 * Applies to collections.<p>
 * 
 * It allows to define a value different for <code>@{@link Chart}</code> in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@Charts({
 * &nbsp;&nbsp;&nbsp;@Chart(forViews="DEFAULT", ... ),
 * &nbsp;&nbsp;&nbsp;@Chart(forViews="Simple, VerySimple", ... ),
 * &nbsp;&nbsp;&nbsp;@Chart(forViews="Complete", ... )
 * &nbsp;})
 * </pre>
 * 
 * @Chart is repeatable, so you don't need to use @Charts explicitly, 
 * just put several @Chart annotating the same collection.
 *  
 * @version 7.4 
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Charts {
	
	Chart [] value();
	
}
