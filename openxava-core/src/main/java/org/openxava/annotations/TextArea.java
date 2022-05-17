package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * String used to represent a big area of text. <p>
 * 
 * The user interface is an TextArea or Memo, where the user can type several lines.
 * 
 * The data type is String with 500 as length by default.
 * 
 * Applies to properties.<p>
 * 
 * Example:
 * <pre>
 * &nbsp;@TextArea
 * &nbsp;@Column(length=2000)
 * &nbsp;private String remarks;
 * </pre>
 * 
 * It's synonymous of @Stereotype("TEXT_AREA").
 *
 * @since 6.6
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface TextArea {
	
}
