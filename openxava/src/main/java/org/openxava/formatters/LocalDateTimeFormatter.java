package org.openxava.formatters;

import java.text.*;
import java.time.*;
import java.time.format.*;
import java.time.temporal.*;

import javax.servlet.http.*;

import org.openxava.util.*;

public class LocalDateTimeFormatter extends DateTimeBaseFormatter implements IFormatter {
	
	private static DateTimeFormatter extendedFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"); // Only for some locales like "es" and "pl"
	
	private static DateTimeFormatter [] extendedFormatters = { // Only for some locales like "es", "fr", "ca" and "pl"
			DateTimeFormatter.ofPattern("d/M/yyyy HH:mm").withResolverStyle(ResolverStyle.SMART),
			DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withResolverStyle(ResolverStyle.SMART),
			DateTimeFormatter.ofPattern("dd/MM/yy HH:mm").withResolverStyle(ResolverStyle.SMART),
			DateTimeFormatter.ofPattern("d/M/yy HH:mm").withResolverStyle(ResolverStyle.SMART),
	        DateTimeFormatter.ofPattern("ddMMyy HH:mm").withResolverStyle(ResolverStyle.SMART),
	        DateTimeFormatter.ofPattern("ddMMyyyy HH:mm").withResolverStyle(ResolverStyle.SMART),
	        DateTimeFormatter.ofPattern("dd.MM.yy HH:mm").withResolverStyle(ResolverStyle.SMART),
	        DateTimeFormatter.ofPattern("d.M.yy HH:mm").withResolverStyle(ResolverStyle.SMART),
	        DateTimeFormatter.ofPattern("d.M.yyyy HH:mm").withResolverStyle(ResolverStyle.SMART),
	        DateTimeFormatter.ofPattern("yyyy/M/d HH:mm").withResolverStyle(ResolverStyle.SMART),
	        DateTimeFormatter.ofPattern("yyyy/M/d ah:mm").withResolverStyle(ResolverStyle.SMART),
	        //DateTimeFormatter.ofPattern("yyyy/M/d ah:mm:ss").withResolverStyle(ResolverStyle.SMART),
	};
	
	private static DateTimeFormatter dotFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"); // Only for some locales like "hr"
	private static DateTimeFormatter zhFormatter = DateTimeFormatter.ofPattern("yyyy/M/d ah:mm");
	
	
	
	public String format(HttpServletRequest request, Object date) {
		if (date == null) return "";
		System.out.println("format " + date.toString());
		if (date instanceof String || date instanceof Number) {
			System.out.println("date instanceof String || date instanceof Number");
			return date.toString();
		}
		//if (isZhFormat()) return getDateTimeFormatter(false).format((LocalDateTime)date).replace("-", "/");
		//if (isZhFormatAndJavaIs21orBetter()) return getDateTimeFormatter(false).format((LocalDateTime)date).replace("p. m.", "PM").replace("a. m.", "AM"); //use java 17 blank space \u00a0
		System.out.println(getDateTimeFormatter(false).format((LocalDateTime)date)); 
		return getDateTimeFormatter(false).format((LocalDateTime)date); 
	}

	public Object parse(HttpServletRequest request, String string) throws ParseException {
		if (Is.emptyString(string)) return null;
		System.out.println("parse " + string);
		if (string.indexOf('-') >= 0 && !isDashFormat()) { // SimpleDateFormat does not work well with -
			string = Strings.change(string, "-", "/");
		}
		System.out.println(1);
		//if (isZhFormatAndJavaIs21orBetter()) string = string.replace("PM", "p. m.").replace("AM", "a. m.");
		System.out.println(2);
		DateTimeFormatter [] dateTimeFormats = getDateTimeFormats();
		
		for (int i=0; i<dateTimeFormats.length; i++) {
			try {
				TemporalAccessor parsedDate = dateTimeFormats[i].parse(string);
	            LocalDateTime result = LocalDateTime.from(parsedDate);
				System.out.println(result);
				return result;
			}
			catch (DateTimeParseException ex) {
			} 
		}
		System.out.println("parse final");
		LocalDateTime result = LocalDateTime.parse(string, getDateTimeFormatter(true));
		return result; 
	}

	private DateTimeFormatter getDateTimeFormatter(boolean b) { 
		if (isExtendedFormat()) return extendedFormatter;
		if (isDotFormat()) return dotFormatter; 
		if (isZhFormat()) return zhFormatter;
		return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(Locales.getCurrent());    
	}
	
	private DateTimeFormatter[] getDateTimeFormats() {
		if (isExtendedFormat() || isDotFormat() || isZhFormat()) return extendedFormatters;
		return new DateTimeFormatter [] { getDateTimeFormatter(true) }; 
	}
	
}