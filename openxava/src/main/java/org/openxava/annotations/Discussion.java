package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Field that contains a discussion thread, just like the ones in forums, trackers, blogs, etc.<p>
 * 
 * The data type is String with a length of 32.
 * 
 * Applies to properties.<p>
 * 
 * Example:
 * <pre>
 * &nbsp;@Discussion
 * &nbsp;@Column(length=32)
 * &nbsp;private String discussion;
 * </pre>
 * 
 * It's synonymous of @Stereotype("DISCUSSION").
 *
 * @since 6.6
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Discussion {
	
}
