package org.openxava.formatters;

import java.text.*;

import javax.servlet.http.*;

import org.openxava.util.*;


/**
 * Date/Time (combined) formatter with multilocale support. <p>
 *
 * @author Peter Smith
 * @author Javier Paniza
 */

public class DateTimeCombinedFormatter extends DateTimeBaseFormatter implements IFormatter {

	private static DateFormat extendedDateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	public String format(HttpServletRequest request, Object date) {
		if (date == null) return "";
		if (Dates.getYear((java.util.Date)date) < 2) return "";
		return getDateTimeFormat().format(date);
	}

	public Object parse(HttpServletRequest request, String string) throws ParseException {
		if (Is.emptyString(string)) return null;
		if (string.indexOf('-') >= 0) { // SimpleDateFormat does not work well with -
			string = Strings.change(string, "-", "/");
		}
		DateFormat [] dateFormats = getDateTimeFormats();
		for (int i=0; i<dateFormats.length; i++) {
			try {
				java.util.Date result = (java.util.Date) dateFormats[i].parseObject(string);
				return new java.sql.Timestamp( result.getTime() );
			}
			catch (ParseException ex) {
			}
		}
		throw new ParseException(XavaResources.getString("bad_date_format",string),-1);
	}

	private DateFormat getDateTimeFormat() {
		if (isExtendedFormat()) return extendedDateTimeFormat;		
		return Dates.getDateTimeFormat(); 
	}

	private DateFormat[] getDateTimeFormats() {
		if (isExtendedFormat()) return getExtendedDateTimeFormats();
		return new DateFormat [] { getDateTimeFormat() };
	}

}
