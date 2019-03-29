package org.openxava.web;

import java.util.*;
import org.openxava.model.meta.*;

/**
 * Utilities used from JSP files for lists. 
 * 
 * @since 4.5.1
 * @author Javier Paniza 
 */
public class Lists {
	
	public final static String FOCUS_PROPERTY_ID="conditionValue___0";
	
	public static String getOverflow(String browser, Collection<MetaProperty> metaProperties) {
		boolean ie9 = browser != null && browser.indexOf("MSIE 9") >= 0;
		if (!ie9) return "overflow: auto;";
		int size = 0;
		for (MetaProperty m: metaProperties) {
			int increment;
			if (m.isNumber()) increment=Math.min(5, m.getSize());
			else if (java.util.Date.class.isAssignableFrom(m.getType())) increment=Math.min(8, m.getSize());
			else increment=Math.min(20, m.getSize());
			size+=increment;
		}
		return size>75?"overflow-x: scroll;":"";
	}

}
