package org.openxava.actions;

/**
 * @author Javier Paniza
 */

public class GoPreviousPageAction extends TabBaseAction {
	
	public void execute() throws Exception {
		getTab().pageBack();
		getTab().setNotResetNextTime(true);
		
		// getTab().setAllSelected(new int[0]);
	}

}
