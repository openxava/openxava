package com.openxava.naviox.impl;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * tmr
 * @since 7.0
 * @author Javier Paniza
 */

public interface IOrganizationsCurrentProvider {
	
	String get(ServletRequest request);
	
	String getName(HttpServletRequest request);
		
}
