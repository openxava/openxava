package org.openxava.util;

import java.lang.reflect.*;
import java.util.*;




/**
 * Compare <code>java.lang.reflect.Field</code> by name 
 * 
 * @author Javier Paniza
 */
public class FieldComparator implements Comparator {
	
	final private static FieldComparator instance = new FieldComparator();
	
	
	
	// Use getInstance
	private FieldComparator() {		
	}

	public int compare(Object f1, Object f2) {
		if (f1 == f2) return 0;
		if (f1 == null) return -1;
		if (f2 == null) return 1;
		String name1 = ((Field) f1).getName().toLowerCase();
		if (name1.startsWith("_")) name1 = name1.substring(1);
		String name2 = ((Field) f2).getName().toLowerCase();
		if (name2.startsWith("_")) name2 = name2.substring(1);		
		return name1.compareTo(name2);
	}
	
	public static FieldComparator getInstance() {
		return instance;
	}

}
