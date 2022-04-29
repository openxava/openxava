package org.openxava.test.actions;

import javax.servlet.http.*;

import org.openxava.actions.*;
import org.openxava.controller.*;

/**
 * 
 * @author Javier Paniza
 */
public class TestContextObjectAction extends BaseAction {

	private String object;
	
	public void execute() throws Exception {
		if (getContext(getRequest()).exists(getRequest(), getObject())) {
			addMessage("attribute_exists", "'" + getObject() + "'");
		}
		else {
			addMessage("attribute_not_exists", "'" + getObject() + "'");
		}
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}
	
	private static ModuleContext getContext(HttpServletRequest request) { 
		return (ModuleContext) request.getSession().getAttribute("context");
	}
}
