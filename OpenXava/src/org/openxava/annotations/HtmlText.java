package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Stores rich text in HTML format that the user can edit with a rich text editor. <p>
 * 
 * This allow the user to use a word processor for a field, 
 * including all formating styles typical of a word processor, not just plain text.<br> 
 * 
 * The data type is String with a length of 3000.<br>
 * 
 * Applies to properties.<p>
 * 
 * Example:
 * <pre>
 * &nbsp;@HtmlText
 * &nbsp;private String synopsis;
 * </pre>
 * 
 * It's synonymous of @Stereotype("HTML_TEXT").
 *
 * @since 6.6
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface HtmlText {
	
	/**
	 * When true the user interface for editing the text is simpler, 
	 * with a simplified button bar with only the most common used buttons.
	 */
	boolean simple() default false;
	
}
