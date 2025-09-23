package org.openxava.util;

import java.text.*;
import java.time.chrono.*;
import java.time.format.*;
import java.util.*;

import org.openxava.formatters.*;
import org.openxava.model.meta.*;

/**
 * Utilities to working with dates (<tt>java.util.Date</tt>). <p>  
 * 
 * @author Javier Paniza
 * @author Peter Smith
 */
public class Dates {
	
	/**
	 * With hour to 0.
	 * If day, month and year are 0 return null.
	 * <p>
	 * The time zone is the default one, so if you print this date in the same JVM you will
	 * get the correct one. However, if you move the date object from one machine to other, or
	 * save it in a database that will be read from another locale maybe the dates will not match.
	 * </p> 
	 */	
	public static Date create(int day, int month, int year) {
		return create(day, month, year, 0, 0, 0);
	}
	
	/**
	 * If day, month and year are 0 return null.
	 * <p>
	 * The time zone is the default one, so if you print this date in the same JVM you will
	 * get the correct one. However, if you move the date object from one machine to other, or
	 * save it in a database that will be read from another locale maybe the dates will not match.
	 * </p> 
	 */	
	public static Date create(int day, int month, int year, int hourofday, int minute, int second) {
		if (day == 0 && month == 0 && year == 0) return null;
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, day, hourofday, minute, second);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();			
	}

		
	/**
	 * Current date without time. 
	 */
	public static Date createCurrent() {
		return removeTime(new java.util.Date());
	}
	
	/**
	 * Returns the day of date. <p>
	 * 
	 * If date is null return 0.
	 */
	public static int getDay(Date date) {
		if (date == null) return 0;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * Returns the year (4 digits) of date. <o> 
	 * 
	 * If date is null returns 0.
	 */
	public static int getYear(Date date) {
		if (date == null) return 0;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}
	
	/**
	 * Returns the month (1 to 12) of date. <p>
	 * 
	 * If date is null returns 0.
	 */
	public static int getMonth(Date date) {
		if (date == null) return 0;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MONTH) + 1;
	}	
	
	/**
	 * Put the day to the date. <p>
	 * 
	 * If date is null it has no effect (but no exception is thrown)
	 */
	public static void setDay(Date date, int day) {
		if (date == null) return;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, day);
		date.setTime(cal.getTimeInMillis()); 
	}
	
	/**
	 * Put the month (1 to 12) to the date. <p>
	 * 
	 * If date is null it has no effect (but no exception is thrown)
	 */
	public static void setMonth(Date date, int month) {
		if (date == null) return;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MONTH, month - 1);
		date.setTime(cal.getTimeInMillis()); 
	}
	
	/**
	 * Put the year to the date. <p>
	 * 
	 * If date is null it has no effect (but no exception is thrown)
	 */
	public static void setYear(Date date, int year) {
		if (date == null) return;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.YEAR, year);
		date.setTime(cal.getTimeInMillis()); 
	}
	
	
	/**
	 * Puts hours, minutes, seconds and milliseconds to zero. <p>
	 * 
	 * @return The same date sent as argument (a new date is not created). If null
	 * 			if sent a null is returned.
	 */
	public static Date removeTime(Date date) {
		if (date == null) return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0); 
		cal.set(Calendar.MINUTE, 0);		
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		date.setTime(cal.getTime().getTime());
		return date;
	}
	
	/**
	 * If the hours, minutes, seconds and milliseconds are zero. <p>
	 * 
	 * @return If argument is null it return false, without throwing exception.
	 * @since 4.8.1
	 */
	public static boolean hasTime(Date date) { 
		if (date == null) return false;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return 
			cal.get(Calendar.HOUR_OF_DAY) != 0 || 
			cal.get(Calendar.MINUTE) != 0 ||
			cal.get(Calendar.SECOND) != 0 ||
			cal.get(Calendar.MILLISECOND) != 0;
	}
	
	/**
	 * Returns a clone but without hours, minutes, seconds and milliseconds. <p>
	 * 
	 * @return If null if sent a null is returned.
	 */
	public static Date cloneWithoutTime(Date date) { 
		if (date == null) return null;
		Date result = (Date) date.clone();
		removeTime(result);
		return result;
	}
	
	/**
	 * Returns a clone but with 23:59:59:999 for hours, minutes, seconds and milliseconds. <p>
	 * 
	 * @return The same date sent as argument (a new date is not created). If null
	 * 			if sent a null is returned.
	 */
	public static Date cloneWith2359(Date date) { 
		if (date == null) return null;
		Date result = (Date) date.clone();		
		Calendar cal = Calendar.getInstance();
		cal.setTime(result);
		cal.set(Calendar.HOUR_OF_DAY, 23); 
		cal.set(Calendar.MINUTE, 59);		
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		result.setTime(cal.getTime().getTime());
		return result;
	}
	
	/**
	 * Returns a clone but with 999 for milliseconds. <p>
	 * 
	 * @return The same date sent as argument (a new date is not created). If null
	 * 			if sent a null is returned.
	 * @since 7.1.5
	 */
	public static java.sql.Timestamp cloneWith999(java.sql.Timestamp date) { 
		if (date == null) return null;
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.set(Calendar.MILLISECOND, 999);
	    
	    return new java.sql.Timestamp(cal.getTime().getTime());
	}
	
	/**
	 * Returns a clone but with 59 seconds and 999 milliseconds. <p>
	 * 
	 * @return The same date sent as argument (a new date is not created). If null
	 * 			if sent a null is returned.
	 * @since 7.1.6
	 */
	public static java.sql.Timestamp cloneWith59999(java.sql.Timestamp date) { 
		if (date == null) return null;
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.set(Calendar.SECOND, 59);
	    cal.set(Calendar.MILLISECOND, 999);
	    
	    return new java.sql.Timestamp(cal.getTime().getTime());
	}
	
		
	/**
	 * Creates a java.sql.Date from a java.util.Date. <p>
	 * 
	 * @param date If null returns null
	 */
	public static java.sql.Date toSQL(java.util.Date date) {
		if (date == null) return null;
		return new java.sql.Date(date.getTime());		
	}
	
	/**
	 * String with date in short format according current <i>locale</i>. <p>
	 * 
	 * Current locale is from {@link Locales#getCurrent}. <br>
	 * 
	 * @param date  If null returns empty string
	 * @return Not null
	 */
	public static String toString(java.util.Date date) {
		return DateFormat.getDateInstance(DateFormat.SHORT, Locales.getCurrent()).format(date);			
	}
	
	/**
	 * DateFormat for date + time consistently among all Java versions, according current <i>locale</i>. <p>
	 * 
	 * The date format is consistent among all Java versions. 
	 * While standard Java format differently since Java 9. 
	 * 
	 * Current locale is from {@link Locales#getCurrent}. <br>
	 * 
	 * @return Not null
	 * @since 7.4
	 */
	public static DateTimeFormatter getLocalDateTimeFormat() {
		return getLocalDateTimeFormat(Locales.getCurrent(), true);
	}
	
	/**
	 * DateFormat for date + time consistently among all Java versions, according current <i>locale</i>. <p>
	 * 
	 * The date format is consistent among all Java versions.
	 * While standard Java format differently since Java 9. 
	 * 
	 * Current locale is from {@link Locales#getCurrent}. <br>
	 * 
	 * @return Not null
	 * @since 7.4
	 */
	public static DateTimeFormatter getLocalDateTimeFormatForParsing() { 
		return getLocalDateTimeFormat(Locales.getCurrent(), false);
	}
	
	/**
	 * DateFormat for date + time consistently among all Java versions, according current <i>locale</i>. <p>
	 * 
	 * The date format is consistent among all Java versions, including Java 6, 7, 8, 9, 10 and 11. 
	 * While standard Java format differently since Java 9. 
	 * 
	 * Current locale is from {@link Locales#getCurrent}. <br>
	 * 
	 * @return Not null
	 * @since 6.0
	 */
	public static DateFormat getDateTimeFormat() {  
		// To use the Java 8 (and previous) format for Java 9 and better
		return getDateTimeFormat(Locales.getCurrent());
	}

	/**
	 * DateFormat for date + time consistently among all Java versions. <p>
	 * 
	 * The date format is consistent among all Java versions, including Java 6, 7, 8, 9, 10 and 11. 
	 * While standard Java format differently since Java 9.
	 * 
	 * Also uses always 4 digits for year no matter the locale (new in v6.5.2). 
	 * 
	 * @return Not null
	 * @since 6.0
	 */	
	public static DateFormat getDateTimeFormat(Locale locale) { 
		return getDateTimeFormat(locale, true);
	}
	
	/**
	 * DateFormat for date + time consistently among all Java versions for using in parsing. <p>
	 * 
	 * The date format is consistent among all Java versions, including Java 6, 7, 8, 9, 10 and 11. 
	 * While standard Java format differently since Java 9. 
	 * 
	 * It uses 2 or 4 digits for year depending on locale. 
	 * 
	 * @return Not null
	 * @since 6.5.2
	 */	
	public static DateFormat getDateTimeFormatForParsing(Locale locale) { 
		return getDateTimeFormat(locale, false);
	}
	
	private static DateFormat getDateTimeFormat(Locale locale, boolean fourDigitsForYear) { 
		// To use the Java 8 (and previous) format for Java 9 and better
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);
		if (df instanceof SimpleDateFormat) {
			String patternBeforeReFormat = ((SimpleDateFormat) df).toPattern();
			String pattern = locale.toString().equalsIgnoreCase("sr")?patternBeforeReFormat.substring(0,10) + (patternBeforeReFormat.substring(10, patternBeforeReFormat.length())).replace(".", ":"):patternBeforeReFormat;
			if (locale.toString().equalsIgnoreCase("zh_CN") || locale.toString().equalsIgnoreCase("nl")) fourDigitsForYear = true;
			boolean java9 = XSystem.isJava9orBetter();
			if (java9) pattern = pattern.replace(", ", " ").replace((char) 8239, (char) 32);
			if (!java9 && locale.toString().equals("es_US")) pattern = pattern.replace("M/d", "d/M"); 
			if (fourDigitsForYear && !pattern.contains("yyyy")) pattern = pattern.replace("yy", "yyyy");
			System.out.println("Dates.getDateTimeFormat() pattern=" + pattern); // tmr
			// TMR ME QUEDÉ POR AQUÍ. FALLA EN Event. CREO QUE LA SOLUCIÓN ESTÁ AQUÍ
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			if (java9) {
				DateFormatSymbols symbols = new DateFormatSymbols(locale);
		        symbols.setAmPmStrings(new String[] { "AM", "PM" });
		        sdf.setDateFormatSymbols(symbols);
			}
			df = sdf;
		}
		return df;
	}
	
	private static DateTimeFormatter getLocalDateTimeFormat(Locale locale, boolean fourDigitsForYear) {
		DateFormat df = getDateTimeFormat(locale, fourDigitsForYear);
		String pattern = "";
		if (df instanceof SimpleDateFormat) {
	        pattern = ((SimpleDateFormat) df).toPattern();
			System.out.println("Dates.getLocalDateTimeFormat() pattern=" + pattern); // tmr
	    }
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, locale);
		return formatter;
	}

	/**
	 * Date partern for short format to be use for both Date and LocalDate. <p>
	 * 
	 * It uses always 4 digits for year.
	 */
	public static String getLocalizedDatePattern(Locale locale) { 
		String pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT, null, IsoChronology.INSTANCE, locale);
		pattern = pattern.replace(". ", "."); // In order Java 25 works like previous versions for Serbian
		if (pattern.contains("yyyy")) return pattern;
		return pattern.replace("yy", "yyyy");
	}

	/**
	 * Creates a date with day, month and year of original,
	 * but with current time. <p>
	 *  
	 * @param date  It is not changed
	 * @return If arguments is null then is null
	 */
	public static java.util.Date withTime(java.util.Date date) {
		if (date == null) return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(new java.util.Date());
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		return cal.getTime();
	}
	
	/**
	 * Compares if 2 dates are equals at day, month and year
	 * level, ignoring time in comparing. 
	 * 
	 * @param f1 Can be null
	 * @param f2 Can be null
	 */
	public static boolean isDifferentDay(java.util.Date f1, java.util.Date f2) {
		if (f1 == null && f2 == null) return false;
		if (f1 == null || f2 == null) return true;
		Calendar cal = Calendar.getInstance();
		cal.setTime(f1);
		int dd1 = cal.get(Calendar.DAY_OF_MONTH);
		int mm1 = cal.get(Calendar.MONTH);
		int aa1 = cal.get(Calendar.YEAR);
		cal.setTime(f2);
		int dd2 = cal.get(Calendar.DAY_OF_MONTH);
		int mm2 = cal.get(Calendar.MONTH);
		int aa2 = cal.get(Calendar.YEAR);
		return !(aa1==aa2 && mm1==mm2 && dd1==dd2);
	}
	
	/**
	 * Difference of 2 dates in years, months and days. <p>  
	 *  
	 * @param f1  If null returns null
	 * @param f2  If null returns null 
	 */	
	public static DateDistance dateDistance(java.util.Date f1, java.util.Date f2, boolean includeStartDate ) {
		DateDistance df = new DateDistance();
		if (null == f1 || null == f2)
			return null;
		Calendar fmax = Calendar.getInstance(), fmin = Calendar.getInstance();

		f1 = Dates.removeTime(f1);
		f2 = Dates.removeTime(f2);

		if (f1.after(f2)) {
			fmax.setTime(f1);
			fmin.setTime(f2);
		} 
		else {
			fmin.setTime(f1);
			fmax.setTime(f2);
		}
        
		int initDay = fmin.get(Calendar.DATE);
		int initMonth = fmin.get(Calendar.MONTH);
		int initYear = fmin.get(Calendar.YEAR);
		int endMonth = fmax.get(Calendar.MONTH);
		int endYear = fmax.get(Calendar.YEAR);
		int finalLimit = fmax.getActualMaximum(Calendar.DATE);
		int initPeak = 0;
		int finalPeak = 0;
        
		if (initMonth == endMonth && initYear == endYear) {
			while ( fmin.getTime().before(fmax.getTime()) ) {
				fmin.add(Calendar.DATE, 1);
				df.days++;
			}

			if (includeStartDate) {
				df.days++;
			}
			if (df.days >= finalLimit) {
				df.months++;
				df.days = 0;
			}
			return df;
		}
        
		if (initDay != 1) {
			while (fmin.get(Calendar.DATE) != 1) {
				fmin.add(Calendar.DATE, 1);
				initPeak++;
			}
		}
        
		while (fmin.get(Calendar.MONTH) != endMonth
				|| fmin.get(Calendar.YEAR) != endYear) {
			fmin.add(Calendar.MONTH, 1);
			df.months++;
			if (df.months == 12) {
				df.years++;
				df.months = 0;
			}
		}
        
		while ( fmin.getTime().before(fmax.getTime()) ) {
			fmin.add(Calendar.DATE, 1);
			finalPeak++;
		}
        
		int peak = initPeak + finalPeak;
		if (includeStartDate) {
			peak++;
		}
        
		if (peak >= finalLimit) {
			peak = peak - finalLimit;
			df.months++;
			if (df.months == 12) {
				df.years++;
				df.months = 0;
			}
		}
		df.days = peak;
		return df;        
	}

	/**
	 * Difference of 2 dates in years, months and days. <p>  
	 *  
	 * @param f1  If null returns null
	 * @param f2  If null returns null 
	 */	
	public static DateDistance dateDistance(java.util.Date f1, java.util.Date f2) {
		return dateDistance(f1, f2, false);		
	}
		
	public static DateDistance addDateDistances(DateDistance dis1, DateDistance dis2) {
		DateDistance df=new DateDistance();
		if ( null == dis1 || null == dis2 ) return null;
				
		int years, months, days;
		days = dis1.days + dis2.days;
		months = days / 30;
		days = days % 30;
		
		months = months + dis1.months + dis2.months ;
		years =  months / 12 ;
		months = months % 12;		
				
		years = years + ( dis1.years + dis2.years );
		df.years = years;
		df.months=months;
		df.days=days;
				
		return df;
	}
	
	
	public static DateDistance subtractDateDistances(DateDistance dis1, DateDistance dis2) {
		DateDistance df = new DateDistance();
		if ( null == dis1 || null == dis2 ) return null;
				
		int years=0;
		int months=0;
		int days=0;
		days = dis1.days - dis2.days;
		months = dis1.months - dis2.months;
		years =dis1.years - dis2.years;
			
		if (days<0) {
			days=days+30;
			months=months-1;
		}
		if (months<0){
			months=months+12;
			years=years-1;
		}
			
		df.years = years;
		df.months = months;
		df.days = days;
				
		return df;
	}
	
	/**
	 * @param locale  Removed in 6.5
	 * @since 4.8.1
	 */
	public static String dateFormatForJSCalendar(boolean includeTime) { 		
		if (includeTime) return dateTimeFormatForJSCalendar();
		return dateFormatForJSCalendar();
	}	
	
	/**
	 * @param locale  Removed in 6.5
	 */
	public static String dateFormatForJSCalendar() { 
		Locale locale = Locales.getCurrent();
		DateFormatter df = new DateFormatter();
		String date = "hr".equals(locale.getLanguage())?"01.02.1971":df.format(null, create(1, 2, 1971)); // d, m, y
		boolean always4InYear= "es".equals(locale.getLanguage()) || "pl".equals(locale.getLanguage());
		return date.
			replaceAll("01", "d").
			replaceAll("02", "m").
			replaceAll("1971", "Y").
			replaceAll("71", always4InYear?"Y":"y").
			replaceAll("1", "j").
			replaceAll("2", "n");		
	}	
	
	/*
	 * @since 7.3
	 */
	public static String timeFormatForJSCalendar() { 
	    String dateTimeFormat = dateTimeFormatForJSCalendar();
	    int indexOfSpace = dateTimeFormat.indexOf(' ');
	    if (indexOfSpace != -1) {
	        return dateTimeFormat.substring(indexOfSpace + 1);
	    } else {
	        return "";
	    }
	}	

	/**
	 * @param locale  Removed in 6.5
	 */	
	public static String dateTimeFormatForJSCalendar() {
		Locale locale = Locales.getCurrent();
		DateTimeCombinedFormatter df = new DateTimeCombinedFormatter(); 
		String datetime = df.format(null, create(1, 2, 1971, 15, 59, 0)); // d, m, y, hr, min, sec	
		boolean always4InYear= "es".equals(locale.getLanguage()) || "pl".equals(locale.getLanguage());
		String result = datetime.
				
				// time part
				replaceAll("15", "H").	  // 24hr format 
				replaceAll("03", "G").	  // 12hr format - double digit 
				replaceAll("3", "h").	  // 12hr format - single digit
				replaceAll("59","i").	  // minute
				replaceAll("PM", "K").	  // AM/PM - uppercase
				replaceAll("pm", "K").	  // am/pm - lowercase // Not supported by the current calendar JS
				replaceAll("p.\u00A0m.", "K"). // AM/PM - In some locales (like Chinese) with Java 21 

				// date part
				replaceAll("01", "d").	// day - double digit
				replaceAll("02", "m").	// month - double digit
				replaceAll("1971", "Y").	// year - 4 digit
				replaceAll("71", always4InYear?"Y":"y"). 	// year - 2 digit 		
				replaceAll("1", "j"). 	// day - single digit
				replaceAll("2", "n").	// month - ??? seems only double digit is supported by calendar
				
				// In order Java 25 works like previous versions for Serbian
				replace("d. ", "d.").	
				replace("m. ", "m.").	
				replace("j. ", "d.").	
				replace("n. ", "m.").	
				
				// Java 21 
				replace((char) 8239, (char) 32) 
				;
		
		return result;
	}	
	

	/** 
	 * Returns number of days between startDate and endDate<p> 
	 *  
	 * @param java.util.Date startDate
	 * @param java.util.Date endDate
	 * @param boolean includeStartDate<p>
	 *  
	 */	
	public static int daysInterval (Date startDate, Date endDate,
	  		boolean includeStartDate ) {
		
		startDate = Dates.removeTime(startDate);
		Calendar start = Calendar.getInstance();
		start.setTime(startDate);		

		endDate = Dates.removeTime(endDate);		
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		
		if (includeStartDate) {
			start.add(Calendar.DATE, -1);
		}
		
		int days = 0;
		while (start.before(end)) {
			days++;
			start.add(Calendar.DATE,1);
		}
		return days;
	}	
	  
	/**
	 * Add the days to the date. <p>
	 *
	 * If date is null it has no effect (but no exception is thrown)
	 * 
	 * @since 4.8.1
	 */
	public static void addDays(Date date, int days) {
		if (date == null) return;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		date.setTime(cal.getTimeInMillis()); 
	}	
	
	/**
	 * The date of the last day of year. <p>
	 * 
	 * @param  The root date, it is not modified.
	 * @return If null if sent a null is returned.
	 * @since 4.8.1
	 */
	public static Date lastOfYear(Date date) {
		if (date == null) return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MONTH, 11);
		cal.set(Calendar.DAY_OF_MONTH, 31);
		return cal.getTime();
	}

	/**
	 * The date of the last day of month. <p>
	 * 
	 * @param  The root date, it is not modified.
	 * @return If null if sent a null is returned.
	 * @since 4.8.1
	 */
	public static Date lastOfMonth(Date date) {
		if (date == null) return null;		  
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH),
				cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}

	/**
	 * The date of the first day of month. <p>
	 * 
	 * @param  The root date, it is not modified.
	 * @return If null if sent a null is returned.
	 * @since 4.8.1
	 */
	public static Date firstOfMonth(Date date) {
		if (date == null) return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH),
				cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}	  

	
	public static class DateDistance {
		public  int days;
		public  int months;
		public  int years;
	}
	
	/**
	 * Used to verify if the entity has dates
	 * 
	 * @since 7.1
	 */
	public static boolean hasLocalDate(List<MetaProperty> listProperty) {
		List<String> dates = Arrays.asList(
								"java.time.LocalDate", 
								"java.util.Date", 
								"java.sql.Date",
								"java.sql.Timestamp",
								"java.time.LocalDateTime");
	    for (MetaProperty property : listProperty) {
	        String propertyTypeName = property.getTypeName();
	        if (dates.contains(propertyTypeName)) {
	            return true;
	        }
	    }
	    return false;
	}
	
}


