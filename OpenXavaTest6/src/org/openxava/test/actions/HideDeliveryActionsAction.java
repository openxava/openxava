package org.openxava.test.actions;

import org.openxava.actions.*;


/**
 * @author Javier Paniza
 */
public class HideDeliveryActionsAction extends BaseAction implements INavigationAction {
		
	public void execute() throws Exception {
	}
		
	public String[] getNextControllers() {		
		return new String [] { "CRUD" };
	}

	public String getCustomView() {		
		return SAME_VIEW;
	}

}
