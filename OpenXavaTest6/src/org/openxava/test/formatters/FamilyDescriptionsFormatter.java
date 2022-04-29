package org.openxava.test.formatters;

import javax.servlet.http.*;

import org.openxava.formatters.*;

/**
 * @author Javier Paniza
 */

public class FamilyDescriptionsFormatter extends BaseFormatter {	

	public String format(HttpServletRequest request, Object object)	throws Exception {		
		return "0" + object;
	}

	public Object parse(HttpServletRequest request, String string) throws Exception {
		return null;
	}

}
