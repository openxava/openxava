package org.openxava.formatters;

import java.text.*;
import java.time.*;
import java.time.format.*;

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
			DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss").withResolverStyle(ResolverStyle.SMART),
			DateTimeFormatter.ofPattern("dd/MM/yy HH:mm").withResolverStyle(ResolverStyle.SMART),
			DateTimeFormatter.ofPattern("ddMMyy HH:mm").withResolverStyle(ResolverStyle.SMART),
			DateTimeFormatter.ofPattern("ddMMyy HH:mm:ss").withResolverStyle(ResolverStyle.SMART),
			DateTimeFormatter.ofPattern("dd.MM.yy HH:mm").withResolverStyle(ResolverStyle.SMART),
			DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss").withResolverStyle(ResolverStyle.SMART),
			DateTimeFormatter.ofPattern("dd/MM/yy").withResolverStyle(ResolverStyle.SMART),
			DateTimeFormatter.ofPattern("ddMMyy").withResolverStyle(ResolverStyle.SMART),
			DateTimeFormatter.ofPattern("dd.MM.yy").withResolverStyle(ResolverStyle.SMART),
			DateTimeFormatter.ofPattern("yyyy/M/d ah:mm").withResolverStyle(ResolverStyle.SMART),
	};
	
	public String format(HttpServletRequest request, Object date) {
		if (date == null) return "";
		if (date instanceof String || date instanceof Number) return date.toString();
		return getLocalDateTimeFormatter(false).format((LocalDateTime)date);
	}

	public Object parse(HttpServletRequest request, String string) throws ParseException {
		if (Is.emptyString(string)) return null;
		if (string.indexOf('-') >= 0 && !isDashFormat()) { // SimpleDateFormat does not work well with -
			string = Strings.change(string, "-", "/");
		}
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
	
}