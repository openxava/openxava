package org.openxava.formatters;

import java.text.*;
import java.time.*;
import java.time.chrono.*;
import java.time.format.*;
import java.util.*;

import javax.servlet.http.*;

import org.openxava.util.*;

public class TimeFormatter implements IFormatter {
	
	private static DateTimeFormatter zhTimeFormat = DateTimeFormatter.ofPattern("ah:mm");

	public String format(HttpServletRequest request, Object time) {
		//System.out.println("time " + time);
		if (time == null) return "";
		if (time instanceof String || time instanceof Number) return time.toString(); 
		//System.out.println(getTimeFormat().format((LocalTime) time));
		return getTimeFormat().format((LocalTime) time);
	}
	
	public Object parse(HttpServletRequest request, String string) throws ParseException {
		//System.out.println("tiem parse " + string);
		if (Is.emptyString(string)) return null;
		DateTimeFormatter timeFormat = getTimeFormat();
		try {
		return LocalTime.parse(string, timeFormat);
		} catch (Exception ex) {
		}
		throw new ParseException(XavaResources.getString("bad_time_format",string),-1);
	}
	
	private boolean isZhFormatAndJavaLessThan9() {
		return "zh_CN".equals(Locales.getCurrent().toString()) || "zh_TW".equals(Locales.getCurrent().toString()) && !XSystem.isJava9orBetter();
	}
	
	private DateTimeFormatter getTimeFormat() {
		if (isZhFormatAndJavaLessThan9()) return zhTimeFormat;
		return DateTimeFormatter.ofPattern(getTimePattern(Locales.getCurrent()));
	}
	
	private String getTimePattern(Locale locale) {
	    String pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(
	        null, FormatStyle.SHORT, Chronology.ofLocale(locale), locale);
	    return pattern;
	}
	
}
