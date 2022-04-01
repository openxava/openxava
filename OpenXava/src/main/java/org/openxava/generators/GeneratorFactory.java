package org.openxava.generators;

import java.util.*;



import org.openxava.formatters.UpperCaseFormatter;
import org.openxava.generators.xmlparse.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */

public class GeneratorFactory {
	
	private static Map ejbClasses;
	private static Map pojoClasses;
	
	
	public static IPropertyCodeGenerator create(MetaProperty metaProperty, boolean ejb) throws Exception {
		if (!has(metaProperty, ejb)) return null;
		Map classes = ejb?ejbClasses:pojoClasses;
		String className = (String) classes.get(metaProperty.getStereotype());
		Object o = instantiate(className);
		if (!(o instanceof IPropertyCodeGenerator)) {
			throw new XavaException("implements_required", className, "IPropertyCodeGenerator");
		}
		IPropertyCodeGenerator generator = (IPropertyCodeGenerator) o;
		generator.setMetaProperty(metaProperty);
		return generator;		
	}
	
	public static boolean has(MetaProperty metaProperty, boolean ejb) throws Exception {		
		configure();
		Map classes = ejb?ejbClasses:pojoClasses;
		return classes.containsKey(metaProperty.getStereotype());				
	}
	
	public static void _addForStereotype(String name, String modelType, String className) throws XavaException {
		if (ejbClasses == null || pojoClasses==null) {
			throw new XavaException("only_from_parse", "GeneratorFactory._addForStereotype");
		}		
		if ("ejb".equals(modelType)) {  
			ejbClasses.put(name, className);
		}
		else if ("pojo".equals(modelType)) {  
			pojoClasses.put(name, className);
		}
		else {
			ejbClasses.put(name, className);
			pojoClasses.put(name, className);
		}
	}
	
	private static Object instantiate(String className) throws Exception {
		return Class.forName(className).newInstance();		
	}
	
	private static void configure() throws XavaException {
		if (ejbClasses != null) return;
		ejbClasses = new HashMap();
		pojoClasses = new HashMap();
		GeneratorsParser.configureGenerators();
	}

}
