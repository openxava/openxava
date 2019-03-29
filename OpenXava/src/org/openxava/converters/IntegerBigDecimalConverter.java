package org.openxava.converters;

import java.math.*;





/**
 * In java a int and in database a BigDecimal. <p>
 * 
 * @author Javier Paniza
 */
public class IntegerBigDecimalConverter implements IConverter {

	
	
	public Object toDB(Object o) throws ConversionException {
		if (!(o instanceof Integer)) {		
			throw new ConversionException("conversion_db_integer_expected");
		}				
		return new BigDecimal(o.toString());
	}
	
	public Object toJava(Object o) throws ConversionException {
		if (!(o instanceof BigDecimal)) {		
			throw new ConversionException("conversion_java_bigdecimal_expected");
		}
		return new Integer(((BigDecimal) o).intValue());
	}
			
}
