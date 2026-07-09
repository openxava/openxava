package org.openxava.model.meta;

import java.lang.annotation.*;
import java.util.*;

import org.openxava.model.meta.xmlparse.*;
import org.openxava.util.*;

/**
 * Utility class to access to default type from an annotation. <p>
 * 
 * @since 6.6
 * @author Javier Paniza
 */
public class TypeAnnotationDefault {
		
	private static Map<String, String> annotations;
	
	
	
	/**
	* @throws XavaException
	 */
	public static void _addForAnnotation(String annotationClassName, String type) {
		if (annotations == null) {
			throw new XavaException("only_from_parse", "TypeAnnotationDefault._addForAnnotation");
		}				
		annotations.put(annotationClassName, type);
	}
	
	/**
	* @throws XavaException
	 */
	public static String forAnnotation(Annotation annotation) throws ElementNotFoundException {
		if (annotations == null) {
			configure();
		}		 
		String result = (String) annotations.get(annotation.annotationType().getName());		
		if (result == null) {
			throw new ElementNotFoundException("default_type_for_annotation_not_found", annotation); 
		}
		
		return result;
	}
			
	/**
	* @throws XavaException
	 */
	private static void configure() {
		annotations = new HashMap<>();		
		AnnotationTypeDefaultParser.configureAnnotationTypeDefault();
	}
	
}
