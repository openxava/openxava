package com.openxava.naviox.web.dwr;

import javax.servlet.http.*;
import org.openxava.util.*; 

/**
 * 
 * @since 5.2.1
 * @author Javier Paniza
 */
class RequestReseter {
	
	public static void reset(HttpServletRequest request) {
		Locales.setCurrent(request); 
	}

}
