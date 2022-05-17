package org.openxava.util;

import java.util.*;

import org.apache.commons.lang3.*;
import org.apache.commons.logging.*;




/**
 * Utilities to work with collections, enumerations and iterators. <p> 
 * 
 * @author Javier Paniza
 */
public class XCollections {
	
	private static Log log = LogFactory.getLog(XCollections.class);
	
	/**
	 * Returns the last element of sent collection. <p>
	 * 
	 * @param collection  Can be null
	 * @return Last element, or null if collection is null or empty.
	 */
	public static Object last(Collection collection) {
		if (collection == null) return null;
		if (collection.isEmpty()) return null;
		List list = collection instanceof List?(List) collection:new ArrayList(collection);		
		return list.get(list.size() - 1);
	}


	/**
	 * Adds elements from the enumeration to the collection. <p>
	 * 	 
	 * @param collection Not null
	 * @param toAdd  If null no elements are added.
	 */
	public static void add(Collection collection, Enumeration toAdd) {
		Assert.arg(collection);	
		if (toAdd == null) return;
		while (toAdd.hasMoreElements()) {
			collection.add(toAdd.nextElement());
		}	
	}
	
	/**
	 * Print in standard output the collection elements. <p> 
	 * 
	 * Util to debug.<br>
	 * @param c  Can be null.
	 */
	public static void println(Collection c) {
		if (c == null) return;
		println(c.iterator());
	}
	
	/**
	 * Print in standard output the elements by it iterate. <p> 
	 * 
	 * Util to debug.<br>
	 * @param c  Can be null.
	 */
	public static void println(Iterator it) {
		if (it == null) return;
		while (it.hasNext()) {
			log.debug(" - " + it.next());
		}
	}
	
	/**
	 * Returns a collection from a enumeration. <p>
	 * 
	 * @param e  If null then returns a empty collection.
	 * @return  Not null.
	 */
	public static Collection toCollection(Enumeration e) {
		Collection result = new ArrayList();
		if (e == null) return result;
		while (e.hasMoreElements()) {
			result.add(e.nextElement());
		}	
		return result;
	}
	
	/**
	 * Returns a List from an Iterable. <p>
	 * 
	 * @param it  If null then returns a empty list.
	 * @return  Not null.
	 * @since 5.6.1
	 */
	public static List toList(Iterable it) { 
		List result = new ArrayList();
		if (it == null) return result;
		for (Object e: it) {
			result.add(e);
		}	
		return result;
	}
	
	/**
	 * Returns a String [] from a collection of Strings. <p>
	 * 
	 * @return  Not null.
	 * @param c  Elements must be of type String. If null then returns a empty array.
	 */
	public static String [] toStringArray(Collection c) {
		if (c == null) return new String[0];
		String [] result = new String[c.size()];
		c.toArray(result);
		return result;
	}
	
	/**
	 *  @since 5.2
	 */
	public static void move(List list, int from, int to) { 
		Object fromValue = list.remove(from);		
		list.add(to, fromValue);			
	}
	
}
