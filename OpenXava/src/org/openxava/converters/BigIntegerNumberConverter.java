package org.openxava.converters;

import java.math.*;




/**
 * In Java <code>java.math.BigInteger</code> and in database <code>Number</code>
 * of any type.
 * 
 * @author Javier Paniza
 */
public class BigIntegerNumberConverter implements IConverter {		

	
	
	public Object toDB(Object o) throws ConversionException {
		return o==null?BigInteger.ZERO:o;
	}
	
	public Object toJava(Object o) throws ConversionException {
		if (o == null) return BigInteger.ZERO;
		if (!(o instanceof Number)) {		
			throw new ConversionException("conversion_java_number_expected");
		}
		if (o instanceof BigInteger) {
			return (BigInteger) o;
		}		
		return new BigInteger(o.toString());
	}
			
}
