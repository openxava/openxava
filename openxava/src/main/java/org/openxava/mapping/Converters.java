package org.openxava.mapping;

import java.util.*;



import org.openxava.mapping.xmlparse.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;

/**
 * Utility class for access to default converter for a property. <p> 
 * 
 * @author Javier Paniza
 */
public class Converters {
		
	private static Map stereotypeConverters;
	private static Map stereotypeCmpTypes;
	private static Map typeConverters;
	private static Map typeCmpTypes;
	
	
	/**
	* @throws XavaException
	 */
	public static void _addForStereotype(String name, String converterClass, String cmpType) {
		if (stereotypeConverters == null) {
			throw new XavaException("only_from_parse", "Converters._addForStereotype");
		}		
		stereotypeConverters.put(name, converterClass);
		stereotypeCmpTypes.put(name, cmpType);		
	}
	
	/**
	* @throws XavaException
	 */
	public static void _addForType(String typeName, String converterClass, String cmpType) {
		if (typeConverters == null) {
			throw new XavaException("only_from_parse", "Converters._addForType");
		}			
		typeConverters.put(typeName, converterClass);
		typeCmpTypes.put(typeName, cmpType);
	}
	
	/**
	 * @return null if argument has no default converter associated. <p>  
	 * @throws XavaException
	 */
	public static String getConverterClassNameFor(MetaProperty p) {
		if (stereotypeConverters == null) configure();
		String result = (String) stereotypeConverters.get(p.getStereotype());
		if (result != null) return result;
		return (String) typeConverters.get(p.getType().getName());
	}

	/**
	 * @return null if argument has no default converter associated. <p>  
	 * @throws XavaException
	 */
	public static String getCmpTypeFor(MetaProperty p) {
		if (stereotypeCmpTypes == null) configure();
		String result = (String) stereotypeCmpTypes.get(p.getStereotype());
		if (result != null) return result;
		return (String) typeCmpTypes.get(p.getType().getName());
	}	
			
	/**
	* @throws XavaException
	 */
	private static void configure() {
		stereotypeConverters = new HashMap();
		stereotypeCmpTypes = new HashMap();
		typeConverters = new HashMap();
		typeCmpTypes = new HashMap();
		ConvertersParser.configureConverters();
	}
		
}
