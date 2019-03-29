package org.openxava.formatters;

import javax.servlet.http.*;

/**
 * For convert to String [] (used in HTML page) to Object (used in java side),
 * and vice versa. <p>
 * 
 * @author Javier Paniza
 */

public interface IMultipleValuesFormatter {
				
	/** 
	 * From a object return a <code>String []</code> to render in HTML.
	 */
	public String [] format(HttpServletRequest request, Object object) throws Exception;
	/** 
	 * From a <code>String []</code> obtained from a HTTP request return a java object.
	 */
	public Object parse(HttpServletRequest request, String [] string) throws Exception;
	 
}
