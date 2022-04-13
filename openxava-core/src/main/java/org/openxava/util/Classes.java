package org.openxava.util;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * Utility class to work with classes. <p>
 * 
 * @author: Javier Paniza
 */

public class Classes {
		
	/**
	 * If the class contains the exact method without argumetns. <p>
	 */
	public static boolean hasMethod(Class theClass, String method) { 
		try {
			theClass.getMethod(method, (Class<?> []) null); 
			return true;
		}
		catch (NoSuchMethodException ex) {
			return false;
		}
	}
	
	/**
	 * All fields from all superclasess and including private, protected and public. 
	 * 
	 * @param theClass
	 * @return
	 */
	public static Collection<Field> getFieldsAnnotatedWith(Class theClass, Class<? extends Annotation> annotation) {
		Collection<Field> result = new ArrayList<Field>();
		fillFieldsAnnotatedWith(result, theClass, annotation);
		return result;
	}
	
	private static void fillFieldsAnnotatedWith(Collection<Field> result, Class theClass, Class<? extends Annotation> annotation) {
		if (Object.class.equals(theClass)) return;
		for (Field field: theClass.getDeclaredFields()) {
			if (field.isAnnotationPresent(annotation)) result.add(field);
		}
		fillFieldsAnnotatedWith(result, theClass.getSuperclass(), annotation);
	}
	
	/** 
	 * Returns the collection of methods with the given annotation
	 * 
	 * @param theClass Class object to be examined
	 * @param annotation Annotation type to be search for
	 * @return a Collection of Method, never null, might be empty.
	 * @since 4.0.1
	 */
	public static Collection<Method> getMethodsAnnotatedWith(Class theClass, Class<? extends Annotation> annotation) {
		Collection<Method> result = new ArrayList<Method>();
		fillMethodsAnnotatedWith(result, theClass, annotation);
		return result;
	}

	private static void fillMethodsAnnotatedWith(Collection<Method> result, Class theClass, Class<? extends Annotation> annotation) {
		if (Object.class.equals(theClass)) return;
		for (Method method: theClass.getDeclaredMethods()) {
			if (method.isAnnotationPresent(annotation)) result.add(method);
		}
		fillMethodsAnnotatedWith(result, theClass.getSuperclass(), annotation);
	}
}
