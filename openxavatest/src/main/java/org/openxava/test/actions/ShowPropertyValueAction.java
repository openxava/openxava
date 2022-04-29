package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class ShowPropertyValueAction extends ViewBaseAction {
	
	private String property;

	public void execute() throws Exception {
		addMessage(property + "=" + getView().getValue(property));
		
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}
	
}
