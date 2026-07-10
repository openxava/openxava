package org.openxava.filters;

import jakarta.servlet.http.*;



import org.openxava.controller.*;
import org.openxava.util.*;


/**
 * Base filter to allow access to a context object of
 * current module and the other modules too. <p>
 * 
 * @author Javier Paniza
 */

abstract public class BaseContextFilter implements IRequestFilter {

	transient private HttpServletRequest request; 	
	private ModuleContext context;
	

	public void setRequest(HttpServletRequest request) {
		this.request = request;		
		this.context = null; 
	}
	
	protected ModuleContext getContext() {
		if (context == null) {
			context = (ModuleContext) request.getSession().getAttribute("context");
			Assert.assertNotNull(XavaResources.getString("webcontext_required"), context);
		}
		return context;		
	}	
	
	/**
	* @throws XavaException
	 */
	protected Object get(String name) {		
		return getContext().get(request, name);
	}
	
	/**
	* @throws XavaException
	 */
	protected String getString(String name) {
		return (String) get(name);
	}
	
	/**
	* @throws XavaException
	 */
	protected Integer getInteger(String name) {
		return (Integer) get(name);
	}

	/**
	* @throws XavaException
	 */
	protected Long getLong(String name) {
		return (Long) get(name);
	}
	
	/**
	 * Environment allows you access to the environment variables for the current module. <p>
	 * @return
	 * @throws XavaException
	 */
	protected Environment getEnvironment() {		
		return ((ModuleManager) get("manager")).getEnvironment();
	}
	
}
