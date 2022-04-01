package org.openxava.converters;






/**
 * In java a int and in database a Number of any type. <p>
 * 
 * @author Javier Paniza
 */
public class IntegerNumberConverter implements IConverter {
	
	private final static Integer ZERO = new Integer(0);
	private static IConverter instance;
	private boolean nullToZero = true;
	
	public static IConverter getInstance() {
		if (instance == null) {
			instance = new IntegerNumberConverter();
		}
		return instance;
	}

	public Object toDB(Object o) throws ConversionException {
		return o==null && isNullToZero()?ZERO:o;
	}
	
	public Object toJava(Object o) throws ConversionException {
		if (o == null) return isNullToZero()?ZERO:null;				
		if (!(o instanceof Number)) {		
			throw new ConversionException("conversion_java_number_expected");
		}
		return new Integer(((Number) o).intValue());
	}

	/**
	 * If <code>true</code> convert null to zero. <p>
	 * 
	 * The default value is true.
	 */
	public boolean isNullToZero() {
		return nullToZero;
	}

	/**
	 * If <code>true</code> convert null to zero. <p>
	 * 
	 * The default value is true.
	 */	
	public void setNullToZero(boolean nullToZero) {
		this.nullToZero = nullToZero;
	}
			
}
