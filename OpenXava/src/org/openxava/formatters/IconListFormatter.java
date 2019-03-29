package org.openxava.formatters;

import javax.servlet.http.*;

import org.openxava.web.style.*;

/**
 * @author Javier Paniza
 */
public class IconListFormatter extends BaseFormatter  {

	public String format(HttpServletRequest request, Object object) throws Exception {
		if (object == null) return "";
		Style style = (Style) request.getAttribute("style"); 
		return "<span class='" + style.getIconInList() + "'><i class='mdi mdi-" + object + "'></i></span>";
	}

	public Object parse(HttpServletRequest request, String string) throws Exception {
		return null;
	}
	
}
