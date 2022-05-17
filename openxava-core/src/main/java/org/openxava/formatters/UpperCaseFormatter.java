package org.openxava.formatters;

import javax.servlet.http.*;




/**
 * @author Javier Paniza
 */

public class UpperCaseFormatter implements IFormatter {
	
	
	
	public String format(HttpServletRequest request, Object string) {		
		return string==null?"":string.toString().toUpperCase();			
	}
	
	public Object parse(HttpServletRequest request, String string) {
		return string==null?"":string.toUpperCase();		
	}
	
}
