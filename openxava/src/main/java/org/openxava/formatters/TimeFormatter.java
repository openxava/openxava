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
		return reformatTime(getTimeFormat().format((LocalTime) time));
	}
	
	public Object parse(HttpServletRequest request, String string) throws ParseException {
		if (Is.emptyString(string)) return null;
		string = reformatTime(string, true);
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
	
	protected String reformatTime(String string) {
		return reformatTime(string, false);
	}
	
	protected String reformatTime(String string, boolean parsing) {
		String date = string;
		if (parsing) {
			if (XSystem.isJava21orBetter()) {
				date = date.replace(" PM", "\u202fPM").replace(" AM", "\u202fAM");
				date = date.replace("PM ", "PM\u202f").replace("AM ", "AM\u202f");
				return date;
			}
			if (XSystem.isJava17orBetter()) return date.replace("PM", "p.\u00a0m.").replace("AM", "a.\u00a0m.");
			if (XSystem.isJava9orBetter()) return date.replace("PM", "p.m.").replace("AM", "a.m.");
		} else {
			if (XSystem.isJava17orBetter()) return date.replace("p.\u00a0m.", "PM").replace("a.\u00a0m.", "AM");
			if (XSystem.isJava9orBetter()) return date.replace("p.m.", "PM").replace("a.m.", "AM");
		}
		return date;
	}
	
}
