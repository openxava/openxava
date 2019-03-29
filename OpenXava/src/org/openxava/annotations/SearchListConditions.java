package org.openxava.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A group of <code>@{@link SearchListCondition}</code> associated to the same reference or collection. <p>
 * 
 * Applies to references and @OneToMany/@ManyToMany collections. <p>
 * 
 * It allows to define a value different for <code>@{@link SearchListCondition}</code> 
 * in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@SearchListConditions({
 * &nbsp;&nbsp;&nbsp;@SearchListCondition(forViews="DEFAULT", value= ... ),
 * &nbsp;&nbsp;&nbsp;@SearchListCondition(forViews="Simple, VerySimple", value= ... ),
 * &nbsp;&nbsp;&nbsp;@SearchListCondition(forViews="Complete", value= ... )
 * &nbsp;})
 * </pre>
 * 
 * Since 6.1 @SearchListCondition is repeatable, so you don't need to use @SearchListConditions any more.
 *
 * @author Federico Alcantara
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface SearchListConditions {

	SearchListCondition[] value();

}
