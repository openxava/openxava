package org.openxava.formatters;

import java.text.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

import javax.servlet.http.*;

import org.openxava.util.*;

/**
 * Time formatter with multilocale support. <p> 
 * 
 * @since v7.4
 * @author Chungyen Tsai
 */

public class LocalDateTimeFormatter extends DateTimeBaseFormatter implements IFormatter {
	
	private static DateTimeFormatter extendedFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"); // Only for some locales like "es" and "pl"
	private static DateTimeFormatter dotFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"); // Only for some locales like "hr"
	private static DateTimeFormatter zhFormatter = DateTimeFormatter.ofPattern("yyyy/M/d ah:mm");
	
	private static DateTimeFormatter[] extendedFormatters = { 
			DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss"),
			DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"),
			DateTimeFormatter.ofPattern("ddMMyy HH:mm"),
			DateTimeFormatter.ofPattern("ddMMyy HH:mm:ss"),
			DateTimeFormatter.ofPattern("dd.MM.yy HH:mm"),
			DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss"),
			DateTimeFormatter.ofPattern("dd/MM/yy"),
			DateTimeFormatter.ofPattern("ddMMyy"),
			DateTimeFormatter.ofPattern("dd.MM.yy"),
			DateTimeFormatter.ofPattern("yyyy/M/d ah:mm")
	};
	
	public String format(HttpServletRequest request, Object date) {
		if (date == null) return "";
		if (date instanceof String || date instanceof Number) return date.toString();
		return (String) reformatLocalDateTime(getLocalDateTimeFormatter(false).format((LocalDateTime)date));
	}

	public Object parse(HttpServletRequest request, String string) throws ParseException {
		if (Is.emptyString(string)) return null;
		if (string.indexOf('-') >= 0 && !isDashFormat()) { // SimpleDateFormat does not work well with -
			string = Strings.change(string, "-", "/");
		}
		string = reformatLocalDateTime(string, true);
		DateTimeFormatter [] dateTimeFormats = getLocalDateTimeFormats();
		for (int i=0; i<dateTimeFormats.length; i++) {
			try {
				LocalDateTime result = LocalDateTime.parse(string, dateTimeFormats[i]);
	            return result;
			}
			catch (DateTimeParseException ex) {
			} 
		}
		DateTimeFormatter dateTimeFormat = Dates.getLocalDateTimeFormat();
		return LocalDateTime.parse(string, dateTimeFormat); 
	}

	private DateTimeFormatter getLocalDateTimeFormatter(boolean forParsing) { 
		if (isExtendedFormat()) return extendedFormatter;
		if (isDotFormat()) return dotFormatter; 
		if (isZhFormat()) return zhFormatter;
		return forParsing ? Dates.getLocalDateTimeFormatForParsing() : Dates.getLocalDateTimeFormat();    
	}
	
	private DateTimeFormatter[] getLocalDateTimeFormats() {
		if (isExtendedFormat() || isDotFormat() || isZhFormat()) return extendedFormatters;
		return new DateTimeFormatter[] { getLocalDateTimeFormatter(false) }; 
	}
	
	private String reformatLocalDateTime(String string) {
		return reformatLocalDateTime(string, false);
	}
			
	private String reformatLocalDateTime(String string, boolean parsing) {
		String date = string;
		LocalTime specificTime = LocalTime.of(15, 0);
		Locale locale = isZhFormat()?Locale.getDefault():Locales.getCurrent(); // In order the above trick to fix Chinese AM/PM works
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("a", locale);     
        String formattedTimePM = specificTime.format(timeFormat);
        String formattedTimeAM;
        if (Character.isUpperCase(formattedTimePM.charAt(0))) {
        	formattedTimeAM = formattedTimePM.replace("P", "A");
        } else {
        	formattedTimeAM = formattedTimePM.replace("p", "a"); 
        }
		if (parsing) return date.replace("PM", formattedTimePM).replace("AM", formattedTimeAM);		 
		else return date.replace(formattedTimePM, "PM").replace(formattedTimeAM, "AM");		        
	}
	
}