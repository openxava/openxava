package org.openxava.formatters;

import javax.servlet.http.*;

import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza 
 */
public class WebURLFormatter implements IFormatter {

	public String format(HttpServletRequest request, Object object) throws Exception {
		if (Is.empty(object)) return "";
		return "<a href='" + object + "'>" + object + "</a>";
	}

	public Object parse(HttpServletRequest request, String string) throws Exception {
		// Because it is only used for lists
		return null;
	}

}
