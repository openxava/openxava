package org.openxava.converters;

import java.util.*;

/**
 * In java a <tt>java.util.Date</tt> and in database a
 * <tt>java.sql.Date</tt>. <p>
 * 
 * @author Javier Paniza
 */
public class DateUtilSQLConverter implements IConverter {

	
	
	public Object toDB(Object o) throws ConversionException {
		if (o == null) return null;		
		if (!(o instanceof java.util.Date)) {		
			throw new ConversionException("conversion_db_utildate_expected");
		}
		return new java.sql.Date(((Date)o).getTime());
	}
	
	public Object toJava(Object o) throws ConversionException {
		if (o == null) return null;
		if (o instanceof Number || o instanceof String) return o; 
		if (!(o instanceof java.util.Date)) { // java.util.Date is more tolerant that java.sql.Date in this case		
			throw new ConversionException("conversion_java_sqldate_expected");
		}
		return new Date(((java.util.Date) o).getTime());
	}

}
