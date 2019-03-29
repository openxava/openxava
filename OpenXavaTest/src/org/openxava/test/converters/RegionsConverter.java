package org.openxava.test.converters;

import org.openxava.converters.*;

/**
 * 
 * @author Javier Paniza
 */

public class RegionsConverter implements IConverter {

	public Object toJava(Object o) throws ConversionException {
		if (o == null) return new String[0];
		String dbValue = (String) o; 
		String [] javaValue = new String [dbValue.length()];
		for (int i = 0; i < javaValue.length; i++) {
			javaValue[i] = String.valueOf(dbValue.charAt(i));			
		}
		return javaValue;
	}

	public Object toDB(Object o) throws ConversionException {
		if (o == null) return "";
		String [] javaValue = (String []) o;
		StringBuffer dbValue = new StringBuffer();
		for (int i = 0; i < javaValue.length; i++) {
			dbValue.append(javaValue[i]);
		}
		return dbValue.toString();
	}

}
