package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class RemoveActionToProperyAction extends ViewBaseAction {
	
	private String property;
	private String action;
	
	public void execute() throws Exception {
		getView().removeActionForProperty(property, action);
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
