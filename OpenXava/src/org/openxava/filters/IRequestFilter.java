package org.openxava.filters;

import javax.servlet.http.*;



/**
 * Filter that receive a HTTP request before filter.
 * 
 * Evidently these filters did not work in a swing version, 
 * but they offer a necessary flexibility.
 *  
 * @author Javier Paniza
 */

public interface IRequestFilter extends IFilter {
				
	public void setRequest(HttpServletRequest request); 

}
