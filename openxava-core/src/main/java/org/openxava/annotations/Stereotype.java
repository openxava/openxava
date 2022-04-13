package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A stereotype is the way to determine a specific behavior of a type. <p>
 * 
 * Applies to properties. <p>
 * 
 * For example, a name, a comment, a description, etc. all correspond to 
 * the Java type java.lang.String but you surely wish validators, default sizes, 
 * visual editors, etc. different in each case and you need to tune finer; 
 * you can do this assigning a stereotype to each case. That is, you can have 
 * the next sterotypes NAME, MEMO or DESCRIPTION and assign them to your 
 * properties.<br>
 * Example:
 * <pre>
 * &nbsp;@Stereotype("PERSON_NAME")  
 * &nbsp;private String name;
 * </pre>
 * 
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Stereotype {
	
	String value();
	
}
