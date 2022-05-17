package org.openxava.formatters;

import javax.servlet.http.*;

import org.jsoup.*;

/**
 * @since 5.7
 * @author Javier Paniza
 */

public class HtmlTextListFormatter implements IFormatter { 
	
	private final static int MAX_LENGHT = 200;
	private final static int TOOLTIP_MAX_LENGHT = 4000;

	public String format(HttpServletRequest request, Object object) throws Exception {
		if (object == null) return "";
		String text = Jsoup.parse(object.toString()).text();
		if (text.length() > MAX_LENGHT) {
			String content = text.substring(0, MAX_LENGHT) + "...";
			String tooltip  = text.length() > TOOLTIP_MAX_LENGHT?text.substring(0, TOOLTIP_MAX_LENGHT) + "...":text;
			return "<span title='" + tooltip + "'>" + content + "</span>";
		}
		else {
			return text;
		}
	}

	public Object parse(HttpServletRequest request, String string) throws Exception {
		return null;
	}

}
