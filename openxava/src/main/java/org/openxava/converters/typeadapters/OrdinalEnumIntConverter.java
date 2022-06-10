package org.openxava.converters.typeadapters;

import org.openxava.converters.*;

/**
 * For using Java 5 enums in tabs, specifically for converting filter data. <p>
 *  
 * @author Javier Paniza
 */

public class OrdinalEnumIntConverter implements IConverter {

	public Object toJava(Object o) throws ConversionException {
		// No conversion, because it's used only for converting filtering data
		return o; 
	}

	public Object toDB(Object o) throws ConversionException {
		if (o == null) return null; 
		return ((Enum) o).ordinal();
	}

}
