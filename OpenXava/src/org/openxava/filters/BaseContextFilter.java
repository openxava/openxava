package org.openxava.filters;

import javax.servlet.http.*;



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
	
	protected Object get(String name) throws XavaException {		
		return getContext().get(request, name);
	}
	
	protected String getString(String name) throws XavaException {
		return (String) get(name);
	}
	
	protected Integer getInteger(String name) throws XavaException {
		return (Integer) get(name);
	}

	protected Long getLong(String name) throws XavaException {
		return (Long) get(name);
	}
	
	/**
	 * Environment allows you access to the environment variables for the current module. <p>
	 * @return
	 */
	protected Environment getEnvironment() throws XavaException {		
		return ((ModuleManager) get("manager")).getEnvironment();
	}
	
}
