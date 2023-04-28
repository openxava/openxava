package org.openxava.formatters;

import javax.servlet.http.*;

/**
 * tmr Quitarlo
 * @since 7.0.1
 * @author Javier Paniza
 */

public class HtmlTextFormatter implements IFormatter { 
	
	private final static int MAX_LENGHT = 200;
	private final static int TOOLTIP_MAX_LENGHT = 4000;

	public String format(HttpServletRequest request, Object object) throws Exception {
		if (!(object instanceof String)) return "";
		String html = (String) object;
		// tmr return html.replace("&lt;", "&lt;<!-- -->");
		return html; // tmr
	}

	public Object parse(HttpServletRequest request, String html) throws Exception {
		if (html == null) return "";
		// tmr return html.replace("&lt;<!-- -->", "&lt;");
		return html; // tmr .replace("&lt;<!-- -->", "&lt;");
	}

}
