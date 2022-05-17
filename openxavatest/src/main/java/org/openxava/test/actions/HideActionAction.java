package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza 
 */

public class HideActionAction extends BaseAction {

	private String action;
	
	public void execute() throws Exception {
		removeActions(getAction());		
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

}
