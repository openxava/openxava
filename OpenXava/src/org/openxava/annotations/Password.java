package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * tmr Redoc
 * The user can upload a file in this property, so the file is attached to the entity. <p>
 * 
 * If the uploaded file is an image, an image preview is shown. 
 * The user can download the file or see the image (if it is an image) just clicking.
 * 
 * The data type is String with a length of 32.
 * 
 * Applies to properties.<p>
 * 
 * Example:
 * <pre>
 * &nbsp;@File
 * &nbsp;@Column(length=32)
 * &nbsp;private String document;
 * </pre>
 * 
 * It's synonymous of @Stereotype("FILE").
 *
 * @since 6.6
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Password {
	
}
