package org.openxava.web;

import javax.servlet.http.*;

import org.openxava.util.*;
import org.openxava.view.*;

/**
 * Utilities used from JSP files for collections. 
 * 
 * @since 4.4
 * @author Javier Paniza 
 */
public class Collections {
	
	public static String id(HttpServletRequest request, String collectionName) {
		String propertyPrefix = request.getParameter("propertyPrefix");
		if (Is.emptyString(propertyPrefix)) {
			return collectionName;
		}
		else {
			// removing xava.ModelName.
			int idx = propertyPrefix.indexOf('.');
			idx = propertyPrefix.indexOf('.', idx+1) + 1;
			return propertyPrefix.substring(idx) + collectionName;
		}
	}
	
	public static String tabObject(String collectionId) { 
		return org.openxava.tab.Tab.COLLECTION_PREFIX + collectionId.replace('.', '_');
	}
	
	/**
	 * @since 5.9
	 */
	public static String sumPropertyScript(HttpServletRequest request, View rootView, String sumProperty) { 
		return EditorsJS.calculateScript(request.getParameter("application"), request.getParameter("module"), rootView, sumProperty); 
	}

}
