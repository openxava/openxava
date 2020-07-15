/*
 * Created on 27/10/2006
 * @author Miguel Angel Embuena
 */
package org.openxava.formatters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
 
public class StringTimeFormatter extends TimeBaseFormatter {
	private static final DateFormat format5 = new SimpleDateFormat("HH:mm");
	
	public Object parse(HttpServletRequest request, String string) throws ParseException {		
		TimeData timeData = (TimeData)super.parse(request, string);
		if (timeData == null) return null; 
		
//		format5 (HH:mm) because  in the DB we generally have a String with hours and minutes only, 
//		so we cut the unused chars in order to prevent data truncation errors
		return format5.format(timeData.time());
	}

}
