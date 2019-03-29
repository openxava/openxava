package org.openxava.formatters;

import javax.servlet.http.*;

import org.openxava.util.*;

/**
 * Shows a check icon for true values in list mode. 
 * 
 * @since 5.9
 * @author Javier Paniza
 */

public class BooleanIconListFormatter implements IFormatter {
	
	public String format(HttpServletRequest request, Object booleanValue) {
		String yes = Labels.get("yes", Locales.getCurrent());		
		String no = Labels.get("no", Locales.getCurrent()); 
		return BooleanListFormatter.toBoolean(booleanValue)?"<div style='display: inline-block; width: calc(100% - 10px); text-align: center;'><i class='mdi mdi-check'></i><span style='display: none;'>" + yes + "</span></div>":"<span style='display: none;'>" + no + "</span>"; 
	}
	
	public Object parse(HttpServletRequest request, String string) {
		return null; // Not needed for list
	}
	
}
