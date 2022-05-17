package org.openxava.test.formatters;

import javax.servlet.http.*;
import org.openxava.formatters.*;

/**
 * @author Javier Paniza
 */
public class CityNameFormatter implements IFormatter { 
	
	public String format(HttpServletRequest request, Object object)	throws Exception {
		if (object == null) return "";
		return object + " CITY";
	}

	public Object parse(HttpServletRequest request, String string) throws Exception {
		return string;
	}
	
}
