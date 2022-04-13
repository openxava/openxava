package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Indicates that the date must be displayed and editing including the time part. <p>
 * 
 * The data type is any java.util.Date, including java.sql.Date. <p>
 * 
 * Applies to properties.<p>
 * 
 * Example:
 * <pre>
 * &nbsp;@DateTime
 * &nbsp;java.util.Date startDate;
 * </pre>
 * 
 * It's synonymous of @Stereotype("DATETIME").
 *
 * @since 6.6
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface DateTime {
	
}
