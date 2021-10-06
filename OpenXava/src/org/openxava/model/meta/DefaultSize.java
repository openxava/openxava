package org.openxava.model.meta;

import java.lang.annotation.*;
import java.util.*;

import org.openxava.model.meta.xmlparse.*;
import org.openxava.util.*;

/**
 * Utility class to access to default length of a type or stereotype. <p>
 * 
 * @author Javier Paniza; modified by Radoslaw Ostrzycki, Newitech
 */
public class DefaultSize {
		
	// tmr Todos los <String, Integer>
	private static Map<String, Integer> stereotypes;
	private static Map<String, Integer> types;
	private static Map<String, Integer> annotations; // tmr
	
	private static Map<String, Integer> scaleStereotypes;
	private static Map<String, Integer> scaleTypes;
	private static Map<String, Integer> scaleAnnotations; // tmr
	
	
	
	public static void _addForStereotype(String name, int length) throws XavaException {
		if (stereotypes == null) {
			throw new XavaException("only_from_parse", "DefaultSize._addForStereotype");
		}		
		stereotypes.put(name, new Integer(length));
	}
	
	public static void _addForType(String className, int length) throws XavaException {
		if (types == null) {
			throw new XavaException("only_from_parse", "DefaultSize._addForType");
		}			
		types.put(className, new Integer(length));
	}
	
	public static void _addForAnnotation(String className, int length) throws XavaException { // tmr
		if (types == null) {
			throw new XavaException("only_from_parse", "DefaultSize._addForAnnotation");
		}			
		annotations.put(className, length);
	}
	
	public static void _addScaleForStereotype(String name, int length) throws XavaException {
		if (stereotypes == null) {
			throw new XavaException("only_from_parse", "DefaultSize._addScaleForStereotype");
		}		
		scaleStereotypes.put(name, new Integer(length));
	}
	
	public static void _addScaleForType(String className, int length) throws XavaException {
		if (types == null) {
			throw new XavaException("only_from_parse", "DefaultSize._addScaleForType");
		}			
		scaleTypes.put(className, new Integer(length));
	}
	
	public static void _addScaleForAnnotation(String className, int length) throws XavaException { // tmr
		if (types == null) {
			throw new XavaException("only_from_parse", "DefaultSize._addScaleForAnnotation");
		}			
		scaleAnnotations.put(className, length);
	}	

	
	/**
	 * 
	 * @param name
	 * @return size for stereotype
	 * @throws ElementNotFoundException
	 * @throws XavaException
	 */
	public static int forStereotype(String name) throws ElementNotFoundException, XavaException {
		if (stereotypes == null) {
			configure();
		}
		Integer result = (Integer) stereotypes.get(name);
		if (result == null) {
			throw new ElementNotFoundException("default_size_for_stereotype_not_found", name);
		}
		return result.intValue();
	}
	
	/**
	 * tmr redoc 
	 * @param name
	 * @return size for stereotype
	 * @throws ElementNotFoundException
	 * @throws XavaException
	 */
	public static int forAnnotation(Annotation annotation) throws ElementNotFoundException, XavaException { // tmr
		if (stereotypes == null) {
			configure();
		}
		Integer result = annotations.get(annotation.annotationType().getName());
		if (result == null) {
			throw new ElementNotFoundException("default_size_for_stereotype_not_found", annotation.annotationType()); // tmr i18n
		}
		return result;
	}
	
	/**
	 * 
	 * @param className
	 * @return size param for class
	 * @throws ElementNotFoundException
	 * @throws XavaException
	 */
	public static int forType(Class className) throws ElementNotFoundException, XavaException {
		if (types == null) {
			configure();
		}
		Integer result = (Integer) types.get(className.getName());
		if (result == null) {
			throw new ElementNotFoundException("default_size_for_type_not_found", className.getName());
		}
		return result.intValue();		
	}
	
	private static void configure() throws XavaException {
		stereotypes = new HashMap<>();
		types = new HashMap<>();
		annotations = new HashMap<>(); // tmr
		scaleStereotypes = new HashMap<>();
		scaleTypes = new HashMap<>();
		scaleAnnotations = new HashMap<>(); // tmr
		DefaultSizeParser.configureDefaultSize();
	}
	
	/**
	 * @author Radoslaw Ostrzycki, Newitech Sp. z o.o.
	 * @param name
	 * @return
	 * @throws ElementNotFoundException
	 * @throws XavaException
	 */
	public static int scaleForStereotype(String name) throws ElementNotFoundException, XavaException {
		if (stereotypes == null) {
			configure();
		}
		Integer result = (Integer) scaleStereotypes.get(name);
		if (result == null) {
			throw new ElementNotFoundException("default_scale_for_stereotype_not_found", name);
		}
		return result.intValue();
	}
	
	/**
	 * @author Radoslaw Ostrzycki, Newitech Sp. z o.o.
	 * @param className
	 * @return
	 * @throws ElementNotFoundException
	 * @throws XavaException
	 */
	public static int scaleForType(Class className) throws ElementNotFoundException, XavaException {
		if (types == null) {
			configure();
		}
		Integer result = (Integer) scaleTypes.get(className.getName());
		if (result == null) {
			throw new ElementNotFoundException("default_size_for_type_not_found", className.getName());
		}
		return result.intValue();		
	}
	
	public static int scaleForAnnotation(Annotation annotation) throws ElementNotFoundException, XavaException { // tmr
		if (types == null) {
			configure();
		}
		Integer result = scaleAnnotations.get(annotation.annotationType().getName());
		if (result == null) {
			throw new ElementNotFoundException("default_size_for_type_not_found", annotation.annotationType()); // tmr i18n
		}
		return result;		
	}
		
}
