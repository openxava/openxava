package org.openxava.formatters;

import java.text.*;
import java.time.*;
import java.time.format.*;

import javax.servlet.http.*;

import org.openxava.util.*;

public class LocalDateTimeFormatter extends DateTimeBaseFormatter implements IFormatter {
	
	private static DateTimeFormatter extendedFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"); // Only for some locales like "es" and "pl"
	
	private static DateTimeFormatter [] extendedFormatters = { // Only for some locales like "es", "fr", "ca" and "pl"
			DateTimeFormatter.ofPattern("d/M/yyyy HH:mm").withResolverStyle(ResolverStyle.SMART),
	        DateTimeFormatter.ofPattern("d/M/yy HH:mm").withResolverStyle(ResolverStyle.SMART),
	        DateTimeFormatter.ofPattern("ddMMyy HH:mm").withResolverStyle(ResolverStyle.SMART),
	        DateTimeFormatter.ofPattern("ddMMyyyy HH:mm").withResolverStyle(ResolverStyle.SMART),
	        DateTimeFormatter.ofPattern("d.M.yy HH:mm").withResolverStyle(ResolverStyle.SMART),
	        DateTimeFormatter.ofPattern("d.M.yyyy HH:mm").withResolverStyle(ResolverStyle.SMART),
	        DateTimeFormatter.ofPattern("yyyy/M/d HH:mm").withResolverStyle(ResolverStyle.SMART)
	};
	
	private static DateTimeFormatter dotFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"); // Only for some locales like "hr"
	private static DateTimeFormatter zhFormatter = DateTimeFormatter.ofPattern("yyyy/M/d ah:mm");
	
	
	
	public String format(HttpServletRequest request, Object date) {
		if (date == null) return "";
		System.out.println("format " + date.toString());
		if (date instanceof String || date instanceof Number) return date.toString();
		System.out.println(1);
		System.out.println(date instanceof java.time.LocalDateTime);
		//if (((java.time.LocalDate)date).getYear() < 2) return "";
		//if (date instanceof java.util.Date && Dates.getYear((java.util.Date)date) < 2) return ""; 
		System.out.println(2);
		if (isZhFormatAndJavaIs21orBetter()) return getDateTimeFormatter(false).format((LocalDateTime)date).replace("p. m.", "PM").replace("a. m.", "AM"); //use java 17 blank space \u00a0
		/*
		System.out.println(3);
		Locale currentLocale = Locales.getCurrent();
		System.out.println(currentLocale);
		DateTimeFormatter formatter1 =DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(currentLocale);
		DateTimeFormatter formatter2 =DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(currentLocale);
		DateTimeFormatter formatter3 =DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG).withLocale(currentLocale);
		DateTimeFormatter formatter4 =DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).withLocale(currentLocale);
		LocalDateTime now = LocalDateTime.now();
		System.out.println("Fecha y hora formateada1: " + formatter1.format(now));
		System.out.println("Fecha y hora formateada2: " + formatter2.format(now));
		System.out.println("Fecha y hora formateada3: " + formatter3.format(now));
		System.out.println("Fecha y hora formateada4: " + formatter4.format(now));
		*/
		return getDateTimeFormatter(false).format((LocalDateTime)date); 
	}

	public Object parse(HttpServletRequest request, String string) throws ParseException {
		if (Is.emptyString(string)) return null;
		return null;
		/*
		System.out.println("parse " + string);
		if (string.indexOf('-') >= 0 && !isDashFormat()) { // SimpleDateFormat does not work well with -
			string = Strings.change(string, "-", "/");
		}
		System.out.println(1);
		if (isZhFormatAndJavaIs21orBetter()) string = string.replace("PM", "p. m.").replace("AM", "a. m.");
		System.out.println(2);
		DateTimeFormatter [] dateTimeFormats = getDateTimeFormats();
		for (int i=0; i<dateTimeFormats.length; i++) {
			try {
				java.util.Date result = (java.util.Date) dateTimeFormats[i].parseObject(string);
				return new java.sql.Timestamp( result.getTime() );
			}
			catch (ParseException ex) {
			} 
		}
		System.out.println(3);
		java.util.Date result = (java.util.Date) new DateFormatter().parse(request, string);
		return new java.sql.Timestamp( result.getTime() );*/
	}

	private DateTimeFormatter getDateTimeFormatter(boolean b) { 
		if (isExtendedFormat()) return extendedFormatter;
		if (isDotFormat()) return dotFormatter; 
		if (isZhFormatAndJavaIs21orBetter()) return zhFormatter;
		if (isZhFormatAndJavaLessThan9()) return zhFormatter;
		System.out.println(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(Locales.getCurrent()));
		System.out.println(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(Locales.getCurrent()).withResolverStyle(ResolverStyle.SMART));
		return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(Locales.getCurrent());    
	}
	
	private DateTimeFormatter[] getDateTimeFormats() {
		if (isExtendedFormat() || isDotFormat() || isZhFormatAndJavaIs21orBetter()) return extendedFormatters;
		return new DateTimeFormatter [] { getDateTimeFormatter(true) }; 
	}
	
}