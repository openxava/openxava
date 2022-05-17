package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link Tree}</code> associated to the same collection. <p>
 * 
 * Applies to @OneToMany/@ManyToMany collections.<p>
 * 
 * It allows to define a value different for <code>@{@link Tree}</code> in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@Trees({
 * &nbsp;&nbsp;&nbsp;@Tree(forViews="DEFAULT", value= ... ),
 * &nbsp;&nbsp;&nbsp;@Tree(forViews="Simple, VerySimple", value= ... ),
 * &nbsp;&nbsp;&nbsp;@Tree(forViews="Complete", value= ... )
 * &nbsp;})
 * </pre>
 * 
 * Since 6.1 @Tree is repeatable, so you don't need to use @Trees any more.
 *
 * @author Federico Alcántara
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target({ ElementType.FIELD, ElementType.METHOD}) 
public @interface Trees {
	Tree[] value();
}
