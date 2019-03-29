package org.openxava.formatters;

import javax.servlet.http.*;

import org.openxava.model.meta.*;

/**
 *  
 * @since 4.8
 * @author Javier Paniza
 */

public class BooleanListFormatter implements IMetaPropertyFormatter {
	
	public String format(HttpServletRequest request, MetaProperty p, Object booleanValue) {
		return toBoolean(booleanValue)?p.getLabel():"";
	}
	
	public Object parse(HttpServletRequest request, MetaProperty p, String string) {
		return null; // Not needed for list
	}
	
	static boolean toBoolean(Object booleanValue) {
		if (booleanValue instanceof Boolean) { 		
			return ((Boolean) booleanValue).booleanValue();
			
		}
		if (booleanValue instanceof Number) {
			return ((Number) booleanValue).intValue() != 0;
		}
		return false;
	}

}
