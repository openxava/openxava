package org.openxava.converters;





/**
 * In java a boolean and in database String that
 * can be 'S' or 'N'. 
 * 
 * @author Javier Paniza
 */
public class BooleanSNConverter implements IConverter {

	
	
	public Object toDB(Object o) throws ConversionException {
		if (!(o instanceof Boolean)) {		
			throw new ConversionException("conversion_db_boolean_expected");
		}
		return booleanToString(((Boolean) o).booleanValue());
	}
	
	public Object toJava(Object o) throws ConversionException {
		if (o == null) return Boolean.FALSE;
		if (!(o instanceof String)) {		
			throw new ConversionException("conversion_java_string_expected");
		}
		return Boolean.valueOf(stringToBoolean((String) o));
	}
	
	public static String booleanToString(boolean value) {
		return value?"S":"N";
	}
	
	public static boolean stringToBoolean(String value) {
		if (value == null) return false;
		return value.equalsIgnoreCase("S");
	}

}
