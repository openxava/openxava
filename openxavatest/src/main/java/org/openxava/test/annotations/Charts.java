package org.openxava.test.annotations;

import java.lang.annotation.*;

import org.openxava.annotations.*;

/**
 * tmr Rehacer doc. Mover a openxava
 * A group of <code>@{@link DescriptionsList}</code> associated to the same member. <p>
 * 
 * Applies to references.<p>
 * 
 * It allows to define a value different for <code>@{@link DescriptionsList}</code> in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@DescriptionsLists({
 * &nbsp;&nbsp;&nbsp;@DescriptionsList(forViews="DEFAULT", ... ),
 * &nbsp;&nbsp;&nbsp;@DescriptionsList(forViews="Simple, VerySimple", ... ),
 * &nbsp;&nbsp;&nbsp;@DescriptionsList(forViews="Complete", ... )
 * &nbsp;})
 * </pre>
 * 
 * Since 6.1 @DescriptionsLists is repeatable, so you don't need to use @DescriptionsList any more.
 *  
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Charts {
	
	Chart [] value();
	
}
