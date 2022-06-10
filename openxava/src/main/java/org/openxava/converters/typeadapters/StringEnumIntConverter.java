package org.openxava.converters.typeadapters;

import java.util.*;

import org.openxava.converters.*;
import org.openxava.util.*;

/**
 * For using Java 5 enums in tabs, specifically for converting filter data. <p>
 *  
 * @author Javier Paniza
 */

public class StringEnumIntConverter implements IConverter {
	
	private List enumConstants = new ArrayList();		

	public Object toJava(Object o) throws ConversionException {		
		if (Is.empty(o)) return null; 
		return enumConstants.indexOf(o); 
	}

	public Object toDB(Object o) throws ConversionException {
		return o.toString();
	}

	public void setEnumConstants(String enumConstants) {		
		StringTokenizer st = new StringTokenizer(enumConstants, ";");
		while (st.hasMoreTokens()) {
			this.enumConstants.add(st.nextToken());
		}
	}

}
