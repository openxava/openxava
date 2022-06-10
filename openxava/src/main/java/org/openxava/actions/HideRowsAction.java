package org.openxava.actions;

/**
 * 
 * @author Javier Paniza
 */

public class HideRowsAction extends TabBaseAction {
	
	public void execute() throws Exception {
		getTab().hideRows();		
	}

}
