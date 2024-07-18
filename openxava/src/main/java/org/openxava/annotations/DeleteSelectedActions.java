package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link DeleteSelectedAction}</code> associated to the same 
 * collection. <p>
 * 
 * Applies to @OneToMany/@ManyToMany collections.<p>
 * 
 * It allows to define a value different for @DeleteSelectedAction in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@DeleteSelectedActions({
 * &nbsp;&nbsp;&nbsp;@DeleteSelectedAction(forViews="DEFAULT", value= ... ),
 * &nbsp;&nbsp;&nbsp;@DeleteSelectedAction(forViews="Simple, VerySimple", value= ... ),
 * &nbsp;&nbsp;&nbsp;@DeleteSelectedAction(forViews="Complete", value= ... )
 * &nbsp;})
 * </pre>
 * 
 * @author Chungyen Tsai
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface DeleteSelectedActions {
	
	DeleteSelectedAction [] value();
	
}
