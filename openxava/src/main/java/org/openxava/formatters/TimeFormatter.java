package org.openxava.formatters;

import java.text.*;
import java.time.*;
import java.time.chrono.*;
import java.time.format.*;
import java.util.*;

import javax.servlet.http.*;

import org.openxava.util.*;

/**
 * Time formatter with multilocale support. <p> 
 * 
 * @author Chungyen Tsai
 */

public class TimeFormatter implements IFormatter {
	
	private static DateTimeFormatter zhTimeFormat = DateTimeFormatter.ofPattern("ah:mm");

	public String format(HttpServletRequest request, Object time) { 
		if (time == null) return "";
		if (time instanceof String || time instanceof Number) return time.toString();
		String formattedTime = getTimeFormat().format((LocalTime) time);
		if (XSystem.isJava17orBetter()) return getTimeFormat().format((LocalTime) time).replace("p. m.", "PM").replace("a. m.", "AM"); //use java 17 blank space \u00a0
		if (XSystem.isJava9orBetter()) return getTimeFormat().format((LocalTime) time).replace("p. m.", "PM").replace("a. m.", "AM"); //use normal blank space \u0020
		return formattedTime;
	}
	
	public Object parse(HttpServletRequest request, String string) throws ParseException {
		if (Is.emptyString(string)) return null;
		if (XSystem.isJava17orBetter() && isZhFormat()) {
			string = string.replace("p. m.", "PM").replace("a. m.", "AM"); 
		} else if (XSystem.isJava9orBetter() && isZhFormat()) {
			string = string.replace("p. m.", "PM").replace("a. m.", "AM");
		}
		DateTimeFormatter timeFormat = getTimeFormat();
		try {
			return LocalTime.parse(string, timeFormat);
		} catch (Exception ex) {
		}
		throw new ParseException(XavaResources.getString("bad_time_format",string),-1);
	}
	
	private boolean isZhFormat() {
		return "zh_CN".equals(Locales.getCurrent().toString()) || "zh_TW".equals(Locales.getCurrent().toString());
	}
	
	private DateTimeFormatter getTimeFormat() {
		if (isZhFormat()) return zhTimeFormat;
		return DateTimeFormatter.ofPattern(getTimePattern(Locales.getCurrent()));
	}
	
	private String getTimePattern(Locale locale) {
	    String pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(
	        null, FormatStyle.SHORT, Chronology.ofLocale(locale), locale);
	    return pattern;
	}
	
}
