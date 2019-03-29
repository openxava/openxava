package org.openxava.formatters;

import javax.servlet.http.*;

import org.openxava.controller.*;
import org.openxava.util.*;


/**
 * Base formatter that allow to access to context object
 * in current module. 
 * 
 * @author Javier Paniza
 */

abstract public class BaseFormatter implements IFormatter {
	
	
	
	private ModuleContext getContext(HttpServletRequest request) {
		ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");
		Assert.assertNotNull(XavaResources.getString("context_required_in_session"), context);		
		return context;		
	}	
	
	/** Put a object in the context of the current module. */
	protected void put(HttpServletRequest request, String name, Object value) throws XavaException {
		getContext(request).put(request, name, value);
	}
	
	/** Get a object from the context of the current module. */
	protected Object get(HttpServletRequest request, String name) throws XavaException {
		return getContext(request).get(request, name);
	}
	
	/** Get a object from the context of the current module. */
	protected Object get(HttpServletRequest request, String name, String className) throws XavaException {
		return getContext(request).get(request, name, className);
	}
		
	/** Get a String object from the context of the current module. */
	protected String getString(HttpServletRequest request, String name) throws XavaException {
		return (String) get(request, name);
	}
	
	/** Get a Integer object from the context of the current module. */
	protected Integer getInteger(HttpServletRequest request, String name) throws XavaException {
		return (Integer) get(request, name);
	}
	
}
