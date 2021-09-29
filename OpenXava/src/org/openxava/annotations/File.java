package org.openxava.annotations;

import java.lang.annotation.*;

/**
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
public @interface File {
	
	/** 
	 * Comma separated list of accepted mime types. <p>
	 * 
	 * If the uploaded file does not match with specified types the file is rejected.<br>
	 * For example, with this code:
	 * <pre>
	 * @File(acceptFileTypes="image/*")
	 * @Column(length=32)
	 * private String photo;
	 * </pre>
	 * 
	 * The user can only upload images, and with this one:
	 * <pre>
	 * @File(acceptFileTypes="text/csv, application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	 * @Column(length=32)
  	 * private String spreadsheet;
  	 * </pre>
	 * Only CSV and Excel files. As you can see in acceptFileTypes you put a 
	 * <a href="https://docs.w3cub.com/http/basics_of_http/mime_types/complete_list_of_mime_types">list of mime types</a> 
	 * separated by commas and you can use wildcards.
	 */
	String acceptFileTypes() default "";

	// tmr doc
	long maxFileSizeInKb() default -1L;
}
