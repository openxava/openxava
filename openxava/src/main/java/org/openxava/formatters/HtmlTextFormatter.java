package org.openxava.formatters;

import javax.servlet.http.*;

/**
 * 
 * @since 7.0.1
 * @author Javier Paniza
 */

public class HtmlTextFormatter implements IFormatter { 
	
	private final static int MAX_LENGHT = 200;
	private final static int TOOLTIP_MAX_LENGHT = 4000;

	public String format(HttpServletRequest request, Object object) throws Exception {
		if (!(object instanceof String)) return "";
		String html = (String) object;
		return html.replace("&lt;", "&lt;<!-- -->");
	}

	public Object parse(HttpServletRequest request, String html) throws Exception {
		if (html == null) return "";
		return html.replace("&lt;<!-- -->", "&lt;");
	}

}
