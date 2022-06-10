package org.openxava.model.meta;

import java.util.*;



import org.openxava.model.meta.xmlparse.*;
import org.openxava.util.*;

/**
 * Utility class to access to default type from a stereotype. <p>
 * 
 * @author Javier Paniza
 */
public class TypeStereotypeDefault {
		
	private static Map stereotypes;
	
	
	
	public static void _addForStereotype(String name, String type) throws XavaException {
		if (stereotypes == null) {
			throw new XavaException("only_from_parse", "TypeStereotypeDefault._addForStereotype");
		}				
		stereotypes.put(name, type);
	}
	
	public static String forStereotype(String name) throws ElementNotFoundException, XavaException {
		if (stereotypes == null) {
			configure();
		}		 
		String result = (String) stereotypes.get(name);		
		if (result == null) {
			throw new ElementNotFoundException("default_type_for_stereotype_not_found", name);
		}
		
		return result;
	}
			
	private static void configure() throws XavaException {
		stereotypes = new HashMap();		
		StereotypeTypeDefaultParser.configureStereotypeTypeDefault();
	}
	
}
