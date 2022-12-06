package org.openxava.formatters;

import java.text.*;

import javax.servlet.http.*;

import org.openxava.util.*;


/**
 * Date/Time (combined) formatter with multilocale support. <p>
 *
 * @author Peter Smith
 * @author Javier Paniza
 */

public class DateTimeCombinedFormatter extends DateTimeBaseFormatter implements IFormatter {

	private static DateFormat extendedDateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	private static DateFormat dotDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm"); // Only for some locales like "hr"
	
	public String format(HttpServletRequest request, Object date) {
		System.out.println("format");
		if (date == null) return "";
		if (date instanceof String || date instanceof Number) {
			System.out.println("format date.toString " + date.toString() );
			return date.toString();
		}
		if (Dates.getYear((java.util.Date)date) < 2) {
			System.out.println("Dates.getYear((java.util.Date)date) < 2");
			return "";
		}
		return getDateTimeFormat(false).format(date); 
	}

	public Object parse(HttpServletRequest request, String string) throws ParseException {
		if (Is.emptyString(string)) return null;
		if (string.indexOf('-') >= 0) { // SimpleDateFormat does not work well with -
			string = Strings.change(string, "-", "/");
		}
		DateFormat [] dateFormats = getDateTimeFormats();
		for (int i=0; i<dateFormats.length; i++) {
			try {
				java.util.Date result = (java.util.Date) dateFormats[i].parseObject(string);
				return new java.sql.Timestamp( result.getTime() );
			}
			catch (ParseException ex) {
			}
		}
		java.util.Date result = (java.util.Date) new DateFormatter().parse(request, string); 
		return new java.sql.Timestamp( result.getTime() );
	}

	private DateFormat getDateTimeFormat(boolean forParsing) { 
		System.out.println("getDateTimeFormat " + forParsing);
		System.out.println("Locales.getCurrent().getLanguage() " + Locales.getCurrent().getLanguage());
		if (isExtendedFormat()) {
			System.out.println("isExtendedFormat " + extendedDateTimeFormat);
			return extendedDateTimeFormat;
		}
		if (isDotFormat()) {
			System.out.println("isDotFormat " + dotDateFormat);
			return dotDateFormat; 
		}
		return forParsing?Dates.getDateTimeFormatForParsing(Locales.getCurrent()):Dates.getDateTimeFormat();  
	}
	
	private DateFormat[] getDateTimeFormats() {
		System.out.println("DateFormat[] getDateTimeFormat ");
		if (isExtendedFormat() || isDotFormat()) {
			System.out.println("isExtendedFormat() || isDotFormat()" + getExtendedDateTimeFormats());
			return getExtendedDateTimeFormats(); 
		}
		return new DateFormat [] { getDateTimeFormat(true) }; 
	}
	
}
