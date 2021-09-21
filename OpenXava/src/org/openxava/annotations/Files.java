package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * The user can upload several files in this property, so the files are attached to the entity. <p>
 * 
 * The files that are images are shown with image preview. 
 * The user can download any file or see any image (if it is an image) just clicking on it.
 * 
 * The data type is String with a length of 32.
 * 
 * Applies to properties.<p>
 * 
 * Example:
 * <pre>
 * &nbsp;@Files
 * &nbsp;@Column(length=32)
 * &nbsp;private String documents;
 * </pre>
 * 
 * It's synonymous of @Stereotype("FILES").
 *
 * @since 6.6
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Files {
	
}
