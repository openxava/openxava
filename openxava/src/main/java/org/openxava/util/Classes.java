package org.openxava.util;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import org.openxava.web.meta.*;

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
	
	/**
	 * Get a field of the specified class or any of its subclasses, even if it is private.  
	 * 
	 * @param theClass  Class where we want to get the field
	 * @return fieldName  Name of the field we want to get
	 * @throws NoSuchFieldException  If the field is not present in theClass and in any of its subclasses
	 * @since 7.0
	 */	
	public static Field getField(Class theClass, String fieldName) throws NoSuchFieldException { 
		try {
			return theClass.getDeclaredField(fieldName);
		} 
		catch (NoSuchFieldException ex) {
			if (theClass.equals(Object.class)) throw ex;
			return getField(theClass.getSuperclass(), fieldName);
		}
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

	public static String getAnnotationAttributeValue(Annotation annotation, String attribute) { // tmr Poner en changelog
		Object value = null;
		try {
			value = XObjects.execute(annotation, attribute);
		}
		catch (NoSuchMethodException ex) {			
		} 
		catch (Exception ex) {
			MetaWebEditors.log.warn(XavaResources.getString("impossible_get_value_annotation_attribute", attribute, annotation.annotationType().getName()), ex);			
		}		
		return value==null?null:value.toString();
	}
	
	public static Annotation[] getAnnotationsWithRepeatables(AnnotatedElement element) { // tmr En changelog
	    List<Annotation> allAnnotations = new ArrayList<>();
	    Annotation[] annotations = element.getAnnotations();
	    for (Annotation annotation : annotations) {
	        Method valueMethod;
	        try {
	            valueMethod = annotation.annotationType().getMethod("value");
	            Class<?> returnType = valueMethod.getReturnType();
	            if (returnType.isArray() && Annotation.class.isAssignableFrom(returnType.getComponentType())) {
	                Annotation[] repeatableAnnotations = (Annotation[]) valueMethod.invoke(annotation);
	                for (Annotation repeatableAnnotation : repeatableAnnotations) {
	                    allAnnotations.add(repeatableAnnotation);
	                }
	            } else {
	                allAnnotations.add(annotation);
	            }
	        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
	            allAnnotations.add(annotation);
	        }
	    }
	    return allAnnotations.toArray(new Annotation[0]);
	}
	
}
