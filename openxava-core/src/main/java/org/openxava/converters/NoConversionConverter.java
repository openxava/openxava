package org.openxava.converters;




/**
 * 
 * @author Ana Andres 
 */
public class NoConversionConverter implements IConverter{

	
	
	public Object toJava(Object o) throws ConversionException {
		return o;
	}

	public Object toDB(Object o) throws ConversionException {
		return o;
	}

}
