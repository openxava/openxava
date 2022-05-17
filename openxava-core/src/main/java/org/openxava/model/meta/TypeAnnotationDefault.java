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
	
	
	
	public static void _addForAnnotation(String annotationClassName, String type) throws XavaException {
		if (annotations == null) {
			throw new XavaException("only_from_parse", "TypeAnnotationDefault._addForAnnotation");
		}				
		annotations.put(annotationClassName, type);
	}
	
	public static String forAnnotation(Annotation annotation) throws ElementNotFoundException, XavaException {
		if (annotations == null) {
			configure();
		}		 
		String result = (String) annotations.get(annotation.annotationType().getName());		
		if (result == null) {
			throw new ElementNotFoundException("default_type_for_annotation_not_found", annotation); 
		}
		
		return result;
	}
			
	private static void configure() throws XavaException {
		annotations = new HashMap<>();		
		AnnotationTypeDefaultParser.configureAnnotationTypeDefault();
	}
	
}
