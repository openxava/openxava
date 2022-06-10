package org.openxava.converters;




/**
 * @author Javier Paniza
 */
public class StringNotNullConverter implements IConverter {

	
	
	public Object toJava(Object o) throws ConversionException {
	    if (o == null) return "";
		return o;
	}

	public Object toDB(Object o) throws ConversionException {
	    if (o == null) return "";
		return o;
	}
		
}
