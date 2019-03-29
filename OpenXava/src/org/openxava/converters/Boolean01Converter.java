package org.openxava.converters;






/**
 * In java boolean and in database Number that
 * it can be 1 or 0.
 *  
 * @author Javier Paniza
 */
public class Boolean01Converter implements IConverter {

	
	
	public Object toDB(Object o) throws ConversionException {
		if (!(o instanceof Boolean)) {		
			throw new ConversionException("conversion_db_boolean_expected");
		}
		return new Integer(booleanToInt(((Boolean) o).booleanValue()));
	}
	
	public Object toJava(Object o) throws ConversionException {
		if (o == null) return Boolean.FALSE;
		if (o instanceof Boolean) return o; // By pass of Boolean objects 
		if (!(o instanceof Number)) {		
			throw new ConversionException("conversion_java_number_expected");
		}
		return Boolean.valueOf(intToBoolean(((Number) o).intValue()));
	}
	
	
	public static int booleanToInt(boolean valor) {
		return valor?1:0;
	}
	
	public static boolean intToBoolean(int valor) {
		return valor != 0;
	}

}
