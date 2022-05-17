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

	/** 
	 * Comma separated list of accepted mime types. <p>
	 * 
	 * If the uploaded file does not match with specified types the file is rejected.<br>
	 * For example, with this code:
	 * <pre>
	 * @Files(acceptFileTypes="image/*")
	 * @Column(length=32)
	 * private String photos;
	 * </pre>
	 * 
	 * The user can only upload images, and with this one:
	 * <pre>
	 * @Files(acceptFileTypes="text/csv, application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	 * @Column(length=32)
  	 * private String spreadsheets;
  	 * </pre>
	 * Only CSV and Excel files. As you can see in acceptFileTypes you put a 
	 * <a href="https://docs.w3cub.com/http/basics_of_http/mime_types/complete_list_of_mime_types">list of mime types</a> 
	 * separated by commas and you can use wildcards.
	 */
	String acceptFileTypes() default "";
	
	/**
	 * Maximum size of the file to upload in Kb. <p>
	 * 
	 * If the uploaded file is greater than the specified size the file is rejected.<br>
	 * For example, with this code:
	 * <pre>
	 * @Files(maxFileSizeInKb=200)
	 * @Column(length=32)
	 * private String documents;
	 * </pre>
	 * The user can only upload files of 200 Kb or less.<br>
	 * 
	 * The default value is -1L that means no limit.
	 */
	long maxFileSizeInKb() default -1L;
	
}
