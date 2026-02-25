package org.openxava.test.formatters;

import javax.servlet.http.*;

import org.openxava.formatters.*;

/**
 * Formatter that truncates descriptions to 10 characters adding "..." if longer.
 * 
 * @author Javier Paniza
 * @since 7.7
 */
public class TruncateDescriptionsFormatter implements IFormatter {
	
	private static final int MAX_LENGTH = 10;

	@Override
	public String format(HttpServletRequest request, Object object) throws Exception {
		if (object == null) return "";
		String value = object.toString();
		if (value.length() > MAX_LENGTH) {
			return value.substring(0, MAX_LENGTH) + "...";
		}
		return value;
	}

	@Override
	public Object parse(HttpServletRequest request, String string) throws Exception {
		return string;
	}

}
