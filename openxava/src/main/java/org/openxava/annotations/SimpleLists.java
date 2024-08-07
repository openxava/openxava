package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link SimpleList}</code> associated to the same member. <p>
 * 
 * Applies to collections.<p>
 * 
 * It allows to define a different value for <code>@{@link SimpleList}</code> in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@SimpleLists({
 * &nbsp;&nbsp;&nbsp;@SimpleList(forViews="DEFAULT"),
 * &nbsp;&nbsp;&nbsp;@SimpleList(forViews="Simple, VerySimple"),
 * &nbsp;&nbsp;&nbsp;@SimpleList(forViews="Complete")
 * &nbsp;})
 * </pre>
 * 
 * @SimpleLists is repeatable, so you don't need to use @SimpleLists explicitly, 
 * just put several @SimpleList annotating the same property.
 *  
 * @version 7.4 
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface SimpleLists {
	
	SimpleList [] value();
	
}
