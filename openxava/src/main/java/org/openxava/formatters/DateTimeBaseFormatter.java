package org.openxava.formatters;

import java.text.*;

import org.openxava.util.*;

/**
 * Common code for DateTime formatters. <p>
 * 
 * @author Javier Paniza
 */

abstract public class DateTimeBaseFormatter {
		
	private static DateFormat [] extendedDateTimeFormats = {
		new SimpleDateFormat("dd/MM/yy HH:mm:ss"),
		new SimpleDateFormat("dd/MM/yy HH:mm"),
		new SimpleDateFormat("ddMMyy HH:mm"),
		new SimpleDateFormat("ddMMyy HH:mm:ss"),
		new SimpleDateFormat("dd.MM.yy HH:mm"),		
		new SimpleDateFormat("dd.MM.yy HH:mm:ss"),
		new SimpleDateFormat("yyyy/M/d ah:mm"),
		new SimpleDateFormat("dd/MM/yy"),		
		new SimpleDateFormat("ddMMyy"),		
		new SimpleDateFormat("dd.MM.yy")
	};	
	
	protected DateFormat [] getExtendedDateTimeFormats() {
		return extendedDateTimeFormats;
	}
	
	protected boolean isExtendedFormat() {
		if ("es".equals(Locales.getCurrent().getLanguage())) {
			String country = Locales.getCurrent().getCountry();
			// Exclude United States (US) and Puerto Rico (PR) for Spanish language
			if ("US".equals(country) || "PR".equals(country)) {
				return false;
			}
			return true;
		}
		return "ca".equals(Locales.getCurrent().getLanguage()) || 
			"pl".equals(Locales.getCurrent().getLanguage()) ||
			"fr".equals(Locales.getCurrent().getLanguage());
	}
	
	protected boolean isDashFormat() {
		return "nl".equals(Locales.getCurrent().getLanguage());
	}
	
	protected boolean isZhFormatAndJavaLessThan9() {
		return isZhFormat() && !XSystem.isJava9orBetter();
	}
	
	protected boolean isZhFormatAndJavaIs21orBetter() {
		return isZhFormat() && XSystem.isJava21orBetter();
	}
	
	protected boolean isZhFormat() {
		return ("zh_CN".equals(Locales.getCurrent().toString()) || "zh_TW".equals(Locales.getCurrent().toString()));
	}
	
	protected boolean isDotFormat() { 
		return "hr".equals(Locales.getCurrent().getLanguage());
	}

	
	private static String dateTimeSeparator = XSystem.isJava9orBetter()?", ":" ";
	protected String getDateTimeSeparator() {
		return dateTimeSeparator;
	}
			
}
