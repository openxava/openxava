package org.openxava.actions;

/**
 * Returns to previous view and previous controllers. <p>
 * 
 * If called from a dialog it does NOT close it.
 * 
 * @author Javier Paniza
 */

public class ReturnAction extends ViewBaseAction implements INavigationAction {

	public void execute() throws Exception {
		returnToPreviousView();		
	}	
	
	public String [] getNextControllers() {
		return PREVIOUS_CONTROLLERS;		
	}
	
	public String getCustomView() {
		return PREVIOUS_VIEW; 
	}

}
