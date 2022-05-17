package org.openxava.converters;




/**
 * @author Javier Paniza
 */
public class NotNullStringConverter implements IConverter {

	
	
	public Object toJava(Object o) throws ConversionException {
		return notNull(o);
	}

	public Object toDB(Object o) throws ConversionException {		
		return notNull(o);
	}

	private String notNull(Object o) throws ConversionException {
		if (o == null) return "";
		return o.toString();
	}
	
}
