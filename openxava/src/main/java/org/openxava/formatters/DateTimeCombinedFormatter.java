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
	private static DateFormat zhDateFormat = new SimpleDateFormat("yyyy/M/d ah:mm");
	
	public String format(HttpServletRequest request, Object date) {
		if (date == null) return "";
		if (date instanceof String || date instanceof Number) return date.toString();
		if (Dates.getYear((java.util.Date)date) < 2) return "";
		if (isZhFormatAndJavaIs21orBetter()) return getDateTimeFormat(false).format(date).replace("p.\u00a0m.", "PM").replace("a.\u00a0m.", "AM"); //use java 17 blank space \u00a0
		return getDateTimeFormat(false).format(date); 
	}

	public Object parse(HttpServletRequest request, String string) throws ParseException {
		if (Is.emptyString(string)) return null;
		if (string.indexOf('-') >= 0 && !isDashFormat()) { // SimpleDateFormat does not work well with -
			string = Strings.change(string, "-", "/");
		}
		if (isZhFormatAndJavaIs21orBetter()) string = string.replace("PM", "p.\u00a0m.").replace("AM", "a.\u00a0m.");
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
		if (isExtendedFormat()) return extendedDateTimeFormat;
		if (isDotFormat()) return dotDateFormat; 
		if (isZhFormatAndJavaIs21orBetter()) return zhDateFormat;
		if (isZhFormatAndJavaLessThan9()) return zhDateFormat;
		return forParsing?Dates.getDateTimeFormatForParsing(Locales.getCurrent()):Dates.getDateTimeFormat();  
	}
	
	private DateFormat[] getDateTimeFormats() {
		if (isExtendedFormat() || isDotFormat() || isZhFormatAndJavaIs21orBetter()) return getExtendedDateTimeFormats();
		return new DateFormat [] { getDateTimeFormat(true) }; 
	}
	
}
