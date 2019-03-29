package org.openxava.formatters;

import javax.servlet.http.*;



import org.openxava.util.*;


/**
 * @author Javier Paniza
 */

public class BooleanFormatter implements IFormatter {
	
	private boolean nullAsFalse = false; 
	
	public String format(HttpServletRequest request, Object booleanValue) {
		if (booleanValue == null) {
			return Labels.get("no", Locales.getCurrent());
		}
		boolean r = false;
		if (booleanValue instanceof Boolean) { 		
			r = ((Boolean) booleanValue).booleanValue();
			
		}
		else if (booleanValue instanceof Number) {
			r = ((Number) booleanValue).intValue() != 0;
		}
		else {
			return "";
		}
		return r?Labels.get("yes", Locales.getCurrent()):Labels.get("no", Locales.getCurrent());
	}
	
	public Object parse(HttpServletRequest request, String string) {
		if (Is.emptyString(string)) return nullAsFalse?Boolean.FALSE:null;		
		if (
			"yes".equalsIgnoreCase(string) ||
			"S\u00ED".equalsIgnoreCase(string) || 
			"S\u00CD".equalsIgnoreCase(string) ||
			"Si".equalsIgnoreCase(string) ||
			"true".equalsIgnoreCase(string) ||
			"verdadero".equalsIgnoreCase(string)) return Boolean.TRUE;
		return Boolean.FALSE;
	}

	public boolean isNullAsFalse() {
		return nullAsFalse;
	}

	public void setNullAsFalse(boolean nullAsFalse) {
		this.nullAsFalse = nullAsFalse;
	}
	
}
