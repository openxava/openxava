package org.openxava.formatters;

import javax.servlet.http.*;

import org.jsoup.*;

/**
 * @since 5.7
 * @author Javier Paniza
 */

public class HtmlTextListFormatter implements IFormatter { 
	
	private final static int MAX_LENGHT = 200;

	public String format(HttpServletRequest request, Object object) throws Exception {
		if (object == null) return "";
		String text = Jsoup.parse(object.toString()).text();
		return text.length() > MAX_LENGHT?text.substring(0, MAX_LENGHT) + "...":text;
	}

	public Object parse(HttpServletRequest request, String string) throws Exception {
		return null;
	}

}
