package org.openxava.converters;

import java.util.*;



import org.openxava.util.*;


/**
 * It store dates, but never save null, instead save a 1/1/1 date. <p>
 * 
 * @author Ana Andrés
 */
public class NotNullDateConverter implements IConverter{
	
	private static final Date NULL_DATE = Dates.create(1,1,1);
	
	
	public Object toJava(Object o) throws ConversionException {
		if(o == null) return null;
		if (!(o instanceof Date)) {		
			throw new ConversionException("conversion_java_utildate_expected");
		}
		Date date = new Date(((java.util.Date) o).getTime());
		return (date.compareTo(NULL_DATE) == 0) ? null : date;
	}

	public Object toDB(Object o) throws ConversionException {
		if (o == null) return Dates.toSQL(NULL_DATE);
		if (!(o instanceof Date)) {		
			throw new ConversionException("conversion_db_utildate_expected");
		}
		return Dates.toSQL((Date)o);
	}

}
