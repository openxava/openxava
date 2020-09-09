package org.openxava.formatters;

import java.text.*;
import java.time.*;
import java.time.format.*;
import javax.servlet.http.*;
import org.openxava.util.*;

/**
 * LocalDate formatter with multilocale support. <p>
 * 
 * Although it does some refinement for Spanish, Catalan, Polish, Croatian and French, 
 * it supports formatting on locale basis.<br>
 *  
 * @since 6.1 
 * @author Javier Paniza
 */

public class LocalDateFormatter implements IFormatter {
	
	private static DateTimeFormatter extendedFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Only for some locales like "es" and "pl"
	
	private static DateTimeFormatter [] extendedFormatters = { // Only for some locales like "es", "fr", "ca" and "pl"
		DateTimeFormatter.ofPattern("d/M/yyyy").withResolverStyle(ResolverStyle.SMART),
		DateTimeFormatter.ofPattern("d/M/yy").withResolverStyle(ResolverStyle.SMART), 
		DateTimeFormatter.ofPattern("ddMMyy").withResolverStyle(ResolverStyle.SMART),
		DateTimeFormatter.ofPattern("ddMMyyyy").withResolverStyle(ResolverStyle.SMART),
		DateTimeFormatter.ofPattern("d.M.yy").withResolverStyle(ResolverStyle.SMART),
		DateTimeFormatter.ofPattern("d.M.yyyy").withResolverStyle(ResolverStyle.SMART)
	};
	
	private static DateTimeFormatter dotFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy"); // Only for some locales like "hr"
	
	public String format(HttpServletRequest request, Object date) {
		if (date == null) return "";
		if (date instanceof String || date instanceof Number) return date.toString(); 
		if (((java.time.LocalDate)date).getYear() < 2) return "";
		return getFormatter().format((LocalDate) date);
	}
		
	public Object parse(HttpServletRequest request, String string) throws java.text.ParseException {
		if (Is.emptyString(string)) return null;				
		if (isExtendedFormat()) { 
			if (string.indexOf('-') >= 0) { 
				string = Strings.change(string, "-", "/");
			}		
		}
		DateTimeFormatter [] formatters = getFormatters(); 
		for (int i=0; i<formatters.length; i++) {
			try {
				return LocalDate.parse(string, formatters[i]);
			}
			catch (DateTimeParseException ex) {
			}						
		}
		throw new java.text.ParseException(XavaResources.getString("bad_date_format",string),-1);
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
	
	private DateTimeFormatter getFormatter() {
		if (isExtendedFormat()) return extendedFormatter;
		if (isDotFormat()) return dotFormatter; 
		return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locales.getCurrent());		
	}
	
	private DateTimeFormatter[] getFormatters() {
		//if (isExtendedFormat()) return extendedFormatters;
		if (isExtendedFormat() || isDotFormat()) return extendedFormatters; 
		return new DateTimeFormatter [] { getFormatter() };
	}

}
