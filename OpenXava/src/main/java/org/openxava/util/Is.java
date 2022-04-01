package org.openxava.util;

import java.math.*;
import java.util.*;

import org.apache.commons.lang3.*;

import net.sf.jasperreports.engine.virtualization.*;

/**
 * Utility class to reduce the ifs size. <p>
 * 
 * Useful for implementing asserts (invariants, preconditions, postcondition).<br>
 * 
 * For example:
 * <pre>
 * if (name != null || name.trim().equals("") ||
 *   surname1 != null || surname1.trim().equals("")) ||
 *   surname2 != null || surname2.trim().equals(""))
 * {
 *   doSomething();
 * }
 * </pre>
 * can be write:
 * <pre>
 * if (Is.emptyString(name, surname1, surname2)) {
 *   doSomethis();
 * }
 * </pre>  
 *  
 * @author  Javier Paniza
 * @author  Hayrol Reyes
 */

public class Is {
	
	private static BigDecimal ZERO = new BigDecimal("0"); 
	
	/**
	 * Compare the first argurment with the rest and if any if equal returns true.
	 * 
	 * Uses Is.equal() to compare the elements. The null is a valid value, so if you
	 * send null as first argument and any of the possible value is null returns true.
	 * 
	 * @param object  The object we are looking for. Can null.
	 * @param possibleValues The objects where we are looking for. Can contain nulls.
	 * @since 5.6
	 */
	public static boolean anyEqual(Object object, Object ... possibleValues) { 
		for (Object possibleValue: possibleValues) {
			if (equal(object, possibleValue)) return true;
		}
		return false;
	}

	
	/**
	 * Verifies if the sent object is <code>null</code> or empty string 
	 * (if it's string) or 0 (if it's number) or empty Map. <p>
	 * 
	 * Since v5.9 it supports Java native arrays. 
	 */
	public final static boolean empty(Object object) {
		if (object == null) return true;
		if (object instanceof String) return ((String) object).trim().equals("");
		if (object instanceof BigDecimal) return ZERO.compareTo((BigDecimal)object) == 0;
		if (object instanceof Number) return ((Number) object).intValue() == 0;
		if (object instanceof Map) return Maps.isEmptyOrZero((Map) object);
		if (object.getClass().isArray()) return ArrayUtils.toString(object).equals("{}"); 
		return false;
	}
	
	/**
	 * Verifies if some of the sent strings are <code>null</code> or empty string. <p>
	 */
	public final static boolean emptyString(String... strs) { 
		if (strs == null) return true;
		for (int i = 0; i < strs.length; i++) {
		    if (strs[i] == null || strs[i].trim().equals("")) return true;
		}
		return false;
	}
	
	/**
	 * Verifies if all sent strings are <code>null</code> or empty string. <p>
	 */
	public final static boolean emptyStringAll(String... strs) { 
		if (strs == null) return true;
		for (int i = 0; i < strs.length; i++) {
		    if (strs[i] != null && !strs[i].trim().equals("")) return false;
		}
		return true;
	}  
  
	/**
	 * If <code>a</code> is equals to <code>b</code>. <p>
	 *
	 * Takes in account the nulls. Use compareTo when appropriate and
	 * compares Java 5 enums with numbers by the ordinal value. Compares
	 * Integer, Long, Short among themselves.<br>
	 * Also admits to compare objects of not compatible types, just it returns 
	 * false in this case.
	 * 
	 * Since v5.9 works fine with Java native arrays.
	 * 
	 * @param a Can be null.
	 * @param b Can be null.
	 */
	public final static boolean equal(Object a, Object b) {
		if (a == null) return b == null;
		if (b == null) return false;
		if (a instanceof Enum) a = enumToInteger(a); 
		if (b instanceof Enum) b = enumToInteger(b);		
		if (isInteger(a)) a = toLong(a);
		if (isInteger(b)) b = toLong(b);
		if (a.getClass().isArray() && b.getClass().isArray()) {
			return ArrayUtils.isEquals(a, b);
		}
		try {
			if (a instanceof Comparable) {
				try {
					return ((Comparable) a).compareTo(b) == 0;
				}
				catch (ClassCastException ex) {
					if (b instanceof Comparable) {					
						return ((Comparable) b).compareTo(a) == 0;
					}
					else return false;
				}
			}
			return a.equals(b);
		}
		catch (ClassCastException ex) {
			return false;
		}
	}
		
	private static Long toLong(Object integer) {
		return new Long(((Number) integer).longValue());
	}

	private static boolean isInteger(Object o) {		
		return o instanceof Integer || o instanceof Long || o instanceof Short;
	}

	private static Integer enumToInteger(Object theEnum) {
		try {
			return (Integer) XObjects.execute(theEnum, "ordinal"); 
		}
		catch (Exception ex) {
			throw new XavaException("enum_to_integer_error");
		}		
	}

	/**
	 * If <code>a.toString().trim()</code> is equals to <code>b.toString().trim()</code>. <p>
	 *
	 * Takes in account the nulls.<br>
	 * 
	 * @param a Can be null.
	 * @param b Can be null.
	 */
	public final static boolean equalAsString(Object a, Object b) {
		a = a==null?"":a.toString().trim();
		b = b==null?"":b.toString().trim();
		return a.equals(b);
	}

	/**
	 * If <code>a.toString().trim()</code> is equal to <tt>b.toString().trim()</tt> ignoring case. <p>
	 *
	 * Takes in account the nulls.<br> 
	 * 
	 * @param a Can be null.
	 * @param b Can be null.
	 */
	public final static boolean equalAsStringIgnoreCase(Object a, Object b) {
		String sa = a == null?"":a.toString().trim();
		String sb = b == null?"":b.toString().trim();
		return sa.equalsIgnoreCase(sb);
	}
  
}
