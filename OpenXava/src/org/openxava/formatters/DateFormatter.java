package org.openxava.formatters;

import java.text.*;

import javax.servlet.http.*;

import org.openxava.util.*;

/**
 * Date formatter with multilocale support. <p>
 * 
 * Although it does some refinement in Spanish, Catalan, Polish, Croatian and French case, 
 * it support formatting on locale basis.<br>
 *  
 * @author Javier Paniza
 */

public class DateFormatter implements IFormatter {
	
	private static DateFormat extendedDateFormat = new SimpleDateFormat("dd/MM/yyyy"); // Only for some locales like "es" and "pl"
	
	private static DateFormat [] extendedDateFormats = { // Only for some locales like "es", "fr", "ca" and "pl"
		new SimpleDateFormat("dd/MM/yy"), 
		new SimpleDateFormat("ddMMyy"),
		new SimpleDateFormat("dd.MM.yy")				
	};

	private static DateFormat dotDateFormat = new SimpleDateFormat("dd.MM.yyyy"); // Only for some locales like "hr"
	
	public String format(HttpServletRequest request, Object date) {
		if (date == null) return "";
		if (date instanceof String || date instanceof Number) return date.toString(); 
		if (Dates.getYear((java.util.Date)date) < 2) return "";
		return getDateFormat().format(date);
	}
		
	public Object parse(HttpServletRequest request, String string) throws ParseException {
		if (Is.emptyString(string)) return null;				
		if (isExtendedFormat()) { 
			if (string.indexOf('-') >= 0) { // SimpleDateFormat does not work well with -
				string = Strings.change(string, "-", "/");
			}		
		}
		DateFormat [] dateFormats = getDateFormats(); 
		for (int i=0; i<dateFormats.length; i++) {
			try {
				dateFormats[i].setLenient(false);
				return dateFormats[i].parseObject(string);
			}
			catch (ParseException ex) {
			}						
		}
		throw new ParseException(XavaResources.getString("bad_date_format",string),-1);
	}
	
	private boolean isExtendedFormat() {
		return "es".equals(Locales.getCurrent().getLanguage()) ||
			"ca".equals(Locales.getCurrent().getLanguage()) || 
			"pl".equals(Locales.getCurrent().getLanguage()) ||
			"fr".equals(Locales.getCurrent().getLanguage());
	}
	
	private boolean isDotFormat() { 
		return "hr".equals(Locales.getCurrent().getLanguage());
	}	
	
	private DateFormat getDateFormat() {
		if (isExtendedFormat()) return extendedDateFormat;
		if (isDotFormat()) return dotDateFormat; 
		return DateFormat.getDateInstance(DateFormat.SHORT, Locales.getCurrent());		
	}
	
	private DateFormat[] getDateFormats() {
		if (isExtendedFormat() || isDotFormat()) return extendedDateFormats; 
		return new DateFormat [] { getDateFormat() };
	}
		
}
