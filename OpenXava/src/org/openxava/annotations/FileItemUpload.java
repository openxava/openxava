package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Configure the way files can be uploaded in a FileItem property. <p>
 * 
 * Applies to properties of org.apache.commons.fileupload.FileItem type.<br>
 * 
 * Example:
 * <pre>
 * @FileItemUpload(acceptFileTypes="text/plain")
 * private FileItem file;
 * </pre>
 * 
 * @since 6.6
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface FileItemUpload {
	
	/** 
	 * Comma separated list of accepted mime types. <p>
	 * 
	 * If the uploaded file does not match with specified types the file is rejected.<br>
	 * For example, with this code:
	 * <pre>
	 * @FileItemUpload(acceptFileTypes="image/*")
	 * private FileItem file;
	 * </pre>
	 * 
	 * The user can only upload images, and with this one:
	 * <pre>
	 * @FileItemUpload(acceptFileTypes = "text/csv, application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	 * private FileItem file;
  	 * </pre>
	 * Only CSV and Excel files. As you can see in acceptFileTypes you put a 
	 * <a href="https://docs.w3cub.com/http/basics_of_http/mime_types/complete_list_of_mime_types">list of mime types</a> 
	 * separated by commas and you can use wildcards.
	 */
	String acceptFileTypes() default "";
	
}
