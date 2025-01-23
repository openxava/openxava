package org.openxava.util;

import java.beans.*;
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

	/** @since 7.4 */
	public static String getAnnotationAttributeValue(Annotation annotation, String attribute) { 
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
	
	/** @since 7.4 */
	public static Annotation[] getAnnotationsWithRepeatables(AnnotatedElement element) { 
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
	
	// tmr ini
	public static Map<String, PropertyDescriptor> getPropertyDescriptors(Class<?> pojoClass) { // tmr ¿Es necesario con HotSwapAgent? Probar quitarlo
		// tmr Comentarios a inglés, o quitarlos
	    Map<String, PropertyDescriptor> result = new HashMap<>();

	    // Recorrer la jerarquía de clases para incluir métodos heredados
	    Class<?> currentClass = pojoClass;
	    while (currentClass != null && currentClass != Object.class) {
	        // Obtener todos los métodos públicos (incluidos los heredados)
	        Method[] methods = currentClass.getMethods();
	        for (Method method : methods) {
	            // Determinar si es un getter
	            if (isGetter(method)) {
	                String propertyName = extractPropertyNameFromGetter(method.getName());
	                try {
	                    // Buscar un setter correspondiente
	                    Method setter = findSetterMethod(currentClass, propertyName, method.getReturnType());
	                    PropertyDescriptor pd = new PropertyDescriptor(propertyName, method, setter);
	                    result.putIfAbsent(propertyName, pd); // Evitar duplicados
	                } catch (Exception e) {
	                	// tmr ¿Qué hacer en este caso? ¿Nada, un log o una excepción? 
	                }
	            }
	        }
	        currentClass = currentClass.getSuperclass();
	    }

	    return result;
	}

	// Verificar si un método es un getter
	private static boolean isGetter(Method method) { 
	    if (!method.getName().startsWith("get") && !method.getName().startsWith("is")) return false;
	    if (method.getParameterCount() != 0) return false;
	    if (void.class.equals(method.getReturnType())) return false;
	    return true;
	}

	// Extraer el nombre de la propiedad a partir del nombre del getter
	private static String extractPropertyNameFromGetter(String getterName) { 
	    if (getterName.startsWith("get")) {
	        return Strings.firstLower(getterName.substring(3));
	    } else if (getterName.startsWith("is")) {
	        return Strings.firstLower(getterName.substring(2));
	    }
	    throw new IllegalArgumentException("El método no es un getter válido: " + getterName); // tmr i18n
	}

	// Buscar el método setter correspondiente
	private static Method findSetterMethod(Class<?> clazz, String propertyName, Class<?> propertyType) {
	    String setterName = "set" + Strings.firstUpper(propertyName);
	    try {
	        return clazz.getMethod(setterName, propertyType);
	    } catch (NoSuchMethodException e) {
	        return null; // Sin setter
	    }
	}
	// tmr fin

}
