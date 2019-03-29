package org.openxava.util;

import javax.servlet.http.*;

/**
 * ReportParametersProvider that receive a HTTP request before filter.
 * 
 * Create on 23/03/2011 (12:43:47)
 * @author Ana Andres
 */
public interface IRequestReportParametersProvider {
	
	void setRequest(HttpServletRequest request);
	
}
