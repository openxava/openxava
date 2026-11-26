package org.openxava.formatters;

import jakarta.servlet.http.*;

import org.openxava.util.*;

/**
 * @author Javier Paniza
 */
public class IconListFormatter extends BaseFormatter  {

	public String format(HttpServletRequest request, Object object) throws Exception {
		if (object == null) return "";
		String icon = object.toString();
		icon = MDIIconMapper.map(icon);
		return "<span class='ox-icon-in-list'><i class='mdi mdi-" + icon + "'></i></span>";
	}

	public Object parse(HttpServletRequest request, String string) throws Exception {
		return null;
	}
	
}
