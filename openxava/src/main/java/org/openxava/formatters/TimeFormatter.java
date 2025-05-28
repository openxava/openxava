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
	
	// ENBLISH to have always AM/PM, with Chinese sometimes is AM/PM sometimes Chinese symbols
	private static DateTimeFormatter zhTimeFormat = DateTimeFormatter.ofPattern("ah:mm", Locale.ENGLISH); 

	public String format(HttpServletRequest request, Object time) {
		if (time == null) return "";
		if (time instanceof String || time instanceof Number) return time.toString();
		// tmr ini
		String stime = getTimeFormat().format((LocalTime) time);
		System.out.println("[TimeFormatter.format] stime=" + stime); // tmr
		String result = reformatTime(stime);
		System.out.println("[TimeFormatter.format] result=" + result); // tmr
		return result;
		// tmr fin
		// tmr return reformatTime(getTimeFormat().format((LocalTime) time));
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
		return DateTimeFormatter.ofPattern(getTimePattern(Locales.getCurrent()), Locales.getCurrent());
	}
	
	private String getTimePattern(Locale locale) {
	    String pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(
	        null, FormatStyle.SHORT, Chronology.ofLocale(locale), locale);
	    return pattern.replace((char) 8239, (char) 32); // The .replace() is for Java 21
	}
	
	protected String reformatTime(String string) {
		return reformatTime(string, false);
	}
	
	protected String reformatTime(String string, boolean parsing) {
		String date = string;
		LocalTime specificTime = LocalTime.of(15, 0);
		Locale locale = isZhFormat()?Locale.ENGLISH:Locales.getCurrent();
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("a", locale);
        String formattedTimePM = specificTime.format(timeFormat);
        String formattedTimeAM;
        if (Character.isUpperCase(formattedTimePM.charAt(0))) {
        	formattedTimeAM = formattedTimePM.replace("P", "A");
        } else {
        	//formattedTimeAM = formattedTimePM.replace("\u0070", "\u0061");
        	formattedTimeAM = formattedTimePM.replace("p", "a"); 
        }
        System.out.println("[TimeFormatter.reformatTime] formattedTimeAM=" + formattedTimeAM); // tmr
        System.out.println("[TimeFormatter.reformatTime] formattedTimePM=" + formattedTimePM); // tmr
		if (parsing) {
	        String unicode = toUnicodeString(formattedTimePM);
	        boolean hasNonBreakingSpace = unicode.contains("\\u00A0");
			if (hasNonBreakingSpace) return date.replace("PM", "p.\u00a0m.").replace("AM", "a.\u00a0m.");
			// tmr if (XSystem.isJava9orBetter()) return date.replace("PM", formattedTimePM).replace("AM", formattedTimeAM);
			return date.replace("PM", formattedTimePM).replace("AM", formattedTimeAM); // tmr
		} else {
			// tmr if (XSystem.isJava9orBetter()) return date.replace(formattedTimePM, "PM").replace(formattedTimeAM, "AM");
			return date.replace(formattedTimePM, "PM").replace(formattedTimeAM, "AM"); // tmr
		}
		// tmr return date;
	}
	
    private static String toUnicodeString(String text) {
        StringBuilder unicodeBuilder = new StringBuilder();
        for (char c : text.toCharArray()) {
            unicodeBuilder.append(String.format("\\u%04X", (int) c));
        }
        return unicodeBuilder.toString();
    }
	
}
