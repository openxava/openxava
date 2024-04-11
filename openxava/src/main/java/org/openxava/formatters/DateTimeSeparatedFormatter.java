package org.openxava.formatters;

import java.text.*;

import javax.servlet.http.*;

import org.openxava.util.*;

/**
 * Date and time formatter with multilocale support. <p>
 *  
 * @author Jose Luis Santiago
 * @author Javier Paniza
 */

public class DateTimeSeparatedFormatter extends DateTimeBaseFormatter implements IMultipleValuesFormatter {
	
	private static DateFormat extendedDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private static DateFormat dotDateFormat = new SimpleDateFormat("dd.MM.yyyy"); // Only for some locales like "hr"
	private static DateFormat zhDateFormat = new SimpleDateFormat("yyyy/M/d");
	
	private static DateFormat [] zhDateFormats = {
			new SimpleDateFormat("yyyy/M/d ah:mm"),
			new SimpleDateFormat("yyyy/M/d a h:mm")
	};
	
	public String [] format(HttpServletRequest request, Object date) throws Exception {
        String[] result = new String[2];
        result[0] = "";
        result[1] = "";
		if (date == null) return result;		
		result[0] = getDateFormat().format(date);
		result[1] = DateFormat.getTimeInstance(DateFormat.SHORT, Locales.getCurrent()).format(date);
		//java 8 sr locale
		result[1] = Locales.getCurrent().getLanguage().equalsIgnoreCase("sr")?result[1].replace(".", ":"):result[1];
		result[1] = result[1].replace((char) 8239, (char) 32); // For Java 21
		if (isZhFormat()) result[1] = result[1].replace("\u4E0A\u5348", "AM").replace("\u4E0B\u5348", "PM");
		return result;
	}

	public Object parse(HttpServletRequest request, String [] strings) throws Exception {
		if( strings == null || strings.length < 2 ) return null;
		if( Is.emptyString(strings[0])) return null;
		String fDate = strings[0];
		String fTime = strings[1];
		if (isZhFormat()) fTime = fTime.replace("\u4E0A\u5348", "AM").replace("\u4E0B\u5348", "PM"); //for zh in dateTime editor
		String dateTime = fDate + " " + fTime; 
		if (dateTime.indexOf('-') >= 0) {
			dateTime = Strings.change(dateTime, "-", "/");
		}
		DateFormat [] dateFormats = getDateTimeFormats();
		for (int i=0; i < dateFormats.length; i++) {
			try {
				java.util.Date result =  (java.util.Date) dateFormats[i].parseObject(dateTime);
				return new java.sql.Timestamp( result.getTime() );
			}
			catch (ParseException ex) {				
			}						
		}
		throw new ParseException(XavaResources.getString("bad_date_format",dateTime),-1);
	}
	
	private DateFormat getDateFormat() {
		if (isExtendedFormat())	return extendedDateFormat;		
		if (isDotFormat()) return dotDateFormat; 
		if (isZhFormatAndJavaLessThan9()) return zhDateFormat;
		return new SimpleDateFormat(Dates.getLocalizedDatePattern(Locales.getCurrent())); 
	}
	
	private DateFormat[] getDateTimeFormats() {
		if (isExtendedFormat() || isDotFormat()) return getExtendedDateTimeFormats();
		if (isZhFormatAndJavaLessThan9()) return zhDateFormats;
		return new DateFormat [] { Dates.getDateTimeFormatForParsing(Locales.getCurrent())}; 
	}
}
