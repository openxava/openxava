package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class AddActionToProperyAction extends ViewBaseAction {
	
	private String property;
	private String action;
	
	public void execute() throws Exception {
		getView().addActionForProperty(getProperty(), getAction());
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
