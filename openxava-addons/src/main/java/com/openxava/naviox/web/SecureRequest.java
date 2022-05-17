package com.openxava.naviox.web;

import javax.servlet.*;
import javax.servlet.http.*;

/** 
 * @author Javier Paniza
 */
public class SecureRequest extends HttpServletRequestWrapper {

	public SecureRequest(ServletRequest request) {
		super( (HttpServletRequest) request);
	}
	
	public String getRemoteUser() {			
		return (String) ((HttpServletRequest) getRequest()).getSession().getAttribute("naviox.user");
	}
		
}
