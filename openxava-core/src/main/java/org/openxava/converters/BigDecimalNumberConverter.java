package org.openxava.converters;

import java.math.*;




/**
 * In Java <code>java.math.BigDecimal</code> and in database <code>Number</code> 
 * of any type.
 * 
 * @author Javier Paniza
 */
public class BigDecimalNumberConverter implements IConverter {
	
	private final static BigDecimal ZERO = new BigDecimal("0");	

	
	
	public Object toDB(Object o) throws ConversionException {
		return o==null?ZERO:o;
	}
	
	public Object toJava(Object o) throws ConversionException {
		if (o == null) return ZERO;
		if (!(o instanceof Number)) {		
			throw new ConversionException("conversion_java_number_expected");
		}
		if (o instanceof BigDecimal) {
			return (BigDecimal) o;
		}
		if (o instanceof Integer) {
			return new BigDecimal(((Integer) o).intValue());
		}
		if (o instanceof Long) {
			return new BigDecimal(((Long) o).longValue());
		}
		if (o instanceof Float) {
			return new BigDecimal(((Float) o).floatValue());
		}
		if (o instanceof Double) {
			return new BigDecimal(((Double) o).doubleValue());
		}				
		throw new ConversionException("conversion_to_bigdecimal_not_supported", o.getClass());
	}
			
}
