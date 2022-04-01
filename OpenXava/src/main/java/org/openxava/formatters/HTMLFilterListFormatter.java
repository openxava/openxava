package org.openxava.formatters;

import javax.servlet.http.HttpServletRequest;

import org.openxava.formatters.IFormatter;
import org.openxava.util.Is;

/**
 * Useful for XSS protection.
 *  
 * @since 5.2.1 
 * @author Aldo Migliau
 */
public class HTMLFilterListFormatter implements IFormatter {

    @Override
    public String format(HttpServletRequest request, Object object) throws Exception {
	    if (Is.empty(object) || !(object instanceof String)) return "";
	    return ((String) object).replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;");
    }

    @Override
    public Object parse(HttpServletRequest request, String string) throws Exception {
	    // Because it is only used for lists
	    return null;
    }

}