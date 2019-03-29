package org.openxava.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A group of <code>@{@link LabelStyle}</code> associated to the same member. <p>
 * 
 * Applies to properties and references with descriptions list.<p>
 * 
 * It allows assigning several styles or to define a value different for <code>@{@link LabelStyle}</code> in each view.<br> 
 * Example:
 * <pre>
 * &nbsp;@LabelStyle({
 * &nbsp;&nbsp;&nbsp;@LabelStyle(value="bold-label" ),
 * &nbsp;&nbsp;&nbsp;@LabelStyle(forViews="Complete", value="italic-label")
 * &nbsp;})
 * </pre>
 * You can apply each style in a different view (if you put forViews) or you can apply several styles in the same property (if you don't put forViews).<br>
 * 
 * Since 6.1 @LabelStyle is repeatable, so you don't need to use @LabelStyles any more.
 * 
 * Create on 07/05/2010 (12:37:01)
 * @author Ana Andres
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface LabelStyles {
	
	LabelStyle [] value();

}
