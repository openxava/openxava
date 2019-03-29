package org.openxava.util.xmlparse;

import org.apache.commons.logging.*;
import org.openxava.util.*;
import org.w3c.dom.*;

/**
 * 
 * @author Javier Paniza
 */

public class ParserUtil {
	
	private static Log log = LogFactory.getLog(ParserUtil.class);
	
	public static Element getElement(Element el, String label) {
		NodeList l = el.getElementsByTagName(label);
		if (l.getLength() < 1)
			return null;
		return (Element) l.item(0);
	}
		
	public static boolean getAttributeBoolean(Element el, String label) {
		String s = el.getAttribute(label);	
		return Boolean.valueOf(s).booleanValue();
	}
	
	public static boolean getAttributeBoolean(Element el, String label, boolean defaultValue) { 
		String s = el.getAttribute(label);	
		if (Is.emptyString(s)) return defaultValue;
		return Boolean.valueOf(s).booleanValue();
	}
	
	public static boolean getBoolean(Element el, String label) {
		String s = getString(el, label);
		return Boolean.valueOf(s).booleanValue();
	}
	
		
	public static int getInt(Element el, String label) throws XavaException {
		String s = getString(el, label);
		if (s == null)
			return 0;
		try {
			return Integer.parseInt(s);
		} 
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("element_or_attribute_to_integer_error", label);
		}
	}
	
	public static int getAttributeInt(Element el, String label) throws XavaException {
		String s = el.getAttribute(label);
		if (Is.emptyString(s)) return 0;
		try {
			return Integer.parseInt(s);
		} 
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("element_or_attribute_to_integer_error", label + " [" + s + "]");
		}
	}
	
	public static String getString(Element el, String label) {
		NodeList l = el.getElementsByTagName(label);
		if (l.getLength() < 1)
			return null;
		Node n = l.item(0).getFirstChild();
		return n == null ? "" : n.getNodeValue();
	}

}

