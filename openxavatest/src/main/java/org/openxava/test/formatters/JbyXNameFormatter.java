package org.openxava.test.formatters;

import javax.servlet.http.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */

public class JbyXNameFormatter extends NameFormatter {

	public String format(HttpServletRequest request, Object object)	throws Exception {
		String result = super.format(request, object);		
		return Strings.change(result, "J", "X");
	}

}
