/*
 * Created on 27/10/2006
 * @author Miguel Angel Embuena
 */
package org.openxava.formatters;

import java.text.ParseException;
import javax.servlet.http.HttpServletRequest;

public class SQLTimeFormatter extends TimeBaseFormatter {
	
	public Object parse(HttpServletRequest request, String string) throws ParseException {		
		TimeData timeData = (TimeData)super.parse(request, string);
		if (timeData == null) return null; 
		return new java.sql.Time(timeData.millis());
	}

}
