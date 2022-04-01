package org.openxava.formatters;

import javax.servlet.http.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */

public class MyReportComparatorListFormatter implements IFormatter {

	public String format(HttpServletRequest request, Object object) 	throws Exception {
		if (object == null) return "";		
		return XavaResources.getString(object.toString());
	}

	public Object parse(HttpServletRequest request, String string) throws Exception {
		return null;
	}

}
