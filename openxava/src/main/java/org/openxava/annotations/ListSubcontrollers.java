package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link ListSubcontroller}</code> associated to the same collection. <p>
 * 
 * Applies to @OneToMany/@ManyToMany collections.<p>
 * 
 * It allows to define a value different for <code>@{@link ListSubcontroller}</code> in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@ListSubcontrollers({
 * &nbsp;&nbsp;&nbsp;@ListSubcontroller(forViews="DEFAULT", value= ... ),
 * &nbsp;&nbsp;&nbsp;@ListSubcontroller(forViews="Simple, VerySimple", value= ... ),
 * &nbsp;&nbsp;&nbsp;@ListSubcontroller(forViews="Complete", value= ... )
 * &nbsp;})
 * </pre>
 * 
 * Since 6.1 @ListSubcontroller is repeatable, so you don't need to use @ListSubcontrollers any more.
 *
 * @since 5.7
 * @author Ana Andres
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface ListSubcontrollers {
	
	ListSubcontroller [] value();
	
}
