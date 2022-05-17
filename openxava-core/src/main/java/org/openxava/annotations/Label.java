package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * String field displayed as a label. <p>
 * 
 * The field is not editable and always displayed as a label.
 * 
 * The data type is String.
 * 
 * Applies to properties.<p>
 * 
 * Example:
 * <pre>
 * &nbsp;@Label
 * public String getZoneOne() {
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return "IN ZONE 1";
 * &nbsp;}
 * </pre>
 * 
 * It's synonymous of @Stereotype("LABEL").
 *
 * @since 6.6
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Label {
	
}
