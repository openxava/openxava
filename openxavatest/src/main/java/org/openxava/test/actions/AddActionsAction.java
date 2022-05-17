package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */

public class AddActionsAction extends BaseAction {
	
	private String actions;

	public void execute() throws Exception {
		for (String action: actions.split(",")) {
			addActions(action);
		}
	}

	public void setActions(String actions) {
		this.actions = actions;
	}

	public String getActions() {
		return actions;
	}

}
